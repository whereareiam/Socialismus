package me.whereareiam.socialismus.module.dialogue.network;

import com.google.inject.Injector;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import me.whereareiam.socialismus.module.dialogue.common.DialogueNotices;
import me.whereareiam.socialismus.module.dialogue.common.delivery.LocalDelivery;
import me.whereareiam.socialismus.module.dialogue.common.delivery.MessageRenderer;
import me.whereareiam.socialismus.module.dialogue.common.delivery.RemoteDeliveries;
import me.whereareiam.socialismus.module.dialogue.common.moderation.Spying;
import me.whereareiam.socialismus.module.dialogue.common.store.DialogueStores;
import me.whereareiam.socialismus.module.dialogue.network.store.ReplicatedConversationStore;
import me.whereareiam.socialismus.module.dialogue.network.store.ReplicatedIgnoreStore;
import me.whereareiam.socialismus.module.dialogue.network.store.ReplicatedPreferencesStore;
import me.whereareiam.socialismus.module.network.api.NetworkResources;
import me.whereareiam.socialismus.module.network.api.NetworkService;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.registry.ResourceRegistry;
import me.whereareiam.socialismus.service.Scheduler;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Dialogue on SocialismusNetwork: private messages to players on other nodes, preferences, ignores and conversations
 * every node shares, and spies who see the messages of the whole network. Only touched while SocialismusNetwork is
 * installed, since its API is reachable only then.
 */
public final class DialogueNetwork {
	private final Injector injector;
	private final SpyCopies spyCopies;

	private DialogueNetwork(@NotNull Injector injector, @NotNull NetworkService network) {
		this.injector = injector;
		this.spyCopies = new SpyCopies(network, injector.getInstance(Spying.class));

		injector.getInstance(DialogueStores.class).use(
				new ReplicatedPreferencesStore(network.replication()),
				new ReplicatedIgnoreStore(network.replication()),
				new ReplicatedConversationStore(network.replication())
		);
		injector.getInstance(RemoteDeliveries.class).use(new DialogueNetworkDelivery(
				network,
				injector.getInstance(LocalDelivery.class),
				injector.getInstance(MessageRenderer.class),
				injector.getInstance(DialogueNotices.class),
				injector.getInstance(PlayerRegistry.class),
				injector.getInstance(Scheduler.class),
				() -> injector.getInstance(DialogueSettings.class).getMessaging().getConfirmation()
		));
		injector.getInstance(EventManager.class).register(spyCopies);
	}

	/**
	 * Puts Dialogue on the network SocialismusNetwork registered.
	 *
	 * @param resources Socialismus's resources, where SocialismusNetwork registered the network
	 * @param injector  Dialogue's injector
	 * @return Dialogue on the network, or empty when the network is not registered
	 */
	public static @NotNull Optional<DialogueNetwork> attach(@NotNull ResourceRegistry resources, @NotNull Injector injector) {
		return resources.get(NetworkResources.NETWORK).map(network -> new DialogueNetwork(injector, network));
	}

	/**
	 * Takes Dialogue off the network: back to its stores in memory and to delivering only on this server.
	 */
	public void detach() {
		injector.getInstance(EventManager.class).unregister(spyCopies);
		injector.getInstance(RemoteDeliveries.class).use(null);
		injector.getInstance(DialogueStores.class).reset();
	}
}
