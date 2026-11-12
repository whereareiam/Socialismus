package me.whereareiam.socialismus.platform.bukkit.audience;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * The audiences of the BUKKIT jar, each a {@link SenderAudience}, so messages reach players and the console.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class SenderAudiences {
	private final Provider<BukkitAudiences> platform;
	private final ComponentSender messages;

	@NotNull
	public Audience sender(@NotNull CommandSender sender) {
		return new SenderAudience(sender, platform.get().sender(sender), messages);
	}

	@NotNull
	public Audience console() {
		return sender(Bukkit.getConsoleSender());
	}

	@NotNull
	public Audience players() {
		List<Audience> players = new ArrayList<>();
		Bukkit.getOnlinePlayers().forEach(player -> players.add(sender(player)));
		return Audience.audience(players);
	}

	@NotNull
	public Audience all() {
		return Audience.audience(console(), players());
	}
}
