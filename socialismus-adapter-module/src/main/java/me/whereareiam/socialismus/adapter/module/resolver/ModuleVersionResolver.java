package me.whereareiam.socialismus.adapter.module.resolver;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.Logger;
import me.whereareiam.socialismus.model.module.InternalModule;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ModuleVersionResolver implements ModuleResolver {
	@Override
	public boolean resolve(InternalModule module) {
		boolean status = module.getSupportedVersions().contains(Constants.SERVER_VERSION);

		if (!status)
			Logger.warn("Module " + module.getName() + " does not support version " + Constants.SERVER_VERSION);

		return status;
	}
}
