package me.whereareiam.socialismus.module.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.module.SocialisticModule;
import me.whereareiam.socialismus.module.type.ModuleState;

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
}
