package me.whereareiam.socialismus;

import me.whereareiam.socialismus.listener.SpigotListenerRegistrar;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;

public final class SocialismusSpigot extends SocialismusBase {
    private BukkitAudiences bukkitAudiences;

    @Override
    public void onEnable() {
        super.onEnable();

        bukkitAudiences = BukkitAudiences.create(this);
        injector.getInstance(SpigotListenerRegistrar.class).registerListeners();
    }

    @Override
    public void onDisable() {
        bukkitAudiences.close();
    }
}