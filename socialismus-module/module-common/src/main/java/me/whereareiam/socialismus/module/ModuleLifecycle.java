package me.whereareiam.socialismus.module;

import com.google.inject.Inject;
import me.whereareiam.socialismus.lifecycle.RuntimeLifecycle;
import me.whereareiam.socialismus.lifecycle.StartupPhase;
import org.jetbrains.annotations.NotNull;

/**
 * Loads and enables the modules once the core runs, so they can register commands and workers, and disables and
 * unloads them when Socialismus stops, before the core does.
 */
public final class ModuleLifecycle implements RuntimeLifecycle {
	private final ModuleService moduleService;

	@Inject
	public ModuleLifecycle(@NotNull ModuleService moduleService) {
		this.moduleService = moduleService;
	}

	@Override
	public @NotNull StartupPhase phase() {
		return StartupPhase.MODULES;
	}

	@Override
	public void initialize() {
		moduleService.loadModules();
	}

	@Override
	public void shutdown() {
		moduleService.unloadModules();
	}
}
