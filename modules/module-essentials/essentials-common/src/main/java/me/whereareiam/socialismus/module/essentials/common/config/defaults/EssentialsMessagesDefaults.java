package me.whereareiam.socialismus.module.essentials.common.config.defaults;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.module.essentials.api.model.config.EssentialsMessages;

import java.util.List;

@Singleton
public class EssentialsMessagesDefaults implements DefaultsProvider<EssentialsMessages> {
	@Override
	public EssentialsMessages supply(EssentialsMessages config) {
		// Default values
		EssentialsMessages.Commands commands = new EssentialsMessages.Commands();

		EssentialsMessages.Commands.FeaturesCommand featuresCommand = new EssentialsMessages.Commands.FeaturesCommand();
		featuresCommand.setFormat(List.of(
				" ",
				"<gold><bold> Socialismus</bold> <white>Features",
				" ",
				"{features}",
				" "
		));
		featuresCommand.setFeatureFormat("  <dark_gray>- <green>{feature}");
		featuresCommand.setNoFeatures("  <red>No features available.");
		commands.setFeaturesCommand(featuresCommand);

		config.setCommands(commands);

		return config;
	}
}
