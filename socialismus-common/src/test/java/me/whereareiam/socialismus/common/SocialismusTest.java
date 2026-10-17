package me.whereareiam.socialismus.common;

import me.whereareiam.socialismus.lifecycle.RuntimeLifecycle;
import me.whereareiam.socialismus.lifecycle.StartupPhase;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Socialismus start-up")
class SocialismusTest {
	private final List<String> journal = new ArrayList<>();

	@DisplayName("Starts every phase before the next, in binding order within a phase")
	@Test
	void startsPhaseByPhase() {
		Socialismus socialismus = socialismus(
				lifecycle("modules", StartupPhase.MODULES),
				lifecycle("configuration", StartupPhase.CONFIGURATION),
				lifecycle("chats", StartupPhase.RUNTIME),
				lifecycle("connections", StartupPhase.CONNECTIONS),
				lifecycle("logging", StartupPhase.LOGGING),
				lifecycle("commands", StartupPhase.RUNTIME),
				lifecycle("integrations", StartupPhase.INTEGRATIONS),
				lifecycle("updates", StartupPhase.CONNECTIONS)
		);

		socialismus.start();

		assertEquals(List.of(
				"start configuration",
				"start logging",
				"start integrations",
				"start chats",
				"start commands",
				"start modules",
				"start connections",
				"start updates"
		), journal);
	}

	@DisplayName("Stops the lifecycles in the reverse order of their start, once")
	@Test
	void stopsInReverse() {
		Socialismus socialismus = socialismus(
				lifecycle("configuration", StartupPhase.CONFIGURATION),
				lifecycle("chats", StartupPhase.RUNTIME),
				lifecycle("commands", StartupPhase.RUNTIME),
				lifecycle("modules", StartupPhase.MODULES)
		);
		socialismus.start();
		journal.clear();

		socialismus.stop();
		socialismus.stop();

		assertEquals(List.of(
				"stop modules",
				"stop commands",
				"stop chats",
				"stop configuration"
		), journal);
	}

	@DisplayName("A failed lifecycle stops itself and the ones started before it in reverse, and the startup fails")
	@Test
	void failureStopsTheStartedLifecyclesInReverse() {
		IllegalStateException failure = new IllegalStateException("migration failed");
		Socialismus socialismus = socialismus(
				lifecycle("configuration", StartupPhase.CONFIGURATION),
				lifecycle("logging", StartupPhase.LOGGING),
				failing("chats", StartupPhase.RUNTIME, failure),
				lifecycle("modules", StartupPhase.MODULES),
				lifecycle("connections", StartupPhase.CONNECTIONS)
		);

		assertSame(failure, assertThrows(IllegalStateException.class, socialismus::start));

		assertEquals(List.of(
				"start configuration",
				"start logging",
				"start chats",
				"stop chats",
				"stop logging",
				"stop configuration"
		), journal);
	}

	private Socialismus socialismus(RuntimeLifecycle... lifecycles) {
		return new Socialismus(new LinkedHashSet<>(List.of(lifecycles)));
	}

	private RuntimeLifecycle lifecycle(String name, StartupPhase phase) {
		return failing(name, phase, null);
	}

	private RuntimeLifecycle failing(String name, StartupPhase phase, RuntimeException failure) {
		return new RuntimeLifecycle() {
			@Override
			public @NotNull StartupPhase phase() {
				return phase;
			}

			@Override
			public void initialize() {
				journal.add("start " + name);
				if (failure != null) throw failure;
			}

			@Override
			public void shutdown() {
				journal.add("stop " + name);
			}
		};
	}
}
