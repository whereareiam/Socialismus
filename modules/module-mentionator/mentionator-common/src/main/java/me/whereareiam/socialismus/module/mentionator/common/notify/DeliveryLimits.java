package me.whereareiam.socialismus.module.mentionator.common.notify;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.module.mentionator.api.model.config.MentionatorSettings;
import me.whereareiam.socialismus.module.mentionator.common.MentionatorStores;
import me.whereareiam.socialismus.store.Store;
import me.whereareiam.socialismus.store.Stores;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Keeps players from being notified too often: {@code limits.repeat}, how often one sender notifies the same player,
 * and {@code limits.received}, how many notifications a player gets in a while. With SocialismusNetwork every server
 * counts together.
 */
@Singleton
public class DeliveryLimits {
	private final Store<UUID, ReceivedWindow> received;
	private final Store<String, Instant> repeats;
	private final Provider<MentionatorSettings> settings;
	private final Clock clock;

	@Inject
	public DeliveryLimits(@NotNull Stores stores, @NotNull Provider<MentionatorSettings> settings, @NotNull Clock clock) {
		this.received = stores.of(MentionatorStores.RECEIVED);
		this.repeats = stores.of(MentionatorStores.REPEATS);
		this.settings = settings;
		this.clock = clock;
	}

	/**
	 * Decides whether a sender notifies a player now, and counts it when they do.
	 *
	 * @param sender           the sender's unique id
	 * @param target           the tagged player's unique id
	 * @param bypassesReceived whether the sender meets {@code limits.received.bypass}
	 * @return whether the player is notified
	 */
	public synchronized boolean admit(@NotNull UUID sender, @NotNull UUID target, boolean bypassesReceived) {
		MentionatorSettings.Limits limits = settings.get().getLimits();
		if (limits == null) return true;

		Instant now = clock.instant();
		String pair = sender + ":" + target;
		Duration repeat = limits.getRepeat();
		if (positive(repeat) && repeats.get(pair).isPresent()) return false;

		MentionatorSettings.Received receivedLimit = limits.getReceived();
		boolean counted = receivedLimit != null && receivedLimit.getCount() >= 0 && positive(receivedLimit.getPer());
		Optional<ReceivedWindow> window = counted
				? received.get(target).filter(current -> current.until() > now.toEpochMilli())
				: Optional.empty();
		if (counted && !bypassesReceived && window.map(ReceivedWindow::count).orElse(0) >= receivedLimit.getCount())
			return false;

		if (positive(repeat)) repeats.put(pair, now, repeat);
		if (!counted) return true;

		long until = window.map(ReceivedWindow::until).orElse(now.plus(receivedLimit.getPer()).toEpochMilli());
		int count = window.map(ReceivedWindow::count).orElse(0) + 1;
		received.put(target, new ReceivedWindow(count, until), Duration.ofMillis(Math.max(1, until - now.toEpochMilli())));

		return true;
	}

	private static boolean positive(@Nullable Duration duration) {
		return duration != null && !duration.isNegative() && !duration.isZero();
	}
}
