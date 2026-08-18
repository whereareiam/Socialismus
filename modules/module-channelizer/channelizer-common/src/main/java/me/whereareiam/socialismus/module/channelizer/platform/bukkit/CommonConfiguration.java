package me.whereareiam.socialismus.module.channelizer.platform.bukkit;

import com.google.inject.AbstractModule;
import me.whereareiam.socialismus.module.channelizer.platform.bukkit.service.ChannelSyncService;
import me.whereareiam.socialismus.service.resource.sync.SyncService;

public class CommonConfiguration extends AbstractModule {
	@Override
	protected void configure() {
		bind(SyncService.class).to(ChannelSyncService.class);
	}
}
