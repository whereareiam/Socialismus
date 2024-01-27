package me.whereareiam.socialismus.core;

import com.google.inject.Injector;
import me.whereareiam.socialismus.api.Socialismus;
import me.whereareiam.socialismus.core.command.management.CommandManager;
import me.whereareiam.socialismus.core.command.management.CommandRegistrar;
import me.whereareiam.socialismus.core.config.ConfigManager;
import me.whereareiam.socialismus.core.module.ModuleLoader;
import me.whereareiam.socialismus.core.module.chat.ChatModule;
import me.whereareiam.socialismus.core.util.InfoPrinterUtil;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class AbstractSocialismus extends JavaPlugin implements Socialismus {
	public static String version;
	protected Injector injector;

	@Override
	public void onEnable() {
		version = getDescription().getVersion();

		ConfigManager configManager = injector.getInstance(ConfigManager.class);
		configManager.reloadConfigs();

		injector.getInstance(CommandRegistrar.class).registerCommands();
		injector.getInstance(CommandManager.class).setCommands();
		injector.getInstance(ModuleLoader.class).loadModules();

		injector.getInstance(InfoPrinterUtil.class).printStartMessage();
	}

	@Override
	public void onDisable() {
		/*injector.getInstance(DatabaseManager.class).shutdown();*/
		injector.getInstance(Scheduler.class).shutdown();
	}

	@Override
	public void reload() {
		injector.getInstance(ConfigManager.class).reloadConfigs();
		injector.getInstance(ModuleLoader.class).reloadModules();
	}

	@Override
	public ChatModule getChatModule() {
		return injector.getInstance(ChatModule.class);
	}
}