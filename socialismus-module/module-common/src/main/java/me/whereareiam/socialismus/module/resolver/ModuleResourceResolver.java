package me.whereareiam.socialismus.module.resolver;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.resource.ResourceRequirement;
import me.whereareiam.socialismus.module.model.InternalModule;
import me.whereareiam.socialismus.registry.ResourceRegistry;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ModuleResourceResolver implements ModuleResolver {
	private final ResourceRegistry registry;

	@Override
	public boolean resolve(InternalModule mod) {
		if (mod.getRequirements() == null) return true;
		for (ResourceRequirement req : mod.getRequirements()) {
			if (!req.isOptional() && !registry.has(req.getResource())) {
				Logger.severe("Module " + mod.getName() + " requires missing resource " + req.getResource());
				return false;
			}
		}
		return true;
	}
}
