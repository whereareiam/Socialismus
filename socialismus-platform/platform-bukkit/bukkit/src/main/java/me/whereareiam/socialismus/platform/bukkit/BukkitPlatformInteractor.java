package me.whereareiam.socialismus.platform.bukkit;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.output.integration.Integration;
import me.whereareiam.socialismus.platform.AbstractPlatformInteractor;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;

import java.util.Set;

@Singleton
public class BukkitPlatformInteractor extends AbstractPlatformInteractor {
	private final Provider<BukkitAudiences> audiences;

	@Inject
	public BukkitPlatformInteractor(Provider<BukkitAudiences> audiences, Provider<Set<Integration>> integrations) {
		super(integrations);
		this.audiences = audiences;
	}

	@Override
	public void broadcast(Component component) {
		audiences.get().all().sendMessage(component);
	}
}