package me.whereareiam.socialismus.module.resolver;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.module.model.InternalModule;
import me.whereareiam.socialismus.type.PlatformType;

@Singleton
public class ModulePlatformResolver implements ModuleResolver {
	private final PlatformType platformType = PlatformType.getType();

	@Override
	public boolean resolve(InternalModule module) {
		boolean status = module.getSupportedPlatforms().contains(platformType);

		if (!status) Logger.warn("Module " + module.getName() + " does not support platform " + platformType);

		return status;
	}
}
