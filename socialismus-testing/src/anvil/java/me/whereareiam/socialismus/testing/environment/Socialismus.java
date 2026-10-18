package me.whereareiam.socialismus.testing.environment;

import me.whereareiam.anvil.integration.junit.AnvilEnvironment;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Consumer;

/**
 * Starts a fresh network with the packaged Socialismus around a test.
 *
 * <pre>{@code
 * @Test
 * @Socialismus(configure = GlobalChat.class)
 * void reachesEveryone(ScenarioContext anvil) {
 * }
 * }</pre>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@AnvilEnvironment(SocialismusScenarios.class)
public @interface Socialismus {
	/**
	 * Where Socialismus runs.
	 */
	Network network() default Network.PAPER;

	/**
	 * Whether the network installs the bundle jar instead of its platform's jar. The bundle carries Paper and
	 * Velocity.
	 */
	boolean bundle() default false;

	/**
	 * A directory of the test resources that Socialismus's data directory starts from, such as the files an earlier
	 * version left, or blank for an empty one.
	 */
	String data() default "";

	/**
	 * Plugins installed beside Socialismus in the {@code plugins/} of the process that runs it, such as one a module
	 * requires.
	 */
	Plugin[] plugins() default {};

	/**
	 * Modules installed in Socialismus's {@code modules/} directory.
	 */
	Module[] modules() default {};

	/**
	 * Altered module jars installed in {@code modules/} beside {@link #modules()}, for journeys about the module
	 * system.
	 */
	ModuleFixture[] moduleFixtures() default {};

	/**
	 * Files Socialismus starts with, applied in order after {@link #data()}. Each class needs a constructor without
	 * parameters.
	 */
	Class<? extends Consumer<ServerFiles>>[] configure() default {};
}
