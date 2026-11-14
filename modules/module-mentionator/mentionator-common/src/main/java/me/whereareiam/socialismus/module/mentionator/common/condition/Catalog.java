package me.whereareiam.socialismus.module.mentionator.common.condition;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.whereareiam.conditia.Conditions;
import me.whereareiam.conditia.Place;
import me.whereareiam.conditia.model.Part;
import me.whereareiam.conditia.model.Requirements;
import me.whereareiam.conditia.model.Situation;
import me.whereareiam.conditia.result.Verdict;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.mentionator.api.model.config.MentionatorSettings;
import me.whereareiam.socialismus.module.mentionator.api.model.notification.Notification;
import me.whereareiam.socialismus.module.mentionator.api.model.notification.NotificationContent;
import me.whereareiam.socialismus.module.mentionator.api.model.tag.Tag;
import me.whereareiam.socialismus.module.mentionator.api.model.tag.TagFormat;
import me.whereareiam.socialismus.service.Phrasebook;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static me.whereareiam.socialismus.model.condition.ConditionParts.MESSAGE;
import static me.whereareiam.socialismus.model.condition.ConditionParts.RECIPIENT;
import static me.whereareiam.socialismus.model.condition.ConditionParts.SENDER;
import static me.whereareiam.socialismus.module.mentionator.common.condition.MentionatorParts.TARGET;

