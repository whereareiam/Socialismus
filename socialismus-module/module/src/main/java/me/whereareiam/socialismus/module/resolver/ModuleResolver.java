package me.whereareiam.socialismus.module.resolver;

import me.whereareiam.socialismus.module.model.InternalModule;

public interface ModuleResolver {
    boolean resolve(InternalModule module);
}
