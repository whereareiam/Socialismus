package me.whereareiam.socialismus.module.database.common;

import me.whereareiam.dialectica.Dialectica;
import me.whereareiam.dialectica.adapter.jdbi.entity.EntityAccess;
import me.whereareiam.dialectica.entity.annotation.Id;
import me.whereareiam.dialectica.entity.annotation.Length;
import me.whereareiam.dialectica.entity.annotation.Table;
import me.whereareiam.dialectica.schema.Schema;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.module.database.api.schema.DatabaseSchema;
import me.whereareiam.socialismus.module.database.common.config.DatabaseSettings;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * What SocialismusDatabase guarantees on every database it supports: an owner's schema installs, rows written through
 * Dialectica's entity access are read back and outlive a restart, installing again changes nothing, and a schema
 * cannot be installed before the one it requires.
 */
abstract class DatabaseServiceContract {
	@TempDir
	Path workingPath;

	@BeforeEach
	void silenceTheLog() {
		Logger.init(new LoggingHelper() {
			@Override
			public void info(String message, Object... objects) {
			}

			@Override
			public void warn(String message, Object... objects) {
			}

			@Override
			public void severe(String message, Object... objects) {
			}

			@Override
			public void debug(String message, Object... objects) {
			}
		});
	}

	/**
	 * Returns the settings of the database under test.
	 */
	protected abstract DatabaseSettings settings();

	@Test
	void keepsTheRowsOfAnInstalledSchemaAcrossARestart() throws Exception {
		UUID owner = UUID.randomUUID();
		try (DefaultDatabaseService database = DefaultDatabaseService.open(settings(), workingPath)) {
			database.getSchemaInstaller().install(new NotesSchema());
			database.access(NoteEntity.class).upsert(new NoteEntity("first", owner, "Remember the milk"));
		}

		try (DefaultDatabaseService database = DefaultDatabaseService.open(settings(), workingPath)) {
			database.getSchemaInstaller().install(new NotesSchema());
			EntityAccess<NoteEntity> notes = database.access(NoteEntity.class);

			Optional<NoteEntity> note = notes.find("first");
			assertTrue(note.isPresent());
			assertEquals(owner, note.get().ownerId());
			assertEquals("Remember the milk", note.get().text());
		}
	}

	@Test
	void installingASchemaAgainChangesNothing() throws Exception {
		try (DefaultDatabaseService database = DefaultDatabaseService.open(settings(), workingPath)) {
			database.getSchemaInstaller().install(new NotesSchema());
			database.access(NoteEntity.class).upsert(new NoteEntity("kept", UUID.randomUUID(), "still here"));

			database.getSchemaInstaller().install(new NotesSchema());

			assertTrue(database.access(NoteEntity.class).find("kept").isPresent());
		}
	}

	@Test
	void refusesASchemaBeforeTheOneItRequires() throws Exception {
		try (DefaultDatabaseService database = DefaultDatabaseService.open(settings(), workingPath)) {
			IllegalStateException failure = assertThrows(IllegalStateException.class,
					() -> database.getSchemaInstaller().install(new NoteTagsSchema()));

			assertTrue(failure.getMessage().contains(NotesSchema.STREAM));
		}
	}

	@Table("contract_notes")
	public record NoteEntity(@Id @Length(64) String noteId, UUID ownerId, String text) {
	}

	@Table("contract_note_tags")
	public record NoteTagEntity(@Id @Length(64) String tag, String noteId) {
	}

	static final class NotesSchema implements DatabaseSchema {
		static final String STREAM = "contract/notes";

		@Override
		public @NotNull String stream() {
			return STREAM;
		}

		@Override
		public @NotNull Schema schema() {
			return Schema.of(Dialectica.entity(NoteEntity.class).table());
		}
	}

	static final class NoteTagsSchema implements DatabaseSchema {
		@Override
		public @NotNull String stream() {
			return "contract/note-tags";
		}

		@Override
		public @NotNull Schema schema() {
			return Schema.of(Dialectica.entity(NoteTagEntity.class).table());
		}

		@Override
		public @NotNull Set<String> requires() {
			return Set.of(NotesSchema.STREAM);
		}
	}
}
