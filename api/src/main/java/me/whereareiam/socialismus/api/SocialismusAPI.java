package me.whereareiam.socialismus.api;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SocialismusAPI {
	private static Socialismus instance;

	@Inject
	public SocialismusAPI(Socialismus instance) {
		SocialismusAPI.instance = instance;
	}

	public static Socialismus getInstance() {
		return instance;
	}
}
