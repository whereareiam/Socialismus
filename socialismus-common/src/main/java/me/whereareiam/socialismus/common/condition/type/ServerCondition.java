package me.whereareiam.socialismus.common.condition.type;

import me.whereareiam.conditia.ConditionType;
import me.whereareiam.conditia.result.Outcome;
import me.whereareiam.conditia.result.Reason;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

/**
 * {@code server: [lobby, survival]}: holds when the player is on one of the servers listed, as the proxy names
 * them; a player whose server is not known yet fails without a reason.
 */
public final class ServerCondition implements ConditionType.Single<SocialismusPlayer, ServerCondition.Settings> {
	@Override
	public @NotNull String name() {
		return "server";
	}

	@Override
	public @NotNull Class<SocialismusPlayer> subject() {
		return SocialismusPlayer.class;
	}

	@Override
	public @NotNull Class<Settings> settings() {
		return Settings.class;
	}

	@Override
	public @NotNull Set<Class<? extends Reason>> reasons() {
		return Set.of(Location.Outside.class, Location.Inside.class);
	}

	@Override
	public @NotNull Outcome holds(@NotNull SocialismusPlayer player, @NotNull Settings settings) {
		String server = player.getServer();
		if (server == null) return Outcome.fail();

		return Location.among(settings.servers(), server, "servers", "server");
	}

	/**
	 * The condition's settings.
	 *
	 * @param servers the servers the player may be in
	 */
	public record Settings(@NotNull List<String> servers) {
	}
}
