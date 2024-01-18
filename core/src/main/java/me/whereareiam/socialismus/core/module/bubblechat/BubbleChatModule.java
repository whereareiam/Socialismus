package me.whereareiam.socialismus.core.module.bubblechat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.socialismus.core.config.module.bubblechat.BubbleChatConfig;
import me.whereareiam.socialismus.core.config.setting.SettingsConfig;
import me.whereareiam.socialismus.core.module.Module;
import me.whereareiam.socialismus.core.util.LoggerUtil;

import java.nio.file.Path;

@Singleton
public class BubbleChatModule implements Module {
	private final SettingsConfig settingsConfig;
	private final BubbleChatConfig bubbleChatConfig;
	private final Path modulePath;
	private boolean moduleStatus;

	@Inject
	public BubbleChatModule(LoggerUtil loggerUtil, @Named("modulePath") Path modulePath, SettingsConfig settingsConfig, BubbleChatConfig bubbleChatConfig) {
		this.settingsConfig = settingsConfig;
		this.bubbleChatConfig = bubbleChatConfig;
		this.modulePath = modulePath;

		loggerUtil.trace("Initializing class: " + this);
	}

	@Override
	public void initialize() {
		bubbleChatConfig.reload(modulePath.resolve("bubblechat.yml"));
		moduleStatus = true;
	}

	@Override
	public boolean isEnabled() {
		return moduleStatus == settingsConfig.modules.bubblechat;
	}

	@Override
	public void reload() {
		bubbleChatConfig.reload(modulePath.resolve("bubblechat.yml"));
	}
}
