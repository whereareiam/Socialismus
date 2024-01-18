package me.whereareiam.socialismus.core.integration.protocollib;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.google.inject.Singleton;
import org.bukkit.entity.Player;

@Singleton
public class PacketSender {
	private final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

	public void sendPacket(Player player, PacketContainer packetContainer) {
		protocolManager.sendServerPacket(player, packetContainer);
	}

	public void broadcastPacket(PacketContainer packetContainer) {
		protocolManager.broadcastServerPacket(packetContainer);
	}
}
