package me.whereareiam.socialismus.adapter.module.resolver;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.api.Logger;
import me.whereareiam.socialismus.api.model.module.InternalModule;
import me.whereareiam.socialismus.api.output.PlatformInteractor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ModuleVersionResolver implements ModuleResolver {
	private final PlatformInteractor interactor;

	@Override
	public boolean resolve(InternalModule module) {
		boolean status = module.getSupportedVersions().contains(interactor.getServerVersion());

		if (!status)
			Logger.warn("Module " + module.getName() + " does not support version " + interactor.getServerVersion());

		return status;
	}
}
