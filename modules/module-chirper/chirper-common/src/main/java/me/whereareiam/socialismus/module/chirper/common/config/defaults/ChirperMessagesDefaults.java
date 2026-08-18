package me.whereareiam.socialismus.module.chirper.common.config.defaults;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.module.chirper.api.model.config.ChirperMessages;

@Singleton
public class ChirperMessagesDefaults implements DefaultsProvider<ChirperMessages> {
	@Override
	public ChirperMessages supply(ChirperMessages config) {
		config.setNoPlayers("{prefix}<white>There are no players online to send the announcement to.");
		config.setNoAnnouncementFound("{prefix}<white>Announcement with id <red>{id}<white> not found.");
		config.setAnnouncementBroadcasted("{prefix}<white>Announcement <red>{id}<white> has been broadcasted.");

		return config;
	}
}
