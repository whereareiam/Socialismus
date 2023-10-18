package me.whereareiam.socialismus.feature.swapper;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.config.swapper.SwapperConfig;
import me.whereareiam.socialismus.feature.Feature;
import me.whereareiam.socialismus.feature.swapper.model.Swapper;
import me.whereareiam.socialismus.integration.protocollib.PacketSender;
import me.whereareiam.socialismus.integration.protocollib.entity.PacketPlayer;
import me.whereareiam.socialismus.util.LoggerUtil;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class SwapperManager implements Feature {
    private final Injector injector;
    private final LoggerUtil loggerUtil;
    private final PacketSender packetSender;
    private final PacketPlayer packetPlayer;

    private final Path swapperDir;
    private final List<Swapper> swappers = new ArrayList<>();

    @Inject
    public SwapperManager(Injector injector, LoggerUtil loggerUtil, PacketSender packetSender, PacketPlayer packetPlayer, Plugin plugin) {
        this.injector = injector;
        this.loggerUtil = loggerUtil;
        this.packetSender = packetSender;
        this.packetPlayer = packetPlayer;
        this.swapperDir = plugin.getDataFolder().toPath().resolve("swapper");

        loggerUtil.trace("Initializing class: " + this);

        File dir = swapperDir.toFile();
        if (!dir.exists()) {
            boolean isCreated = dir.mkdir();
            loggerUtil.debug("Creating swapper dir");
            if (!isCreated) {
                loggerUtil.severe("Failed to create directory: " + swapperDir);
            }
        }
    }

    public void registerSwapper(Swapper swapper) {
        loggerUtil.debug("Adding new swapper: " + swapper);
        swappers.add(swapper);
    }

    public void registerSwappers() {
        loggerUtil.debug("Registering swappers");
        File[] files = swapperDir.toFile().listFiles();
        if (files == null || files.length == 0) {
            loggerUtil.debug("Creating an example swapper, because dir is empty");
            SwapperConfig swapperConfig = createExampleSwapperConfig();
            swapperConfig.reload(swapperDir.resolve("example.yml"));
            swappers.addAll(swapperConfig.swappers);
        } else {
            for (File file : files) {
                loggerUtil.trace("Trying to register swappers in config: " + file.getName());
                SwapperConfig swapperConfig = injector.getInstance(SwapperConfig.class);
                swapperConfig.reload(file.toPath());
                List<Swapper> enabledSwappers = swapperConfig.swappers.stream()
                        .filter(swapper -> swapper.enabled)
                        .toList();
                if (!enabledSwappers.isEmpty()) {
                    loggerUtil.trace("Adding new swappers (" + enabledSwappers.size() + ")");
                    swappers.addAll(enabledSwappers);
                }
            }
        }
    }

    public void cleanSwappers() {
        loggerUtil.debug("Cleaning swappers");
        swappers.clear();
    }

    public void suggestSwappers(Player player) {
        loggerUtil.debug("Sending swappers to " + player.getName());
        for (Swapper swapper : swappers) {
            if (player.hasPermission(swapper.settings.permission)) {
                loggerUtil.trace("Player " + player.getName() + " will receive these swappers: " + swapper.placeholders);
                for (String placeholder : swapper.placeholders) {
                    packetSender.sendPacket(
                            player,
                            packetPlayer.createPacketPlayerInfo(placeholder)
                    );
                }
            }
        }
    }

    public List<Swapper> getSwappers() {
        return swappers;
    }

    private SwapperConfig createExampleSwapperConfig() {
        SwapperConfig swapperConfig = injector.getInstance(SwapperConfig.class);
        Swapper exampleSwapper = injector.getInstance(Swapper.class);
        exampleSwapper.placeholders.add("{example}");
        exampleSwapper.contents.add("example");
        exampleSwapper.settings.directSearch = true;
        exampleSwapper.settings.permission = "";
        exampleSwapper.settings.randomContent = false;

        swapperConfig.swappers.add(exampleSwapper);
        return swapperConfig;
    }

    @Override
    public boolean requiresPlayerAsyncChatListener() {
        return true;
    }
}
