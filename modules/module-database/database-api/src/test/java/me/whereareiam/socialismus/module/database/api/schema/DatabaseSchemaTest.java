package me.whereareiam.socialismus.module.database.api.schema;

import me.whereareiam.dialectica.Dialectica;
import me.whereareiam.dialectica.entity.annotation.Id;
import me.whereareiam.dialectica.entity.annotation.Length;
import me.whereareiam.dialectica.entity.annotation.Nullable;
import me.whereareiam.dialectica.entity.annotation.Table;
import me.whereareiam.dialectica.schema.ColumnDefinition;
import me.whereareiam.dialectica.schema.Schema;
import me.whereareiam.dialectica.schema.TableDefinition;
import me.whereareiam.dialectica.schema.change.AddColumn;
import me.whereareiam.dialectica.schema.type.ColumnType;
import me.whereareiam.strata.Migration;
import me.whereareiam.strata.adapter.database.DatabaseContext;
import me.whereareiam.strata.adapter.database.dialectica.SchemaMigration;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.OptionalInt;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Builds the examples of {@link DatabaseSchema}'s documentation, so that they keep compiling and describe tables.
 */
class DatabaseSchemaTest {
	@Test
	void describesTheTablesOfTheExample() {
		DatabaseSchema schema = new NotesSchema();

		List<TableDefinition> tables = schema.schema().getTables();
		assertEquals(1, tables.size());
		assertEquals("notes_notes", tables.getFirst().getName());
		assertNotNull(tables.getFirst().column("note_id"));
	}

	@Test
	void startsWithoutMigrationsOrRequiredStreams() throws Exception {
		DatabaseSchema schema = new NotesSchema();

		assertTrue(schema.migrations().isEmpty());
		assertTrue(schema.requires().isEmpty());
		assertEquals(OptionalInt.empty(), schema.baseline(null, 0));
	}

	@Test
	void numbersTheMigrationsOfTheExample() {
		List<Migration<DatabaseContext>> migrations = new TitledNotesSchema().migrations();

		assertEquals(List.of(1, 2), migrations.stream().map(Migration::getVersion).toList());
		assertEquals(List.of("note-title", "fill-titles"), migrations.stream().map(Migration::getName).toList());
	}

	@Table("notes_notes")
	public record NoteEntity(@Id @Length(64) String noteId, UUID uniqueId, String text) {
	}

	@Table("notes_notes")
	public record TitledNoteEntity(@Id @Length(64) String noteId, UUID uniqueId, String text, @Nullable @Length(64) String title) {
	}

	private static final class NotesSchema implements DatabaseSchema {
		@Override
		public @NotNull String stream() {
			return "notes/note";
		}

		@Override
		public @NotNull Schema schema() {
			return Schema.of(Dialectica.entity(NoteEntity.class).table());
		}
	}

	private static final class TitledNotesSchema implements DatabaseSchema {
		@Override
		public @NotNull String stream() {
			return "notes/note";
		}

		@Override
		public @NotNull Schema schema() {
			return Schema.of(Dialectica.entity(TitledNoteEntity.class).table());
		}

		@Override
		public @NotNull List<Migration<DatabaseContext>> migrations() {
			return List.of(
					SchemaMigration.of(1, "note-title",
							new AddColumn("notes_notes", ColumnDefinition.builder("title", ColumnType.varchar(64)).build())),
					new Migration<>(2, "fill-titles", database -> database.update(
							"UPDATE notes_notes SET title = 'Untitled' WHERE title IS NULL"))
			);
		}
	}
}
