package me.whereareiam.socialismus.module;

import lombok.Getter;
import me.whereareiam.socialismus.model.update.UpdateConfiguration;
import org.jetbrains.annotations.Nullable;

/**
 * Socialismus's own fields of a module's {@code module.yml}, beside the ones Chassis reads.
 */
@Getter
public final class ModuleFields {
	/**
	 * Where updates of the module come from, for releases and for development builds; {@code null} when it names none.
	 */
	private @Nullable UpdateConfiguration updater;
}
