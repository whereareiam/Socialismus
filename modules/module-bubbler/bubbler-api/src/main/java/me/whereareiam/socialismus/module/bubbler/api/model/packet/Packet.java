package me.whereareiam.socialismus.module.bubbler.api.model.packet;

import com.github.retrooper.packetevents.protocol.player.User;

/**
 * Base interface for all packet types used in bubble rendering.
 * Packets encapsulate Minecraft protocol data and handle sending to clients.
 */
public interface Packet {
	/**
	 * Sends this packet to the specified user.
	 *
	 * @param user the PacketEvents user to send the packet to
	 */
	void send(User user);
}
