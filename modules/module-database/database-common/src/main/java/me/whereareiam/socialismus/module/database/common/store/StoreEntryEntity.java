package me.whereareiam.socialismus.module.database.common.store;

import me.whereareiam.dialectica.entity.annotation.Column;
import me.whereareiam.dialectica.entity.annotation.Id;
import me.whereareiam.dialectica.entity.annotation.Length;
import me.whereareiam.dialectica.entity.annotation.Nullable;
import me.whereareiam.dialectica.entity.annotation.Table;

import java.time.Instant;

/**
 * A value of one of Socialismus's persistent stores: every store shares the table, its rows told apart by the
 * store's name.
 *
 * @param storeName  the store's name, such as {@code mentionator:modes}
 * @param entryKey   the key's text
 * @param entryValue the value as JSON
 * @param expiresAt  when the value expires; {@code null} never
 */
@Table("socialismus_store_entries")
public record StoreEntryEntity(
		@Id @Length(128) @Column("store_name") String storeName,
		@Id @Length(255) @Column("entry_key") String entryKey,
		@Column("entry_value") String entryValue,
		@Nullable @Column("expires_at") Instant expiresAt
) {
}
