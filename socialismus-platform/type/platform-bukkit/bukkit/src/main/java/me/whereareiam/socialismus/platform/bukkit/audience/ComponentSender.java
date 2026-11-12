package me.whereareiam.socialismus.platform.bukkit.audience;

import com.google.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.json.JSONOptions;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * Sends components to a command sender through the chat components of Spigot's API, which every Bukkit server since
 * 1.16 carries. The component is written as the JSON of the server's Minecraft version and read back by the server.
 * <p>
 * adventure-platform-bukkit cannot send them: it is built for Adventure 4 and only implements the
 * {@code sendMessage} with a {@code MessageType}, which Adventure 5 removed, so its plain {@code sendMessage} does nothing.
 */
@Singleton
public class ComponentSender {
	@SuppressWarnings("deprecation")
	private final GsonComponentSerializer serializer = GsonComponentSerializer.builder()
			.options(JSONOptions.schema().stateBuilder()
					.values(JSONOptions.byDataVersion().at(Bukkit.getUnsafe().getDataVersion()))
					// The chat components of older servers only read objects and arrays, never a bare string.
					.value(JSONOptions.EMIT_COMPACT_TEXT_COMPONENT, false)
					.build())
			.build();

	@SuppressWarnings("deprecation")
	public void send(@NotNull CommandSender sender, @NotNull Component message) {
		sender.spigot().sendMessage(ComponentSerializer.parse(serializer.serialize(message)));
	}
}
