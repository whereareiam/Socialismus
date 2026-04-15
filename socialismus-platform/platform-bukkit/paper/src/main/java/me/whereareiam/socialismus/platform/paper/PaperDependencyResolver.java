package me.whereareiam.socialismus.platform.paper;

import me.whereareiam.attache.model.Library;
import me.whereareiam.attache.platform.paper.PaperLibraryManager;
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

		libraryManager.loadLibraries(libraries);
		clearDependencies();
	}

	@Override
	public void loadLibraries() {
		super.loadLibraries();

		addDependency(Library.builder()
				.groupId("me.whereareiam")
				.artifactId("keystone")
				.version(Constants.Dependency.KEYSTONE)
				.resolveTransitiveDependencies(true)
				.build());

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
