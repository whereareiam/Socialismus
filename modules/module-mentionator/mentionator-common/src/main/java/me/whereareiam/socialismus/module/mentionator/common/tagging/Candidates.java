package me.whereareiam.socialismus.module.mentionator.common.tagging;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.mentionator.common.notify.RemoteTaggings;
import me.whereareiam.socialismus.module.mentionator.common.value.PlaceholderValues;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The players tags may name: those this server hosts and, with SocialismusNetwork, those of the whole network, each
 * with their placeholder values. Offline players are no candidates, so their names stay plain text.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class Candidates {
	private final PlayerRegistry players;
	private final RemoteTaggings remote;
	private final PlaceholderValues values;

	/**
	 * Returns every candidate, a player hosted here before the same player elsewhere.
	 *
	 * @return the candidates
	 */
	public @NotNull List<Candidate> all() {
		Map<UUID, SocialismusPlayer> byId = new LinkedHashMap<>();
		players.getPlayers().forEach(player -> byId.putIfAbsent(player.getUniqueId(), player));
		remote.current().ifPresent(tagging -> tagging.players().forEach(player -> byId.putIfAbsent(player.getUniqueId(), player)));

		return byId.values().stream()
				.map(player -> new Candidate(player, values.of(player.getUniqueId())))
				.toList();
	}
}
