package me.whereareiam.socialismus.module.essentials.feature.dialogue.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class Dialogue {
	private SocialismusPlayer sender;
	private String recipientName;
	private String content;

	private String origin;
}
