package me.whereareiam.socialismus.module;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import me.whereareiam.chassis.Extension;
import me.whereareiam.chassis.common.ChassisHost;
import me.whereareiam.chassis.event.ExtensionEvent;
import me.whereareiam.chassis.model.InstalledExtension;
import me.whereareiam.chassis.type.ExtensionState;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.resource.ProvidedResource;
import me.whereareiam.socialismus.registry.ResourceRegistry;
import me.whereareiam.socialismus.service.resource.ResourceProvider;
import me.whereareiam.socialismus.type.ResourceRegistrationOutcome;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registers the resources of a module that is a {@link ResourceProvider} once it is enabled, before any module that
 * depends on it starts, and unregisters them once it stops.
 */
@RequiredArgsConstructor(onConstructor_ = @Inject)
public final class ModuleResources {
	private final ResourceRegistry registry;
	// The resources each module registered, by its name, which the log names it by.
	private final Map<String, List<ProvidedResource<?>>> registered = new ConcurrentHashMap<>();

	/**
	 * Follows the modules' changes of state.
	 *
	 * @param modules the modules
	 * @return stops following them when closed
	 */
	public @NotNull AutoCloseable follow(@NotNull ChassisHost<ModuleContext, ModuleFields> modules) {
		return modules.subscribe(event -> changed(modules, event));
	}

	private void changed(ChassisHost<ModuleContext, ModuleFields> modules, ExtensionEvent event) {
		InstalledExtension module = event.getExtension();
		if (event.getPrevious() == ExtensionState.ENABLED) {
			unregister(module.getDescriptor().getName());
			return;
		}

		if (module.getState() != ExtensionState.ENABLED) return;

		Optional<Extension<ModuleContext>> instance = modules.instance(module.getId());
		if (instance.isPresent() && instance.get() instanceof ResourceProvider provider) register(module, provider);
	}

	private void register(InstalledExtension module, ResourceProvider provider) {
		String name = module.getDescriptor().getName();
		List<ProvidedResource<?>> resources = registered.computeIfAbsent(name, key -> new ArrayList<>());
		for (ProvidedResource<?> resource : provider.provideResources()) {
			String key = resource.getKey().getFullKey();
			Optional<String> holder = holderOf(key);
			if (register(resource) == ResourceRegistrationOutcome.KEY_TAKEN) {
				Logger.warn("Resource %s of module %s is not registered: %s provides it already", key, name,
						holder.map(holderName -> "module " + holderName).orElse("another provider"));
				continue;
			}

			resources.add(resource);
			Logger.info("Registered resource %s from module %s", key, name);
		}
	}

	private <T> ResourceRegistrationOutcome register(ProvidedResource<T> resource) {
		return registry.register(resource.getKey(), resource.getResource());
	}

	private Optional<String> holderOf(String key) {
		return registered.entrySet().stream()
				.filter(entry -> entry.getValue().stream().anyMatch(resource -> resource.getKey().getFullKey().equals(key)))
				.map(Map.Entry::getKey)
				.findFirst();
	}

	private void unregister(String name) {
		List<ProvidedResource<?>> resources = registered.remove(name);
		if (resources == null) return;

		resources.forEach(this::unregister);
	}

	private <T> void unregister(ProvidedResource<T> resource) {
		registry.unregister(resource.getKey(), resource.getResource());
	}
}
