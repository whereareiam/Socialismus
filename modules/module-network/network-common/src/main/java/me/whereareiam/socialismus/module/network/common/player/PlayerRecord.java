package me.whereareiam.socialismus.module.network.common.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.socialismus.module.network.api.type.NodeRole;

/**
 * A player as the node that hosts them announces them.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlayerRecord {
	private String uniqueId;
	private String name;
	private String node;
	private NodeRole role;
	private String server;
}
