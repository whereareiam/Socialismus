package me.whereareiam.socialismus.testing.fixture.library;

import me.whereareiam.socialismus.module.SocialisticModule;
import me.whereareiam.socialismus.testing.fixture.library.api.Greeter;

/**
 * The module that exports a package to JourneyConsumer.
 */
public final class JourneyLibrary extends SocialisticModule {
	@Override
	public void onLoad() {
	}

	@Override
	public void onEnable() {
		Greeter.serve(true);
	}

	@Override
	public void onDisable() {
		Greeter.serve(false);
	}

	@Override
	public void onUnload() {
	}
}
