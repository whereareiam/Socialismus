package me.whereareiam.socialismus.module.chirper.common.config.defaults;

import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.module.chirper.api.model.announcer.Announcer;
import me.whereareiam.socialismus.module.chirper.api.type.OrderType;
import me.whereareiam.socialismus.module.chirper.common.config.dynamic.AnnouncersConfig;

import java.util.List;

public class AnnouncersDefaults implements DefaultsProvider<AnnouncersConfig> {
	@Override
	public AnnouncersConfig supply(AnnouncersConfig config) {
		Announcer example = Announcer.builder()
				.enabled(true)
				.announcements(List.of(
						"example0",
						"bossBarExample0",
						"bossBarExample1",
						"bossBarExample2",
						"titleExample0",
						"titleExample1"
				))
				.settings(Announcer.AnnouncerSettings.builder()
						.interval(300)
						.delay(0)
						.order(OrderType.DESCENDING)
						.build())
				.build();

		config.getAnnouncers().add(example);

		return config;
	}
}
