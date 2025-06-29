package me.whereareiam.socialismus.api.output.resource.sync;

public interface SyncService {
	void publish(String channel, byte[] payload);

	void subscribe(String channel, SyncSubscriber listener);
}