package me.whereareiam.socialismus;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@SuppressWarnings("UnstableApiUsage")
public class SocialismusPaperLoader implements PluginLoader {
    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();

        Properties prop = new Properties();
        try (InputStream input = new FileInputStream("version.properties")) {
            prop.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        resolver.addRepository(
                new RemoteRepository.Builder("maven", "default", "https://repo.maven.apache.org/maven2/").build()
        );
        resolver.addRepository(
                new RemoteRepository.Builder("aikar", "default", "https://repo.aikar.co/content/groups/aikar/").build()
        );

        addDependency(resolver, "com.google.inject:guice", prop.getProperty("google-guice"));
        addDependency(resolver, "net.elytrium:serializer", prop.getProperty("java-serializer"));
        addDependency(resolver, "co.aikar:acf-paper", prop.getProperty("acf-paper") + "-SNAPSHOT");
        addDependency(resolver, "net.kyori:adventure-text-serializer-plain", prop.getProperty("adventure-text-serializer-plain"));
        addDependency(resolver, "net.kyori:adventure-text-minimessage", prop.getProperty("adventure-text-minimessage"));

        classpathBuilder.addLibrary(resolver);
    }

    private void addDependency(MavenLibraryResolver resolver, String artifactId, String version) {
        resolver.addDependency(new Dependency(new DefaultArtifact(artifactId + ":" + version), null));
    }
}
