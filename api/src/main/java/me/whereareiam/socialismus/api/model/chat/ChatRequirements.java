package me.whereareiam.socialismus.api.model.chat;

public class ChatRequirements {
	public boolean enabled = true;
	public ChatSenderRequirements sender = new ChatSenderRequirements();
	public ChatRecipientRequirements recipient = new ChatRecipientRequirements();
}
