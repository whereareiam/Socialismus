package me.whereareiam.socialismus.platform.bukkit;

import me.whereareiam.attache.platform.bukkit.BukkitLibraryManager;
import me.whereareiam.attache.type.VerbosityMode;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.common.CommonInjector;
import me.whereareiam.socialismus.event.plugin.PluginBootstrappedEvent;
import me.whereareiam.socialismus.event.plugin.PluginReadyEvent;
import me.whereareiam.socialismus.event.plugin.PluginShutdownEvent;
import me.whereareiam.socialismus.platform.BukkitIntegrityChecker;
import me.whereareiam.socialismus.platform.BukkitLoggingHelper;
import me.whereareiam.socialismus.platform.BukkitModuleDependencyListener;
import me.whereareiam.socialismus.platform.bukkit.inject.BukkitInjector;
import me.whereareiam.socialismus.type.PluginType;
import me.whereareiam.socialismus.type.Version;
import me.whereareiam.socialismus.util.EventUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public class BukkitSocialismus extends JavaPlugin {
	private final Path dataPath = getDataFolder().toPath();
	private final Logger logger = getLogger();

	@Override
	public void onLoad() {
		PluginType.setPluginType(PluginType.BUKKIT);
		BukkitLoggingHelper.setLogger(logger);
		Constants.SERVER_VERSION = Version.of(Bukkit.getBukkitVersion());

		if (BukkitIntegrityChecker.checkIntegrity(logger)) {
			getPluginLoader().disablePlugin(this);
			return;
		}

		BukkitLibraryManager libraryManager = new BukkitLibraryManager(this, ".libraries");
		libraryManager.setVerbosityMode(VerbosityMode.SUMMARY);
		libraryManager.addMavenCentral();
		libraryManager.addRepository("https://registry.whereareiam.me/maven/packages");
		libraryManager.loadDescriptors();

		new BukkitInjector(this, dataPath);

		EventUtil.callEvent(new PluginBootstrappedEvent(), () -> {});
	}

	@Override
	public void onEnable() {
		// Signal that the plugin is ready for normal operation
		EventUtil.callEvent(new PluginReadyEvent(), () -> {});
		Bukkit.getPluginManager().registerEvents(
				CommonInjector.getInjector().getInstance(BukkitModuleDependencyListener.class),
				this
		);
	}

	@Override
	public void onDisable() {
		// Signal shutdown so common core can clean up
		EventUtil.callEvent(new PluginShutdownEvent(), () -> {});
	}
}
