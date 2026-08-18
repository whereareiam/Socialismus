package me.whereareiam.socialismus.module.essentials.api.model.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EssentialsSettings {
	private Announce announce;

	@Getter
	@Setter
	@ToString
	public static class Announce {
		private boolean featureInitialization;
	}
}
