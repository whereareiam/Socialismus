package me.whereareiam.socialismus.event.resource;

import me.whereareiam.socialismus.model.resource.ResourceKey;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ResourceEventTest {
	private static final ResourceKey<String> TEXT = ResourceKey.create("socialismus", "text", String.class);

	@Test
	void givesTheResourceToTheKeyItIsAbout() {
		ResourceEvent event = new ResourceRegisteredEvent(TEXT, "hello");

		assertEquals(Optional.of("hello"), event.resource(TEXT));
	}

	@Test
	void givesTheResourceToAnotherKeyOfTheSameName() {
		ResourceEvent event = new ResourceUnregisteredEvent(TEXT, "hello");

		assertEquals(Optional.of("hello"), event.resource(ResourceKey.create("socialismus", "text", CharSequence.class)));
	}

	@Test
	void givesNothingToAnotherKey() {
		ResourceEvent event = new ResourceRegisteredEvent(TEXT, "hello");

		assertEquals(Optional.empty(), event.resource(ResourceKey.create("socialismus", "other", String.class)));
	}

	@Test
	void refusesAKeyOfTheSameNameAndAnotherType() {
		ResourceEvent event = new ResourceRegisteredEvent(TEXT, "hello");

		assertThrows(IllegalArgumentException.class, () -> event.resource(ResourceKey.create("socialismus", "text", Integer.class)));
	}
}
