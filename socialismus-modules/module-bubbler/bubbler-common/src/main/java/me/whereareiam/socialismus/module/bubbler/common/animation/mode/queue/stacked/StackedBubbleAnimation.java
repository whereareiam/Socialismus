package me.whereareiam.socialismus.module.bubbler.common.animation.mode.queue.stacked;

import com.github.retrooper.packetevents.protocol.player.User;
import com.google.inject.Provider;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.position.Position;
import me.whereareiam.socialismus.model.scheduler.DelayedRunnableTask;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.Bubble;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleGroup;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleLine;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerSettings;
import me.whereareiam.socialismus.module.bubbler.api.renderer.BubbleRenderer;
import me.whereareiam.socialismus.module.bubbler.api.renderer.RenderedLine;
import me.whereareiam.socialismus.module.bubbler.api.type.TransitionType;
import me.whereareiam.socialismus.module.bubbler.common.animation.type.queue.AbstractQueuedAnimation;
import me.whereareiam.socialismus.module.bubbler.common.animation.type.queue.BubbleQueue;
import me.whereareiam.socialismus.module.bubbler.common.renderer.BubbleRendererFactory;
import me.whereareiam.socialismus.module.bubbler.common.renderer.strategy.TextDisplayRenderStrategy;
import me.whereareiam.socialismus.service.Scheduler;

import java.util.*;

abstract class StackedBubbleAnimation extends AbstractQueuedAnimation {
	protected static final long TICK_MS = 50L;

	protected final Provider<BubblerSettings> settings;

	protected StackedBubbleAnimation(
			Scheduler scheduler,
			BubbleRendererFactory rendererFactory,
			Provider<BubblerSettings> settings,
			EventManager eventManager
	) {
		super(scheduler, rendererFactory, eventManager);
		this.settings = settings;
	}

	protected abstract boolean useSpawnAnimation();

	protected abstract boolean useRemovalAnimation();

	protected abstract long extraSpawnDelayMs(Bubble bubble);

	@Override
	protected final void playGroup(SocialismusPlayer sender, BubbleMessage msg, BubbleGroup group, BubbleQueue queue) {
		// Fire BEFORE transition for first group
		if (queue.getCurrentGroupIndex() == 0) {
			fireTransition(TransitionType.BEFORE, msg);
		} else {
			// Fire INTER transition between groups
			fireTransition(TransitionType.INTER, msg);
		}

		List<BubbleLine> lines = new ArrayList<>(group.getLines());
		Collections.reverse(lines);

		step(sender, msg, lines, 0, new ArrayList<>(), queue);
	}

	private void step(
			SocialismusPlayer sender,
			BubbleMessage msg,
			List<BubbleLine> lines,
			int index,
			List<RenderedLine> renderedLines,
			BubbleQueue queue
	) {
		Bubble bubble = msg.getBubble();
		float headGap = bubble.getDisplay().getHeadLineGap();
		float spacing = bubble.getDisplay().getLineSpacing();
		int maxLines = bubble.getDisplay().getMaxLinesCount();
		Position eyePos = sender.getEyePosition();
		Collection<User> recipients = users(msg.getRecipients());
		BubbleRenderer renderer = getRenderer();

		if (index >= lines.size()) {
			if (renderedLines.isEmpty()) {
				// Fire AFTER transition if this was the last group
				if (queue.isLastGroup()) {
					fireTransition(TransitionType.AFTER, msg);
				}

				queue.setProcessing(false);
				nextGroup(sender, queue);
				return;
			}

			RenderedLine toRemove = renderedLines.get(0);
			if (useRemovalAnimation()) {
				renderer.animateRemoval(toRemove, bubble, recipients, () -> {
					renderedLines.remove(0);
					renderer.getStrategy().updateStackedPositions(sender, renderedLines, headGap, spacing, recipients);
					schedule(settings.get().getAnimation().getPopoutDelay(),
							() -> step(sender, msg, lines, index, renderedLines, queue));
				});
			} else {
				renderer.destroy(toRemove, recipients);
				renderedLines.remove(0);
				renderer.getStrategy().updateStackedPositions(sender, renderedLines, headGap, spacing, recipients);
				schedule(settings.get().getAnimation().getPopoutDelay(),
						() -> step(sender, msg, lines, index, renderedLines, queue));
			}
			return;
		}

		RenderedLine line = renderer.getStrategy().spawnStackedLine(bubble, lines.get(index), eyePos, renderedLines);

		// Send spawn packet first
		renderer.sendSpawn(line, recipients);

		// THEN apply initial scale if using spawn animation (sends metadata AFTER spawn)
		if (useSpawnAnimation()) {
			renderer.applyInitialScale(line, bubble, recipients);
		}

		if (useSpawnAnimation()) {
			renderer.animateSpawn(line, bubble, recipients);
		}

		// Attach the new line and reposition existing lines immediately
		renderer.getStrategy().attachNewStackedLine(sender, line, renderedLines, recipients);

		renderedLines.add(line);

		// For text displays, we need to update positions after adding to the list
		// For armor stands, attachNewStackedLine already set up the chain correctly
		if (renderer.getStrategy() instanceof TextDisplayRenderStrategy) {
			renderer.getStrategy().updateStackedPositions(sender, renderedLines, headGap, spacing, recipients);
		}

		if (renderedLines.size() > maxLines) {
			RenderedLine overflow = renderedLines.get(0);
			if (useRemovalAnimation()) {
				renderer.animateRemoval(overflow, bubble, recipients, () -> {
					renderedLines.remove(0);
					renderer.getStrategy().updateStackedPositions(sender, renderedLines, headGap, spacing, recipients);
				});
			} else {
				renderer.destroy(overflow, recipients);
				renderedLines.remove(0);
				renderer.getStrategy().updateStackedPositions(sender, renderedLines, headGap, spacing, recipients);
			}
		}

		long readMs = lines.get(index).getDisplayTime() * settings.get().getAnimation().getPopoutDelay();
		long nextMs = extraSpawnDelayMs(bubble) + readMs;
		schedule(nextMs, () -> step(sender, msg, lines, index + 1, renderedLines, queue));
	}

	protected void schedule(long delayMs, Runnable r) {
		scheduler.schedule(DelayedRunnableTask.builder()
				.module("bubbler")
				.delay(delayMs)
				.runnable(r)
				.build());
	}

	protected long tickMs() {
		return TICK_MS;
	}
}
