package me.whereareiam.socialismus.module.bubbler.common.config.defaults;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerSettings;
import me.whereareiam.socialismus.module.bubbler.api.type.BubbleType;

@Singleton
public class BubblerSettingsDefaults implements DefaultsProvider<BubblerSettings> {
	@Override
	public BubblerSettings supply(BubblerSettings config) {
		config.setBubbleType(BubbleType.TEXT_DISPLAY);
		config.setMinRecipients(1);
		config.setMaxQueueSize(30);

		BubblerSettings.Notify notify = new BubblerSettings.Notify();
		notify.setNotifyNoPlayers(true);
		notify.setNotifyNoNearbyPlayers(true);
		notify.setNotifyNoBubbleSelected(false);

		config.setNotify(notify);

		BubblerSettings.Animation animation = getAnimation();

		config.setAnimation(animation);

		return config;
	}

	private static BubblerSettings.Animation getAnimation() {
		BubblerSettings.Animation animation = new BubblerSettings.Animation();
		animation.setPopoutDelay(500);

		BubblerSettings.Animation.Expansion expansion = new BubblerSettings.Animation.Expansion();
		expansion.setStartScale(0.5F);
		expansion.setDuration(400);
		animation.setExpansion(expansion);

		BubblerSettings.Animation.Contraction contraction = new BubblerSettings.Animation.Contraction();
		contraction.setEndScale(0.5F);
		contraction.setDuration(400);
		animation.setContraction(contraction);

		return animation;
	}
}
