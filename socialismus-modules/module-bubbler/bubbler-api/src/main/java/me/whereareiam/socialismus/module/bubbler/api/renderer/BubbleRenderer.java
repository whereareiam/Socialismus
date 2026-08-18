package me.whereareiam.socialismus.module.bubbler.api.renderer;

import com.github.retrooper.packetevents.protocol.player.User;
import me.whereareiam.socialismus.model.position.Position;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.Bubble;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleLine;

import java.util.Collection;
import java.util.List;

/**
 * Interface for rendering bubble text above players.
 * Implementations handle entity creation, positioning, animation, and cleanup
 * using different entity types (armor stands, text displays, etc.).
 * <p>
 * This interface contains ONLY primitive rendering operations.
 * Complex rendering logic should be delegated to RenderStrategy implementations.
 */
public interface BubbleRenderer {
	/**
	 * Creates the entities needed to render a bubble line.
	 *
	 * @param bubble the bubble configuration
	 * @param line the line content to render
	 * @param yOffset vertical offset from the player's eye position
	 * @param eyePos the player's eye position
	 * @return the rendered line containing entity information
	 */
	RenderedLine spawnLine(Bubble bubble, BubbleLine line, float yOffset, Position eyePos);

	/**
	 * Sends spawn packets for a rendered line to recipients.
	 *
	 * @param renderedLine the line to spawn
	 * @param recipients the users to send packets to
	 */
	void sendSpawn(RenderedLine renderedLine, Collection<User> recipients);

	/**
	 * Attaches a rendered line as a passenger of a vehicle entity.
	 *
	 * @param vehicleId the vehicle entity ID
	 * @param line the line to attach
	 * @param recipients the users to send packets to
	 */
	void attachAsPassenger(int vehicleId, RenderedLine line, Collection<User> recipients);

	/**
	 * Updates the position of a rendered line.
	 *
	 * @param line the line to update
	 * @param yOffset vertical offset from the player's eye position
	 * @param eyePos the player's current eye position (can be null if not needed)
	 * @param recipients the users to send packets to
	 */
	void updatePosition(RenderedLine line, float yOffset, Position eyePos, Collection<User> recipients);

	/**
	 * Destroys a single rendered line.
	 *
	 * @param line the line to destroy
	 * @param recipients the users to send packets to
	 */
	void destroy(RenderedLine line, Collection<User> recipients);

	/**
	 * Destroys multiple rendered lines efficiently.
	 *
	 * @param lines the lines to destroy
	 * @param recipients the users to send packets to
	 */
	void destroy(List<RenderedLine> lines, Collection<User> recipients);

	/**
	 * Applies the initial scale for animation (typically zero for grow effects).
	 *
	 * @param line the line to scale
	 * @param bubble the bubble configuration
	 * @param recipients the users to send packets to
	 */
	void applyInitialScale(RenderedLine line, Bubble bubble, Collection<User> recipients);

	/**
	 * Animates a line spawning (e.g., growing from zero to full size).
	 *
	 * @param line the line to animate
	 * @param bubble the bubble configuration
	 * @param recipients the users to send packets to
	 */
	void animateSpawn(RenderedLine line, Bubble bubble, Collection<User> recipients);

	/**
	 * Animates a line being removed (e.g., shrinking to zero).
	 *
	 * @param line the line to animate
	 * @param bubble the bubble configuration
	 * @param recipients the users to send packets to
	 * @param onComplete callback to run when animation completes
	 */
	void animateRemoval(RenderedLine line, Bubble bubble, Collection<User> recipients, Runnable onComplete);

	/**
	 * Gets the entity ID that should be used as the passenger anchor point.
	 * This is used when chaining multiple entities together.
	 *
	 * @param line the rendered line
	 * @return the entity ID for passenger mounting
	 */
	int getPassengerAnchorId(RenderedLine line);

	/**
	 * Gets the render strategy for this renderer.
	 * The strategy handles complex multi-line rendering logic.
	 *
	 * @return the render strategy
	 */
	RenderStrategy getStrategy();
}
