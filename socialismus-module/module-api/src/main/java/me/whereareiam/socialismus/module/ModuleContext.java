package me.whereareiam.socialismus.module;

import me.whereareiam.chassis.ExtensionContext;
import me.whereareiam.chassis.adapter.attache.LibraryGroups;
import me.whereareiam.conditia.ConditionScope;
import org.jetbrains.annotations.NotNull;

/**
 * What a module gets when it is enabled: its descriptor, its own data folder {@code modules/<id>/}, its logger and
 * services, the optional library groups its build declares, and its scope of condition types.
 */
public interface ModuleContext extends ExtensionContext {
	/**
	 * Returns the optional library groups the module declares, to load one once it knows it needs it, such as the
	 * driver of the configured database.
	 *
	 * @return the module's library groups
	 */
	@NotNull LibraryGroups libraries();

	/**
	 * Returns the module's scope of condition types and named lists. A type registered here is named
	 * {@code <module id>:<name>} in configs, such as {@code bubbler:activator}, and goes when the module stops; a list
	 * that still names it then denies everyone until the module is enabled again.
	 *
	 * <pre>{@code
	 * context.conditions().register(new ActivatorCondition());
	 * }</pre>
	 *
	 * @return the module's scope
	 */
	@NotNull ConditionScope conditions();
}
