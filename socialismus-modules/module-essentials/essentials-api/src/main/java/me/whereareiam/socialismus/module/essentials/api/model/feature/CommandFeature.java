package me.whereareiam.socialismus.module.essentials.api.model.feature;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
public class CommandFeature extends Feature {
	private boolean registerCommands;

	public static CommandFeature from(Feature feature) {
		return CommandFeature.builder()
				.enabled(feature.isEnabled())
				.build();
	}
}
