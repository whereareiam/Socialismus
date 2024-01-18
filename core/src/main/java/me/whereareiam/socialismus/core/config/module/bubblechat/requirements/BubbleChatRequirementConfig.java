package me.whereareiam.socialismus.core.config.module.bubblechat.requirements;

import com.google.inject.Singleton;

@Singleton
public class BubbleChatRequirementConfig {
	public boolean enabled = true;
	public BubbleChatSenderRequirementConfig sender = new BubbleChatSenderRequirementConfig();
	public BubbleChatRecipientRequirementConfig recipient = new BubbleChatRecipientRequirementConfig();
}
