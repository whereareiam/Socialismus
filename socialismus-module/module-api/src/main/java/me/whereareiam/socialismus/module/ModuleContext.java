package me.whereareiam.socialismus.module;

import me.whereareiam.chassis.ExtensionContext;
import me.whereareiam.chassis.adapter.attache.LibraryGroups;
import org.jetbrains.annotations.NotNull;

/**
 * What a module gets when it is enabled: its descriptor, its own data folder {@code modules/<id>/}, its logger and
 * services, and the optional library groups its build declares.
 */
public interface ModuleContext extends ExtensionContext {
	/**
	 * Returns the optional library groups the module declares, to load one once it knows it needs it, such as the
	 * driver of the configured database.
	 *
	 * @return the module's library groups
	 */
	@NotNull LibraryGroups libraries();
}
