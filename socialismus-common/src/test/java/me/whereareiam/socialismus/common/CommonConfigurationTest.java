package me.whereareiam.socialismus.common;

import me.whereareiam.conditia.model.Requirements;
import me.whereareiam.configura.Configura;
import me.whereareiam.socialismus.common.config.defaults.PhrasesDefaults;
import me.whereareiam.socialismus.config.PhrasesConfigProvider;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.condition.ConditionLists;
import me.whereareiam.socialismus.model.phrase.Phrases;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommonConfigurationTest {
	@TempDir
	Path dataPath;

	@Test
	void readsTheConditionListsOfAChatAsWritten() {
		Configura configura = new CommonConfiguration(dataPath).provideConfigura(dataPath);
		String yaml = """
				id: local
				requires:
				  - permission: socialismus.chat.local
				reaches:
				  - sender:
				      - not: { world: world_nether }
				""";

		Chat chat = configura.read(yaml.getBytes(StandardCharsets.UTF_8), Chat.class);

		assertEquals(ConditionLists.permission("socialismus.chat.local"), chat.getRequires());
		assertEquals(1, chat.getReaches().getConditions().items().size());
	}

	@Test
	void writesTheDefaultPhrases_WhenTheFileIsMissing() {
		Configura configura = new CommonConfiguration(dataPath).provideConfigura(dataPath);

		Phrases phrases = phrases(configura);

		assertEquals(PhrasesDefaults.PHRASES.texts(), phrases.texts());
		assertEquals(PhrasesDefaults.PHRASES.texts(), configura.read(dataPath.resolve("phrases.yml"), Phrases.class).texts());
	}

	@Test
	void keepsThePhrasesAnAdminWrote_AndAddsTheDefaultsTheFileLacks() throws IOException {
		Files.writeString(dataPath.resolve("phrases.yml"), """
				denied:
				  world.outside: "Not here."
				chat:
				  staff-only: "Staff only."
				""", StandardCharsets.UTF_8);
		Configura configura = new CommonConfiguration(dataPath).provideConfigura(dataPath);

		Phrases phrases = phrases(configura);

		assertEquals(Optional.of("Not here."), phrases.text("denied.world.outside"));
		assertEquals(Optional.of("Staff only."), phrases.text("chat.staff-only"));
		assertEquals(PhrasesDefaults.PHRASES.text("denied.world.inside"), phrases.text("denied.world.inside"));
		assertEquals(phrases.texts(), configura.read(dataPath.resolve("phrases.yml"), Phrases.class).texts());
	}

	private Phrases phrases(Configura configura) {
		return new PhrasesConfigProvider(dataPath, reloadable -> {}, PhrasesDefaults.PHRASES) {
			@Override
			protected Configura configura() {
				return configura;
			}
		}.get();
	}

	@Test
	void readsAChatWithoutConditionsAsMetByEveryone() {
		Configura configura = new CommonConfiguration(dataPath).provideConfigura(dataPath);

		Chat chat = configura.read("id: fallback".getBytes(StandardCharsets.UTF_8), Chat.class);

		assertEquals(Requirements.none(), chat.getRequires());
		assertEquals(Requirements.none(), chat.getReaches());
	}
}
