package me.whereareiam.socialismus.module.dialogue.api.model.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.socialismus.module.dialogue.api.model.DialogueFormat;

import java.util.List;

/**
 * How a private message looks, {@code formats/messaging} in Dialogue's working directory. Each viewer sees the last
 * format of their list whose requirements the sender and the recipient meet.
 */
@Getter
@Setter
@ToString
public class MessagingFormats {
	/**
	 * What the sender sees of their own message.
	 */
	private List<DialogueFormat> sent;

	/**
	 * What the recipient sees.
	 */
	private List<DialogueFormat> received;
}
