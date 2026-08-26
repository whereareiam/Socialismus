package me.whereareiam.socialismus.platform;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.module.PlatformClassLoader;
import me.whereareiam.socialismus.module.model.PlatformDependency;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Singleton
public class BukkitClassLoader implements PlatformClassLoader {
	private final Plugin plugin;

	@Inject
	public BukkitClassLoader(Plugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public ClassLoader getClassLoader() {
		return plugin.getClass().getClassLoader();
	}

	@Override
	public @NotNull Optional<PlatformDependency> findDependency(@NotNull String name) {
		Plugin dependency = plugin.getServer().getPluginManager().getPlugin(name);
		if (dependency == null || !dependency.isEnabled()) return Optional.empty();

		return Optional.of(new PlatformDependency(
				dependency.getName(),
				dependency.getDescription().getVersion(),
				dependency.getClass().getClassLoader()
		));
	}
}
