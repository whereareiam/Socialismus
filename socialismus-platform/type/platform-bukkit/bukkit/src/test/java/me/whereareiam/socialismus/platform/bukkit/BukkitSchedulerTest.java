package me.whereareiam.socialismus.platform.bukkit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("BukkitScheduler Tests")
class BukkitSchedulerTest {
	@Test
	@DisplayName("Should convert milliseconds to ticks of 50 ms")
	void toTicksUsesFiftyMillisecondsPerTick() {
		assertEquals(20L, BukkitScheduler.toTicks(1000L));
		assertEquals(1L, BukkitScheduler.toTicks(50L));
		assertEquals(1200L, BukkitScheduler.toTicks(60_000L));
	}

	@Test
	@DisplayName("Should round a delay under one tick down to the next tick")
	void toTicksRoundsDownBelowOneTick() {
		assertEquals(0L, BukkitScheduler.toTicks(0L));
		assertEquals(0L, BukkitScheduler.toTicks(49L));
		assertEquals(1L, BukkitScheduler.toTicks(99L));
	}

	@Test
	@DisplayName("Should never return a negative tick count")
	void toTicksClampsNegativeDelays() {
		assertEquals(0L, BukkitScheduler.toTicks(-1000L));
	}
}
