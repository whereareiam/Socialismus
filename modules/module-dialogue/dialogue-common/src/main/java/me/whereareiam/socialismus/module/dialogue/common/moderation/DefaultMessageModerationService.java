package me.whereareiam.socialismus.module.dialogue.common.moderation;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.MessageModerationService;
import me.whereareiam.socialismus.module.dialogue.api.PreferencesStore;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import me.whereareiam.socialismus.module.dialogue.common.DialogueNotices;
import org.jetbrains.annotations.NotNull;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class DefaultMessageModerationService implements MessageModerationService {
	private final Provider<DialogueSettings> settings;
	private final PreferencesStore preferences;
	private final DialogueNotices notices;

	@Override
	public void toggleSpy(@NotNull SocialismusPlayer player) {
		DialogueSettings.Moderation moderation = settings.get().getModeration();
		if (moderation.isPrivacy() || !moderation.getSpy().isEnabled()) {
			preferences.update(player.getUniqueId(), current -> current.withSpying(false));
			notices.spyUnavailable(player);
			return;
		}

		boolean spying = preferences.update(player.getUniqueId(), current -> current.withSpying(!current.isSpying())).isSpying();
		notices.spyToggled(player, spying);
	}
}
