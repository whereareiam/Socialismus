package me.whereareiam.socialismus.platform.bukkit;

import me.whereareiam.attache.model.Library;
import me.whereareiam.attache.model.Relocation;
import me.whereareiam.attache.platform.bukkit.BukkitLibraryManager;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.common.CommonDependencyResolver;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class BukkitDependencyResolver extends CommonDependencyResolver {
	public BukkitDependencyResolver(Plugin plugin) {
		this.libraryManager = new BukkitLibraryManager(plugin, ".libraries");
	}

	@Override
	public void resolveDependencies() {
		super.resolveDependencies();
		libraryManager.addJitPack();

		libraryManager.loadLibraries(libraries);
		clearDependencies();
	}

	@Override
	public void loadLibraries() {
		super.loadLibraries();

		// Bukkit specific libraries
		addDependency(Library.builder()
				.groupId("net{}kyori")
				.artifactId("adventure-api")
				.version(Constants.Dependency.ADVENTURE)
				.resolveTransitiveDependencies(true)
				.relocations(List.of(Relocation.builder()
						.pattern("net{}kyori")
						.relocatedPattern("me.whereareiam.socialismus.library")
						.build()))
				.build());

		addDependency(Library.builder()
				.groupId("net{}kyori")
				.artifactId("adventure-text-serializer-plain")
				.version(Constants.Dependency.ADVENTURE)
				.resolveTransitiveDependencies(true)
				.relocations(List.of(Relocation.builder()
						.pattern("net{}kyori")
						.relocatedPattern("me.whereareiam.socialismus.library")
						.build()))
				.build());

		addDependency(Library.builder()
				.groupId("net{}kyori")
				.artifactId("adventure-text-serializer-gson")
				.version(Constants.Dependency.ADVENTURE)
				.resolveTransitiveDependencies(true)
				.relocations(List.of(Relocation.builder()
						.pattern("net{}kyori")
						.relocatedPattern("me.whereareiam.socialismus.library")
						.build()))
				.build());

		addDependency(Library.builder()
				.groupId("net{}kyori")
				.artifactId("adventure-text-minimessage")
				.version(Constants.Dependency.ADVENTURE)
				.resolveTransitiveDependencies(true)
				.relocations(List.of(Relocation.builder()
						.pattern("net{}kyori")
						.relocatedPattern("me.whereareiam.socialismus.library")
						.build()))
				.build());

		addDependency(Library.builder()
				.groupId("net{}kyori")
				.artifactId("adventure-platform-bukkit")
				.version(Constants.Dependency.ADVENTURE_BUKKIT)
				.resolveTransitiveDependencies(true)
				.relocations(List.of(Relocation.builder()
						.pattern("net{}kyori")
						.relocatedPattern("me.whereareiam.socialismus.library")
						.build()))
				.build());

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
				.relocations(List.of(Relocation.builder()
						.pattern("net{}kyori")
						.relocatedPattern("me.whereareiam.socialismus.library")
						.build()))
				.build());

		// Brigadier is needed for cloud-brigadier compilation (transitive dependency of cloud-paper)
		// Note: This is compileOnly in build.gradle.kts, but included here for consistency
		// It will be transitively included by cloud-paper at runtime if needed
		addDependency(Library.builder()
				.groupId("com{}mojang")
				.artifactId("brigadier")
				.version(Constants.Dependency.BRIGADIER)
				.resolveTransitiveDependencies(true)
				.build());
	}
}
