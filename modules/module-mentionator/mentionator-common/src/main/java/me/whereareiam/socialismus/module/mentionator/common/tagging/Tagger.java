package me.whereareiam.socialismus.module.mentionator.common.tagging;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.conditia.result.Verdict;
import me.whereareiam.keystone.model.SerializerContent;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.model.Worker;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.condition.Recordings;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.mentionator.api.model.config.MentionatorMessages;
import me.whereareiam.socialismus.module.mentionator.api.model.config.MentionatorSettings;
import me.whereareiam.socialismus.module.mentionator.api.model.tag.Tag;
import me.whereareiam.socialismus.module.mentionator.api.model.tagging.TagUse;
import me.whereareiam.socialismus.module.mentionator.api.model.tagging.Tagging;
import me.whereareiam.socialismus.module.mentionator.common.condition.Catalog;
import me.whereareiam.socialismus.module.mentionator.common.condition.MentionatorConditions;
import me.whereareiam.socialismus.registry.WorkerProcessor;
import me.whereareiam.socialismus.service.condition.ConditionTexts;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Finds the tags of a message once its chat is chosen and before its recipients are, so a chat's {@code reaches} can
 * let tagged players receive it: checks who may use each tag and whom it tags, applies the limits of a message, puts
 * each tag into the message in its plain format and marks the message with its {@link Tagging}. A sender who may not
 * use a tag is told why; the tag stays plain text. Only messages of players this server hosts are tagged; a message
 * relayed from another server carries its tagging from where it was written.
 */
@Singleton
public class Tagger {
	/**
	 * Where tagging runs among the chat workers: after the chat (50) and its players (75) are known, before the chat's
	 * {@code reaches} chooses recipients (100).
	 */
	public static final int PRIORITY = 90;

	private final MentionatorConditions conditions;
	private final Candidates candidates;
	private final Provider<MentionatorSettings> settings;
	private final Provider<MentionatorMessages> messages;
	private final ConditionTexts texts;
	private final WorkerProcessor<ChatMessage> workers;
	private final Worker<ChatMessage> worker = new Worker<>(this::tag, PRIORITY, true, false);

	@Inject
	public Tagger(
			@NotNull MentionatorConditions conditions,
			@NotNull Candidates candidates,
			@NotNull Provider<MentionatorSettings> settings,
			@NotNull Provider<MentionatorMessages> messages,
			@NotNull ConditionTexts texts,
			@NotNull WorkerProcessor<ChatMessage> workers
	) {
		this.conditions = conditions;
		this.candidates = candidates;
		this.settings = settings;
		this.messages = messages;
		this.texts = texts;
		this.workers = workers;
	}

	/**
	 * Starts tagging the messages written on this server.
	 */
	public void start() {
		workers.addWorker(worker);
	}

	/**
	 * Stops tagging.
	 */
	public void stop() {
		workers.removeWorker(worker);
	}

	/**
	 * Tags a message.
	 *
	 * @param message the message, its chat chosen
	 * @return the message, its tags in place
	 */
	public @NotNull ChatMessage tag(@NotNull ChatMessage message) {
		if (message.isCancelled() || !message.getSender().isHostedHere() || message.getChat() == null || message.getContent() == null) return message;
		if (message.getAttribute(Tagging.ATTRIBUTE).isPresent()) return message;

		Catalog catalog = conditions.catalog();
		if (catalog.getTags().isEmpty()) return message;

		String text = ContentEdits.plain(message.getContent());
		List<Found> found = TagMatcher.find(text, catalog.getTags(), candidates.all());
		if (found.isEmpty()) return message;

		Pass pass = new Pass(catalog, message, text, catalog.limit(message.getSender(), message), isSelfAllowed());
		found.forEach(pass::take);
		pass.tellRefusals();
		if (pass.edits.isEmpty()) return message;

		message.setContent(ContentEdits.apply(message.getContent(), pass.edits));
		if (pass.uses.isEmpty()) return message;

		message.setAttribute(Tagging.ATTRIBUTE, new Tagging(pass.uses));
		// What a used tag's requires record, such as a cooldown, is recorded once the message went out.
		pass.used.forEach(tag -> Recordings.of(message).add(pass.verdicts.get(tag)));

		return message;
	}

