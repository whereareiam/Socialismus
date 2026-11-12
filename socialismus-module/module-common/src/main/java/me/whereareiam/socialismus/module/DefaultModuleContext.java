package me.whereareiam.socialismus.module;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import me.whereareiam.chassis.ExtensionContext;
import me.whereareiam.chassis.adapter.attache.LibraryGroups;
import org.jetbrains.annotations.NotNull;

/**
 * The context of one load of a module: the one Chassis keeps, which tracks what the module registers, and the
 * module's library groups.
 */
@RequiredArgsConstructor
final class DefaultModuleContext implements ModuleContext {
	@Delegate(types = ExtensionContext.class)
	private final @NotNull ExtensionContext base;
	private final @NotNull LibraryGroups libraries;

	@Override
	public @NotNull LibraryGroups libraries() {
		return libraries;
	}
}
