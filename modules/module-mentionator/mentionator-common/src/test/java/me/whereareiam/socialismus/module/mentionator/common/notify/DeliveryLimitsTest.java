package me.whereareiam.socialismus.module.mentionator.common.notify;

import me.whereareiam.socialismus.common.config.SerializationServiceAdapter;
import me.whereareiam.socialismus.common.store.DefaultStores;
import me.whereareiam.socialismus.module.mentionator.api.model.config.MentionatorSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeliveryLimitsTest {
	private static final UUID ALEX = UUID.randomUUID();
	private static final UUID BOB = UUID.randomUUID();
	private static final UUID CARL = UUID.randomUUID();
	private static final UUID STEVE = UUID.randomUUID();

	private final MentionatorSettings settings = new MentionatorSettings();
	private Instant now = Instant.parse("2026-11-14T12:00:00Z");
	private DeliveryLimits limits;

	@BeforeEach
	void setUp() {
		settings.getLimits().setRepeat(Duration.ofSeconds(10));
		settings.getLimits().getReceived().setCount(2);
		settings.getLimits().getReceived().setPer(Duration.ofMinutes(1));
		Clock clock = new Clock() {
			public ZoneOffset getZone() {
				return ZoneOffset.UTC;
			}

			public Clock withZone(java.time.ZoneId zone) {
				return this;
			}

			public Instant instant() {
				return now;
			}
		};
		limits = new DeliveryLimits(new DefaultStores(new SerializationServiceAdapter()), () -> settings, clock);
	}

	@Test
	void theSameSenderNotifiesAPlayerOnlyOnceInARepeat() {
		assertTrue(limits.admit(ALEX, STEVE, false));
		assertFalse(limits.admit(ALEX, STEVE, false));
		assertTrue(limits.admit(BOB, STEVE, false));
	}

	@Test
	void aPlayerGetsOnlySoManyNotificationsInAWhile() {
		assertTrue(limits.admit(ALEX, STEVE, false));
		assertTrue(limits.admit(BOB, STEVE, false));
		assertFalse(limits.admit(CARL, STEVE, false));

		now = now.plus(Duration.ofMinutes(2));
		assertTrue(limits.admit(CARL, STEVE, false));
	}

	@Test
	void aSenderWhoBypassesTheLimitStillNotifies() {
		limits.admit(ALEX, STEVE, false);
		limits.admit(BOB, STEVE, false);

		assertTrue(limits.admit(CARL, STEVE, true));
	}
}
