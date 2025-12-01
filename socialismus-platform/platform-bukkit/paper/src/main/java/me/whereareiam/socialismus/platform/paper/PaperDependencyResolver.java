package me.whereareiam.socialismus.platform.paper;

import com.alessiodp.libby.Library;
import com.alessiodp.libby.PaperLibraryManager;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.common.CommonDependencyResolver;
import org.bukkit.plugin.Plugin;

public class PaperDependencyResolver extends CommonDependencyResolver {
	public PaperDependencyResolver(Plugin plugin) {
		this.libraryManager = new PaperLibraryManager(plugin, ".libraries");
	}

	@Override
	public void resolveDependencies() {
		super.resolveDependencies();

		libraries.forEach(libraryManager::loadLibrary);
		clearDependencies();
	}

	@Override
	public void loadLibraries() {
		super.loadLibraries();

		// Paper specific libraries
		addDependency(Library.builder()
				.groupId("org{}incendo")
				.artifactId("cloud-paper")
				.version(Constants.Dependency.CLOUD_PAPER)
				.resolveTransitiveDependencies(true)
				.build());

		addDependency(Library.builder()
				.groupId("org{}incendo")
				.artifactId("cloud-minecraft-extras")
				.version(Constants.Dependency.CLOUD_MINECRAFT_EXTRAS)
				.build());
	}
}
