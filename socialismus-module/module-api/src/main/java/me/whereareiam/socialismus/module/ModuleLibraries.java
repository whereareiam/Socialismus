package me.whereareiam.socialismus.module;

import org.jetbrains.annotations.NotNull;

/**
 * The libraries a module declares with Attache in its own build, loaded into the module's class loader.
 *
 * <p>Socialismus loads the libraries a module declares outside any group before it creates the module's main
 * class. Libraries that only some installations need, such as the driver of one database, go in a group, and the
 * module loads the group once it knows it needs it:
 * <pre>{@code
 * // build.gradle.kts of the module
 * attache {
 *     group("postgres") { library(libs.postgresql) }
 *     group("sqlite") { library(libs.sqlite) }
 * }
 *
 * // the module
 * libraries.loadGroups(settings.getType().name().toLowerCase(Locale.ROOT));
 * }</pre>
 *
 * <p>The libraries stay in the module's class loader. Other modules reach one only when this module
 * {@linkplain me.whereareiam.socialismus.module.model.Module#getExports() exports} its packages and they depend on
 * this module, such as the database client a database module shares with the modules that use its API.
 */
public interface ModuleLibraries {
	/**
	 * Downloads the libraries the module declares in the given groups and adds them to its class loader. A library
	 * that is already loaded, or that a parent class loader provides, is skipped, so a group may be asked for more
	 * than once.
	 *
	 * @param groups the names of the groups
	 * @throws IllegalArgumentException when the module declares none of the groups' libraries under one of the names
	 */
	void loadGroups(@NotNull String... groups);
}
