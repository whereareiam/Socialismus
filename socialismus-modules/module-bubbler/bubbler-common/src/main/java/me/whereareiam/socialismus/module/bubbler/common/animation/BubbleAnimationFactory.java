package me.whereareiam.socialismus.module.bubbler.common.animation;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleAnimation;
import me.whereareiam.socialismus.module.bubbler.api.type.AnimationType;
import me.whereareiam.socialismus.module.bubbler.common.animation.mode.queue.StaticBubbleAnimation;
import me.whereareiam.socialismus.module.bubbler.common.animation.mode.queue.stacked.ContractionBubbleAnimation;
import me.whereareiam.socialismus.module.bubbler.common.animation.mode.queue.stacked.ExpansionBubbleAnimation;
import me.whereareiam.socialismus.module.bubbler.common.animation.mode.queue.stacked.PopoutBubbleAnimation;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class BubbleAnimationFactory {
	private final Injector injector;

	public BubbleAnimation getAnimation(AnimationType animationType) {
		return switch (animationType) {
			case CONTRACTION -> injector.getInstance(ContractionBubbleAnimation.class);
			case EXPANSION -> injector.getInstance(ExpansionBubbleAnimation.class);
			case POPOUT -> injector.getInstance(PopoutBubbleAnimation.class);
			case STATIC -> injector.getInstance(StaticBubbleAnimation.class);
		};
	}
}
