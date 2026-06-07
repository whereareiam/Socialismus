package me.whereareiam.socialismus.model;

import lombok.*;
import me.whereareiam.configura.annotation.PreserveUnknownFields;
import me.whereareiam.configura.feature.extension.api.annotation.ExtendableDocument;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configurable definition for a Socialismus command entry.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@ExtendableDocument
@PreserveUnknownFields
@Builder(toBuilder = true)
public class CommandDefinition {
	@Builder.Default
	private boolean enabled = true;
	private List<String> aliases;

	@Builder.Default
	private String permission = "";
	private String description;
	private String usage;

	private Cooldown cooldown;

	@Builder.Default
	private Map<String, String> arguments = new HashMap<>();
	@Builder.Default
	private boolean hide = false;

	/**
	 * Cooldown settings for a command definition.
	 */
	@Getter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	@ExtendableDocument
	@Builder(toBuilder = true)
	public static class Cooldown {
		@Builder.Default
		private boolean enabled = false;
		@Builder.Default
		private int duration = 0;
		@Builder.Default
		private String group = "default";
	}
}
