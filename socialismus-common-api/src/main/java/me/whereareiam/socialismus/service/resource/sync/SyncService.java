package me.whereareiam.socialismus.service.resource.sync;

public interface SyncService {
	void publish(String channel, byte[] payload);

	void subscribe(String channel, SyncSubscriber listener);
}