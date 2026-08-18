package me.whereareiam.socialismus.module.bubbler.common.listener;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.event.EventListener;
import me.whereareiam.socialismus.event.base.SocialisticEvent;
import me.whereareiam.socialismus.module.bubbler.api.event.BubbleTransitionEvent;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleTransition;

@Singleton
public class BubbleTransitionListener implements EventListener {

	@SocialisticEvent
	public void onBubbleTransition(BubbleTransitionEvent event) {
		BubbleTransition.Sound sound = event.getTransition().getSound();
		if (sound == null) return;

		event.getBubbleMessage().getRecipients().forEach(player ->
				player.playSound(sound.getType(), sound.getVolume(), sound.getPitch())
		);
	}
}
