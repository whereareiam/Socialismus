package me.whereareiam.socialismus.module.bubbler.common.animation.mode.queue.stacked;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.Bubble;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerSettings;
import me.whereareiam.socialismus.module.bubbler.common.renderer.BubbleRendererFactory;
import me.whereareiam.socialismus.service.Scheduler;

@Singleton
public final class ExpansionBubbleAnimation extends StackedBubbleAnimation {
	@Inject
	public ExpansionBubbleAnimation(
			Scheduler scheduler,
			BubbleRendererFactory rendererFactory,
			Provider<BubblerSettings> settings,
			EventManager eventManager
	) {
		super(scheduler, rendererFactory, settings, eventManager);
	}

	@Override
	protected boolean useSpawnAnimation() {
		return true;
	}

	@Override
	protected boolean useRemovalAnimation() {
		return false;
	}

	@Override
	protected long extraSpawnDelayMs(Bubble bubble) {
		return tickMs() + settings.get().getAnimation().getExpansion().getDuration();
	}
}
