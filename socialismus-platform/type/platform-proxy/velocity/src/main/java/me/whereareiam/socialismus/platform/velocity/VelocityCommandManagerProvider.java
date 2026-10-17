package me.whereareiam.socialismus.platform.velocity;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import me.whereareiam.keystone.Actor;
import me.whereareiam.socialismus.command.CommandManagerProvider;
import me.whereareiam.socialismus.model.config.Settings;
import me.whereareiam.socialismus.platform.velocity.mapper.CommandSourceMapper;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.velocity.VelocityCommandManager;

@Singleton
public class VelocityCommandManagerProvider extends CommandManagerProvider {
	private final PluginContainer plugin;
	private final ProxyServer proxyServer;

	// Mapper
	private final CommandSourceMapper commandSourceMapper;

	@Inject
	public VelocityCommandManagerProvider(
			Provider<Settings> settings,
			PluginContainer plugin,
			ProxyServer proxyServer,
			CommandSourceMapper commandSourceMapper
	) {
		super(settings);
		this.plugin = plugin;
		this.proxyServer = proxyServer;
		this.commandSourceMapper = commandSourceMapper;
	}

	@Override
	protected CommandManager<Actor> createLegacyCommandManager() {
		throw new UnsupportedOperationException("LegacyCommandManager is not supported on Velocity");
	}

	@Override
	protected CommandManager<Actor> createPaperCommandManager() {
		throw new UnsupportedOperationException("PaperCommandManager is not supported on Velocity");
	}

	@Override
	protected CommandManager<Actor> createVelocityCommandManager() {
		return new VelocityCommandManager<>(
				plugin,
				proxyServer,
				ExecutionCoordinator.asyncCoordinator(),
				commandSourceMapper
		);
	}
}
