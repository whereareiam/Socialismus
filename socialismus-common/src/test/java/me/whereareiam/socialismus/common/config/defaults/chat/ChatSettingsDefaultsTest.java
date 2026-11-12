package me.whereareiam.socialismus.common.config.defaults.chat;

import me.whereareiam.conditia.adapter.configura.ConditiaFeature;
import me.whereareiam.configura.Config;
import me.whereareiam.configura.Configura;
import me.whereareiam.configura.type.Format;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.model.requirement.RequirementLists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChatSettingsDefaultsTest {
	@TempDir
	Path dataPath;

	@Test
	void givesChatHistoryTheAdministratorPermissionWhereTheFileHasNoRequirements() throws IOException {
		Path file = write("history:\n  historySize: 500\n");

		ChatSettings settings = configura().update(file, ChatSettings.class);

		assertEquals(500, settings.getHistory().getHistorySize());
		assertEquals(RequirementLists.permission("socialismus.admin"), settings.getHistory().getRequires());
		assertEquals(RequirementLists.permission("socialismus.admin"), settings.getHistory().getBypass());
		assertEquals(RequirementLists.permission("socialismus.chat.toggle.bypass"), settings.getToggle().getBypass());
	}

	@Test
	void keepsTheRequirementListsOfChatHistoryAsWritten() throws IOException {
		Path file = write("""
				history:
				  requires:
				    - world: world
				""");

		configura().update(file, ChatSettings.class);
		ChatSettings.ChatHistorySettings history = configura().update(file, ChatSettings.class).getHistory();

		assertEquals(RequirementLists.of("world", "world"), history.getRequires());
		assertEquals(RequirementLists.permission("socialismus.admin"), history.getBypass());
	}

	private Configura configura() {
		return Config.builder()
				.format(Format.YAML)
				.feature(new ConditiaFeature())
				.build()
				.withDefaults(ChatSettingsDefaults.class);
	}

	private Path write(String content) throws IOException {
		Path file = dataPath.resolve("settings.yml");
		Files.writeString(file, content);

		return file;
	}
}
