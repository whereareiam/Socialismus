package me.whereareiam.socialismus.module.provider.sync;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.service.resource.sync.SyncService;
import me.whereareiam.socialismus.service.resource.sync.SyncSubscriber;

/**
 * A do-nothing implementation used when sync is off.
 */
@Singleton
public final class DummySyncService implements SyncService {
	@Override
	public void publish(String ch, byte[] data) {
	}

	@Override
	public void subscribe(String channel, SyncSubscriber listener) {
	}
}
