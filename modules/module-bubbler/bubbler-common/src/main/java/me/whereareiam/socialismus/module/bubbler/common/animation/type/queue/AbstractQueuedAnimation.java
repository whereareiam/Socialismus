package me.whereareiam.socialismus.module.bubbler.common.animation.type.queue;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import lombok.NonNull;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.bubbler.api.event.BubbleTransitionEvent;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleAnimation;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleGroup;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleTransition;
import me.whereareiam.socialismus.module.bubbler.api.renderer.BubbleRenderer;
import me.whereareiam.socialismus.module.bubbler.api.type.TransitionType;
import me.whereareiam.socialismus.module.bubbler.common.renderer.BubbleRendererFactory;
import me.whereareiam.socialismus.service.Scheduler;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class AbstractQueuedAnimation extends BubbleAnimation {

	private final Map<SocialismusPlayer, BubbleQueue> queues = new ConcurrentHashMap<>();
	protected final BubbleRendererFactory rendererFactory;
	protected final EventManager eventManager;

	protected AbstractQueuedAnimation(Scheduler scheduler, BubbleRendererFactory rendererFactory, EventManager eventManager) {
		super(scheduler);
		this.rendererFactory = rendererFactory;
		this.eventManager = eventManager;
	}

	@Override
	public final void display(@NonNull BubbleMessage message) {
		SocialismusPlayer sender = message.getSender();
		BubbleQueue queue = queues.computeIfAbsent(sender, $ -> new BubbleQueue());

		queue.addMessage(message);

		if (!queue.isProcessing()) {
			nextGroup(sender, queue);
		}
	}

	protected final void nextGroup(SocialismusPlayer sender, BubbleQueue queue) {
		queue.processNextGroup(
				(msg, grp) -> playGroup(sender, msg, grp, queue),
				() -> nextGroup(sender, queue)
		);
	}

	protected abstract void playGroup(
			SocialismusPlayer sender,
			BubbleMessage message,
			BubbleGroup group,
			BubbleQueue queue
	);

	protected BubbleRenderer getRenderer() {
		return rendererFactory.getRenderer();
	}

	protected User user(SocialismusPlayer player) {
		return PacketEvents.getAPI()
				.getPlayerManager()
				.getUser(player.getAudience());
	}

	protected Collection<User> users(Collection<SocialismusPlayer> players) {
		return players.stream()
				.map(this::user)
				.collect(Collectors.toList());
	}

	protected void fireTransition(TransitionType type, BubbleMessage message) {
		BubbleTransition transition = message.getBubble().getTransitions().get(type);
		if (transition != null) {
			eventManager.call(new BubbleTransitionEvent(type, message));
		}
	}
}
