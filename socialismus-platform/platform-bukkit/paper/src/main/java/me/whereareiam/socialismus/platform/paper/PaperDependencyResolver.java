package me.whereareiam.socialismus.platform.paper;

import com.alessiodp.libby.Library;
import com.alessiodp.libby.PaperLibraryManager;
import me.whereareiam.socialismus.common.CommonDependencyResolver;
import me.whereareiam.socialismus.shared.Constants;
import org.bukkit.plugin.Plugin;

public class PaperDependencyResolver extends CommonDependencyResolver {
    private final PaperLibraryManager libraryManager;

    public PaperDependencyResolver(Plugin plugin) {
        this.libraryManager = new PaperLibraryManager(plugin, ".libraries");
    }

    @Override
    public void resolveDependencies() {
        libraryManager.addMavenCentral();

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
