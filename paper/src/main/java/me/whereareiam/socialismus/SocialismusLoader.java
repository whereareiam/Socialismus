package me.whereareiam.socialismus;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class SocialismusLoader implements PluginLoader {
    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();

        resolver.addRepository(
                new RemoteRepository.Builder("maven", "default", "https://repo.maven.apache.org/maven2/").build()
        );
        resolver.addRepository(
                new RemoteRepository.Builder("aikar", "default", "https://repo.aikar.co/content/groups/aikar/").build()
        );

        resolver.addDependency(
                new Dependency(new DefaultArtifact("com.google.inject:guice:7.0.0"), null)
        );
        resolver.addDependency(
                new Dependency(new DefaultArtifact("net.elytrium:serializer:1.1.1"), null)
        );
        resolver.addDependency(
                new Dependency(new DefaultArtifact("co.aikar:acf-paper:0.5.1-SNAPSHOT"), null)
        );
        resolver.addDependency(
                new Dependency(new DefaultArtifact("net.kyori:adventure-text-serializer-plain:4.14.0"), null)
        );
        resolver.addDependency(
                new Dependency(new DefaultArtifact("net.kyori:adventure-text-minimessage:4.14.0"), null)
        );

        classpathBuilder.addLibrary(resolver);
    }
}
