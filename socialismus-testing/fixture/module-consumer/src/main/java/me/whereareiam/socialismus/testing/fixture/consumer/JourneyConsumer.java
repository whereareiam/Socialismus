package me.whereareiam.socialismus.testing.fixture.consumer;

import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.module.ModuleContext;
import me.whereareiam.socialismus.module.SocialisticModule;
import me.whereareiam.socialismus.testing.fixture.library.api.Greeter;
import org.jetbrains.annotations.NotNull;

/**
 * The module that reports, in the server's log, what it reaches of JourneyLibrary and of the PacketEvents plugin,
 * and whether the library still serves it while it stops.
 */
public final class JourneyConsumer implements SocialisticModule {
	private static final String SECRET = "me.whereareiam.socialismus.testing.fixture.library.internal.Secret";
	private static final String PACKET_EVENTS = "com.github.retrooper.packetevents.PacketEvents";

	@Override
	public void enable(@NotNull ModuleContext context) {
		Logger.info("JourneyConsumer greets: " + Greeter.greet());
		Logger.info("JourneyConsumer reaches the library's internals: " + reaches(SECRET));
		Logger.info("JourneyConsumer reaches PacketEvents: " + reaches(PACKET_EVENTS));
	}

	@Override
	public void disable() {
		Logger.info("JourneyConsumer stops while the library serves: " + Greeter.isServing());
	}

	private boolean reaches(String className) {
		try {
			Class.forName(className, false, getClass().getClassLoader());
			return true;
		} catch (ClassNotFoundException notReached) {
			return false;
		}
	}
}
