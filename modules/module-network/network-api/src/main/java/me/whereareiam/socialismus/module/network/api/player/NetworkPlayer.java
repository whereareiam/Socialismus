package me.whereareiam.socialismus.module.network.api.player;

import lombok.Getter;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.position.Position;
import me.whereareiam.socialismus.module.network.api.model.NodeId;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * A player on another node of the network, as this node knows them: who they are, which node hosts them and which
 * server they are on. It holds data only: nothing can be sent to them from here, they hold no permissions here and are
 * in no world of this server. What reaches them goes through the node that hosts them.
 */
public final class NetworkPlayer extends SocialismusPlayer {
	/**
	 * The node that hosts the player.
	 */
	@Getter
	private final @NotNull NodeId node;
	private String server;

	public NetworkPlayer(@NotNull UUID uniqueId, @NotNull String username, @NotNull NodeId node, @Nullable String server) {
		super(uniqueId, username, false);
		this.node = node;
		this.server = server;
	}

	@Override
	public void sendMessage(@NotNull Component message) {
		// Reaches the player only through the node that hosts them.
	}

	@Override
	public boolean hasPermission(@NotNull String permission) {
		return false;
	}

	@Override
	public @NotNull Audience getAudience() {
		return Audience.empty();
	}

	@Override
	public @Nullable String getLocation() {
		return null;
	}

	@Override
	public @Nullable String getServer() {
		return server;
	}

	@Override
	public void setServer(@Nullable String server) {
		this.server = server;
	}

	@Override
	public @Nullable Position getPosition() {
		return null;
	}

	@Override
	public @Nullable Position getEyePosition() {
		return null;
	}

	@Override
	public boolean isWithinRange(@NotNull SocialismusPlayer other, double range) {
		return false;
	}

	@Override
	public void playSound(@NotNull String sound, float volume, float pitch) {
		// Reaches the player only through the node that hosts them.
	}
}
