package me.whereareiam.socialismus.core.integration.placeholderapi;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.core.integration.IntegrationType;
import me.whereareiam.socialismus.core.integration.MessagingIntegration;
import me.whereareiam.socialismus.core.integration.placeholderapi.placeholders.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@Singleton
public class PlaceholderAPI implements MessagingIntegration {
	private static int placeholdersCount;
	private final Injector injector;
	private final Plugin plugin = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
	private boolean isEnabled;

	@Inject
	public PlaceholderAPI(Injector injector) {
		this.injector = injector;
	}

	public static int getPlaceholdersCount() {
		return placeholdersCount;
	}

	@Override
	public void initialize() {
		if (plugin == null)
			return;

		isEnabled = plugin.isEnabled();
		Placeholders placeholders = injector.getInstance(Placeholders.class);

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
	public String formatMessage(Player player, String string) {
		if (!isEnabled) {
			return string;
		}

		return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, string);
	}
}

