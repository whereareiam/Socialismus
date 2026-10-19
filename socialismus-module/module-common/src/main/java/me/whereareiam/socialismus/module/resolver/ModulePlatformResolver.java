package me.whereareiam.socialismus.module.resolver;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.module.model.InternalModule;
import me.whereareiam.socialismus.module.model.ModulePlatform;
import me.whereareiam.socialismus.type.PlatformType;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Accepts a module that lists the running server platform among its {@code platforms}, in a version it accepts.
 */
@Singleton
public class ModulePlatformResolver implements ModuleResolver {
	private final PlatformType platformType;
	private final Supplier<String> platformVersion;

	@Inject
	public ModulePlatformResolver() {
		this(PlatformType.getType(), () -> Constants.PLATFORM_VERSION);
	}

	ModulePlatformResolver(PlatformType platformType, Supplier<String> platformVersion) {
		this.platformType = platformType;
		this.platformVersion = platformVersion;
	}

	@Override
	public boolean resolve(InternalModule module) {
		Optional<ModulePlatform> platform = module.getPlatforms().stream()
				.filter(candidate -> candidate.getType() == platformType)
				.findFirst();
		if (platform.isEmpty()) {
			Logger.warn("Module %s does not run on %s", module.getName(), platformType);
			return false;
		}

		String version = platformVersion.get();
		if (!VersionPattern.accepts(platform.get().getVersion(), version)) {
			Logger.warn("Module %s does not run on %s %s", module.getName(), platformType, version);
			return false;
		}

		return true;
	}
}
