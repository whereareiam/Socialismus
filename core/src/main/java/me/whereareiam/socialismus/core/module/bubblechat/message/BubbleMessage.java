package me.whereareiam.socialismus.core.module.bubblechat.message;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Collection;

public record BubbleMessage(double displayTime, Component message, Player sender,
							Collection<? extends Player> receivers) {
}
