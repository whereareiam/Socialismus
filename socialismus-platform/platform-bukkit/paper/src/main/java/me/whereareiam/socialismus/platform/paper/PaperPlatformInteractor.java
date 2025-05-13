package me.whereareiam.socialismus.platform.paper;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.output.integration.Integration;
import me.whereareiam.socialismus.platform.AbstractPlatformInteractor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

import java.util.Set;

@Singleton
public class PaperPlatformInteractor extends AbstractPlatformInteractor {
	@Inject
	public PaperPlatformInteractor(Provider<Set<Integration>> integrations) {
		super(integrations);
	}

	@Override
	public void broadcast(Component component) {
		Bukkit.broadcast(component);
	}
}
