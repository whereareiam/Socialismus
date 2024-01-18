package me.whereareiam.socialismus.core.module.swapper;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.socialismus.api.model.swapper.Swapper;
import me.whereareiam.socialismus.core.config.module.swapper.SwapperConfig;
import me.whereareiam.socialismus.core.config.setting.SettingsConfig;
import me.whereareiam.socialismus.core.integration.protocollib.PacketSender;
import me.whereareiam.socialismus.core.integration.protocollib.entity.PlayerPacket;
import me.whereareiam.socialismus.core.listener.state.ChatListenerState;
import me.whereareiam.socialismus.core.module.Module;
import me.whereareiam.socialismus.core.util.LoggerUtil;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Singleton
public class SwapperModule implements Module {
	private final Injector injector;
	private final LoggerUtil loggerUtil;
	private final SettingsConfig settingsConfig;
	private final SwapperRequirementValidator swapperRequirementValidator;
	private final Path swapperPath;
	private final List<Swapper> swappers = new ArrayList<>();

	private boolean moduleStatus;

	@Inject
	public SwapperModule(Injector injector, LoggerUtil loggerUtil,
						 @Named("modulePath") Path modulePath, SettingsConfig settingsConfig,
						 SwapperRequirementValidator swapperRequirementValidator) {
		this.injector = injector;
		this.loggerUtil = loggerUtil;
		this.settingsConfig = settingsConfig;

		this.swapperRequirementValidator = swapperRequirementValidator;
		this.swapperPath = modulePath.resolve("swapper");

		loggerUtil.trace("Initializing class: " + this);
	}

	private void registerSwappers() {
		loggerUtil.debug("Registering swappers");
		List<File> files = Arrays.stream(swapperPath.toFile().listFiles()).filter(file -> file.getName().endsWith(".yml")).toList();
		if (files.isEmpty()) {
			loggerUtil.debug("Creating an example swapper, because dir is empty");
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
				if (! enabledSwappers.isEmpty()) {
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

	public void suggestSwappers(Player player) {
		if (! settingsConfig.modules.swapper.suggest)
			return;

		final PacketSender packetSender = injector.getInstance(PacketSender.class);
		final PlayerPacket playerPacket = injector.getInstance(PlayerPacket.class);
		loggerUtil.debug("Sending swappers to " + player.getName());
		for (Swapper swapper : swappers) {
			if (swapperRequirementValidator.validatePlayer(swapper, player, false)) {
				loggerUtil.trace("Player " + player.getName() + " will receive these swappers: " + swapper.placeholders);
				for (String placeholder : swapper.placeholders) {
					packetSender.sendPacket(
							player,
							playerPacket.createPlayerInfoPacket(placeholder)
					);
				}
			}
		}
	}

	public List<Swapper> getSwappers() {
		return swappers;
	}

	private SwapperConfig createExampleSwapperConfig() {
		SwapperConfig swapperConfig = injector.getInstance(SwapperConfig.class);
		Swapper exampleSwapper = injector.getInstance(Swapper.class);
		exampleSwapper.placeholders.add("{example}");
		exampleSwapper.content.add("example");
		exampleSwapper.settings.directSearch = true;
		exampleSwapper.settings.randomContent = false;
		exampleSwapper.requirements.enabled = false;
		exampleSwapper.requirements.usePermission = "";

		swapperConfig.swappers.add(exampleSwapper);
		return swapperConfig;
	}

	@Override
	public void initialize() {
		File swapperDir = swapperPath.toFile();
		if (! swapperDir.exists()) {
			boolean isCreated = swapperDir.mkdir();
			loggerUtil.debug("Creating swapper dir");
			if (! isCreated) {
				loggerUtil.severe("Failed to create directory: " + swapperPath);
			}
		}

		ChatListenerState.setRequired(true);
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
