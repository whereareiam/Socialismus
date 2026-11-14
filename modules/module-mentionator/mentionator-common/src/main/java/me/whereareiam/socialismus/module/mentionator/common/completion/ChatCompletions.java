package me.whereareiam.socialismus.module.mentionator.common.completion;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.event.EventListener;
import me.whereareiam.socialismus.event.base.SocialisticEvent;
import me.whereareiam.socialismus.event.player.SocialismusPlayerAddedEvent;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.mentionator.api.MentionatorService;
import me.whereareiam.socialismus.module.mentionator.api.model.config.MentionatorSettings;
import me.whereareiam.socialismus.module.mentionator.api.model.tag.Tag;
import me.whereareiam.socialismus.module.mentionator.common.tagging.Candidate;
import me.whereareiam.socialismus.module.mentionator.common.tagging.Candidates;
import me.whereareiam.socialismus.module.mentionator.common.tagging.Form;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.type.PlatformType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Offers each player, as they type in chat, the tags they may use: a literal tag such as {@code @everyone} as it is
 * written, and a tag naming a player or a value with each name or value it may name. Bare forms are left to the
 * client, which completes names by itself. Only clients of game servers show them.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ChatCompletions implements EventListener {
	private final PlayerRegistry players;
	private final MentionatorService service;
	private final Candidates candidates;
	private final Provider<MentionatorSettings> settings;

	@SocialisticEvent
	public void onAdded(SocialismusPlayerAddedEvent event) {
		if (!PlatformType.isGameServer()) return;

		offer(event.getPlayer(), candidates.all());
	}

	/**
	 * Offers every player this server hosts their completions again, as players come and go.
	 */
	public void refresh() {
		if (!PlatformType.isGameServer()) return;

		List<Candidate> all = candidates.all();
		players.getPlayers().forEach(player -> offer(player, all));
	}

	/**
	 * Takes the offered completions back from every player, such as when Mentionator stops.
	 */
	public void clear() {
		players.getPlayers().forEach(player -> player.setChatCompletions(List.of()));
	}

	private void offer(SocialismusPlayer player, List<Candidate> all) {
		player.setChatCompletions(completions(player, service.usableTags(player), all, isSelfAllowed()));
	}

	/**
	 * Returns the completions of a player: each form of each tag they may use, with the names or values it may name.
	 *
	 * @param player the player
	 * @param tags   the tags they may use
	 * @param all    the candidates
	 * @param self   whether players may tag themselves
	 * @return the completions, in order
	 */
	static @NotNull List<String> completions(@NotNull SocialismusPlayer player, @NotNull List<Tag> tags, @NotNull List<Candidate> all, boolean self) {
		Set<String> completions = new LinkedHashSet<>();
		for (Tag tag : tags) {
			for (String written : tag.getWritten()) {
				Form form = Form.of(written);
				if (!form.namesValue()) {
					completions.add(written);
					continue;
				}
				if (form.bare()) continue;

				for (Candidate candidate : all) {
					if (!self && candidate.player().getUniqueId().equals(player.getUniqueId())) continue;

					String value = form.username() ? candidate.player().getUsername() : candidate.values().get(form.placeholder());
					if (value == null || value.isBlank()) continue;

					completions.add(form.prefix() + value + form.suffix());
				}
			}
		}

		return new ArrayList<>(completions);
	}

	private boolean isSelfAllowed() {
		MentionatorSettings.Limits limits = settings.get().getLimits();

		return limits != null && limits.isSelf();
	}
}
