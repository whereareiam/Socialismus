package me.whereareiam.socialismus.module.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.module.SocialisticModule;
import me.whereareiam.socialismus.module.type.ModuleState;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * Represents an internal module configuration with additional runtime properties.
 * Extends the base {@link Module} class to add functionality specific to the
 * plugin's module handling system.
 * <p>
 * This class maintains module state, file path, and the associated module instance,
 * providing essential information for module management during runtime.
 */
@Getter
@Setter
@ToString
@SuperBuilder(toBuilder = true)
public class InternalModule extends Module {
	/**
	 * The file system path to the module
	 */
	private Path path;

	/**
	 * The loaded module instance
	 */
	private SocialisticModule module;

	/**
	 * The current state of the module
	 */
	private ModuleState state;

	/**
	 * Creates the runtime state of a module read from its jar, keeping everything its {@code module.json} says.
	 *
	 * @param module the module as its {@code module.json} describes it
	 * @param path   the module's jar
	 * @return the module, not yet loaded
	 */
	public static @NotNull InternalModule of(@NotNull Module module, @NotNull Path path) {
		return InternalModule.builder()
				.name(module.getName())
				.version(module.getVersion())
				.authors(module.getAuthors())
				.socialismus(module.getSocialismus())
				.platforms(module.getPlatforms())
				.dependencies(module.getDependencies())
				.requirements(module.getRequirements())
				.updater(module.getUpdater())
				.exports(module.getExports())
				.main(module.getMain())
				.path(path)
				.state(ModuleState.UNKNOWN)
				.build();
	}
}
