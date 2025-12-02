package me.whereareiam.socialismus.platform.paper;

import me.whereareiam.socialismus.common.CommonInjector;
import me.whereareiam.socialismus.common.IntegrityChecker;
import me.whereareiam.socialismus.event.plugin.PluginBootstrappedEvent;
import me.whereareiam.socialismus.event.plugin.PluginReadyEvent;
import me.whereareiam.socialismus.event.plugin.PluginShutdownEvent;
import me.whereareiam.socialismus.integration.bstats.bStatsIntegration;
import me.whereareiam.socialismus.integration.packetevents.PacketEventsIntegration;
import me.whereareiam.socialismus.integration.placeholderapi.PlaceholderAPIIntegration;
import me.whereareiam.socialismus.platform.BukkitLoggingHelper;
import me.whereareiam.socialismus.platform.paper.inject.PaperInjector;
import me.whereareiam.socialismus.type.PluginType;
import me.whereareiam.socialismus.util.EventUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.util.logging.Logger;

@SuppressWarnings("unused")
	public class PaperSocialismus extends JavaPlugin {
	private final Path dataPath = getDataFolder().toPath();
	private final Logger logger = getLogger();

	@Override
	public void onLoad() {
		PluginType.setPluginType(PluginType.PAPER);
		BukkitLoggingHelper.setLogger(logger);

		PaperDependencyResolver dependencyResolver = new PaperDependencyResolver(this);
		dependencyResolver.loadLibraries();
		dependencyResolver.resolveDependencies();

		new PaperInjector(this, dependencyResolver, dataPath);

		EventUtil.callEvent(new PluginBootstrappedEvent(), () -> {});

		if (CommonInjector.getInjector().getInstance(IntegrityChecker.class).checkIntegrity())
			getServer().getPluginManager().disablePlugin(this);
	}

	@Override
	public void onEnable() {
		CommonInjector.getInjector().getInstance(PlaceholderAPIIntegration.class);
		CommonInjector.getInjector().getInstance(PacketEventsIntegration.class);
		CommonInjector.getInjector().getInstance(bStatsIntegration.class);

		// Signal that the plugin is ready for normal operation
		EventUtil.callEvent(new PluginReadyEvent(), () -> {});
	}

	@Override
	public void onDisable() {
		// Signal shutdown so common core can clean up
		EventUtil.callEvent(new PluginShutdownEvent(), () -> {});
	}
}
