package me.whereareiam.socialismus.platform;

import com.google.inject.AbstractModule;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.integration.bstats.Metrics;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.module.PlatformClassLoader;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public class PlatformCommonConfiguration extends AbstractModule {
	private final Plugin plugin;

	@Override
	protected void configure() {
		bind(LoggingHelper.class).to(BukkitLoggingHelper.class);
		bind(PlatformClassLoader.class).to(BukkitClassLoader.class);

		bind(org.bstats.bukkit.Metrics.class).toInstance(new org.bstats.bukkit.Metrics(plugin, Constants.BStats.BUKKIT_ID));
		bind(Metrics.class).to(BukkitMetrics.class);
	}
}
