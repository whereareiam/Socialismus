package me.whereareiam.socialismus.module.type;

/**
 * What a module's dependency points at. Socialismus itself is no dependency: every module needs it, and
 * {@link me.whereareiam.socialismus.module.model.Module#getSocialismus()} names the versions a module is built for.
 */
public enum DependencyType {
	/**
	 * Another Socialismus module, by the name in its {@code module.json}. It loads first, and its
	 * {@linkplain me.whereareiam.socialismus.module.model.Module#getExports() exports} reach the module that depends
	 * on it.
	 */
	MODULE,

	/**
	 * A plugin that Socialismus itself integrates with, by the name of its integration, such as {@code PacketEvents}.
	 * The module uses it through Socialismus.
	 */
	INTEGRATION,

	/**
	 * Something the server platform loaded, used directly: a plugin on Paper, Bukkit or Velocity, by its name. Every
	 * class it has reaches the module, looked up after Socialismus and the module's own classes and libraries. The
	 * server software itself is no dependency; {@link me.whereareiam.socialismus.module.model.Module#getPlatforms()}
	 * names where a module runs.
	 */
	PLATFORM
}
