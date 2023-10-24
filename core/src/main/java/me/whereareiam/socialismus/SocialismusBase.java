package me.whereareiam.socialismus;

import com.google.inject.Injector;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class SocialismusBase extends JavaPlugin {
    public static String version;
    protected Injector injector;

    @Override
    public void onEnable() {
        version = getDescription().getVersion();
    }

    @Override
    public void onDisable() {
        injector.getInstance(Scheduler.class).shutdown();
    }
}