package me.whereareiam.socialismus.module.network.common.wire;

import me.whereareiam.socialismus.module.network.api.model.NodeId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

/**
 * One entry of a replicated cache as it travels between nodes and into a store: its key, the revision of its last
 * change, its value, or none when it was removed, and how much longer it lives.
 *
 * @param key     the entry's key
 * @param revision the revision of the change
 * @param value   the encoded value, or {@code null} for a removal
 * @param ttlMs   how many milliseconds the entry still lives, or {@code -1} for no expiry
 */
public record EntryState(@NotNull String key, @NotNull Revision revision, byte @Nullable [] value, long ttlMs) {
	/**
	 * Returns whether the entry was removed.
	 */
	public boolean isRemoval() {
		return value == null;
	}

	/**
	 * Encodes one entry, for a change.
	 */
	public byte @NotNull [] encode() {
		return encodeAll(List.of(this));
	}

	/**
	 * Decodes one entry of a change.
	 *
	 * @throws UncheckedIOException when the bytes are no entry
	 */
	public static @NotNull EntryState decode(byte @NotNull [] data) {
		List<EntryState> states = decodeAll(data);
		if (states.size() != 1) throw new UncheckedIOException(new IOException("A change carries one entry"));

		return states.getFirst();
	}

	/**
	 * Encodes several entries, for a snapshot.
	 */
	public static byte @NotNull [] encodeAll(@NotNull List<EntryState> states) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try (DataOutputStream out = new DataOutputStream(bytes)) {
			out.writeInt(states.size());
			for (EntryState state : states) {
				out.writeUTF(state.key);
				out.writeLong(state.revision.counter());
				out.writeUTF(state.revision.node().value());
				out.writeLong(state.ttlMs);
				out.writeBoolean(state.value != null);
				if (state.value == null) continue;

				out.writeInt(state.value.length);
				out.write(state.value);
			}
		} catch (IOException failure) {
			throw new UncheckedIOException(failure);
		}

		return bytes.toByteArray();
	}

	/**
	 * Decodes the entries of a snapshot.
	 *
	 * @throws UncheckedIOException when the bytes are no entries
	 */
	public static @NotNull List<EntryState> decodeAll(byte @NotNull [] data) {
		try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(data))) {
			int count = in.readInt();
			List<EntryState> states = new ArrayList<>(Math.max(0, count));
			for (int i = 0; i < count; i++) {
				String key = in.readUTF();
				Revision revision = new Revision(in.readLong(), NodeId.of(in.readUTF()));
				long ttlMs = in.readLong();
				byte[] value = null;
				if (in.readBoolean()) {
					value = new byte[in.readInt()];
					in.readFully(value);
				}
				states.add(new EntryState(key, revision, value, ttlMs));
			}

			return states;
		} catch (IOException | IllegalArgumentException | NegativeArraySizeException failure) {
			throw new UncheckedIOException(new IOException("Not a replicated entry", failure));
		}
	}
}
