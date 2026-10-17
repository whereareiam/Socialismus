package me.whereareiam.socialismus.platform.paper.inject;

import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.Getter;
import me.whereareiam.socialismus.command.CommandConfiguration;
import me.whereareiam.socialismus.common.CommonConfiguration;
import me.whereareiam.socialismus.common.CommonInjector;
import me.whereareiam.socialismus.module.ModuleConfiguration;
import me.whereareiam.socialismus.platform.PlatformCommonConfiguration;
import org.bukkit.plugin.Plugin;

import java.nio.file.Path;

@Getter
public class PaperInjector {
	public PaperInjector(Plugin plugin, Path dataPath) {
		Injector injector = Guice.createInjector(
				new PaperInjectorConfiguration(plugin),
				new PlatformCommonConfiguration(plugin),
				new CommonConfiguration(dataPath),
				new CommandConfiguration(),
				new ModuleConfiguration()
		);

		CommonInjector.setInjector(injector);
	}
}
