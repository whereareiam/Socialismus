package me.whereareiam.socialismus.module.network.api.lease;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.whereareiam.socialismus.module.network.api.model.NodeId;
import org.jetbrains.annotations.NotNull;

/**
 * Who holds a key of {@link Leases}, and with which priority.
 */
@Getter
@Builder
@ToString
@EqualsAndHashCode
public final class Lease {
	/**
	 * The node that holds the key.
	 */
	private final @NotNull NodeId holder;

	/**
	 * The priority it claimed the key with; a claim of a higher priority takes the key over.
	 */
	private final int priority;
}
