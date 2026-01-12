package me.whereareiam.socialismus.model.player;

import me.whereareiam.socialismus.model.position.Position;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Lightweight SocialismusPlayer implementation used for cross-server sync.
 * This player is data-only and does not support messaging or permissions.
 */
public class SyncedSocialismusPlayer extends SocialismusPlayer {
	private String server;
	private String location;
	private Position position;
	private Position eyePosition;

	public SyncedSocialismusPlayer(@NotNull UUID uniqueId, @NotNull String username) {
		super(uniqueId, username);
	}

	public SyncedSocialismusPlayer(
			@NotNull UUID uniqueId,
			@NotNull String username,
			@Nullable String server,
			@Nullable String location,
			@Nullable Position position,
			@Nullable Position eyePosition
	) {
		super(uniqueId, username);
		this.server = server;
		this.location = location;
		this.position = position;
		this.eyePosition = eyePosition;
	}

	@Override
	public void sendMessage(@NotNull Component message) {
		// No-op for synced players
	}

	@Override
	public boolean hasPermission(@NotNull String permission) {
		return false;
	}

	@Override
	@NotNull
	public Audience getAudience() {
		return Audience.empty();
	}

	@Override
	@Nullable
	public String getLocation() {
		return location;
	}

	@Override
	public void setLocation(@Nullable String location) {
		this.location = location;
	}

	@Override
	@Nullable
	public String getServer() {
		return server;
	}

	@Override
	public void setServer(@Nullable String server) {
		this.server = server;
	}

	@Override
	@Nullable
	public Position getPosition() {
		return position;
	}

	@Override
	@Nullable
	public Position getEyePosition() {
		return eyePosition;
	}

	@Override
	public boolean isWithinRange(@NotNull SocialismusPlayer other, double range) {
		String server = normalize(getServer());
		String otherServer = normalize(other.getServer());
		if (server != null || otherServer != null) {
			if (server == null || otherServer == null) return false;
			if (!server.equals(otherServer)) return false;
		} else {
			String location = normalize(getLocation());
			String otherLocation = normalize(other.getLocation());
			if (location == null || otherLocation == null) return false;
			if (!location.equals(otherLocation)) return false;
		}

		Position origin = getPosition();
		Position target = other.getPosition();
		if (origin == null || target == null) return false;

		double dx = origin.getX() - target.getX();
		double dy = origin.getY() - target.getY();
		double dz = origin.getZ() - target.getZ();

		return dx * dx + dy * dy + dz * dz <= range * range;
	}

	@Override
	public void playSound(@NotNull String sound, float volume, float pitch) {
		// No-op for synced players
	}

	private String normalize(String value) {
		if (value == null) return null;
		String trimmed = value.trim();

		return trimmed.isEmpty() ? null : trimmed;
	}
}
