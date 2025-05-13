package me.whereareiam.socialismus.platform.bukkit;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.output.PlatformInteractor;
import me.whereareiam.socialismus.api.output.integration.Integration;
import me.whereareiam.socialismus.api.output.integration.SynchronizationIntegration;
import me.whereareiam.socialismus.api.type.Version;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Singleton
public class BukkitPlatformInteractor implements PlatformInteractor {
    private final Provider<BukkitAudiences> audiences;
    private final Provider<Set<Integration>> integrations;

    @Inject
    public BukkitPlatformInteractor(Provider<BukkitAudiences> audiences, Provider<Set<Integration>> integrations) {
        this.audiences = audiences;
        this.integrations = integrations;
    }

    @Override
    public void broadcast(Component component) {
        audiences.get().all().sendMessage(component);
    }

    @Override
    public boolean areWithinRange(UUID player1, UUID player2, double range) {
        if (integrations.get().stream().anyMatch(i -> i instanceof SynchronizationIntegration)) {
            Optional<SynchronizationIntegration> syncIntegration = integrations.get().stream()
                    .filter(i -> i instanceof SynchronizationIntegration)
                    .map(i -> (SynchronizationIntegration) i)
                    .findFirst();

            if (syncIntegration.isPresent()) {
                Optional<String> location1 = syncIntegration.get().getLocation(player1);
                Optional<String> location2 = syncIntegration.get().getLocation(player2);

                if (location1.isPresent() && location2.isPresent())
                    return location1.get().equals(location2.get());
            }
        }

        Player p1 = Bukkit.getPlayer(player1);
        Player p2 = Bukkit.getPlayer(player2);
        if (p1 == null || p2 == null) return false;

        return p1.getLocation().distanceSquared(p2.getLocation()) <= range * range;
    }

    @Override
    public boolean hasPermission(String username, String permission) {
        Player player = Bukkit.getPlayer(username);
        if (player == null) return false;

        return player.hasPermission(permission);
    }

    @Override
    public boolean hasPermission(DummyPlayer dummyPlayer, String permission) {
        Player player = Bukkit.getPlayer(dummyPlayer.getUniqueId());
        if (player == null) return false;

        return player.hasPermission(permission);
    }

    @Override
    public int getOnlinePlayersCount() {
        return Bukkit.getOnlinePlayers().size();
    }

    @Override
    public Version getServerVersion() {
        return Version.of(Bukkit.getVersion());
    }
}