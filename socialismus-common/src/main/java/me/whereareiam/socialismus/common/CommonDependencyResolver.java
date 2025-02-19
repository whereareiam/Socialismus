package me.whereareiam.socialismus.common;

import com.alessiodp.libby.Library;
import com.alessiodp.libby.relocation.Relocation;
import me.whereareiam.socialismus.api.input.DependencyResolver;
import me.whereareiam.socialismus.shared.Constants;

import java.util.ArrayList;
import java.util.List;

public abstract class CommonDependencyResolver implements DependencyResolver {
    protected final List<Library> libraries = new ArrayList<>();

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

        // Jackson libraries
        addDependency(Library.builder()
                .groupId("com{}fasterxml{}jackson{}core")
                .artifactId("jackson-databind")
                .version(Constants.Dependency.JACKSON)
                .resolveTransitiveDependencies(true)
                .relocate(
                        Relocation.builder()
                                .pattern("com{}fasterxml{}jackson")
                                .relocatedPattern("me.whereareiam.socialismus.library.jackson")
                                .build()
                ).build());

        addDependency(Library.builder()
                .groupId("com{}fasterxml{}jackson{}dataformat")
                .artifactId("jackson-dataformat-yaml")
                .version(Constants.Dependency.JACKSON)
                .resolveTransitiveDependencies(true)
                .relocate(
                        Relocation.builder()
                                .pattern("com{}fasterxml{}jackson")
                                .relocatedPattern("me.whereareiam.socialismus.library.jackson")
                                .build()
                ).build());
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
