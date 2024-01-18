package me.whereareiam.socialismus.core.config.setting.statistic;

public class StatisticSettingsConfig {
	public boolean enabled = true;
	public ChatStatisticSettingsConfig chat = new ChatStatisticSettingsConfig();
	public SwapperStatisticSettingsConfig swapper = new SwapperStatisticSettingsConfig();
	public BubbleChatStatisticSettingsConfig bubbleChat = new BubbleChatStatisticSettingsConfig();
}
