package me.whereareiam.socialismus.common.provider;

import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.registry.base.Registry;

import java.util.HashSet;
import java.util.Set;

@Singleton
public class ReloadableProvider implements Provider<Set<Reloadable>>, Registry<Reloadable> {
    private final Set<Reloadable> reloadables = new HashSet<>();

    @Override
    public void register(Reloadable reloadable) {
        reloadables.add(reloadable);
    }

    @Override
    public Set<Reloadable> get() {
        return reloadables;
    }
}
