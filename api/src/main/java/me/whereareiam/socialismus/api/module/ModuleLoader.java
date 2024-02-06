package me.whereareiam.socialismus.api.module;

public interface ModuleLoader {
	void loadModules();

	void unloadModules();

	void reloadModules();

	Module getModule(String name);
}
