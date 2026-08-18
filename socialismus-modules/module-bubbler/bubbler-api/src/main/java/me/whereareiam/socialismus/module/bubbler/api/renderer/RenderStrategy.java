package me.whereareiam.socialismus.module.bubbler.api.renderer;

import com.github.retrooper.packetevents.protocol.player.User;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.position.Position;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.Bubble;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleGroup;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleLine;

import java.util.Collection;
import java.util.List;

/**
 * Strategy for handling complex multi-line rendering operations.
 *
 * <p>Each renderer provides its own strategy that encapsulates renderer-specific logic
 * for handling multi-line scenarios like static groups and stacked animations.
 *
 * <h2>Why Use Strategies?</h2>
 * Different entity types have fundamentally different rendering approaches:
 * <ul>
 *   <li><b>TextDisplays:</b> Use batch attachment and metadata for positioning</li>
 *   <li><b>ArmorStands:</b> Use passenger chaining with spacer entities</li>
 * </ul>
 *
 * <p>By encapsulating these differences in strategies, animations can remain format-agnostic
 * and simply call strategy methods without knowing whether they're working with text displays
 * or armor stands.
 *
 * @see me.whereareiam.socialismus.module.bubbler.common.renderer.strategy.TextDisplayRenderStrategy
 * @see me.whereareiam.socialismus.module.bubbler.common.renderer.strategy.ArmorStandRenderStrategy
 */
public interface RenderStrategy {
	/**
	 * Spawns multiple lines for static display (all at once).
	 *
	 * <p>Used by static animations where all lines appear simultaneously.
	 * Each strategy handles attachment differently:
	 * <ul>
	 *   <li><b>TextDisplays:</b> Spawn all at calculated positions, batch attach to player</li>
	 *   <li><b>ArmorStands:</b> Build passenger chain from player down through all lines</li>
	 * </ul>
	 *
	 * @param bubble the bubble configuration
	 * @param group the group of lines to spawn
	 * @param headGap gap from head
	 * @param spacing spacing between lines
	 * @param eyePos the player's eye position
	 * @param sender the player entity
	 * @param recipients the users to send packets to
	 * @return list of rendered lines
	 */
	List<RenderedLine> spawnStaticGroup(
			Bubble bubble,
			BubbleGroup group,
			float headGap,
			float spacing,
			Position eyePos,
			SocialismusPlayer sender,
			Collection<User> recipients
	);

	/**
	 * Spawns a single line in a stacked animation context.
	 *
	 * <p>Called when a new line spawns in a stacked animation. The strategy determines
	 * what type of line to create based on context:
	 * <ul>
	 *   <li><b>TextDisplays:</b> Always spawns with headGap offset</li>
	 *   <li><b>ArmorStands:</b> First line without spacer, subsequent lines with spacer</li>
	 * </ul>
	 *
	 * @param bubble the bubble configuration
	 * @param line the line content to render
	 * @param eyePos the player's eye position
	 * @param existingLines previously rendered lines in the stack
	 * @return the rendered line
	 */
	RenderedLine spawnStackedLine(
			Bubble bubble,
			BubbleLine line,
			Position eyePos,
			List<RenderedLine> existingLines
	);

	/**
	 * Updates positions of multiple stacked lines and reattaches them.
	 *
	 * <p>Called when lines are removed from the stack (popout/overflow) to maintain
	 * correct positioning and attachment. Each strategy handles this differently:
	 * <ul>
	 *   <li><b>TextDisplays:</b> Updates Y positions via translation metadata, batch reattaches</li>
	 *   <li><b>ArmorStands:</b> Rebuilds the entire passenger chain from scratch</li>
	 * </ul>
	 *
	 * @param sender the player these bubbles belong to
	 * @param lines the lines to update
	 * @param headGap gap from head
	 * @param spacing spacing between lines
	 * @param recipients the users to send packets to
	 */
	void updateStackedPositions(
			SocialismusPlayer sender,
			List<RenderedLine> lines,
			float headGap,
			float spacing,
			Collection<User> recipients
	);

	/**
	 * Attaches a newly spawned line in stacked animation, pushing existing lines down.
	 *
	 * <p>Called immediately after spawning a new line, BEFORE adding it to the list.
	 * This allows renderer-specific incremental attachment without full rebuilds.
	 *
	 * <p>Behavior by renderer:
	 * <ul>
	 *   <li><b>TextDisplays:</b> No-op (batch attachment handled in updateStackedPositions)</li>
	 *   <li><b>ArmorStands:</b> Incrementally rebuilds chain to avoid visual glitches</li>
	 * </ul>
	 *
	 * @param sender the player these bubbles belong to
	 * @param newLine the newly spawned line
	 * @param existingLines the existing lines that need to be pushed down
	 * @param recipients the users to send packets to
	 */
	void attachNewStackedLine(
			SocialismusPlayer sender,
			RenderedLine newLine,
			List<RenderedLine> existingLines,
			Collection<User> recipients
	);
}
