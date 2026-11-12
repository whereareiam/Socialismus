package me.whereareiam.socialismus.testing.fixture.library;

import me.whereareiam.socialismus.module.ModuleContext;
import me.whereareiam.socialismus.module.SocialisticModule;
import me.whereareiam.socialismus.testing.fixture.library.api.Greeter;
import org.jetbrains.annotations.NotNull;

/**
 * The module that exports a package to JourneyConsumer.
 */
public final class JourneyLibrary implements SocialisticModule {
	@Override
	public void enable(@NotNull ModuleContext context) {
		Greeter.serve(true);
	}

	@Override
	public void disable() {
		Greeter.serve(false);
	}
}
