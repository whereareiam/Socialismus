package me.whereareiam.socialismus.module.mentionator.network;

import com.google.inject.Injector;
import me.whereareiam.socialismus.module.mentionator.api.model.tagging.Tagging;
import me.whereareiam.socialismus.module.mentionator.common.notify.Notifier;
import me.whereareiam.socialismus.module.mentionator.common.notify.RemoteTaggings;
import me.whereareiam.socialismus.module.network.api.NetworkResources;
import me.whereareiam.socialismus.module.network.api.NetworkService;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.registry.ResourceRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Mentionator on SocialismusNetwork: every player of the network may be tagged and is notified by their own node, and
 * the tags of a message travel with it to every node, so each shows them and a chat's {@code reaches} there knows whom
 * they tag. Only touched while SocialismusNetwork is installed, since its API is reachable only then.
 */
public final class MentionatorNetwork {
	private final Injector injector;
	private final NetworkService network;
	private final NetworkTagging tagging;

	private MentionatorNetwork(@NotNull Injector injector, @NotNull NetworkService network) {
		this.injector = injector;
		this.network = network;

		this.tagging = new NetworkTagging(network, injector.getInstance(Notifier.class), injector.getInstance(PlayerRegistry.class));

		network.carry(Tagging.ATTRIBUTE);
		injector.getInstance(RemoteTaggings.class).use(tagging);
	}

	/**
	 * Puts Mentionator on the network SocialismusNetwork registered.
	 *
	 * @param resources Socialismus's resources, where SocialismusNetwork registered the network
	 * @param injector  Mentionator's injector
	 * @return Mentionator on the network, or empty when the network is not registered
	 */
	public static @NotNull Optional<MentionatorNetwork> attach(@NotNull ResourceRegistry resources, @NotNull Injector injector) {
		return resources.get(NetworkResources.NETWORK).map(network -> new MentionatorNetwork(injector, network));
	}

	/**
	 * Takes Mentionator off the network: back to tagging only the players this server hosts.
	 */
	public void detach() {
		injector.getInstance(RemoteTaggings.class).use(null);
		tagging.close();
		network.drop(Tagging.ATTRIBUTE);
	}
}
