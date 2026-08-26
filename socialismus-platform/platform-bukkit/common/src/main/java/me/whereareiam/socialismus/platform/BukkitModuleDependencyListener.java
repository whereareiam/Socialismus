package me.whereareiam.socialismus.platform;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.module.ModuleService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;

/**
 * Retries Socialismus modules when a Bukkit plugin becomes available after
 * Socialismus has started.
 */
@RequiredArgsConstructor(onConstructor_ = @Inject)
public final class BukkitModuleDependencyListener implements Listener {
	private final ModuleService moduleService;

	@EventHandler
	public void onPluginEnable(PluginEnableEvent event) {
		moduleService.loadPendingModules();
	}
}
