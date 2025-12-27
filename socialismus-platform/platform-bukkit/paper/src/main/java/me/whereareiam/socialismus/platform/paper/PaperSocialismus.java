package me.whereareiam.socialismus.platform.paper;

import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.common.CommonInjector;
import me.whereareiam.socialismus.event.plugin.PluginBootstrappedEvent;
import me.whereareiam.socialismus.event.plugin.PluginReadyEvent;
import me.whereareiam.socialismus.event.plugin.PluginShutdownEvent;
import me.whereareiam.socialismus.integration.bstats.bStatsIntegration;
import me.whereareiam.socialismus.integration.packetevents.PacketEventsIntegration;
import me.whereareiam.socialismus.integration.placeholderapi.PlaceholderAPIIntegration;
import me.whereareiam.socialismus.platform.BukkitIntegrityChecker;
import me.whereareiam.socialismus.platform.BukkitLoggingHelper;
import me.whereareiam.socialismus.platform.paper.inject.PaperInjector;
import me.whereareiam.socialismus.type.PluginType;
import me.whereareiam.socialismus.type.Version;
import me.whereareiam.socialismus.util.EventUtil;
import org.bukkit.Bukkit;
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
		Constants.SERVER_VERSION = Version.of(Bukkit.getBukkitVersion());

		if (BukkitIntegrityChecker.checkIntegrity(logger)) {
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		PaperDependencyResolver dependencyResolver = new PaperDependencyResolver(this);
		dependencyResolver.loadLibraries();
		logger.info("Loading runtime libraries...");
		dependencyResolver.resolveDependencies();
		logger.info("Runtime libraries loaded, creating injector...");

		new PaperInjector(this, dependencyResolver, dataPath);
		logger.info("Injector created successfully");

		EventUtil.callEvent(new PluginBootstrappedEvent(), () -> {
		});
	}

	@Override
	public void onEnable() {
		CommonInjector.getInjector().getInstance(PlaceholderAPIIntegration.class);
		CommonInjector.getInjector().getInstance(PacketEventsIntegration.class);
		CommonInjector.getInjector().getInstance(bStatsIntegration.class);

		// Signal that the plugin is ready for normal operation
		EventUtil.callEvent(new PluginReadyEvent(), () -> {
		});
	}

	@Override
	public void onDisable() {
		// Signal shutdown so common core can clean up
		EventUtil.callEvent(new PluginShutdownEvent(), () -> {
		});
	}
}
