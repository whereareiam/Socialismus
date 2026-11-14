package me.whereareiam.socialismus.module.mentionator.common.value;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.event.EventListener;
import me.whereareiam.socialismus.event.base.SocialisticEvent;
import me.whereareiam.socialismus.event.player.SocialismusPlayerAddedEvent;
import me.whereareiam.socialismus.event.player.SocialismusPlayerServerChangedEvent;
import me.whereareiam.socialismus.integration.Integration;
import me.whereareiam.socialismus.integration.placeholder.PlaceholderIntegration;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.mentionator.api.model.tag.Tag;
import me.whereareiam.socialismus.module.mentionator.common.MentionatorStores;
import me.whereareiam.socialismus.module.mentionator.common.condition.MentionatorConditions;
import me.whereareiam.socialismus.module.mentionator.common.tagging.Form;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.store.Store;
import me.whereareiam.socialismus.store.Stores;
import me.whereareiam.socialismus.util.PlaceholderUtil;
import org.jetbrains.annotations.NotNull;

import java.time.Clock;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The values of the {@code %placeholder%} forms of the tags, for every player: each server reads them for the players
 * it hosts when they join or change servers and every {@code refresh} of the tag, and keeps them where every server
 * finds them, so {@code @%luckperms_primary_group_name%} tags players on other servers too.
 */
@Singleton
public class PlaceholderValues implements EventListener {
	/**
	 * How often a placeholder is read again when its tag says nothing.
	 */
	public static final Duration REFRESH = Duration.ofMinutes(1);

	private final Store<UUID, Values> values;
	private final Provider<Set<Integration>> integrations;
	private final PlayerRegistry players;
	private final MentionatorConditions conditions;
	private final Clock clock;
	private final Map<String, Long> lastRead = new ConcurrentHashMap<>();

	@Inject
	public PlaceholderValues(
			@NotNull Stores stores,
			@NotNull Provider<Set<Integration>> integrations,
			@NotNull PlayerRegistry players,
			@NotNull MentionatorConditions conditions,
			@NotNull Clock clock
	) {
		this.values = stores.of(MentionatorStores.VALUES);
		this.integrations = integrations;
		this.players = players;
		this.conditions = conditions;
		this.clock = clock;
	}

	/**
	 * Returns a player's values, as their server last read them.
	 *
	 * @param player the player's unique id
	 * @return the values by placeholder, empty when none was read
	 */
	public @NotNull Map<String, String> of(@NotNull UUID player) {
		return values.get(player).map(Values::values).orElse(Map.of());
	}

	@SocialisticEvent
	public void onAdded(SocialismusPlayerAddedEvent event) {
		read(event.getPlayer());
	}

	@SocialisticEvent
	public void onServerChanged(SocialismusPlayerServerChangedEvent event) {
		read(event.getPlayer());
	}

	/**
	 * Reads every placeholder the tags name for a player this server hosts.
	 *
	 * @param player the player
	 */
	public void read(@NotNull SocialismusPlayer player) {
		read(player, refreshes().keySet());
	}

	/**
	 * Reads again, for every player this server hosts, the placeholders whose {@code refresh} has passed.
	 */
	public void refresh() {
		long now = clock.millis();
		Map<String, Duration> refreshes = refreshes();
		Set<String> due = ConcurrentHashMap.newKeySet();
		refreshes.forEach((placeholder, every) -> {
			long last = lastRead.getOrDefault(placeholder, 0L);
			if (now - last < every.toMillis()) return;

			due.add(placeholder);
			lastRead.put(placeholder, now);
		});
		if (due.isEmpty()) return;

		players.getPlayers().forEach(player -> read(player, due));
	}

	private void read(SocialismusPlayer player, Set<String> placeholders) {
		Optional<PlaceholderIntegration> integration = integration();
		if (placeholders.isEmpty() || integration.isEmpty()) return;

		Map<String, String> read = new HashMap<>(of(player.getUniqueId()));
		for (String placeholder : placeholders)
			read.put(placeholder, PlaceholderUtil.resolve(integration.get(), player.getUniqueId(), placeholder));

		values.put(player.getUniqueId(), new Values(read), lifetime());
	}

	// Long enough to outlast a few refreshes, so a player's values are only forgotten once no server reads them.
	private Duration lifetime() {
		Duration longest = refreshes().values().stream().max(Duration::compareTo).orElse(REFRESH);

		return longest.multipliedBy(3).plus(REFRESH);
	}

	private Optional<PlaceholderIntegration> integration() {
		return integrations.get().stream()
				.filter(PlaceholderIntegration.class::isInstance)
				.filter(Integration::isAvailable)
				.map(PlaceholderIntegration.class::cast)
				.findFirst();
	}

	/**
	 * Returns the placeholders the enabled tags name, each with how often it is read again: the shortest
	 * {@code refresh} of the tags naming it.
	 */
	private Map<String, Duration> refreshes() {
		Map<String, Duration> refreshes = new HashMap<>();
		for (Tag tag : conditions.catalog().getTags()) {
			Duration every = tag.getParameters() == null || tag.getParameters().getRefresh() == null
					? REFRESH
					: tag.getParameters().getRefresh();
			for (String written : tag.getWritten()) {
				String placeholder = Form.of(written).placeholder();
				if (placeholder == null) continue;

				refreshes.merge(placeholder, every, (one, other) -> one.compareTo(other) <= 0 ? one : other);
			}
		}

		return refreshes;
	}
}
