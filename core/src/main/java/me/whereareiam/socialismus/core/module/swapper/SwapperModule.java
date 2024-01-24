package me.whereareiam.socialismus.core.module.swapper;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.socialismus.api.model.swapper.Swapper;
import me.whereareiam.socialismus.api.module.Module;
import me.whereareiam.socialismus.core.config.module.swapper.SwapperConfig;
import me.whereareiam.socialismus.core.config.setting.SettingsConfig;
import me.whereareiam.socialismus.core.integration.IntegrationManager;
import me.whereareiam.socialismus.core.listener.state.ChatListenerState;
import me.whereareiam.socialismus.core.listener.state.JoinListenerState;
import me.whereareiam.socialismus.core.util.LoggerUtil;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Singleton
public class SwapperModule implements Module {
	private final Injector injector;
	private final LoggerUtil loggerUtil;
	private final IntegrationManager integrationManager;
	private final SettingsConfig settingsConfig;
	private final Path swapperPath;
	private final List<Swapper> swappers = new ArrayList<>();

	private boolean moduleStatus;

	@Inject
	public SwapperModule(Injector injector, LoggerUtil loggerUtil, IntegrationManager integrationManager,
	                     @Named("modulePath") Path modulePath, SettingsConfig settingsConfig) {
		this.injector = injector;
		this.loggerUtil = loggerUtil;
		this.integrationManager = integrationManager;
		this.settingsConfig = settingsConfig;

		this.swapperPath = modulePath.resolve("swapper");

		loggerUtil.trace("Initializing class: " + this);
	}

	private void registerSwappers() {
		loggerUtil.debug("Registering swappers");
		List<File> files = Arrays.stream(swapperPath.toFile().listFiles()).filter(file -> file.getName().endsWith(".yml")).toList();
		if (files.isEmpty()) {
			loggerUtil.trace("Creating an example swapper, because dir is empty");
			SwapperConfig swapperConfig = createExampleSwapperConfig();
			swapperConfig.reload(swapperPath.resolve("example.yml"));
			swappers.addAll(swapperConfig.swappers);
		} else {
			for (File file : files) {
				loggerUtil.trace("Trying to register swappers in config: " + file.getName());
				SwapperConfig swapperConfig = injector.getInstance(SwapperConfig.class);
				swapperConfig.reload(file.toPath());
				List<Swapper> enabledSwappers = swapperConfig.swappers.stream()
						.filter(swapper -> swapper.enabled)
						.toList();
				if (!enabledSwappers.isEmpty()) {
					loggerUtil.trace("Adding new swappers (" + enabledSwappers.size() + ")");
					swappers.addAll(enabledSwappers);
				}
			}
		}
	}

	public void cleanSwappers() {
		loggerUtil.debug("Cleaning swappers");
		swappers.clear();
	}

	public List<Swapper> getSwappers() {
		return swappers;
	}

	private SwapperConfig createExampleSwapperConfig() {
		SwapperConfig swapperConfig = injector.getInstance(SwapperConfig.class);

		String[] phrases = {"happy", "sad", "love", "laugh", "cry", "angry", "surprised", "confused", "excited", "bored"};
		String[] replacements = {":)", ":(", "<3", ":D", ":'(", ">:(", ":O", ":/", ":)", ":|"};

		for (int i = 0; i < 10; i++) {
			Swapper swapper = injector.getInstance(Swapper.class);
			swapper.placeholders.add("{" + phrases[i] + "}");
			swapper.content.add(replacements[i]);
			swapper.settings.directSearch = true;
			swapper.settings.randomContent = false;
			swapper.requirements.enabled = false;
			swapper.requirements.usePermission = "";

			swapperConfig.swappers.add(swapper);
		}

		return swapperConfig;
	}

	@Override
	public void initialize() {
		File swapperDir = swapperPath.toFile();
		if (!swapperDir.exists()) {
			boolean isCreated = swapperDir.mkdir();
			loggerUtil.debug("Creating swapper dir");
			if (!isCreated) {
				loggerUtil.severe("Failed to create directory: " + swapperPath);
			}
		}

		ChatListenerState.setRequired(true);
		if (integrationManager.isIntegrationEnabled("ProtocolLib"))
			JoinListenerState.setRequired(true);

		registerSwappers();
		moduleStatus = true;
	}

	@Override
	public boolean isEnabled() {
		return moduleStatus == settingsConfig.modules.swapper.enabled;
	}

	@Override
	public void reload() {
		cleanSwappers();
		registerSwappers();
	}
}
