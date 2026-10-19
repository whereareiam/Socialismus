package me.whereareiam.socialismus.module.dialogue.network;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.socialismus.module.dialogue.api.type.DeliveryStatus;

/**
 * The answer of the node a private message was sent to: whether it delivered it, why not, and how the recipient spells
 * their name.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmationRecord {
	private String id;
	private String target;
	private String recipientName;
	private DeliveryStatus status;
}
