package me.whereareiam.socialismus.module.dialogue.common.moderation;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.MessageModerationService;
import me.whereareiam.socialismus.module.dialogue.api.SpyStore;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import me.whereareiam.socialismus.module.dialogue.common.DialogueNotices;
import org.jetbrains.annotations.NotNull;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class DefaultMessageModerationService implements MessageModerationService {
	private final Provider<DialogueSettings> settings;
	private final SpyStore spies;
	private final DialogueNotices notices;

	@Override
	public void toggleSpy(@NotNull SocialismusPlayer player) {
		DialogueSettings.Moderation moderation = settings.get().getModeration();
		if (moderation.isPrivacy() || !moderation.getSpy().isEnabled()) {
			spies.setSpying(player.getUniqueId(), false);
			notices.spyUnavailable(player);
			return;
		}

		boolean spying = !spies.isSpying(player.getUniqueId());
		spies.setSpying(player.getUniqueId(), spying);
		notices.spyToggled(player, spying);
	}
}
