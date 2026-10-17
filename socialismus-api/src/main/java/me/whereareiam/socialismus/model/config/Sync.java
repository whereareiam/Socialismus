package me.whereareiam.socialismus.model.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.configura.feature.postprocess.api.PostProcess;
import me.whereareiam.socialismus.Constants;

/**
 * How this server takes part in the network, {@code sync} in the data directory: whether it syncs with other
 * servers through SocialismusRedis or SocialismusChannelizer, and under which name. What chats do when synced is in
 * {@code chat/sync}.
 */
@Getter
@Setter
@ToString
public class Sync {
	/**
	 * Whether this server syncs with the others.
	 */
	private boolean enabled;

	/**
	 * The identifier this server uses on the network.
	 */
	private String server;

	/**
	 * Whether a proxy uses the real names of its servers instead of {@link #server}.
	 */
	private boolean useRealServerName;

	/**
	 * Whether players are synced across servers.
	 */
	private boolean crossPlayerSync;

	@PostProcess
	public void applyConstants() {
		Constants.Synchronization.IDENTIFIER = server;
		Constants.Synchronization.SYNCHRONIZATION = enabled;
		Constants.Synchronization.CROSS_PLAYER_SYNC = crossPlayerSync;
	}
}
