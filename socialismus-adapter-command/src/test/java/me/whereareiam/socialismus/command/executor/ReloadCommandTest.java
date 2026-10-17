package me.whereareiam.socialismus.command.executor;

import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.config.ConfigProvider;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReloadCommandTest {
	@Test
	void reloadsTheDocumentsBeforeWhatIsBuiltFromThem() {
		Reloadable container = () -> {};
		Reloadable document = new Document();
		Set<Reloadable> registered = new LinkedHashSet<>(List.of(container, document));

		assertEquals(List.of(document, container), ReloadCommand.inReloadOrder(registered));
	}

	private static final class Document extends ConfigProvider<Object> {
		private Document() {
			super(Path.of("."), "document", Object.class, reloadable -> {});
		}
	}
}
