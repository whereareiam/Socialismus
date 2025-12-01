package me.whereareiam.socialismus.platform.bukkit;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.keystone.Actor;
import me.whereareiam.socialismus.command.CommandManagerProvider;
import me.whereareiam.socialismus.model.config.Settings;
import me.whereareiam.socialismus.platform.bukkit.mapper.CommandSenderMapper;
import org.bukkit.plugin.Plugin;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

@Singleton
public class BukkitCommandManagerProvider extends CommandManagerProvider {
	private final Plugin plugin;

	// Mapper
	private final CommandSenderMapper commandSenderMapper;

	@Inject
	public BukkitCommandManagerProvider(
			Provider<Settings> settings,
			Plugin plugin,
			CommandSenderMapper commandSenderMapper
	) {
		super(settings);
		this.plugin = plugin;
		this.commandSenderMapper = commandSenderMapper;
	}

	@Override
	protected CommandManager<Actor> createLegacyCommandManager() {
		LegacyPaperCommandManager<Actor> commandManager = new LegacyPaperCommandManager<>(
				plugin,
				ExecutionCoordinator.asyncCoordinator(),
				commandSenderMapper
		);

		if (commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION))
			commandManager.registerAsynchronousCompletions();

		return commandManager;
	}

	@Override
	protected CommandManager<Actor> createPaperCommandManager() {
		return createLegacyCommandManager();
	}

	@Override
	protected CommandManager<Actor> createVelocityCommandManager() {
		throw new UnsupportedOperationException("VelocityCommandManager is not supported on Bukkit");
	}
}
