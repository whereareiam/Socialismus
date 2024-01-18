package me.whereareiam.socialismus.core.model.chat;

public class ChatRequirements {
	public boolean enabled = true;
	public boolean mentionable = true;
	public ChatSenderRequirements sender = new ChatSenderRequirements();
	public ChatRecipientRequirements recipient = new ChatRecipientRequirements();
}
