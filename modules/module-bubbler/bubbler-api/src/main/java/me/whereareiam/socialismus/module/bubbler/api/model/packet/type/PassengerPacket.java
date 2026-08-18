package me.whereareiam.socialismus.module.bubbler.api.model.packet.type;

import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.Packet;

/**
 * Packet for mounting entities as passengers on a vehicle entity.
 * Used to attach bubble display entities to invisible carrier entities.
 */
@SuperBuilder
public class PassengerPacket implements Packet {
	private final int vehicleId;
	private final int[] passengerIds;

	@Override
	public void send(User user) {
		WrapperPlayServerSetPassengers packet = new WrapperPlayServerSetPassengers(vehicleId, passengerIds);
		user.sendPacket(packet);
	}
}
