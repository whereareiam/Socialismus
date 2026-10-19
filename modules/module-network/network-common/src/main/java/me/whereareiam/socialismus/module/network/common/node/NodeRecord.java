package me.whereareiam.socialismus.module.network.common.node;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.socialismus.module.network.api.model.NetworkNode;
import me.whereareiam.socialismus.module.network.api.model.NodeId;
import me.whereareiam.socialismus.module.network.api.type.NodeRole;

/**
 * A node's announcement as it is replicated.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NodeRecord {
	private String id;
	private NodeRole role;
	private String server;
	private String version;

	static NodeRecord of(NetworkNode node) {
		return new NodeRecord(node.getId().value(), node.getRole(), node.getServer(), node.getVersion());
	}

	NetworkNode toNode() {
		return NetworkNode.builder()
				.id(NodeId.of(id))
				.role(role)
				.server(server)
				.version(version)
				.build();
	}
}
