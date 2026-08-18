package me.whereareiam.socialismus.module.bubbler.common.renderer.strategy;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.position.Position;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.Bubble;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleGroup;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleLine;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.PassengerPacket;
import me.whereareiam.socialismus.module.bubbler.api.renderer.BubbleRenderer;
import me.whereareiam.socialismus.module.bubbler.api.renderer.RenderStrategy;
import me.whereareiam.socialismus.module.bubbler.api.renderer.RenderedLine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Rendering strategy for TextDisplay entities.
 *
 * <h2>Rendering Approach</h2>
 * TextDisplays use a simple batch attachment system:
 * <ul>
 *   <li>All text display entities spawn at calculated Y offsets</li>
 *   <li>All entities attach directly to the player as passengers in a single batch</li>
 *   <li>Position updates use translation metadata to move entities</li>
 * </ul>
 *
 * <h2>Chain Structure</h2>
 * <pre>
 * Player
 *   ├─ Line1 (at headGap + 0 * spacing)
 *   ├─ Line2 (at headGap + 1 * spacing)
 *   └─ Line3 (at headGap + 2 * spacing)
 * </pre>
 * All lines are direct children of the player, with positions managed via metadata.
 *
 * <h2>Static vs Stacked Animations</h2>
 * <ul>
 *   <li><b>Static:</b> All lines spawn at once and attach in a single batch</li>
 *   <li><b>Stacked:</b> Lines spawn one by one, but batch reattachment happens after each spawn</li>
 * </ul>
 *
 * @see ArmorStandRenderStrategy for comparison with armor stand rendering
 */
@RequiredArgsConstructor
public class TextDisplayRenderStrategy implements RenderStrategy {
	private final BubbleRenderer renderer;

	@Override
	public List<RenderedLine> spawnStaticGroup(
			Bubble bubble,
			BubbleGroup group,
			float headGap,
			float spacing,
			Position eyePos,
			SocialismusPlayer sender,
			Collection<User> recipients
	) {
		List<RenderedLine> lines = new ArrayList<>();
		List<BubbleLine> bubbleLines = group.getLines();

		// Spawn all lines
		for (int i = 0; i < bubbleLines.size(); i++) {
			float yOffset = headGap + i * spacing;
			RenderedLine line = renderer.spawnLine(bubble, bubbleLines.get(i), yOffset, eyePos);
			renderer.sendSpawn(line, recipients);
			lines.add(line);
		}

		// Attach all as passengers in a single batch
		batchAttachToPlayer(sender, lines, recipients);

		return lines;
	}

	@Override
	public RenderedLine spawnStackedLine(
			Bubble bubble,
			BubbleLine line,
			Position eyePos,
			List<RenderedLine> existingLines
	) {
		// For text displays, always use headGap positioning
		float yOffset = bubble.getDisplay().getHeadLineGap();
		return renderer.spawnLine(bubble, line, yOffset, eyePos);
	}

	@Override
	public void updateStackedPositions(
			SocialismusPlayer sender,
			List<RenderedLine> lines,
			float headGap,
			float spacing,
			Collection<User> recipients
	) {
		// Update positions via translation metadata
		for (int i = lines.size() - 1, level = 0; i >= 0; i--, level++) {
			float yOffset = headGap + level * spacing;
			renderer.updatePosition(lines.get(i), yOffset, null, recipients);
		}

		// Reattach all as passengers
		batchAttachToPlayer(sender, lines, recipients);
	}

	@Override
	public void attachNewStackedLine(
			SocialismusPlayer sender,
			RenderedLine newLine,
			List<RenderedLine> existingLines,
			Collection<User> recipients
	) {
		// For text displays, we don't do incremental attachment
		// Just batch attach all lines together (this will be called in updateStackedPositions)
	}

	private void batchAttachToPlayer(SocialismusPlayer sender, List<RenderedLine> lines, Collection<User> recipients) {
		if (lines.isEmpty())
			return;

		int vehicleId = PacketEvents.getAPI()
				.getPlayerManager()
				.getUser(sender.getAudience())
				.getEntityId();

		int[] passengerIds = lines.stream()
				.mapToInt(RenderedLine::getPrimaryEntityId)
				.toArray();

		PassengerPacket packet = PassengerPacket.builder()
				.vehicleId(vehicleId)
				.passengerIds(passengerIds)
				.build();

		recipients.forEach(packet::send);
	}
}