/**
 * One generation of Mentionator's tags and notifications with their bound lists, bound together when they are loaded
 * and closed together when they are reloaded, so a tag is never checked against the lists of another generation.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Catalog implements AutoCloseable {
	/**
	 * The enabled tags, highest priority first.
	 */
	@Getter
	private final List<Tag> tags;
	private final Map<String, Tag> tagsById;
	private final Map<String, Notification> notifications;
	private final Map<Tag, TagPlaces> tagPlaces;
	private final Map<NotificationContent, Place> contentPlaces;
	private final List<Limit> perMessage;
	private final @Nullable Place receivedBypass;
	private final List<Place> bound;

	/**
	 * A catalog without tags, used before Mentionator's lists are bound.
	 */
	public static @NotNull Catalog empty() {
		return new Catalog(List.of(), Map.of(), Map.of(), Map.of(), Map.of(), List.of(), null, List.of());
	}

	/**
	 * Binds the lists of the tags, notifications and settings.
	 *
	 * @param conditions    binds the lists
	 * @param phrases       checks the phrases the lists' texts refer to
	 * @param tags          the tags of every file in {@code tags/}
	 * @param notifications the notifications of every file in {@code notifications/}
	 * @param settings      Mentionator's settings
	 * @return the catalog
	 * @throws me.whereareiam.conditia.exception.ConditionException when a list cannot be bound; nothing stays bound
	 */
	public static @NotNull Catalog bind(
			@NotNull Conditions conditions,
			@NotNull Phrasebook phrases,
			@NotNull List<Tag> tags,
			@NotNull List<Notification> notifications,
			@NotNull MentionatorSettings settings
	) {
		Binding binding = new Binding(conditions, phrases, new ArrayList<>());
		try {
			Map<String, Tag> tagsById = new LinkedHashMap<>();
			for (Tag tag : tags) {
				if (!tag.isEnabled() || tag.getId() == null) continue;
				if (tagsById.putIfAbsent(tag.getId(), tag) != null)
					Logger.warn("Mentionator knows the tag %s already; the second one is left out", tag.getId());
			}

			Map<Tag, TagPlaces> tagPlaces = new IdentityHashMap<>();
			for (Tag tag : tagsById.values()) tagPlaces.put(tag, binding.tag(tag));

			Map<String, Notification> notificationsById = new LinkedHashMap<>();
			Map<NotificationContent, Place> contentPlaces = new IdentityHashMap<>();
			for (Notification notification : notifications) {
				if (notification.getId() == null || notificationsById.putIfAbsent(notification.getId(), notification) != null)
					continue;

				List<NotificationContent> contents = notification.getContents() == null ? List.of() : notification.getContents();
				for (int index = 0; index < contents.size(); index++) {
					NotificationContent content = contents.get(index);
					if (content == null || content.getRequires() == null) continue;

					String path = "notifications." + notification.getId() + ".contents[" + index + "].requires";
					contentPlaces.put(content, binding.open(path, content.getRequires(), TARGET, SENDER));
				}
			}
			warnUnknownNotifications(tagsById.values(), notificationsById);

			MentionatorSettings.Limits limits = settings.getLimits();
			List<Limit> perMessage = new ArrayList<>();
			List<MentionatorSettings.PerMessage> entries = limits == null || limits.getPerMessage() == null ? List.of() : limits.getPerMessage();
			for (int index = 0; index < entries.size(); index++) {
				MentionatorSettings.PerMessage entry = entries.get(index);
				perMessage.add(new Limit(entry.getLimit(), binding.open("limits.perMessage[" + index + "].requires", entry.getRequires(), SENDER, MESSAGE)));
			}
			Place receivedBypass = limits == null || limits.getReceived() == null || limits.getReceived().getBypass() == null
					? null
					: binding.open("limits.received.bypass", limits.getReceived().getBypass(), SENDER, MESSAGE);

			List<Tag> byPriority = new ArrayList<>(tagsById.values());
			byPriority.sort(Comparator.comparingInt(Tag::getPriority).reversed());

			return new Catalog(
					List.copyOf(byPriority),
					tagsById,
					notificationsById,
					tagPlaces,
					contentPlaces,
					List.copyOf(perMessage),
					receivedBypass,
					List.copyOf(binding.bound())
			);
		} catch (RuntimeException failure) {
			binding.bound().forEach(Place::close);
			throw failure;
		}
	}

	private static void warnUnknownNotifications(Iterable<Tag> tags, Map<String, Notification> notifications) {
		for (Tag tag : tags) {
			if (tag.getNotify() == null) continue;

			tag.getNotify().stream()
					.filter(id -> !notifications.containsKey(id))
					.forEach(id -> Logger.warn("The tag %s notifies with %s, which no file in notifications/ holds", tag.getId(), id));
		}
	}

	/**
	 * Finds an enabled tag by its id.
	 *
	 * @param id the tag's id
	 * @return the tag
	 */
	public @NotNull Optional<Tag> tag(@NotNull String id) {
		return Optional.ofNullable(tagsById.get(id));
	}

	/**
	 * Finds a notification by its id.
	 *
	 * @param id the notification's id
	 * @return the notification
	 */
	public @NotNull Optional<Notification> notification(@NotNull String id) {
		return Optional.ofNullable(notifications.get(id));
	}

	/**
	 * Checks whether a sender may use a tag: its {@code requires}.
	 *
	 * @param tag     the tag, from this catalog
	 * @param sender  the sender
	 * @param message the message they write
	 * @return the verdict; what it records, such as a cooldown, is recorded once the message went out
	 */
	public @NotNull Verdict uses(@NotNull Tag tag, @NotNull SocialismusPlayer sender, @NotNull ChatMessage message) {
		TagPlaces places = tagPlaces.get(tag);
		if (places == null) return unbound(tag);

		return places.requires().check(Situation.of(SENDER, sender).and(MESSAGE, message));
	}

	/**
	 * Checks whether a tag tags a player: its {@code targets}.
	 *
	 * @param tag     the tag, from this catalog
	 * @param target  the player
	 * @param sender  the sender
	 * @param message the message
	 * @return whether it tags them
	 */
	public boolean targets(@NotNull Tag tag, @NotNull SocialismusPlayer target, @NotNull SocialismusPlayer sender, @NotNull ChatMessage message) {
		TagPlaces places = tagPlaces.get(tag);

		return places != null && places.targets().check(Situation.of(TARGET, target).and(SENDER, sender).and(MESSAGE, message)).passed();
	}

	/**
	 * Checks whether a sender's tag notifies players who turned tags off: its {@code bypass}.
	 *
	 * @param tag     the tag, from this catalog
	 * @param sender  the sender
	 * @param message the message
	 * @return whether it notifies them
	 */
	public boolean bypasses(@NotNull Tag tag, @NotNull SocialismusPlayer sender, @NotNull ChatMessage message) {
		TagPlaces places = tagPlaces.get(tag);
		if (places == null || places.bypass() == null) return false;

		return places.bypass().check(Situation.of(SENDER, sender).and(MESSAGE, message)).passed();
	}

	/**
	 * Returns the formats of a tag whose {@code requires} a sender meets, by their place in its list.
	 *
	 * @param tag     the tag, from this catalog
	 * @param sender  the sender
	 * @param message the message
	 * @return the places of the formats
	 */
	public @NotNull List<Integer> formats(@NotNull Tag tag, @NotNull SocialismusPlayer sender, @NotNull ChatMessage message) {
		TagPlaces places = tagPlaces.get(tag);
		if (places == null) return List.of();

		List<Integer> met = new ArrayList<>();
		Situation situation = Situation.of(SENDER, sender).and(MESSAGE, message);
		for (int index = 0; index < places.formats().size(); index++)
			if (places.formats().get(index).requires().check(situation).passed()) met.add(index);

		return met;
	}

	/**
	 * Returns whether a format of a tag has an empty {@code reaches}, so every viewer meets it.
	 *
	 * @param tag    the tag, from this catalog
	 * @param format the format's place in its list
	 * @return whether every viewer meets it
	 */
	public boolean reachesAnyone(@NotNull Tag tag, int format) {
		TagPlaces places = tagPlaces.get(tag);

		return places != null && format < places.formats().size() && places.formats().get(format).reachesAnyone();
	}

	/**
	 * Checks whether a viewer sees a tag in one of its formats: the format's {@code reaches}.
	 *
	 * @param tag     the tag, from this catalog
	 * @param format  the format's place in its list
	 * @param viewer  the viewer
	 * @param sender  the sender
	 * @param message the message, carrying as its tagging only the tag being shown
	 * @return whether they see it so
	 */
	public boolean reaches(@NotNull Tag tag, int format, @NotNull SocialismusPlayer viewer, @NotNull SocialismusPlayer sender, @NotNull ChatMessage message) {
		TagPlaces places = tagPlaces.get(tag);
		if (places == null || format >= places.formats().size()) return false;

		FormatPlaces formatPlaces = places.formats().get(format);
		if (formatPlaces.reachesAnyone()) return true;

		return formatPlaces.reaches().check(Situation.of(RECIPIENT, viewer).and(SENDER, sender).and(MESSAGE, message)).passed();
	}

	/**
	 * Returns how many tags a sender's message may hold: {@code limits.perMessage}, the last entry they meet.
	 *
	 * @param sender  the sender
	 * @param message the message
	 * @return the limit, {@code -1} for any number
	 */
	public int limit(@NotNull SocialismusPlayer sender, @NotNull ChatMessage message) {
		Situation situation = Situation.of(SENDER, sender).and(MESSAGE, message);
		for (Limit limit : perMessage.reversed())
			if (limit.place().check(situation).passed()) return limit.limit();

		return -1;
	}

	/**
	 * Checks whether a sender's tags notify players past {@code limits.received}: its {@code bypass}.
	 *
	 * @param sender  the sender
	 * @param message the message
	 * @return whether they do
	 */
	public boolean bypassesReceived(@NotNull SocialismusPlayer sender, @NotNull ChatMessage message) {
		return receivedBypass != null && receivedBypass.check(Situation.of(SENDER, sender).and(MESSAGE, message)).passed();
	}

	/**
	 * Checks whether a tagged player gets a content of a notification: its {@code requires}.
	 *
	 * @param content the content, from this catalog
	 * @param target  the tagged player
	 * @param sender  the player who tagged them
	 * @return whether they get it
	 */
	public boolean receives(@NotNull NotificationContent content, @NotNull SocialismusPlayer target, @NotNull SocialismusPlayer sender) {
		Place place = contentPlaces.get(content);

		return place == null || place.check(Situation.of(TARGET, target).and(SENDER, sender)).passed();
	}

	@Override
	public void close() {
		bound.forEach(Place::close);
	}

	private static Verdict unbound(Tag tag) {
		String path = "tags." + tag.getId();

		return new Verdict.Broken(path, path, new IllegalStateException("The lists of " + path + " are not bound"));
	}

	private record TagPlaces(Place requires, Place targets, @Nullable Place bypass, List<FormatPlaces> formats) {
	}

	private record FormatPlaces(Place requires, Place reaches, boolean reachesAnyone) {
	}

	private record Limit(int limit, Place place) {
	}

	private record Binding(Conditions conditions, Phrasebook phrases, List<Place> bound) {
		Place open(String path, @Nullable Requirements requires, Part<?>... parts) {
			Place place = conditions.place(path, requires == null ? Requirements.none() : requires, parts);
			bound.add(phrases.checked(MentionatorConditions.PHRASE_SCOPE, place));

			return place;
		}

		TagPlaces tag(Tag tag) {
			String path = "tags." + tag.getId();
			List<FormatPlaces> formats = new ArrayList<>();
			List<TagFormat> written = tag.getFormats() == null ? List.of() : tag.getFormats();
			for (int index = 0; index < written.size(); index++) {
				TagFormat format = written.get(index);
				Requirements reaches = format.getReaches() == null ? Requirements.none() : format.getReaches();
				formats.add(new FormatPlaces(
						open(path + ".formats[" + index + "].requires", format.getRequires(), SENDER, MESSAGE),
						open(path + ".formats[" + index + "].reaches", reaches, RECIPIENT, SENDER, MESSAGE),
						reaches.getConditions().items().isEmpty()
				));
			}

			return new TagPlaces(
					open(path + ".requires", tag.getRequires(), SENDER, MESSAGE),
					open(path + ".targets", tag.getTargets(), TARGET, SENDER, MESSAGE),
					tag.getBypass() == null ? null : open(path + ".bypass", tag.getBypass(), SENDER, MESSAGE),
					formats
			);
		}
	}
}
