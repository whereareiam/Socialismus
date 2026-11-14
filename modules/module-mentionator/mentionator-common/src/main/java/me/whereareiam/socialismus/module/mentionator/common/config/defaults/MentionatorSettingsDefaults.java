package me.whereareiam.socialismus.module.mentionator.common.config.defaults;

import com.google.inject.Singleton;
import me.whereareiam.conditia.model.Requirements;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.module.mentionator.api.model.config.MentionatorSettings;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static me.whereareiam.socialismus.module.mentionator.common.config.defaults.ConditionLines.line;

@Singleton
public class MentionatorSettingsDefaults implements DefaultsProvider<MentionatorSettings> {
	@Override
	public MentionatorSettings supply(MentionatorSettings settings) {
		MentionatorSettings.Limits limits = new MentionatorSettings.Limits();
		limits.setPerMessage(new ArrayList<>(List.of(
				new MentionatorSettings.PerMessage(3, Requirements.none()),
				new MentionatorSettings.PerMessage(10, ConditionLines.of(line("permission", "socialismus.mentionator.limit.vip"))),
				new MentionatorSettings.PerMessage(-1, ConditionLines.of(line("permission", "socialismus.admin")))
		)));

		MentionatorSettings.Received received = new MentionatorSettings.Received();
		received.setCount(5);
		received.setPer(Duration.ofMinutes(1));
		received.setBypass(ConditionLines.of(line("permission", "socialismus.mentionator.limit.bypass")));
		limits.setReceived(received);
		limits.setRepeat(Duration.ofSeconds(10));
		limits.setSelf(false);

		settings.setLimits(limits);

		return settings;
	}
}
