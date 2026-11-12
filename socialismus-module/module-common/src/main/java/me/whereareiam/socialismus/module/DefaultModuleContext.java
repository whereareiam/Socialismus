package me.whereareiam.socialismus.module;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import me.whereareiam.chassis.ExtensionContext;
import me.whereareiam.chassis.adapter.attache.LibraryGroups;
import me.whereareiam.conditia.ConditionScope;
import org.jetbrains.annotations.NotNull;

/**
 * The context of one load of a module: the one Chassis keeps, which tracks what the module registers, and the
 * module's library groups and scope of condition types.
 */
@RequiredArgsConstructor
final class DefaultModuleContext implements ModuleContext {
	@Delegate(types = ExtensionContext.class)
	private final @NotNull ExtensionContext base;
	private final @NotNull LibraryGroups libraries;
	private final @NotNull ConditionScope conditions;

	@Override
	public @NotNull LibraryGroups libraries() {
		return libraries;
	}

	@Override
	public @NotNull ConditionScope conditions() {
		return conditions;
	}
}
