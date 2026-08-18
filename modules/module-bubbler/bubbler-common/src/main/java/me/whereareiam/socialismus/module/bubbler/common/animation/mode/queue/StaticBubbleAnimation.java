package me.whereareiam.socialismus.module.bubbler.common.animation.mode.queue;

import com.github.retrooper.packetevents.protocol.player.User;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.position.Position;
import me.whereareiam.socialismus.model.scheduler.DelayedRunnableTask;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.Bubble;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleGroup;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;
import me.whereareiam.socialismus.module.bubbler.api.renderer.BubbleRenderer;
import me.whereareiam.socialismus.module.bubbler.api.renderer.RenderedLine;
import me.whereareiam.socialismus.module.bubbler.api.type.TransitionType;
import me.whereareiam.socialismus.module.bubbler.common.animation.type.queue.AbstractQueuedAnimation;
import me.whereareiam.socialismus.module.bubbler.common.animation.type.queue.BubbleQueue;
import me.whereareiam.socialismus.module.bubbler.common.renderer.BubbleRendererFactory;
import me.whereareiam.socialismus.service.Scheduler;

import java.util.Collection;
import java.util.List;

@Singleton
public final class StaticBubbleAnimation extends AbstractQueuedAnimation {
	@Inject
	public StaticBubbleAnimation(
			Scheduler scheduler,
			BubbleRendererFactory rendererFactory,
			EventManager eventManager
	) {
		super(scheduler, rendererFactory, eventManager);
	}

	@Override
	protected void playGroup(
			SocialismusPlayer sender,
			BubbleMessage msg,
			BubbleGroup group,
			BubbleQueue queue
	) {
		Bubble bubble = msg.getBubble();
		Collection<User> recipients = users(msg.getRecipients());
		float headGap = bubble.getDisplay().getHeadLineGap();
		float spacing = bubble.getDisplay().getLineSpacing();
		Position eyePos = sender.getEyePosition();

		// Fire BEFORE transition for first group
		if (queue.getCurrentGroupIndex() == 0) {
			fireTransition(TransitionType.BEFORE, msg);
		} else {
			// Fire INTER transition between groups
			fireTransition(TransitionType.INTER, msg);
		}

		BubbleRenderer renderer = getRenderer();
		List<RenderedLine> lines = renderer.getStrategy().spawnStaticGroup(
				bubble, group, headGap, spacing, eyePos, sender, recipients);

		long delayMs = group.calculateDisplayTime() * 1_000L;
		scheduler.schedule(
				DelayedRunnableTask.builder()
						.module("bubbler")
						.delay(delayMs)
						.runnable(() -> {
							renderer.destroy(lines, recipients);

							// Fire AFTER transition if this was the last group
							if (queue.isLastGroup()) {
								fireTransition(TransitionType.AFTER, msg);
							}

							queue.setProcessing(false);
							nextGroup(sender, queue);
						})
						.build()
		);
	}
}
