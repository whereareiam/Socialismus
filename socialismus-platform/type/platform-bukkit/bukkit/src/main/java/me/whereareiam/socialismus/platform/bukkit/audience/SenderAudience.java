package me.whereareiam.socialismus.platform.bukkit.audience;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * The audience of one command sender: messages go through {@link ComponentSender}, everything else, such as titles,
 * sounds and boss bars, through adventure-platform-bukkit.
 *
 * @param sender    the command sender
 * @param platform  the sender's audience of adventure-platform-bukkit
 * @param messages  the sender of messages
 */
public record SenderAudience(@NotNull CommandSender sender, @NotNull Audience platform, @NotNull ComponentSender messages)
		implements ForwardingAudience.Single {
	@Override
	@NotNull
	public Audience audience() {
		return platform;
	}

	@Override
	public void sendMessage(@NotNull Component message) {
		messages.send(sender, message);
	}
}
