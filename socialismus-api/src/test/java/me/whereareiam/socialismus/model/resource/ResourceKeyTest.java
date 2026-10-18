package me.whereareiam.socialismus.model.resource;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceKeyTest {
	@Test
	void namesTheResourceByNamespaceAndKey() {
		ResourceKey<String> key = ResourceKey.create("translator", "translation", String.class);

		assertEquals("translator:translation", key.getFullKey());
		assertEquals("translator:translation", key.toString());
	}

	@Test
	void rejectsPartsThatWouldMakeTheFullKeyAmbiguous() {
		assertThrows(IllegalArgumentException.class, () -> ResourceKey.create("socialismus:core", "sync", String.class));
		assertThrows(IllegalArgumentException.class, () -> ResourceKey.create("socialismus", "Sync", String.class));
		assertThrows(IllegalArgumentException.class, () -> ResourceKey.create("", "sync", String.class));
	}

	@Test
	void castsAResourceOfItsType() {
		ResourceKey<CharSequence> key = ResourceKey.create("socialismus", "text", CharSequence.class);

		assertEquals("hello", key.cast("hello"));
	}

	@Test
	void refusesAResourceOfAnotherType() {
		ResourceKey<String> key = ResourceKey.create("socialismus", "text", String.class);

		IllegalArgumentException failure = assertThrows(IllegalArgumentException.class, () -> key.cast(42));
		assertTrue(failure.getMessage().contains("socialismus:text"));
	}
}
