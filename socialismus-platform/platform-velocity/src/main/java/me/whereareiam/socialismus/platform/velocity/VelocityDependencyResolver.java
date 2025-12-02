package me.whereareiam.socialismus.platform.velocity;

import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import me.whereareiam.attache.model.Library;
import me.whereareiam.attache.platform.velocity.VelocityLibraryManager;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.common.CommonDependencyResolver;
import org.slf4j.Logger;

import java.nio.file.Path;

public class VelocityDependencyResolver extends CommonDependencyResolver {
	public VelocityDependencyResolver(ProxyServer proxyServer, PluginContainer pluginContainer, Logger logger, Path dataPath) {
		this.libraryManager = new VelocityLibraryManager(proxyServer, pluginContainer, logger, dataPath, ".libraries");
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

		// Velocity specific libraries
		addDependency(Library.builder()
				.groupId("org{}incendo")
				.artifactId("cloud-velocity")
				.version(Constants.Dependency.CLOUD_VELOCITY)
				.resolveTransitiveDependencies(true)
				.build());

		addDependency(Library.builder()
				.groupId("org{}incendo")
				.artifactId("cloud-minecraft-extras")
				.version(Constants.Dependency.CLOUD_MINECRAFT_EXTRAS)
				.build());
	}
}
