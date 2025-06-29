package me.whereareiam.socialismus.api.output.resource.sync;

public interface SyncSubscriber {
	void onMessage(String channel, byte[] body);
}