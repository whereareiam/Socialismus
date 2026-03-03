package me.whereareiam.socialismus.platform.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import jakarta.inject.Inject;
import lombok.Getter;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.event.plugin.PluginBootstrappedEvent;
import me.whereareiam.socialismus.event.plugin.PluginReadyEvent;
import me.whereareiam.socialismus.event.plugin.PluginShutdownEvent;
import me.whereareiam.socialismus.platform.velocity.inject.VelocityInjector;
import me.whereareiam.socialismus.type.PluginType;
import me.whereareiam.socialismus.type.Version;
import me.whereareiam.socialismus.util.EventUtil;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
		id = "socialismus",
		name = Constants.NAME,
		version = Constants.VERSION,
		authors = "whereareiam",
		dependencies = {
				@Dependency(id = "packetevents", optional = true),
				@Dependency(id = "papiproxybridge", optional = true),
		}
)
public class VelocitySocialismus {
	private final ProxyServer proxyServer;
	private final PluginContainer pluginContainer;
	@Getter
	private final Logger logger;
	private final Path dataPath;

	@Inject
	public VelocitySocialismus(ProxyServer proxyServer, PluginContainer pluginContainer, Logger logger, @DataDirectory Path dataPath) {
		this.proxyServer = proxyServer;
		this.pluginContainer = pluginContainer;
		this.logger = logger;
		this.dataPath = dataPath;
	}

	@Subscribe
	public void onProxyInitializationEvent(ProxyInitializeEvent event) {
		PluginType.setPluginType(PluginType.VELOCITY);
		VelocityLoggingHelper.setLogger(logger);
		Constants.SERVER_VERSION = Version.getLatest();

		VelocityDependencyResolver dependencyResolver = new VelocityDependencyResolver(proxyServer, pluginContainer, logger, dataPath);
		dependencyResolver.loadLibraries();
		dependencyResolver.resolveDependencies();

		new VelocityInjector(
				this,
				pluginContainer,
				proxyServer,
				dependencyResolver,
				dataPath
		);

		EventUtil.callEvent(new PluginBootstrappedEvent(), () -> {});

		// Signal that the plugin is ready for normal operation
		EventUtil.callEvent(new PluginReadyEvent(), () -> {});
	}

	@Subscribe
	public void onProxyShutdownEvent(ProxyShutdownEvent event) {
		// Signal shutdown so common core can clean up
		EventUtil.callEvent(new PluginShutdownEvent(), () -> {});
	}
}
