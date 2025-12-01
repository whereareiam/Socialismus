package me.whereareiam.socialismus.output.resource.sync;

public interface SyncService {
	void publish(String channel, byte[] payload);

	void subscribe(String channel, SyncSubscriber listener);
}