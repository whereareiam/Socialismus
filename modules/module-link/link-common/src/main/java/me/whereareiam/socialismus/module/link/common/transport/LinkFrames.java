package me.whereareiam.socialismus.module.link.common.transport;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * The frames of the link: a length, then that many bytes. A message frame holds its channel and the message.
 */
final class LinkFrames {
	/**
	 * The largest frame the link carries, in bytes.
	 */
	static final int MAX_FRAME = 16 * 1024 * 1024;

	private LinkFrames() {
	}

	static void write(@NotNull DataOutputStream out, byte @NotNull [] frame) throws IOException {
		out.writeInt(frame.length);
		out.write(frame);
		out.flush();
	}

	static byte @NotNull [] read(@NotNull DataInputStream in) throws IOException {
		int length = in.readInt();
		if (length < 0 || length > MAX_FRAME) throw new IOException("A frame of " + length + " bytes is not allowed");

		byte[] frame = new byte[length];
		in.readFully(frame);
		return frame;
	}

	static byte @NotNull [] message(@NotNull String channel, byte @NotNull [] message) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream(message.length + channel.length() + 8);
		try (DataOutputStream out = new DataOutputStream(bytes)) {
			out.writeUTF(channel);
			out.write(message);
		} catch (IOException impossible) {
			throw new IllegalStateException(impossible);
		}

		return bytes.toByteArray();
	}

	/**
	 * A message frame read back: its channel and the message.
	 */
	record Message(@NotNull String channel, byte @NotNull [] payload) {
		static @NotNull Message decode(byte @NotNull [] frame) throws IOException {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(frame));
			String channel = in.readUTF();
			return new Message(channel, in.readAllBytes());
		}
	}
}
