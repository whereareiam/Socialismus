package me.whereareiam.socialismus.core;

import co.aikar.commands.BukkitCommandManager;
import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import me.whereareiam.socialismus.api.Socialismus;
import me.whereareiam.socialismus.api.SocialismusAPI;
import me.whereareiam.socialismus.core.cache.CacheInterceptor;
import me.whereareiam.socialismus.core.cache.Cacheable;
import me.whereareiam.socialismus.core.config.setting.SettingsConfig;
import me.whereareiam.socialismus.core.util.LoggerUtil;
import org.bukkit.plugin.Plugin;

import java.nio.file.Path;

public abstract class SocialismusConfig extends AbstractModule {
	protected final AbstractSocialismus instance;
	protected final Plugin plugin;

	public SocialismusConfig(AbstractSocialismus instance, Plugin plugin) {
		this.instance = instance;
		this.plugin = plugin;
	}

	@Override
	protected void configure() {
		bind(Socialismus.class).toInstance(instance);
		bind(SocialismusAPI.class).asEagerSingleton();
		bind(Plugin.class).toInstance(plugin);

		bind(Path.class).annotatedWith(Names.named("pluginPath")).toInstance(plugin.getDataFolder().toPath());
		bind(Path.class).annotatedWith(Names.named("modulePath")).toInstance(plugin.getDataFolder().toPath().resolve("modules"));

		bind(BukkitCommandManager.class).toInstance(new BukkitCommandManager(plugin));

		SettingsConfig settingsConfig = new SettingsConfig();
		bind(SettingsConfig.class).toInstance(settingsConfig);

		LoggerUtil loggerUtil = new LoggerUtil(settingsConfig);
		loggerUtil.setBukkitLogger(plugin.getLogger());
		bind(LoggerUtil.class).toInstance(loggerUtil);

		bindInterceptor(Matchers.any(),
				Matchers.annotatedWith(Cacheable.class),
				new CacheInterceptor(settingsConfig, loggerUtil)
		);

		bind(Updater.class).asEagerSingleton();

		configurePlatformSpecifics();
	}

	protected abstract void configurePlatformSpecifics();
}
