package me.whereareiam.socialismus.module.bubbler.common.renderer;

import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.Vector3d;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.model.position.Position;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.Bubble;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleLine;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.PassengerPacket;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.AreaEffectCloudPacket;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.DestroyEntitiesPacket;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.living.ArmorStandPacket;
import me.whereareiam.socialismus.module.bubbler.api.renderer.BubbleRenderer;
import me.whereareiam.socialismus.module.bubbler.api.renderer.RenderStrategy;
import me.whereareiam.socialismus.module.bubbler.api.renderer.RenderedLine;
import me.whereareiam.socialismus.module.bubbler.common.renderer.strategy.ArmorStandRenderStrategy;
import me.whereareiam.socialismus.module.bubbler.common.util.PacketUtil;

import java.util.Collection;
import java.util.List;

@Singleton
public class ArmorStandRenderer implements BubbleRenderer {
	private final RenderStrategy strategy;

	@Inject
	public ArmorStandRenderer() {
		this.strategy = new ArmorStandRenderStrategy(this);
	}

	@Override
	public RenderedLine spawnLine(Bubble bubble, BubbleLine line, float yOffset, Position eyePos) {
		Vector3d pos = PacketUtil.toVector3d(eyePos);

		ArmorStandPacket armorStand = ArmorStandPacket.builder()
				.position(new Vector3d(pos.getX(), pos.getY() + yOffset, pos.getZ()))
				.customName(line.getContent())
				.noGravity(true)
				.build();

		return new ArmorStandRenderedLine(armorStand);
	}

	public RenderedLine spawnLineWithSpacer(BubbleLine line, Position eyePos) {
		Vector3d pos = PacketUtil.toVector3d(eyePos);

		AreaEffectCloudPacket spacer = AreaEffectCloudPacket.builder()
				.position(pos)
				.radius(0)
				.noGravity(true)
				.build();

		ArmorStandPacket armorStand = ArmorStandPacket.builder()
				.position(pos)
				.customName(line.getContent())
				.noGravity(true)
				.build();

		return new ArmorStandRenderedLine(armorStand, spacer);
	}

	@Override
	public void sendSpawn(RenderedLine renderedLine, Collection<User> recipients) {
		ArmorStandRenderedLine line = (ArmorStandRenderedLine) renderedLine;

		if (line.hasSpacer()) {
			recipients.forEach(r -> line.getSpacerPacket().send(r));
		}
		recipients.forEach(r -> line.getArmorStandPacket().send(r));
	}

	@Override
	public void attachAsPassenger(int vehicleId, RenderedLine renderedLine, Collection<User> recipients) {
		ArmorStandRenderedLine line = (ArmorStandRenderedLine) renderedLine;

		if (line.hasSpacer()) {
			PassengerPacket spacerPassenger = PassengerPacket.builder()
					.vehicleId(vehicleId)
					.passengerIds(new int[]{line.getSpacerId()})
					.build();
			recipients.forEach(spacerPassenger::send);

			PassengerPacket armorStandPassenger = PassengerPacket.builder()
					.vehicleId(line.getSpacerId())
					.passengerIds(new int[]{line.getPrimaryEntityId()})
					.build();
			recipients.forEach(armorStandPassenger::send);
		} else {
			PassengerPacket passenger = PassengerPacket.builder()
					.vehicleId(vehicleId)
					.passengerIds(new int[]{line.getPrimaryEntityId()})
					.build();
			recipients.forEach(passenger::send);
		}
	}

	@Override
	public void updatePosition(RenderedLine line, float yOffset, Position eyePos, Collection<User> recipients) {
		// ArmorStand positions are managed via passenger chain, no update needed
	}

	@Override
	public void destroy(RenderedLine line, Collection<User> recipients) {
		DestroyEntitiesPacket packet = DestroyEntitiesPacket.builder()
				.entityIds(line.getAllEntityIds().stream().mapToInt(Integer::intValue).toArray())
				.build();
		recipients.forEach(packet::send);
	}

