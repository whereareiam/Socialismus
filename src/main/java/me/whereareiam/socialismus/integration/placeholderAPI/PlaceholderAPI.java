package me.whereareiam.socialismus.integration.placeholderAPI;

import me.whereareiam.socialismus.integration.Integration;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class PlaceholderAPI implements Integration {
    private final Plugin plugin = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
    private boolean isEnabled;

    @Override
    public void initialize() {
        try {
            isEnabled = plugin.isEnabled();
        } catch (NullPointerException e) {
            isEnabled = false;
        }
    }

    @Override
    public String getName() {
        return plugin.getName();
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    //TODO
}

