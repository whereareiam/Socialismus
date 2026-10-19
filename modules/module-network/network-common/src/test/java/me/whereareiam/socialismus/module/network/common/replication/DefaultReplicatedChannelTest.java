package me.whereareiam.socialismus.module.network.common.replication;

import me.whereareiam.socialismus.module.network.api.replication.ReplicatedChannel;
import me.whereareiam.socialismus.module.network.common.TestNetwork;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultReplicatedChannelTest {
	private final TestNetwork network = new TestNetwork();

	@Test
	void aMessageReachesTheOtherNodesButNotItsSender() {
		ReplicatedChannel<String> a = network.node("a").service().replication().channel("test:chat", String.class);
		ReplicatedChannel<String> b = network.node("b").service().replication().channel("test:chat", String.class);
		List<String> atA = new ArrayList<>();
		List<String> atB = new ArrayList<>();
		a.subscribe((message, origin) -> atA.add(message));
		b.subscribe((message, origin) -> atB.add(message + "@" + origin));

		a.publish("hello");

		assertEquals(List.of(), atA);
		assertEquals(List.of("hello@remote:a"), atB);
	}
}
