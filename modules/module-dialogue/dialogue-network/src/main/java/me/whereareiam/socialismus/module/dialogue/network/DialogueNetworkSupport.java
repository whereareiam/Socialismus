package me.whereareiam.socialismus.module.dialogue.network;

import com.google.inject.Injector;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import me.whereareiam.socialismus.module.dialogue.common.DialogueNotices;
import me.whereareiam.socialismus.module.dialogue.common.delivery.LocalDelivery;
import me.whereareiam.socialismus.module.dialogue.common.delivery.MessageRenderer;
import me.whereareiam.socialismus.module.dialogue.common.delivery.RemoteDelivery;
import me.whereareiam.socialismus.module.network.api.NetworkResources;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.registry.ResourceRegistry;
import me.whereareiam.socialismus.service.Scheduler;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Dialogue's part on SocialismusNetwork. Only touched while SocialismusNetwork is installed, since its API is
 * reachable only then.
 */
public final class DialogueNetworkSupport {
	private DialogueNetworkSupport() {
	}

	/**
	 * Returns the delivery of private messages through the network SocialismusNetwork registered.
	 *
	 * @param resources Socialismus's resources, where SocialismusNetwork registered the network
	 * @param injector  Dialogue's injector
	 * @return the delivery, or empty when the network is not registered
	 */
	public static @NotNull Optional<RemoteDelivery> delivery(@NotNull ResourceRegistry resources, @NotNull Injector injector) {
		return resources.get(NetworkResources.NETWORK).map(network -> new DialogueNetworkDelivery(
				network,
				injector.getInstance(LocalDelivery.class),
				injector.getInstance(MessageRenderer.class),
				injector.getInstance(DialogueNotices.class),
				injector.getInstance(PlayerRegistry.class),
				injector.getInstance(Scheduler.class),
				() -> injector.getInstance(DialogueSettings.class).getMessaging().getConfirmation()
		));
	}
}
