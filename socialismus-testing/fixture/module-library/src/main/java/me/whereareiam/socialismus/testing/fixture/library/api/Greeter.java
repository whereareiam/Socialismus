package me.whereareiam.socialismus.testing.fixture.library.api;

/**
 * What JourneyLibrary offers the modules that depend on it.
 */
public final class Greeter {
	private static volatile boolean serving;

	private Greeter() {
	}

	/**
	 * Returns the library's greeting.
	 */
	public static String greet() {
		return "hello from JourneyLibrary";
	}

	/**
	 * Returns whether the library is enabled.
	 */
	public static boolean isServing() {
		return serving;
	}

	/**
	 * Sets whether the library is enabled.
	 */
	public static void serve(boolean serving) {
		Greeter.serving = serving;
	}
}
