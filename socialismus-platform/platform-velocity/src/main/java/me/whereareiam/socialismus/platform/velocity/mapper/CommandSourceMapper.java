package me.whereareiam.socialismus.platform.velocity.mapper;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import me.whereareiam.socialismus.input.container.PlayerContainerService;
import me.whereareiam.socialismus.model.player.DummyCommandPlayer;
import me.whereareiam.socialismus.model.player.DummyPlayer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.SenderMapper;

import javax.annotation.Nonnull;
import java.util.Optional;

@Singleton
public class CommandSourceMapper implements SenderMapper<CommandSource, DummyPlayer> {
    private final PlayerContainerService playerContainer;

    @Inject
    public CommandSourceMapper(PlayerContainerService playerContainer) {
        this.playerContainer = playerContainer;
    }

    @Override
    public @NonNull DummyPlayer map(@NonNull CommandSource source) {
        if (source instanceof ConsoleCommandSource) {
            return DummyCommandPlayer.builder().commandSender(source).audience(source).build();
        }

        Player player = (Player) source;
        Optional<DummyPlayer> dummyPlayer = playerContainer.getPlayer(player.getUniqueId());
        if (dummyPlayer.isPresent())
            return DummyCommandPlayer.from(dummyPlayer.get(), source);

        throw new NullPointerException("A player with the name " + player.getUsername() + " was not found");
    }

    @Override
    public @Nonnull CommandSource reverse(final @Nonnull DummyPlayer dummyPlayer) {
        return (CommandSource) ((DummyCommandPlayer) dummyPlayer).getCommandSender();
    }
}
