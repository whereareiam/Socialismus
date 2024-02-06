package me.whereareiam.socialismus.core;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.module.Module;
import me.whereareiam.socialismus.api.module.ModuleLoader;

@Singleton
public class SocialismusModuleLoader implements ModuleLoader {
	@Override
	public void loadModules() {

	}

	@Override
	public void unloadModules() {

	}

	@Override
	public void reloadModules() {

	}

	@Override
	public Module getModule(String name) {
		return null;
	}
}
