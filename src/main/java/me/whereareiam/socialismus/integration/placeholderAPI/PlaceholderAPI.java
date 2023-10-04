package me.whereareiam.socialismus.integration.placeholderAPI;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.integration.IntegrationType;
import me.whereareiam.socialismus.integration.MessagingIntegration;
import me.whereareiam.socialismus.integration.placeholderAPI.placeholders.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@Singleton
public class PlaceholderAPI implements MessagingIntegration {
    private static int placeholdersCount;
    private final Plugin plugin = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
    private boolean isEnabled;

    public static int getPlaceholdersCount() {
        return placeholdersCount;
    }

    @Override
    public void initialize() {
        try {
            isEnabled = plugin.isEnabled();
        } catch (NullPointerException e) {
            isEnabled = false;
        }

        Placeholders placeholders = new Placeholders();
        if (placeholders.register()) {
            placeholdersCount = placeholders.getPlaceholdersCount();
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

    @Override
    public IntegrationType getType() {
        return IntegrationType.MESSAGING;
    }

    @Override
    public String formatMessage(Player player, String text) {
        if (!isEnabled) {
            return text;
        }

        return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, text);
    }
}

