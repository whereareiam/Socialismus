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
import me.whereareiam.socialismus.module.bubbler.api.renderer.RenderStrategy;
import me.whereareiam.socialismus.module.bubbler.api.renderer.RenderedLine;
import me.whereareiam.socialismus.module.bubbler.common.renderer.ArmorStandRenderedLine;
import me.whereareiam.socialismus.module.bubbler.common.renderer.ArmorStandRenderer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Rendering strategy for ArmorStand entities.
 *
 * <h2>Rendering Approach</h2>
 * ArmorStands use passenger chaining with spacer entities for vertical offset:
 * <ul>
 *   <li>Each armor stand (except the first) has an invisible spacer entity</li>
 *   <li>Spacers provide vertical offset without needing position updates</li>
 *   <li>Entities chain together: each entity becomes a passenger of the one below it</li>
 *   <li>Passengers appear ABOVE their vehicle, so the chain grows UPWARD from player</li>
 * </ul>
 *
 * <h2>Chain Structure</h2>
 * <pre>
 * Line1_ArmorStand (oldest, at top)        ← Highest above head
 *   ↑ (passenger of)
 * Line2_Spacer                             ← Pushes Line1 higher
 *   ↑ (passenger of)
 * Line2_ArmorStand
 *   ↑ (passenger of)
 * Line3_Spacer                             ← Pushes Line2 higher
 *   ↑ (passenger of)
 * Line3_ArmorStand (newest, near head)    ← Closest to head
 *   ↑ (passenger of)
 * Player (at bottom)                       ← Base of the chain
 * </pre>
 * The newest line's armor stand attaches directly to the player (no spacer first).
 * Each line's spacer (if present) goes ABOVE its armor stand to push older lines higher.
 *
 * <h2>Why This Structure?</h2>
 * <ul>
 *   <li>Keeps newest line close to head (no spacer lifting it up)</li>
 *   <li>Spacers push OLD lines upward, away from head</li>
 *   <li>Minecraft passengers appear ABOVE their vehicle, so chain grows upward</li>
 * </ul>
 *
 * <h2>Static vs Stacked Animations</h2>
 * <ul>
 *   <li><b>Static:</b> Entire chain built at once, spacers between each line</li>
 *   <li><b>Stacked:</b> Chain rebuilt incrementally - new line attaches to player,
 *       old lines rechain above it</li>
 * </ul>
 *
 * <h2>Key Methods</h2>
 * <ul>
 *   <li>{@code attachArmorStandOnly} - Attaches ONLY armor stand (skips spacer)</li>
 *   <li>{@code rechainPassenger} - Attaches both spacer AND armor stand</li>
 *   <li>{@code getPassengerAnchorId} - Returns armor stand ID (bottom of line's chain)</li>
 *   <li>{@code getBottomAnchorId} - Returns spacer ID if present, else armor stand ID</li>
 * </ul>
 *
 * @see TextDisplayRenderStrategy for comparison with text display rendering
 */
@RequiredArgsConstructor
public class ArmorStandRenderStrategy implements RenderStrategy {
	private final ArmorStandRenderer renderer;

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

		int vehicleId = PacketEvents.getAPI()
				.getPlayerManager()
				.getUser(sender.getAudience())
				.getEntityId();

		// Create chain: Player -> Line0 -> Line1 -> Line2...
		for (int i = 0; i < bubbleLines.size(); i++) {
			RenderedLine line;
			if (i == 0) {
				line = renderer.spawnLine(bubble, bubbleLines.get(i), 0, eyePos);
			} else {
				line = renderer.spawnLineWithSpacer(bubbleLines.get(i), eyePos);
			}

			renderer.sendSpawn(line, recipients);
			renderer.attachAsPassenger(vehicleId, line, recipients);
			lines.add(line);

			vehicleId = renderer.getPassengerAnchorId(line);
		}

		return lines;
	}

	@Override
	public RenderedLine spawnStackedLine(
			Bubble bubble,
			BubbleLine line,
			Position eyePos,
			List<RenderedLine> existingLines
	) {
		// For stacked animations, use spacer for proper vertical spacing
		// First line doesn't need spacer, subsequent lines do
		if (existingLines.isEmpty()) {
			return renderer.spawnLine(bubble, line, 0, eyePos);
		}

		return renderer.spawnLineWithSpacer(line, eyePos);
	}

	/**
	 * Updates positions and reattaches all lines in the stack.
	 *
	 * <p>This method rebuilds the ENTIRE chain from scratch. It's called when lines are removed
	 * (during popout/overflow) to ensure the chain remains intact.
	 *
	 * <p><b>Note:</b> This method is NOT called after spawning a new line in armor stand rendering,
	 * because {@link #attachNewStackedLine} already handles the attachment incrementally.
	 *
	 * <h3>Chain Rebuilding Logic:</h3>
	 * <ol>
	 *   <li>Attach newest line's armor stand to player (bottom of chain)</li>
	 *   <li>Attach second-newest above newest's top anchor (spacer if present)</li>
	 *   <li>Continue chaining older lines above the ones below them</li>
	 * </ol>
	 *
	 * <p>The chain grows upward: Player → Newest → ... → Oldest (at top)
	 *
	 * @param sender the player entity
	 * @param lines all lines in the stack (newest at end, oldest at start)
	 * @param headGap gap from head (unused for armor stands)
	 * @param spacing line spacing (unused for armor stands)
	 * @param recipients packet recipients
	 */
	@Override
	public void updateStackedPositions(
			SocialismusPlayer sender,
			List<RenderedLine> lines,
			float headGap,
			float spacing,
			Collection<User> recipients
	) {
		// For armor stands in stacked animations, rechain them
		// The chain grows upward: Player (bottom) → Newest → ... → Oldest (top)
		// Only the newest line's armor stand attaches directly to player (no spacer first)
		// This keeps the newest line near the head while older lines are pushed upward by spacers
		if (lines.isEmpty())
			return;

		int vehicleId = PacketEvents.getAPI()
				.getPlayerManager()
				.getUser(sender.getAudience())
				.getEntityId();

		// Attach ONLY the armor stand of the newest line to player (keeps it near head)
		renderer.attachArmorStandOnly(vehicleId, lines.get(lines.size() - 1), recipients);

		// Chain the rest: each older line (with its spacer) attaches above the line below it
		// Work backwards from second-newest to oldest, building upward
		if (lines.size() > 1) {
			// Attach the second-newest line above the newest line's anchor
			renderer.rechainPassenger(
					renderer.getPassengerAnchorId(lines.get(lines.size() - 1)),
					lines.get(lines.size() - 2),
					recipients
			);

			// Continue chaining upward for remaining lines
			for (int i = lines.size() - 3; i >= 0; i--) {
				RenderedLine currentLine = lines.get(i);
				RenderedLine lineBelow = lines.get(i + 1);
				renderer.rechainPassenger(renderer.getPassengerAnchorId(lineBelow), currentLine, recipients);
			}
		}
	}

	/**
	 * Attaches a newly spawned line in a stacked animation context.
	 *
	 * <p>This method is called immediately after a new line spawns, BEFORE adding it to the list.
	 * It performs incremental attachment to maintain the chain structure without rebuilding everything.
	 *
	 * <h3>Chain Building Logic:</h3>
	 * <ol>
	 *   <li>Attach new line's armor stand to player (keeps newest near head)</li>
	 *   <li>If new line has spacer, attach spacer above armor stand</li>
	 *   <li>Reattach all existing lines to chain above new line's top (spacer or armor stand)</li>
	 * </ol>
	 *
	 * <h3>Example:</h3>
	 * <pre>
	 * Before: Line1 (passenger of) → Player
	 *
	 * After:  Line1                       ← Moved higher
	 *           ↑ (passenger of)
	 *         Line2_Spacer                ← Pushes Line1 up
	 *           ↑ (passenger of)
	 *         Line2_ArmorStand (new)      ← Near head
	 *           ↑ (passenger of)
	 *         Player
	 * </pre>
	 *
	 * @param sender the player entity
	 * @param newLine the newly spawned line
	 * @param existingLines lines that were already in the stack (need to be moved higher)
	 * @param recipients packet recipients
	 */
	@Override
	public void attachNewStackedLine(
			SocialismusPlayer sender,
			RenderedLine newLine,
			List<RenderedLine> existingLines,
			Collection<User> recipients
	) {
		int vehicleId = com.github.retrooper.packetevents.PacketEvents.getAPI()
				.getPlayerManager()
				.getUser(sender.getAudience())
				.getEntityId();

		// Always attach the armor stand of the newest line directly to player
		// This keeps the newest line near the head
		renderer.attachArmorStandOnly(vehicleId, newLine, recipients);

		// If the new line has a spacer, attach it ABOVE the armor stand (as passenger)
		// Chain grows upward: Player (bottom) → NewArmorStand → NewSpacer → OldLines (top)
		ArmorStandRenderedLine asNewLine = (ArmorStandRenderedLine) newLine;
		if (asNewLine.hasSpacer()) {
			// Attach the spacer as passenger of the armor stand (appears above it)
			PassengerPacket spacerPacket = PassengerPacket.builder()
					.vehicleId(asNewLine.getPrimaryEntityId())
					.passengerIds(new int[]{asNewLine.getSpacerId()})
					.build();
			recipients.forEach(spacerPacket::send);
		}

		// Rebuild the chain from newest to oldest existing line
		// They should attach above the new line's "top" (its spacer if it has one, otherwise armor stand)
		if (existingLines.isEmpty()) return;

		int bottomAnchor = renderer.getBottomAnchorId(newLine);
		// Build the chain upward from newest existing line to oldest
		for (int i = existingLines.size() - 1; i >= 0; i--) {
			RenderedLine currentLine = existingLines.get(i);

			if (i == existingLines.size() - 1) {
				// Newest existing line attaches above new line's top anchor
				renderer.rechainPassenger(bottomAnchor, currentLine, recipients);
				continue;
			}

			// Other lines attach above the line below them in the chain
			RenderedLine lineBelow = existingLines.get(i + 1);
			renderer.rechainPassenger(renderer.getPassengerAnchorId(lineBelow), currentLine, recipients);
		}
	}
}
