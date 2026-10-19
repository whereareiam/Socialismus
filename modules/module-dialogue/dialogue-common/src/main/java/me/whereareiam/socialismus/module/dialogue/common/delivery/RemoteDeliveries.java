package me.whereareiam.socialismus.module.dialogue.common.delivery;

import com.google.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * The {@link RemoteDelivery} Dialogue uses, set while one is available, such as while SocialismusNetwork is installed.
 * Without one, a private message reaches only the players this server hosts.
 */
@Singleton
public class RemoteDeliveries {
	private volatile RemoteDelivery current;

	/**
	 * Uses a remote delivery from now on, or none.
	 */
	public void use(@Nullable RemoteDelivery delivery) {
		current = delivery;
	}

	/**
	 * Returns the remote delivery in use.
	 */
	public @NotNull Optional<RemoteDelivery> current() {
		return Optional.ofNullable(current);
	}
}
