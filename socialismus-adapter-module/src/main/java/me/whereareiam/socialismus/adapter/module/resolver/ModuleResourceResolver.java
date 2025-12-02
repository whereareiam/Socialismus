package me.whereareiam.socialismus.adapter.module.resolver;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.ResourceRequirement;
import me.whereareiam.socialismus.model.module.InternalModule;
import me.whereareiam.socialismus.registry.ResourceRegistry;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ModuleResourceResolver implements ModuleResolver {
	private final ResourceRegistry registry;

	@Override
	public boolean resolve(InternalModule mod) {
		if (mod.getRequirements() == null) return true;
		for (ResourceRequirement req : mod.getRequirements()) {
			if (!registry.has(req.getType()) && !req.isOptional()) {
				Logger.severe("Module " + mod.getName() + " requires missing resource " + req.getType());
				return false;
			}
		}
		return true;
	}
}
