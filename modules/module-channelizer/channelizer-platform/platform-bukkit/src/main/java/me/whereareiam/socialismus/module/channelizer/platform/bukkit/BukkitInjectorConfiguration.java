package me.whereareiam.socialismus.module.channelizer.platform.bukkit;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;
import me.whereareiam.socialismus.module.channelizer.api.PlatformMessageBus;

@RequiredArgsConstructor
public class BukkitInjectorConfiguration extends AbstractModule {
	private final Injector parentInjector;

	@Override
	protected void configure() {
		bind(Plugin.class).toInstance(parentInjector.getInstance(Plugin.class));
		bind(PlatformMessageBus.class).to(BukkitMessageBus.class);
	}
}
