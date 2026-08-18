package me.whereareiam.socialismus.module.essentials.feature.dialogue.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.type.DeliveryStatus;

import java.time.Instant;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class Message {
	private String id;

	private SocialismusPlayer sender;
	private String recipientName;

	private String content;
	private Instant timestamp;

	private DeliveryStatus deliveryStatus;

	private String origin;
	private String conversationId;
}
