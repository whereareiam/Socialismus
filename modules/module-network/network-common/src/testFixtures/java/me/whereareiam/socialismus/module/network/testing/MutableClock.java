package me.whereareiam.socialismus.module.network.testing;

import org.jetbrains.annotations.NotNull;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * A clock a test moves forward by hand, to let entries and leases expire without waiting.
 */
public final class MutableClock extends Clock {
	private volatile Instant now = Instant.parse("2026-01-01T00:00:00Z");

	/**
	 * Moves the clock forward.
	 */
	public void advance(@NotNull Duration duration) {
		now = now.plus(duration);
	}

	@Override
	public ZoneId getZone() {
		return ZoneOffset.UTC;
	}

	@Override
	public Clock withZone(ZoneId zone) {
		return this;
	}

	@Override
	public Instant instant() {
		return now;
	}
}