	@Override
	public void destroy(List<RenderedLine> lines, Collection<User> recipients) {
		int[] ids = lines.stream()
				.flatMap(l -> l.getAllEntityIds().stream())
				.mapToInt(Integer::intValue)
				.toArray();
		DestroyEntitiesPacket packet = DestroyEntitiesPacket.builder()
				.entityIds(ids)
				.build();
		recipients.forEach(packet::send);
	}

	@Override
	public void applyInitialScale(RenderedLine line, Bubble bubble, Collection<User> recipients) {
		// ArmorStand doesn't support scale animation
	}

	@Override
	public void animateSpawn(RenderedLine line, Bubble bubble, Collection<User> recipients) {
		// ArmorStand doesn't support spawn animation
	}

	@Override
	public void animateRemoval(RenderedLine line, Bubble bubble, Collection<User> recipients, Runnable onComplete) {
		// ArmorStand doesn't support removal animation - just destroy immediately
		destroy(line, recipients);
		onComplete.run();
	}

	@Override
	public int getPassengerAnchorId(RenderedLine line) {
		// The next entity should attach to the armor stand (top of the chain)
		return line.getPrimaryEntityId();
	}

	/**
	 * Gets the bottom anchor ID where older lines should attach.
	 * For lines with spacers, this is the spacer ID (so spacer pushes old lines upward).
	 * For lines without spacers, this is the armor stand ID.
	 */
	public int getBottomAnchorId(RenderedLine line) {
		ArmorStandRenderedLine asLine = (ArmorStandRenderedLine) line;
		return asLine.hasSpacer() ? asLine.getSpacerId() : asLine.getPrimaryEntityId();
	}

	@Override
	public RenderStrategy getStrategy() {
		return strategy;
	}

	// Public methods used by ArmorStandRenderStrategy

	/**
	 * Reattaches a line (with its spacer if present) to a vehicle.
	 *
	 * <p>If the line has a spacer, builds chain upward:
	 * <ol>
	 *   <li>Attaches spacer to vehicle (as passenger, appears above vehicle)</li>
	 *   <li>Attaches armor stand to spacer (as passenger, appears above spacer)</li>
	 * </ol>
	 *
	 * <p>If the line has no spacer:
	 * <ol>
	 *   <li>Attaches armor stand directly to vehicle</li>
	 * </ol>
	 *
	 * @param vehicleId the entity ID to attach to
	 * @param line the line to attach
	 * @param recipients packet recipients
	 */
	public void rechainPassenger(int vehicleId, RenderedLine line, Collection<User> recipients) {
		ArmorStandRenderedLine asLine = (ArmorStandRenderedLine) line;

		int attachTo = asLine.hasSpacer() ? asLine.getSpacerId() : asLine.getPrimaryEntityId();
		PassengerPacket packet = PassengerPacket.builder()
				.vehicleId(vehicleId)
				.passengerIds(new int[]{attachTo})
				.build();
		recipients.forEach(packet::send);

		if (asLine.hasSpacer()) {
			PassengerPacket armorStandPacket = PassengerPacket.builder()
					.vehicleId(asLine.getSpacerId())
					.passengerIds(new int[]{asLine.getPrimaryEntityId()})
					.build();
			recipients.forEach(armorStandPacket::send);
		}
	}

	/**
	 * Attaches ONLY the armor stand entity to a vehicle, ignoring any spacer.
	 *
	 * <p>Used for attaching the newest line directly to the player to keep it near head.
	 * Even if the line has a spacer, this method skips it and attaches only the armor stand.
	 *
	 * <p><b>Note:</b> If the line has a spacer, the spacer must be manually attached separately
	 * (typically above the armor stand as a passenger) to maintain the chain structure and push
	 * older lines higher up.
	 *
	 * @param vehicleId the entity ID to attach to (typically player)
	 * @param renderedLine the line whose armor stand should be attached
	 * @param recipients packet recipients
	 */
	public void attachArmorStandOnly(int vehicleId, RenderedLine renderedLine, Collection<User> recipients) {
		ArmorStandRenderedLine line = (ArmorStandRenderedLine) renderedLine;

		PassengerPacket packet = PassengerPacket.builder()
				.vehicleId(vehicleId)
				.passengerIds(new int[]{line.getPrimaryEntityId()})
				.build();
		recipients.forEach(packet::send);
	}
}
