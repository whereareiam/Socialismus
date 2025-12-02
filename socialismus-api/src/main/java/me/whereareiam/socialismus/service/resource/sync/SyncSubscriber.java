package me.whereareiam.socialismus.service.resource.sync;

public interface SyncSubscriber {
	void onMessage(String channel, byte[] body);
}