	private boolean isSelfAllowed() {
		MentionatorSettings.Limits limits = settings.get().getLimits();

		return limits != null && limits.isSelf();
	}

	/**
	 * One message being tagged.
	 */
	private final class Pass {
		private final Catalog catalog;
		private final ChatMessage message;
		private final SocialismusPlayer sender;
		private final String text;
		private final int limit;
		private final boolean self;
		private final Map<Tag, Verdict> verdicts = new IdentityHashMap<>();
		private final Map<Tag, Integer> counts = new IdentityHashMap<>();
		private final Map<Tag, List<Integer>> formats = new IdentityHashMap<>();
		private final List<Tag> used = new ArrayList<>();
		private final List<TagUse> uses = new ArrayList<>();
		private final List<ContentEdits.Edit> edits = new ArrayList<>();
		private boolean overLimit;

		private Pass(Catalog catalog, ChatMessage message, String text, int limit, boolean self) {
			this.catalog = catalog;
			this.message = message;
			this.sender = message.getSender();
			this.text = text;
			this.limit = limit;
			this.self = self;
		}

		private void take(Found found) {
			int start = found.escaped() ? found.start() - 1 : found.start();
			if (!ContentEdits.changeable(message.getContent(), start, found.end())) return;
			if (found.escaped()) {
				edits.add(new ContentEdits.Edit(start, found.start(), null));
				return;
			}

			Tag tag = found.tag();
			Verdict verdict = verdicts.computeIfAbsent(tag, ignored -> catalog.uses(tag, sender, message));
			if (!verdict.passed()) return;

			List<UUID> targets = targets(found);
			if (found.form().namesValue() && targets.isEmpty()) return;
			if (!withinTagLimit(tag)) return;
			if (limit >= 0 && uses.size() >= limit) {
				overLimit = true;
				return;
			}

			TagUse use = new TagUse(
					tag.getId(),
					text.substring(found.start(), found.end()),
					found.match(),
					targets,
					formats.computeIfAbsent(tag, ignored -> catalog.formats(tag, sender, message))
			);
			uses.add(use);
			if (!used.contains(tag)) used.add(tag);
			edits.add(new ContentEdits.Edit(found.start(), found.end(), TagRenderer.plain(catalog, tag, use)));
		}

		private List<UUID> targets(Found found) {
			List<UUID> targets = new ArrayList<>();
			for (Candidate candidate : found.candidates()) {
				SocialismusPlayer player = candidate.player();
				if (!self && player.getUniqueId().equals(sender.getUniqueId())) continue;
				if (catalog.targets(found.tag(), player, sender, message)) targets.add(player.getUniqueId());
			}

			return targets;
		}

		private boolean withinTagLimit(Tag tag) {
			Integer cap = tag.getParameters() == null ? null : tag.getParameters().getPerMessage();
			int count = counts.merge(tag, 1, Integer::sum);

			return cap == null || cap < 0 || count <= cap;
		}

		private void tellRefusals() {
			verdicts.forEach((tag, verdict) -> {
				if (!(verdict instanceof Verdict.Failed failed)) return;

				texts.denied(failed, MentionatorConditions.PHRASE_SCOPE, Map.of("tag", tag.getId()))
						.ifPresent(phrase -> sender.sendMessage(Serializer.serialize(phrase.toContent(sender))));
			});
			if (!overLimit) return;

			String told = messages.get().getLimits() == null ? null : messages.get().getLimits().getPerMessage();
			if (told == null || told.isBlank()) return;

			Component component = Serializer.serialize(SerializerContent.builder()
					.receiver(sender)
					.message(told)
					.placeholder("limit", String.valueOf(limit))
					.build());
			sender.sendMessage(component);
		}
	}
}
