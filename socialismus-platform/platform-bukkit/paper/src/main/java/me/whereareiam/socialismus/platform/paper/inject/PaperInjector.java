package me.whereareiam.socialismus.platform.paper.inject;

import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.Getter;
import me.whereareiam.socialismus.adapter.config.ConfigBinder;
import me.whereareiam.socialismus.adapter.module.ModuleConfiguration;
import me.whereareiam.socialismus.command.CommandConfiguration;
import me.whereareiam.socialismus.common.CommonConfiguration;
import me.whereareiam.socialismus.common.CommonInjector;
import me.whereareiam.socialismus.platform.paper.PaperDependencyResolver;
import org.bukkit.plugin.Plugin;

import java.nio.file.Path;

@Getter
public class PaperInjector {
    public PaperInjector(Plugin plugin, PaperDependencyResolver dependencyResolver, Path dataPath) {
        Injector injector = Guice.createInjector(
                new PaperInjectorConfiguration(plugin, dependencyResolver),
                new ConfigBinder(dataPath),
                new CommonConfiguration(),
                new CommandConfiguration(),
                new ModuleConfiguration()
        );

        CommonInjector.setInjector(injector);
    }
}
