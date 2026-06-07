package me.whereareiam.socialismus.common.config.dynamic;

import lombok.Getter;
import me.whereareiam.configura.ConfigDocument;
import me.whereareiam.socialismus.model.chat.Chat;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ChatsConfig extends ConfigDocument {
	private List<Chat> chats = new ArrayList<>();
}
