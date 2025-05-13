package me.whereareiam.socialismus.platform.bukkit.mapper;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.input.container.PlayerContainerService;
import me.whereareiam.socialismus.api.model.player.DummyCommandPlayer;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.SenderMapper;

import javax.annotation.Nonnull;
import java.util.Optional;

@Singleton
public class CommandSenderMapper implements SenderMapper<CommandSender, DummyPlayer> {
    private final PlayerContainerService playerContainer;
    private final Provider<BukkitAudiences> audiences;

    @Inject
    public CommandSenderMapper(PlayerContainerService playerContainer, Provider<BukkitAudiences> audiences) {
        this.playerContainer = playerContainer;
        this.audiences = audiences;
    }

    @Override
    public @NonNull DummyPlayer map(@NonNull CommandSender source) {
        if (source instanceof ConsoleCommandSender)
            return DummyCommandPlayer.builder().commandSender(source).audience(audiences.get().sender(source)).build();

        Optional<DummyPlayer> dummyPlayer = playerContainer.getPlayer(source.getName());
        if (dummyPlayer.isPresent())
            return DummyCommandPlayer.from(dummyPlayer.get(), source);

        throw new NullPointerException("A player with the name " + source.getName() + " was not found");
    }

    @Override
    public @Nonnull CommandSender reverse(final @Nonnull DummyPlayer dummyPlayer) {
        return (CommandSender) ((DummyCommandPlayer) dummyPlayer).getCommandSender();
    }
}