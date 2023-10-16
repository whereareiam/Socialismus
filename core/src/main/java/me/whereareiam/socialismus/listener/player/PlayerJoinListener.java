package me.whereareiam.socialismus.listener.player;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.listener.JoinListener;
import me.whereareiam.socialismus.listener.handler.JoinHandler;
import me.whereareiam.socialismus.util.LoggerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

@Singleton
public class PlayerJoinListener implements JoinListener {
    private final LoggerUtil loggerUtil;
    private final JoinHandler joinHandler;

    @Inject
    public PlayerJoinListener(LoggerUtil loggerUtil, JoinHandler joinHandler) {
        this.loggerUtil = loggerUtil;
        this.joinHandler = joinHandler;

        loggerUtil.trace("Initializing class: " + this);
    }

    @EventHandler
    public void onEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        loggerUtil.debug("PlayerJoinEvent: " + player.getName());

        onPlayerJoinEvent(player);
    }

    @Override
    public void onPlayerJoinEvent(Player player) {
        joinHandler.handleJoinEvent(player);
    }
}