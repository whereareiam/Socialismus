package me.whereareiam.socialismus.module.resolver;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.module.model.InternalModule;

/**
 * Accepts a module built for the running version of Socialismus.
 */
@Singleton
public class ModuleVersionResolver implements ModuleResolver {
	@Override
	public boolean resolve(InternalModule module) {
		boolean status = VersionPattern.accepts(module.getSocialismus(), Constants.VERSION);

		if (!status)
			Logger.warn("Module %s is built for Socialismus %s, but this is %s", module.getName(), module.getSocialismus(), Constants.VERSION);

		return status;
	}
}
