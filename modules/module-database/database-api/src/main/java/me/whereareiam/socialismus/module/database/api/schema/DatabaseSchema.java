package me.whereareiam.socialismus.module.database.api.schema;

import me.whereareiam.dialectica.schema.Schema;
import me.whereareiam.strata.Migration;
import me.whereareiam.strata.adapter.database.DatabaseContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.OptionalInt;
import java.util.Set;

/**
 * The tables one owner keeps in Socialismus's database, and how an installation of them is upgraded.
 *
 * <p>Each owner, typically a module, declares one schema and hands it to the {@link SchemaInstaller} before it
 * reads or writes its tables. The installer runs it as a Strata migration stream named {@link #stream()}: a new
 * installation gets {@link #schema()} in its newest form, and an existing one runs the {@link #migrations()} it
 * has not seen yet. Strata records the stream's version in the {@code strata_history} table, and holds a lock on
 * PostgreSQL, MySQL and MariaDB, so servers that share a database install a schema once.
 *
 * <p>Example:
 * <pre>{@code
 * public final class NotesSchema implements DatabaseSchema {
 *     @Override
 *     public @NotNull String stream() {
 *         return "notes/note";
 *     }
 *
 *     @Override
 *     public @NotNull Schema schema() {
 *         return Schema.of(Dialectica.entity(NoteEntity.class).table());
 *     }
 * }
 * }</pre>
 *
 * <p>Each table is an entity class with Dialectica's annotations, whose names start with the owner's name:
 * <pre>{@code
 * @Table("notes_notes")
 * public record NoteEntity(@Id @Length(64) String noteId, UUID uniqueId, String text) {
 * }
 * }</pre>
 *
 * <p>The schema describes what the last migration leaves behind. Once released, a migration's version and what it
 * does never change; a new one is appended with the next version.
 */
public interface DatabaseSchema {
	/**
	 * Returns the name Strata tracks this schema under. It is made of lowercase segments separated by slashes,
	 * starting with the owner's name, such as {@code dialogue/conversation}, and never changes.
	 *
	 * @return the stream name
	 */
	@NotNull String stream();

	/**
	 * Returns the tables in their newest form, as a new installation gets them. Tables of another owner that they
	 * reference belong to a stream listed in {@link #requires()}.
	 *
	 * @return the schema
	 */
	@NotNull Schema schema();

	/**
	 * Returns the numbered changes that bring an older installation up to {@link #schema()}, in order, starting at
	 * version 1 without gaps. Strata's {@code SchemaMigration}, from {@code strata-adapter-database-dialectica},
	 * applies Dialectica schema changes, which skip what the database already has; a data change is a plain
	 * {@link Migration}:
	 * <pre>{@code
	 * @Override
	 * public @NotNull List<Migration<DatabaseContext>> migrations() {
	 *     return List.of(
	 *             SchemaMigration.of(1, "note-title",
	 *                     new AddColumn("notes_notes", ColumnDefinition.builder("title", ColumnType.varchar(64)).build())),
	 *             new Migration<>(2, "fill-titles", database -> database.update(
	 *                     "UPDATE notes_notes SET title = 'Untitled' WHERE title IS NULL"))
	 *     );
	 * }
	 * }</pre>
	 *
	 * <p>The entity then gains the column too, so that {@link #schema()} keeps describing the newest form.
	 *
	 * @return the migrations; empty by default
	 */
	default @NotNull List<Migration<DatabaseContext>> migrations() {
		return List.of();
	}

	/**
	 * Tells where an installation stands that Strata has no version for, which is the case the first time this
	 * stream runs against a database.
	 *
	 * <p>Return {@link OptionalInt#empty()} for a new installation, which gets {@link #schema()} and is recorded at
	 * the latest version; {@code 0} for tables that predate every migration, which then all run; or the version
	 * whose layout the tables already have. The default answers empty, so every database the stream has not seen
	 * counts as new; creating the schema skips tables that exist:
	 * <pre>{@code
	 * @Override
	 * public @NotNull OptionalInt baseline(@NotNull DatabaseContext database, int latest) throws SQLException {
	 *     return database.tableExists("notes_notes") ? OptionalInt.of(0) : OptionalInt.empty();
	 * }
	 * }</pre>
	 *
	 * @param database the database, under the stream's lock
	 * @param latest   the version of the last migration
	 * @return the version the installation is at, or empty for a new installation
	 * @throws Exception when the database cannot be inspected
	 */
	default @NotNull OptionalInt baseline(@NotNull DatabaseContext database, int latest) throws Exception {
		return OptionalInt.empty();
	}

	/**
	 * Returns the streams that have to be installed before this one, typically because this schema's tables
	 * reference theirs.
	 *
	 * @return the required stream names; empty by default
	 */
	default @NotNull Set<String> requires() {
		return Set.of();
	}
}
