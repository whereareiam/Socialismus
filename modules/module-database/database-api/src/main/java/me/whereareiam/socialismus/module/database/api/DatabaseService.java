package me.whereareiam.socialismus.module.database.api;

import me.whereareiam.dialectica.adapter.jdbi.entity.EntityAccess;
import me.whereareiam.socialismus.module.database.api.schema.SchemaInstaller;
import org.jetbrains.annotations.NotNull;

/**
 * The database SocialismusDatabase opens and registers as the {@link DatabaseResources#DATABASE} resource. Servers
 * run without it; a module that keeps data depends on SocialismusDatabase optionally, looks the database up in
 * Socialismus's {@link me.whereareiam.socialismus.registry.ResourceRegistry}, and keeps its data elsewhere, such as
 * in memory, while none is registered:
 * <pre>{@code
 * registry.get(DatabaseResources.DATABASE).ifPresent(database -> {
 *     database.getSchemaInstaller().install(new NotesSchema());
 *     EntityAccess<NoteEntity> notes = database.access(NoteEntity.class);
 * });
 * }</pre>
 *
 * <p>The database is open while it is registered. Its module unregisters it before it closes the connection, so
 * a consumer that follows the {@link me.whereareiam.socialismus.event.resource.ResourceUnregisteredEvent} can
 * still use it there.
 */
public interface DatabaseService {
	/**
	 * Returns Dialectica's access to the rows of an entity's table. It speaks the database's dialect, so the
	 * entity's columns are stored the way its schema declares them:
	 * <pre>{@code
	 * EntityAccess<NoteEntity> notes = database.access(NoteEntity.class);
	 * notes.upsert(new NoteEntity("first", uniqueId, "Remember the milk"));
	 * Optional<NoteEntity> note = notes.find("first");
	 * }</pre>
	 *
	 * <p>The entity's table belongs to a {@link me.whereareiam.socialismus.module.database.api.schema.DatabaseSchema} that has
	 * been {@linkplain SchemaInstaller#install installed}. The access works while the database is registered.
	 *
	 * @param entityType class of the entity, with Dialectica's annotations
	 * @param <T>        entity type
	 * @return access to the entity's rows
	 * @throws IllegalArgumentException when the class is not a valid Dialectica entity
	 */
	<T> @NotNull EntityAccess<T> access(@NotNull Class<T> entityType);

	/**
	 * Returns the installer that brings the schema of each owner of tables up to date in this database.
	 *
	 * @return the schema installer
	 */
	@NotNull SchemaInstaller getSchemaInstaller();
}
