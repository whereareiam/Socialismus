package me.whereareiam.socialismus.module.network.common.replication;

import lombok.Value;
import me.whereareiam.socialismus.module.network.api.model.NodeId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The version of a change to a replicated entry: a counter from the writer's {@link NetworkClock}, and the writer, which
 * decides between two changes with the same counter. A newer revision always wins, on every node alike.
 */
@Value
class Revision implements Comparable<Revision> {
	/**
	 * The writer's clock when it wrote.
	 */
	long counter;

	/**
	 * The writer.
	 */
	@NotNull NodeId node;

	@Override
	public int compareTo(@NotNull Revision other) {
		int byCounter = Long.compare(counter, other.counter);

		return byCounter != 0 ? byCounter : node.compareTo(other.node);
	}

	/**
	 * Returns whether this revision is newer than another; any revision is newer than none.
	 *
	 * @param other the other revision, or {@code null} for none
	 * @return whether this one wins
	 */
	boolean isNewerThan(@Nullable Revision other) {
		return other == null || compareTo(other) > 0;
	}
}
