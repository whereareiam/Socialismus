package me.whereareiam.socialismus.platform.bukkit.common;

import com.google.inject.Inject;
import com.google.inject.Provider;
import lombok.RequiredArgsConstructor;
import me.whereareiam.chassis.Extensions;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;

/**
 * Retries Socialismus modules when a Bukkit plugin becomes available after
 * Socialismus has started.
 */
@RequiredArgsConstructor(onConstructor_ = @Inject)
public final class BukkitModuleDependencyListener implements Listener {
	private final Provider<Extensions> modules;

	@EventHandler
	public void onPluginEnable(PluginEnableEvent event) {
		modules.get().rescan();
	}
}
