package me.whereareiam.socialismus.platform.paper;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.config.Settings;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.output.PlatformInteractor;
import me.whereareiam.socialismus.command.management.CommandExceptionHandler;
import me.whereareiam.socialismus.command.provider.CommandManagerProvider;
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

    // Mapper
    private final CommandSenderMapper commandSenderMapper;
    private final CommandSourceStackMapper commandSourceStackMapper;

    @Inject
    public PaperCommandManagerProvider(CommandExceptionHandler exceptionHandler, Provider<Settings> settings, Plugin plugin, PlatformInteractor interactor, CommandSenderMapper commandSenderMapper,
                                       CommandSourceStackMapper commandSourceStackMapper) {
        super(exceptionHandler, settings, interactor);
        this.plugin = plugin;
        this.commandSenderMapper = commandSenderMapper;
        this.commandSourceStackMapper = commandSourceStackMapper;
    }

    @Override
    protected CommandManager<DummyPlayer> createLegacyPaperCommandManager() {
        LegacyPaperCommandManager<DummyPlayer> commandManager = new LegacyPaperCommandManager<>(
                plugin,
                ExecutionCoordinator.asyncCoordinator(),
                commandSenderMapper
        );

        if (commandManager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER) && settings.get().getMisc().isAllowBrigadierCommands())
            commandManager.registerBrigadier();
        if (commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION))
            commandManager.registerAsynchronousCompletions();

        return commandManager;
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    protected CommandManager<DummyPlayer> createPaperCommandManager() {
        return PaperCommandManager
                .builder(commandSourceStackMapper)
                .executionCoordinator(ExecutionCoordinator.asyncCoordinator())
                .buildOnEnable(plugin);
    }

    @Override
    protected CommandManager<DummyPlayer> createVelocityCommandManager() {
        throw new UnsupportedOperationException("VelocityCommandManager is not supported on Paper");
    }
}
