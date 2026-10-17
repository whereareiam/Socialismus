package me.whereareiam.socialismus.model.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Duration;

/**
 * The update checker, {@code updater} in the data directory: whether and how often Socialismus and its modules
 * look for new versions, and what they warn about.
 */
@Getter
@Setter
@ToString
public class Updater {
	/**
	 * Whether to look for new versions.
	 */
	private boolean check;

	/**
	 * How often to look, such as {@code "1h"}; zero turns the checks off.
	 */
	private Duration interval;

	/**
	 * What to warn about.
	 */
	private Warn warn;

	/**
	 * What the update checker warns about.
	 */
	@Getter
	@Setter
	@ToString
	public static class Warn {
		/**
		 * Whether to warn about a newer release.
		 */
		private boolean updates;

		/**
		 * Whether to warn when running a local build.
		 */
		private boolean localBuilds;

		/**
		 * Whether to warn when running a development build.
		 */
		private boolean devBuilds;
	}
}
