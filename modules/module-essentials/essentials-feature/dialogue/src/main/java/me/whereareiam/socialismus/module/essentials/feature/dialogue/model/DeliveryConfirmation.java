package me.whereareiam.socialismus.module.essentials.feature.dialogue.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.type.DeliveryStatus;

import java.time.Instant;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class DeliveryConfirmation {
	private String id;

	private String senderName;
	private String recipientName;

	private DeliveryStatus status;
	private String originServer;
	private String deliveryServer;
	private Instant deliveryTime;
}
