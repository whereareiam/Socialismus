package me.whereareiam.socialismus.core.integration.discordsrv;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.core.integration.Integration;
import me.whereareiam.socialismus.core.integration.IntegrationType;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

@Singleton
public class DiscordSRV implements Integration {
	private final Injector injector;
	private final Plugin plugin = Bukkit.getPluginManager().getPlugin("DiscordSRV");
	private boolean isEnabled;

	@Inject
	public DiscordSRV(Injector injector) {
		this.injector = injector;
	}

	@Override
	public void initialize() {
		if (plugin == null)
			return;

		isEnabled = plugin.isEnabled();

		DiscordChatHook chatHook = injector.getInstance(DiscordChatHook.class);
		plugin.getServer().getPluginManager().registerEvents(chatHook, injector.getInstance(Plugin.class));
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
