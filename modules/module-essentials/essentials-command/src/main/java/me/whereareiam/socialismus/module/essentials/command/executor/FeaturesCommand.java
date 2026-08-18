package me.whereareiam.socialismus.module.essentials.command.executor;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.commandant.annotation.Definition;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.essentials.api.feature.FeatureManager;
import me.whereareiam.socialismus.module.essentials.api.model.config.EssentialsMessages;
import me.whereareiam.socialismus.module.essentials.api.model.feature.Feature;
import org.incendo.cloud.annotations.Command;

import java.util.Map;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class FeaturesCommand {
	private static final String COMMAND_NAME = "features";
	private final Provider<EssentialsMessages> messages;
	private final FeatureManager featureManager;

	@Definition(COMMAND_NAME)
	@Command("features")
	public void onCommand(SocialismusPlayer player) {
		var messages = this.messages.get().getCommands().getFeaturesCommand();
		final Map<String, Feature> features = featureManager.getActiveFeatures();

		String format = String.join("\n", messages.getFormat());

		if (features.isEmpty()) {
			format = format.replace("{features}", messages.getNoFeatures());
			player.sendMessage(Serializer.serialize(player, format));
			return;
		}

		StringBuilder featureList = new StringBuilder();
		for (String feature : features.keySet()) {
			String featureFormat = messages.getFeatureFormat().replace("{feature}", feature);
			featureList.append(featureFormat).append("\n");
		}

		format = format.replace("{features}", featureList.toString());
		player.sendMessage(Serializer.serialize(player, format));
	}
}
