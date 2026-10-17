package me.whereareiam.socialismus.platform.velocity.inject;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import me.whereareiam.socialismus.command.CommandConfiguration;
import me.whereareiam.socialismus.common.CommonConfiguration;
import me.whereareiam.socialismus.common.CommonInjector;
import me.whereareiam.socialismus.module.ModuleConfiguration;
import me.whereareiam.socialismus.platform.velocity.VelocitySocialismus;

import java.nio.file.Path;

@Getter
public class VelocityInjector {
	public VelocityInjector(VelocitySocialismus socialismus, PluginContainer plugin, ProxyServer proxyServer, Path dataPath) {
		Injector injector = Guice.createInjector(
				new VelocityInjectorConfiguration(socialismus, plugin, proxyServer),
				new CommonConfiguration(dataPath),
				new CommandConfiguration(),
				new ModuleConfiguration()
		);

		CommonInjector.setInjector(injector);
	}
}
