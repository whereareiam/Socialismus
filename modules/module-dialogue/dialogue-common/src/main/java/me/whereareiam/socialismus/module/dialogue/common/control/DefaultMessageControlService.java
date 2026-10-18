package me.whereareiam.socialismus.module.dialogue.common.control;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.IgnoreStore;
import me.whereareiam.socialismus.module.dialogue.api.MessageControlService;
import me.whereareiam.socialismus.module.dialogue.api.MessageToggleStore;
import me.whereareiam.socialismus.module.dialogue.api.model.IgnoredPlayer;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import me.whereareiam.socialismus.module.dialogue.common.DialogueNotices;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class DefaultMessageControlService implements MessageControlService {
	private final Provider<DialogueSettings> settings;
	private final MessageToggleStore toggles;
	private final IgnoreStore ignores;
	private final RecipientControl recipientControl;
	private final PlayerRegistry playerRegistry;
	private final DialogueNotices notices;

	@Override
	public void toggleMessages(@NotNull SocialismusPlayer player) {
		if (!settings.get().getControl().getToggle().isEnabled()) {
			notices.toggleUnavailable(player);
			return;
		}

		boolean off = !toggles.isOff(player.getUniqueId());
		toggles.setOff(player.getUniqueId(), off);
		notices.messagesToggled(player, off);
	}

	@Override
	public void ignore(@NotNull SocialismusPlayer player, @NotNull String targetName) {
		if (!settings.get().getControl().getIgnore().isEnabled()) {
			notices.ignoreUnavailable(player);
			return;
		}

		// A player already ignored is found by name even when they are offline, so they can be unignored.
		Optional<IgnoredPlayer> alreadyIgnored = ignores.ignored(player.getUniqueId()).stream()
				.filter(ignored -> ignored.getName().equalsIgnoreCase(targetName))
				.findFirst();
		if (alreadyIgnored.isPresent()) {
			ignores.unignore(player.getUniqueId(), alreadyIgnored.get().getUniqueId());
			notices.ignoreChanged(player, alreadyIgnored.get().getName(), false);
			return;
		}

		Optional<SocialismusPlayer> target = playerRegistry.getPlayerData(targetName).filter(player::canSee);
		if (target.isEmpty()) {
			notices.noRecipient(player, targetName);
			return;
		}

		UUID targetId = target.get().getUniqueId();
		if (targetId.equals(player.getUniqueId())) {
			notices.samePlayer(player);
			return;
		}
		if (recipientControl.meets(settings.get().getControl().getIgnore().getExempt(), target.get())) {
			notices.ignoreExempt(player, target.get().getUsername());
			return;
		}

		ignores.ignore(player.getUniqueId(), new IgnoredPlayer(targetId, target.get().getUsername()));
		notices.ignoreChanged(player, target.get().getUsername(), true);
	}

	@Override
	public void showIgnored(@NotNull SocialismusPlayer player) {
		notices.ignored(player, ignores.ignored(player.getUniqueId()));
	}
}
