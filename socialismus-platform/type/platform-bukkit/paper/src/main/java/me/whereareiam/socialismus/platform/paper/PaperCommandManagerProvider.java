package me.whereareiam.socialismus.platform.paper;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.keystone.Actor;
import me.whereareiam.socialismus.command.CommandManagerProvider;
import me.whereareiam.socialismus.model.config.Settings;
import me.whereareiam.socialismus.platform.paper.mapper.CommandSenderMapper;
import me.whereareiam.socialismus.platform.paper.mapper.CommandSourceStackMapper;
import org.bukkit.plugin.Plugin;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.paper.PaperCommandManager;

@Singleton
public class PaperCommandManagerProvider extends CommandManagerProvider {
	private final Plugin plugin;
	private final CommandSenderMapper commandSenderMapper;
	private final CommandSourceStackMapper commandSourceStackMapper;

	@Inject
	public PaperCommandManagerProvider(
			Provider<Settings> settings,
			Plugin plugin,
			CommandSenderMapper commandSenderMapper,
			CommandSourceStackMapper commandSourceStackMapper
	) {
		super(settings);
		this.plugin = plugin;
		this.commandSenderMapper = commandSenderMapper;
		this.commandSourceStackMapper = commandSourceStackMapper;
	}

	@Override
	protected CommandManager<Actor> createLegacyCommandManager() {
		LegacyPaperCommandManager<Actor> commandManager = new LegacyPaperCommandManager<>(
				plugin,
				ExecutionCoordinator.asyncCoordinator(),
				commandSenderMapper
		);

		if (commandManager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER) && settings.get().getCommands().isUseBrigadier())
			commandManager.registerBrigadier();

		if (commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION) && settings.get().getCommands().isUseAsyncCompletions())
			commandManager.registerAsynchronousCompletions();

		return commandManager;
	}

	@Override
	protected CommandManager<Actor> createPaperCommandManager() {
		return PaperCommandManager
				.builder(commandSourceStackMapper)
				.executionCoordinator(ExecutionCoordinator.asyncCoordinator())
				.buildOnEnable(plugin);
	}

	@Override
	protected CommandManager<Actor> createVelocityCommandManager() {
		throw new UnsupportedOperationException("VelocityCommandManager is not supported on Paper");
	}
}
