package me.whereareiam.socialismus.platform.paper;

import com.google.inject.Injector;
import me.whereareiam.attache.platform.paper.PaperLibraryManager;
import me.whereareiam.attache.type.VerbosityMode;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.common.Socialismus;
import me.whereareiam.socialismus.platform.bukkit.common.BukkitCommonConfiguration;
import me.whereareiam.socialismus.platform.bukkit.common.BukkitIntegrityChecker;
import me.whereareiam.socialismus.platform.bukkit.common.BukkitLoggingHelper;
import me.whereareiam.socialismus.platform.bukkit.common.BukkitModuleDependencyListener;
import me.whereareiam.socialismus.platform.common.PlatformInjector;
import me.whereareiam.socialismus.type.PluginType;
import me.whereareiam.socialismus.type.Version;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public class PaperSocialismus extends JavaPlugin {
	private final Path dataPath = getDataFolder().toPath();
	private final Logger logger = getLogger();
	private Injector injector;

	@Override
	public void onLoad() {
		PluginType.setPluginType(PluginType.PAPER);
		BukkitLoggingHelper.setLogger(logger);
		Constants.SERVER_VERSION = Version.of(Bukkit.getBukkitVersion());
		// The game version leads Bukkit's version, such as 1.21.4-R0.1-SNAPSHOT or 26.1.2.build.74.
		Constants.PLATFORM_VERSION = Bukkit.getBukkitVersion().replaceFirst("^([0-9]+(?:\\.[0-9]+)*).*$", "$1");

		if (BukkitIntegrityChecker.checkIntegrity(logger)) {
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		PaperLibraryManager libraryManager = new PaperLibraryManager(this, ".libraries");
		libraryManager.setVerbosityMode(VerbosityMode.SUMMARY);
		libraryManager.addMavenCentral();
		libraryManager.addRepository("https://registry.whereareiam.me/maven/packages");
		libraryManager.loadDescriptors();

		injector = PlatformInjector.create(dataPath, libraryManager, new PaperInjectorConfiguration(this), new BukkitCommonConfiguration(this));
	}

	@Override
	public void onEnable() {
		if (injector == null) return;

		injector.getInstance(Socialismus.class).start();
		Bukkit.getPluginManager().registerEvents(injector.getInstance(BukkitModuleDependencyListener.class), this);
	}

	@Override
	public void onDisable() {
		if (injector == null) return;

		injector.getInstance(Socialismus.class).stop();
	}
}
