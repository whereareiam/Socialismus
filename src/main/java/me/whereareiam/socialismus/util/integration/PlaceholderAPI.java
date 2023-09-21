package me.whereareiam.socialismus.util.integration;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class PlaceholderAPI implements Integration {
    private final Plugin plugin = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
    private boolean isEnabled;

    public PlaceholderAPI() {
        try {
            isEnabled = plugin.isEnabled();
        } catch (NullPointerException e) {
            isEnabled = false;
        }
    }

    public String getName() {
        return plugin.getName();
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    //TODO
}

