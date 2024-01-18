package me.whereareiam.socialismus.core.integration.protocollib;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.core.integration.Integration;
import me.whereareiam.socialismus.core.integration.IntegrationType;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

@Singleton
public class ProtocolLib implements Integration {
	private final Plugin plugin = Bukkit.getPluginManager().getPlugin("ProtocolLib");
	private boolean isEnabled;

	@Override
	public void initialize() {
		if (plugin == null)
			return;

		isEnabled = plugin.isEnabled();
	}

	@Override
	public String getName() {
		return plugin.getName();
	}

	@Override
	public boolean isEnabled() {
		return this.isEnabled;
	}

	@Override
	public IntegrationType getType() {
		return IntegrationType.INTERNAL;
	}
}
