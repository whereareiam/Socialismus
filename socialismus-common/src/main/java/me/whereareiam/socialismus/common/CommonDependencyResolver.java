package me.whereareiam.socialismus.common;

import com.alessiodp.libby.Library;
import com.alessiodp.libby.LibraryManager;
import com.alessiodp.libby.relocation.Relocation;
import me.whereareiam.socialismus.api.Constants;
import me.whereareiam.socialismus.api.input.DependencyResolver;

import java.util.ArrayList;
import java.util.List;

public abstract class CommonDependencyResolver implements DependencyResolver {
	protected LibraryManager libraryManager;
	protected final List<Library> libraries = new ArrayList<>();

	@Override
	public void resolveDependencies() {
		libraryManager.addMavenCentral();
		libraryManager.addRepository("https://maven.whereareiam.me/release");
	}

	@Override
	public void loadLibraries() {
		// Common libraries
		addDependency(Library.builder()
				.groupId("com{}google{}inject")
				.artifactId("guice")
				.version(Constants.Dependency.GUICE)
				.resolveTransitiveDependencies(true)
				.relocate(
						Relocation.builder()
								.pattern("com{}google{}inject")
								.relocatedPattern("me.whereareiam.socialismus.library.guice")
								.build()
				).relocate(
						Relocation.builder()
								.pattern("com{}google{}common")
								.relocatedPattern("me.whereareiam.socialismus.library.guava")
								.build()
				).build());

		addDependency(Library.builder()
				.groupId("me.whereareiam")
				.artifactId("configura")
				.version(Constants.Dependency.CONFIGURA)
				.resolveTransitiveDependencies(true)
				.relocate(
						Relocation.builder()
								.pattern("com{}fasterxml{}jackson")
								.relocatedPattern("me.whereareiam.socialismus.library.jackson")
								.build()
				).relocate(
						Relocation.builder()
								.pattern("org{}yaml{}snakeyaml")
								.relocatedPattern("me.whereareiam.socialismus.library.snakeyaml")
								.build()
				).build());

		// Jedis
		addDependency(Library.builder()
				.groupId("redis.clients")
				.artifactId("jedis")
				.version(Constants.Dependency.JEDIS)
				.resolveTransitiveDependencies(true)
				.build());

		// Cloud libraries
		addDependency(Library.builder()
				.groupId("org{}incendo")
				.artifactId("cloud-core")
				.version(Constants.Dependency.CLOUD)
				.build());

		addDependency(Library.builder()
				.groupId("org{}incendo")
				.artifactId("cloud-processors-cooldown")
				.version(Constants.Dependency.CLOUD_COOLDOWN)
				.build());

		addDependency(Library.builder()
				.groupId("org{}incendo")
				.artifactId("cloud-annotations")
				.version(Constants.Dependency.CLOUD)
				.build());
	}

	@Override
	public void addDependency(Library library) {
		libraries.add(library);
	}

	@Override
	public void clearDependencies() {
		libraries.clear();
	}
}
