package me.whereareiam.socialismus.module;

import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * The class loader of one module. A class is looked up, in this order, in:
 * <ol>
 *   <li>Socialismus, the parent;</li>
 *   <li>the module a {@code MODULE} dependency points at, when the class is in one of its exports;</li>
 *   <li>the module's own jar and the libraries it declares;</li>
 *   <li>the plugins its {@code PLATFORM} dependencies point at, in the order it lists them.</li>
 * </ol>
 */
final class ModuleClassLoader extends URLClassLoader {
	private final Map<String, ClassLoader> exports;
	private final List<ClassLoader> platformDependencies;

	/**
	 * Creates the class loader of a module.
	 *
	 * @param moduleUrl            the module's jar
	 * @param parent               Socialismus's class loader
	 * @param exports              the class loader of the module behind each package exported to this one
	 * @param platformDependencies the class loaders of the plugins it depends on, in the order it lists them
	 */
	ModuleClassLoader(
			@NotNull URL moduleUrl,
			@NotNull ClassLoader parent,
			@NotNull Map<String, ClassLoader> exports,
			@NotNull List<ClassLoader> platformDependencies
	) {
		super(new URL[]{moduleUrl}, parent);
		this.exports = Map.copyOf(exports);
		this.platformDependencies = List.copyOf(platformDependencies);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		ClassLoader exporter = findExporter(name);
		if (exporter != null) return exporter.loadClass(name);

		try {
			return super.findClass(name);
		} catch (ClassNotFoundException notOwn) {
			for (ClassLoader platformDependency : platformDependencies) {
				try {
					return platformDependency.loadClass(name);
				} catch (ClassNotFoundException ignored) {
					// The next plugin may have it.
				}
			}

			throw notOwn;
		}
	}

	private ClassLoader findExporter(String className) {
		return exports.entrySet().stream()
				.filter(entry -> className.startsWith(entry.getKey() + "."))
				.max(Map.Entry.comparingByKey(Comparator.comparingInt(String::length)))
				.map(Map.Entry::getValue)
				.orElse(null);
	}
}
