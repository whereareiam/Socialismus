package me.whereareiam.socialismus.platform.bukkit;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.platform.AbstractPlatformInteractor;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class BukkitPlatformInteractor extends AbstractPlatformInteractor {
	private final Provider<BukkitAudiences> audiences;

	@Override
	public void broadcast(Component component) {
		audiences.get().all().sendMessage(component);
	}
}