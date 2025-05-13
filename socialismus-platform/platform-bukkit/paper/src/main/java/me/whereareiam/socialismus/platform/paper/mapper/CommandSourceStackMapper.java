package me.whereareiam.socialismus.platform.paper.mapper;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.whereareiam.socialismus.api.input.container.PlayerContainerService;
import me.whereareiam.socialismus.api.model.player.DummyCommandPlayer;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.SenderMapper;

import javax.annotation.Nonnull;
import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
@Singleton
public class CommandSourceStackMapper implements SenderMapper<CommandSourceStack, DummyPlayer> {
    private final PlayerContainerService playerContainer;

    @Inject
    public CommandSourceStackMapper(PlayerContainerService playerContainer) {
        this.playerContainer = playerContainer;
    }

    @Override
    public @NonNull DummyPlayer map(@NonNull CommandSourceStack source) {
        if (source.getSender() instanceof ConsoleCommandSender || source.getSender().getClass().getName().equals("io.papermc.paper.brigadier.NullCommandSender")) {
            return DummyCommandPlayer.builder().commandSender(source).audience(source.getSender()).build();
        }

        Optional<DummyPlayer> dummyPlayer = playerContainer.getPlayer(source.getSender().getName());
        if (dummyPlayer.isPresent())
            return DummyCommandPlayer.from(dummyPlayer.get(), source);

        throw new NullPointerException("A player with the name " + source.getSender().getName() + " was not found");
    }

    @Override
    public @Nonnull CommandSourceStack reverse(final @Nonnull DummyPlayer dummyPlayer) {
        return (CommandSourceStack) ((DummyCommandPlayer) dummyPlayer).getCommandSender();
    }
}
