package me.whereareiam.socialismus.module;

import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Comparator;
import java.util.Map;

final class ModuleClassLoader extends URLClassLoader {
	private final Map<String, ClassLoader> externalApiClassLoaders;

	ModuleClassLoader(
			@NotNull URL moduleUrl,
			@NotNull ClassLoader parent,
			@NotNull Map<String, ClassLoader> externalApiClassLoaders
	) {
		super(new URL[]{moduleUrl}, parent);
		this.externalApiClassLoaders = Map.copyOf(externalApiClassLoaders);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		ClassLoader externalClassLoader = findExternalApiClassLoader(name);
		if (externalClassLoader != null) return externalClassLoader.loadClass(name);

		return super.findClass(name);
	}

	private ClassLoader findExternalApiClassLoader(String className) {
		return externalApiClassLoaders.entrySet().stream()
				.filter(entry -> className.startsWith(entry.getKey()))
				.max(Map.Entry.comparingByKey(Comparator.comparingInt(String::length)))
				.map(Map.Entry::getValue)
				.orElse(null);
	}
}
