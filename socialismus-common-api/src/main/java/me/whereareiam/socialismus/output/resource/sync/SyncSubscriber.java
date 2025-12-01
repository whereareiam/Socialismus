package me.whereareiam.socialismus.output.resource.sync;

public interface SyncSubscriber {
	void onMessage(String channel, byte[] body);
}