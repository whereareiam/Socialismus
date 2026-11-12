package me.whereareiam.socialismus.platform.bukkit.common;

import com.google.inject.AbstractModule;
import lombok.RequiredArgsConstructor;
import me.whereareiam.chassis.platform.Platform;
import me.whereareiam.chassis.platform.bukkit.BukkitPlatform;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.integration.bstats.Metrics;
import me.whereareiam.socialismus.logging.LoggingHelper;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public class BukkitCommonConfiguration extends AbstractModule {
	private final Plugin plugin;

	@Override
	protected void configure() {
		bind(LoggingHelper.class).to(BukkitLoggingHelper.class);
		bind(Platform.class).toInstance(new BukkitPlatform(plugin.getServer()));

		bind(org.bstats.bukkit.Metrics.class).toInstance(new org.bstats.bukkit.Metrics(plugin, Constants.BStats.BUKKIT_ID));
		bind(Metrics.class).to(BukkitMetrics.class);
	}
}
