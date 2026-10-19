package me.whereareiam.socialismus.module.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.type.PlatformType;

/**
 * A server platform a module runs on, as {@code module.json} lists it under {@code platforms}:
 *
 * <pre>{@code
 * "platforms": [
 *   {"type": "PAPER", "version": "26\\.1(\\..*)?"},
 *   {"type": "FOLIA", "version": "26\\.1(\\..*)?"},
 *   {"type": "VELOCITY"}
 * ]
 * }</pre>
 *
 * A module loads on any one of them. The detected platform is exact: a fork of Paper counts as {@code PAPER}, while
 * Folia counts only as {@code FOLIA}, so a module runs on Folia only once it lists it.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class ModulePlatform {
	/**
	 * The platform.
	 */
	private PlatformType type;

	/**
	 * The versions of the platform accepted, as a regular expression the whole version must match; any version when
	 * absent. A game server's version is the game's, such as {@code 26.1.2}; a proxy's is its own, such as Velocity's
	 * {@code 3.4.0}.
	 */
	private String version;
}
