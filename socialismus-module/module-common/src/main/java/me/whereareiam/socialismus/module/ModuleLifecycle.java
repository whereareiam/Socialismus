package me.whereareiam.socialismus.module;

import com.google.inject.Inject;
import com.google.inject.Provider;
import lombok.RequiredArgsConstructor;
import me.whereareiam.chassis.common.ChassisHost;
import me.whereareiam.chassis.model.ExtensionDescriptor;
import me.whereareiam.chassis.model.InstalledExtension;
import me.whereareiam.chassis.type.ExtensionState;
import me.whereareiam.socialismus.lifecycle.RuntimeLifecycle;
import me.whereareiam.socialismus.lifecycle.StartupPhase;
import me.whereareiam.socialismus.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * Starts the modules in their phase of Socialismus's start, and stops them, dependents first, when it stops.
 */
@RequiredArgsConstructor(onConstructor_ = @Inject)
public final class ModuleLifecycle implements RuntimeLifecycle {
	// Made in the modules' phase, once the integrations it offers to modules are known.
	private final Provider<ChassisHost<ModuleContext, ModuleFields>> modules;
	private final ModuleResources resources;
	private @Nullable AutoCloseable following;

	@Override
	public @NotNull StartupPhase phase() {
		return StartupPhase.MODULES;
	}

	@Override
	public void initialize() {
		ChassisHost<ModuleContext, ModuleFields> modules = this.modules.get();
		following = resources.follow(modules);
		try {
			modules.start();
		} catch (IOException e) {
			Logger.severe("Could not read the modules: %s", e.getMessage());
			return;
		}

		for (InstalledExtension module : modules.list()) {
			if (module.getState() != ExtensionState.ENABLED) continue;

			ExtensionDescriptor descriptor = module.getDescriptor();
			Logger.info("Enabled module %s %s", descriptor.getName(), descriptor.getVersion());
		}
	}

	@Override
	public void shutdown() {
		modules.get().stop();
		if (following == null) return;

		try {
			following.close();
		} catch (Exception e) {
			Logger.warn("Could not stop following the modules: %s", e.getMessage());
		}
		following = null;
	}
}
