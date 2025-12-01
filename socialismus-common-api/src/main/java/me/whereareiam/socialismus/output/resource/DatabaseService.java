package me.whereareiam.socialismus.output.resource;

import com.j256.ormlite.dao.Dao;

public interface DatabaseService {
	<T> Dao<T, ?> dao(Class<T> clazz);

	void migrate(Class<?>... entities);
}