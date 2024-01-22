package me.whereareiam.socialismus.core.module.swapper;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.swapper.Swapper;
import me.whereareiam.socialismus.core.config.setting.SettingsConfig;
import me.whereareiam.socialismus.core.integration.protocollib.PacketSender;
import me.whereareiam.socialismus.core.integration.protocollib.entity.PlayerPacket;
import me.whereareiam.socialismus.core.util.LoggerUtil;
import org.bukkit.entity.Player;

@Singleton
public class SwapperSuggester {
	private final Injector injector;
	private final LoggerUtil loggerUtil;
	private final SettingsConfig settingsConfig;
	private final SwapperModule swapperModule;
	private final SwapperRequirementValidator swapperRequirementValidator;

	@Inject
	public SwapperSuggester(Injector injector, LoggerUtil loggerUtil, SettingsConfig settingsConfig,
	                        SwapperModule swapperModule, SwapperRequirementValidator swapperRequirementValidator) {
		this.injector = injector;
		this.loggerUtil = loggerUtil;
		this.settingsConfig = settingsConfig;
		this.swapperModule = swapperModule;
		this.swapperRequirementValidator = swapperRequirementValidator;
	}

	public void suggestSwappers(Player player) {
		if (!settingsConfig.modules.swapper.suggest)
			return;

		final PacketSender packetSender = injector.getInstance(PacketSender.class);
		final PlayerPacket playerPacket = injector.getInstance(PlayerPacket.class);
		loggerUtil.debug("Sending swappers to " + player.getName());
		for (Swapper swapper : swapperModule.getSwappers()) {
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
}
