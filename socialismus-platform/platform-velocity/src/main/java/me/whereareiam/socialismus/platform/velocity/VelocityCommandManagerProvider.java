package me.whereareiam.socialismus.platform.velocity;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import me.whereareiam.socialismus.api.model.config.Settings;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.command.management.CommandExceptionHandler;
import me.whereareiam.socialismus.command.provider.CommandManagerProvider;
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
			CommandExceptionHandler exceptionHandler,
			Provider<Settings> settings,
			PluginContainer plugin,
			ProxyServer proxyServer,
			CommandSourceMapper commandSourceMapper
	) {
		super(settings, exceptionHandler);
		this.plugin = plugin;
		this.proxyServer = proxyServer;
		this.commandSourceMapper = commandSourceMapper;
	}

	@Override
	protected CommandManager<DummyPlayer> createLegacyPaperCommandManager() {
		throw new UnsupportedOperationException("LegacyPaperCommandManager is not supported on Velocity");
	}

	@Override
	protected CommandManager<DummyPlayer> createPaperCommandManager() {
		throw new UnsupportedOperationException("PaperCommandManager is not supported on Velocity");
	}

	@Override
	protected CommandManager<DummyPlayer> createVelocityCommandManager() {
		return new VelocityCommandManager<>(
				plugin,
				proxyServer,
				ExecutionCoordinator.asyncCoordinator(),
				commandSourceMapper
		);
	}
}
