package me.whereareiam.socialismus.core.integration.placeholderapi.placeholders;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.whereareiam.socialismus.core.SocialismusBase;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@Singleton
public class Placeholders extends PlaceholderExpansion {

	private final Map<String, Supplier<String>> basicPlaceholders = new HashMap<>();
	private final Map<String, Function<String, String>> advancedPlaceholders = new HashMap<>();

	@Inject
	public Placeholders() {
		//basicPlaceholders.put("test", this::test);
		advancedPlaceholders.put("statistics_chat_", this::getChatMessageCount);
	}

	@Override
	public @NotNull String getIdentifier() {
		return "socialismus";
	}

	@Override
	public @NotNull String getAuthor() {
		return "whereareiam";
	}

	@Override
	public @NotNull String getVersion() {
		return SocialismusBase.version;
	}

	@Override
	public String onPlaceholderRequest(Player player, String identifier) {
		Supplier<String> supplier = basicPlaceholders.get(identifier.toLowerCase());
		if (supplier != null) {
			return supplier.get();
		}
		for (Map.Entry<String, Function<String, String>> entry : advancedPlaceholders.entrySet()) {
			if (identifier.toLowerCase().startsWith(entry.getKey())) {
				return entry.getValue().apply(identifier.substring(entry.getKey().length()));
			}
		}
		return "";
	}

	public int getPlaceholdersCount() {
		return basicPlaceholders.size() + advancedPlaceholders.size();
	}

	private String getChatMessageCount(String chatId) {
		int count = 0; //chatMessageStatistic.getMessageCount(chatId);
		return String.valueOf(count);
	}
}
