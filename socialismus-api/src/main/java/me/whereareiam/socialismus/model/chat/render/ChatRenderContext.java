package me.whereareiam.socialismus.model.chat.render;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.socialismus.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChatRenderContext {
	private FormattedChatMessage message;
	private SocialismusPlayer recipient;

	public SocialismusPlayer getSender() {
		return message != null ? message.getSender() : null;
	}
}
