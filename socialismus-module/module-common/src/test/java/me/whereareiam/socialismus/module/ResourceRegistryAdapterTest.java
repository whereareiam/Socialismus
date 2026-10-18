package me.whereareiam.socialismus.module;

import me.whereareiam.socialismus.event.EventListener;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.event.base.Event;
import me.whereareiam.socialismus.event.base.EventOrder;
import me.whereareiam.socialismus.event.resource.ResourceRegisteredEvent;
import me.whereareiam.socialismus.event.resource.ResourceUnregisteredEvent;
import me.whereareiam.socialismus.model.resource.ResourceKey;
import me.whereareiam.socialismus.type.ResourceRegistrationOutcome;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class ResourceRegistryAdapterTest {
	private static final ResourceKey<CharSequence> TEXT = ResourceKey.create("socialismus", "text", CharSequence.class);

	private final RecordingEventManager events = new RecordingEventManager();
	private final ResourceRegistryAdapter registry = new ResourceRegistryAdapter(events);

	@Test
	void registersAResourceAndAnnouncesIt() {
		assertEquals(ResourceRegistrationOutcome.REGISTERED, registry.register(TEXT, "first"));

		assertEquals(Optional.of("first"), registry.get(TEXT));
		assertTrue(registry.has(TEXT));
		assertTrue(registry.has("socialismus:text"));
		assertEquals(1, events.called.size());
		ResourceRegisteredEvent event = assertInstanceOf(ResourceRegisteredEvent.class, events.called.getFirst());
		assertEquals(TEXT, event.getKey());
		assertEquals("first", event.getResource());
	}

	@Test
	void keepsTheFirstResourceWhenASecondOneClaimsItsKey() {
		registry.register(TEXT, "first");

		assertEquals(ResourceRegistrationOutcome.KEY_TAKEN, registry.register(TEXT, "second"));
		assertEquals(Optional.of("first"), registry.get(TEXT));
		assertEquals(1, events.called.size());
	}

	@Test
	void reportsTheKeyTakenWhenTheSameResourceRegistersAgain() {
		String resource = "first";
		registry.register(TEXT, resource);

		assertEquals(ResourceRegistrationOutcome.KEY_TAKEN, registry.register(TEXT, resource));
		assertEquals(1, events.called.size());
	}

	@Test
	void treatsKeysOfTheSameNameAsOneResource() {
		registry.register(TEXT, "first");

		ResourceKey<String> sameName = ResourceKey.create("socialismus", "text", String.class);
		assertEquals(ResourceRegistrationOutcome.KEY_TAKEN, registry.register(sameName, "second"));
		assertEquals(Optional.of("first"), registry.get(sameName));
	}

	@Test
	void refusesToHandOutAResourceUnderAKeyOfAnotherType() {
		registry.register(TEXT, "first");

		ResourceKey<Integer> otherType = ResourceKey.create("socialismus", "text", Integer.class);
		assertThrows(IllegalArgumentException.class, () -> registry.get(otherType));
	}

	@Test
	@SuppressWarnings({"unchecked", "rawtypes"})
	void refusesAResourceOfAnotherTypeThanItsKey() {
		ResourceKey raw = TEXT;

		assertThrows(IllegalArgumentException.class, () -> registry.register(raw, 42));
		assertFalse(registry.has(TEXT));
		assertTrue(events.called.isEmpty());
	}

	@Test
	void findsNothingUnderAKeyWithoutAResource() {
		assertEquals(Optional.empty(), registry.get(TEXT));
		assertFalse(registry.has(TEXT));
	}

	@Test
	void unregistersItsOwnResourceAndAnnouncesIt() {
		String resource = "first";
		registry.register(TEXT, resource);

		assertTrue(registry.unregister(TEXT, resource));

		assertFalse(registry.has(TEXT));
		ResourceUnregisteredEvent event = assertInstanceOf(ResourceUnregisteredEvent.class, events.called.getLast());
		assertEquals(Optional.of(resource), event.resource(TEXT));
	}

	@Test
	void leavesAnotherProvidersResourceRegistered() {
		registry.register(TEXT, "first");

		assertFalse(registry.unregister(TEXT, new StringBuilder("second")));
		assertEquals(Optional.of("first"), registry.get(TEXT));
		assertEquals(1, events.called.size());
	}

	@Test
	void freesTheKeyForTheNextProvider() {
		String first = "first";
		registry.register(TEXT, first);
		registry.unregister(TEXT, first);

		assertEquals(ResourceRegistrationOutcome.REGISTERED, registry.register(TEXT, "second"));
		assertEquals(Optional.of("second"), registry.get(TEXT));
	}

	@Test
	void announcesTheChangeOnceTheRegistryHoldsIt() {
		AtomicReference<ResourceRegistryAdapter> observed = new AtomicReference<>();
		List<Boolean> heldDuringEvent = new ArrayList<>();
		observed.set(new ResourceRegistryAdapter(new RecordingEventManager() {
			@Override
			public void call(Event event) {
				heldDuringEvent.add(observed.get().has(TEXT));
			}
		}));
		String resource = "first";

		observed.get().register(TEXT, resource);
		observed.get().unregister(TEXT, resource);

		assertEquals(List.of(true, false), heldDuringEvent);
	}

	private static class RecordingEventManager implements EventManager {
		private final List<Event> called = new ArrayList<>();

		@Override
		public void register(EventListener eventListener) {
		}

		@Override
		public <T extends Event> void registerListener(Class<T> event, Object listener, Method method, EventOrder order) {
		}

		@Override
		public void unregister(EventListener eventListener) {
		}

		@Override
		public void call(Event event) {
			called.add(event);
		}
	}
}
