package me.whereareiam.socialismus.module.network.common.replication;

import lombok.Value;
import me.whereareiam.socialismus.module.network.api.model.NodeId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * One message between nodes, as it travels over the transport: who sent it, what kind it is, which cache, channel or
 * leases it belongs to, and its body.
 */
@Value
class Envelope {
	/**
	 * The version of the wire format; a node ignores what another version sends.
	 */
	static final int PROTOCOL = 1;

	/**
	 * The node that sent it.
	 */
	@NotNull NodeId sender;

	/**
	 * What it is.
	 */
	@NotNull Kind kind;

	/**
	 * The cache or channel it belongs to; empty for a message of the network itself.
	 */
	@NotNull String name;

	/**
	 * What it carries, encoded by its kind.
	 */
	byte @NotNull [] body;

	/**
	 * Encodes the envelope.
	 *
	 * @return the envelope as it travels
	 */
	byte @NotNull [] encode() {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream(body.length + 64);
		try (DataOutputStream out = new DataOutputStream(bytes)) {
			out.writeInt(PROTOCOL);
			out.writeUTF(sender.value());
			out.writeByte(kind.ordinal());
			out.writeUTF(name);
			out.writeInt(body.length);
			out.write(body);
		} catch (IOException failure) {
			throw new UncheckedIOException(failure);
		}

		return bytes.toByteArray();
	}

	/**
	 * Decodes an envelope.
	 *
	 * @param data the bytes that arrived
	 * @return the envelope, or {@code null} when the bytes are no envelope of this protocol
	 */
	static @Nullable Envelope decode(byte @NotNull [] data) {
		try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(data))) {
			if (in.readInt() != PROTOCOL) return null;

			NodeId sender = NodeId.of(in.readUTF());
			int kind = in.readUnsignedByte();
			if (kind >= Kind.values().length) return null;

			String name = in.readUTF();
			byte[] body = new byte[in.readInt()];
			in.readFully(body);

			return new Envelope(sender, Kind.values()[kind], name, body);
		} catch (IOException | IllegalArgumentException | NegativeArraySizeException failure) {
			return null;
		}
	}

	/**
	 * What an envelope carries.
	 */
	enum Kind {
		/** A change to one entry of a cache. */
		CHANGE,
		/** A node asks the others for every entry of every cache, after it started or reconnected. */
		SNAPSHOT_REQUEST,
		/** The entries of one cache, answering a request. */
		SNAPSHOT,
		/** A message of a channel. */
		MESSAGE
	}
}
