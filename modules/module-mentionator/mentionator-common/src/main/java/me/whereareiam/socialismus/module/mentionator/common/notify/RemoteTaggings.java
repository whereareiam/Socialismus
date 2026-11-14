package me.whereareiam.socialismus.module.mentionator.common.notify;

import com.google.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * The {@link RemoteTagging} Mentionator uses, set while one is available, such as while SocialismusNetwork is
 * installed. Without one, tags name and notify only the players this server hosts.
 */
@Singleton
public class RemoteTaggings {
	private volatile RemoteTagging current;

	/**
	 * Uses a remote tagging from now on, or none.
	 *
	 * @param tagging the remote tagging, {@code null} for none
	 */
	public void use(@Nullable RemoteTagging tagging) {
		current = tagging;
	}

	/**
	 * Returns the remote tagging in use.
	 */
	public @NotNull Optional<RemoteTagging> current() {
		return Optional.ofNullable(current);
	}
}
