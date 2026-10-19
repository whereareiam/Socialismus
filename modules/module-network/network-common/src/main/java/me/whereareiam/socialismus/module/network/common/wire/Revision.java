package me.whereareiam.socialismus.module.network.common.wire;

import me.whereareiam.socialismus.module.network.api.model.NodeId;
import org.jetbrains.annotations.NotNull;

/**
 * The version of a change to a replicated entry: a counter from the writer's {@link NetworkClock}, and the writer, which
 * decides between two changes with the same counter. A newer revision always wins, on every node alike.
 *
 * @param counter the writer's clock when it wrote
 * @param node    the writer
 */
public record Revision(long counter, @NotNull NodeId node) implements Comparable<Revision> {
	@Override
	public int compareTo(@NotNull Revision other) {
		int byCounter = Long.compare(counter, other.counter);

		return byCounter != 0 ? byCounter : node.compareTo(other.node);
	}

	/**
	 * Returns whether this revision is newer than another; any revision is newer than none.
	 */
	public boolean isNewerThan(Revision other) {
		return other == null || compareTo(other) > 0;
	}
}
