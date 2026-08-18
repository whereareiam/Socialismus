package me.whereareiam.socialismus.module.essentials.api.model.config;

import com.google.inject.Singleton;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@Singleton
public class EssentialsMessages {
	private Commands commands;

	@Getter
	@Setter
	@ToString
	public static class Commands {
		private FeaturesCommand featuresCommand;

		@Getter
		@Setter
		@ToString
		public static class FeaturesCommand {
			private List<String> format;
			private String featureFormat;
			private String noFeatures;
		}
	}
}
