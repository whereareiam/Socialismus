package me.whereareiam.socialismus.module.network.api.lease;

import lombok.Builder;
import lombok.Value;
import me.whereareiam.socialismus.module.network.api.model.NodeId;
import org.jetbrains.annotations.NotNull;

/**
 * Who holds a key of {@link Leases}, and with which priority.
 */
@Value
@Builder
public class Lease {
	/**
	 * The node that holds the key.
	 */
	@NotNull NodeId holder;

	/**
	 * The priority it claimed the key with; a claim of a higher priority takes the key over.
	 */
	int priority;
}
