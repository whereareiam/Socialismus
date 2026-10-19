package me.whereareiam.socialismus.module.network.common.replication;

import me.whereareiam.socialismus.module.network.api.model.NodeId;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class EnvelopeTest {
	@Test
	void anEnvelopeArrivesAsItWasSent() {
		Envelope sent = new Envelope(NodeId.of("lobby"), Envelope.Kind.MESSAGE, "socialismus:chat", new byte[]{1, 2, 3});

		Envelope received = Envelope.decode(sent.encode());

		assertNotNull(received);
		assertEquals(sent.getSender(), received.getSender());
		assertEquals(Envelope.Kind.MESSAGE, received.getKind());
		assertEquals("socialismus:chat", received.getName());
		assertArrayEquals(new byte[]{1, 2, 3}, received.getBody());
	}

	@Test
	void bytesOfAnotherProtocolOrNoEnvelopeAreIgnored() throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try (DataOutputStream out = new DataOutputStream(bytes)) {
			out.writeInt(Envelope.PROTOCOL + 1);
		}

		assertNull(Envelope.decode(bytes.toByteArray()));
		assertNull(Envelope.decode(new byte[]{1}));
	}
}
