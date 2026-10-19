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
import java.util.ArrayList;
import java.util.List;

/**
 * One entry of a replicated cache as it travels between nodes and into a store: its key, the revision of its last
 * change, its value, or none when it was removed, and how much longer it lives.
 */
@Value
class EntryState {
	/**
	 * The entry's key.
	 */
	@NotNull String key;

	/**
	 * The revision of the change.
	 */
	@NotNull Revision revision;

	/**
	 * The encoded value, or {@code null} for a removal.
	 */
	byte @Nullable [] value;

	/**
	 * How many milliseconds the entry still lives, or {@code -1} for no expiry.
	 */
	long ttlMs;

	/**
	 * Returns whether the entry was removed.
	 *
	 * @return whether the change is a removal
	 */
	boolean isRemoval() {
		return value == null;
	}

	/**
	 * Returns the same change without an expiry, as a store keeps it, which tracks the expiry itself.
	 *
	 * @return the change without an expiry
	 */
	@NotNull EntryState withoutExpiry() {
		return new EntryState(key, revision, value, -1);
	}

	/**
	 * Encodes one entry, for a change.
	 *
	 * @return the entry as it travels
	 */
	byte @NotNull [] encode() {
		return encodeAll(List.of(this));
	}

	/**
	 * Decodes one entry of a change.
	 *
	 * @param data the encoded entry
	 * @return the entry
	 * @throws UncheckedIOException when the bytes are no entry
	 */
	static @NotNull EntryState decode(byte @NotNull [] data) {
		List<EntryState> states = decodeAll(data);
		if (states.size() != 1) throw new UncheckedIOException(new IOException("A change carries one entry"));

		return states.getFirst();
	}

	/**
	 * Encodes several entries, for a snapshot.
	 *
	 * @param states the entries
	 * @return the entries as they travel
	 */
	static byte @NotNull [] encodeAll(@NotNull List<EntryState> states) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try (DataOutputStream out = new DataOutputStream(bytes)) {
			out.writeInt(states.size());
			for (EntryState state : states) {
				out.writeUTF(state.key);
				out.writeLong(state.revision.getCounter());
				out.writeUTF(state.revision.getNode().value());
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
	 * @param data the encoded entries
	 * @return the entries
	 * @throws UncheckedIOException when the bytes are no entries
	 */
	static @NotNull List<EntryState> decodeAll(byte @NotNull [] data) {
		try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(data))) {
			int count = in.readInt();
			List<EntryState> states = new ArrayList<>(Math.max(0, count));
			for (int i = 0; i < count; i++) {
				String key = in.readUTF();
				Revision revision = new Revision(in.readLong(), NodeId.of(in.readUTF()));
				long ttlMs = in.readLong();
				byte[] value = in.readBoolean() ? readBytes(in) : null;
				states.add(new EntryState(key, revision, value, ttlMs));
			}

			return states;
		} catch (IOException | IllegalArgumentException | NegativeArraySizeException failure) {
			throw new UncheckedIOException(new IOException("Not a replicated entry", failure));
		}
	}

	private static byte[] readBytes(DataInputStream in) throws IOException {
		byte[] bytes = new byte[in.readInt()];
		in.readFully(bytes);

		return bytes;
	}
}
