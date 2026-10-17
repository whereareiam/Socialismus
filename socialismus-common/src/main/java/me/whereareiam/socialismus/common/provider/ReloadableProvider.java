package me.whereareiam.socialismus.common.provider;

import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.registry.base.Registry;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Every configuration document {@code /socialismus reload} reloads: the core's, loaded in the order they are bound,
 * and those of the modules, in the order the modules load them.
 */
@Singleton
public class ReloadableProvider implements Provider<Set<Reloadable>>, Registry<Reloadable> {
	private final Set<Reloadable> reloadables = new LinkedHashSet<>();

	@Override
	public synchronized void register(Reloadable reloadable) {
		reloadables.add(reloadable);
	}

	/**
	 * Returns the registered documents in the order they registered.
	 */
	@Override
	public synchronized Set<Reloadable> get() {
		return new LinkedHashSet<>(reloadables);
	}
}
