package me.whereareiam.socialismus.module.database.common.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A database server, which several servers can share.
 */
@Getter
@Setter
@ToString(callSuper = true)
public abstract sealed class ServerSettings extends DatabaseSettings permits MysqlSettings, PostgresSettings {
	private String host = "localhost";
	private int port;
	private String database = "socialismus";
	private String username;
	@ToString.Exclude
	private String password = "";

	/**
	 * Extra connection parameters, as {@code key=value} pairs joined by {@code &}, such as {@code useSSL=true}; empty
	 * for none.
	 */
	private String parameters = "";

	protected ServerSettings(DatabaseType type, int port, String username) {
		super(type);
		this.port = port;
		this.username = username;
	}
}
