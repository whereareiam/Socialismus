package me.whereareiam.socialismus.core.module;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.socialismus.api.module.Module;
import me.whereareiam.socialismus.core.module.announcer.AnnouncerModule;
import me.whereareiam.socialismus.core.module.bubblechat.BubbleChatModule;
import me.whereareiam.socialismus.core.module.chat.ChatModule;
import me.whereareiam.socialismus.core.module.chatmention.ChatMentionModule;
import me.whereareiam.socialismus.core.module.swapper.SwapperModule;
import me.whereareiam.socialismus.core.util.LoggerUtil;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Singleton
public class ModuleLoader {
	private final Injector injector;
	private final LoggerUtil loggerUtil;

	private final List<Module> modules = new ArrayList<>();

	@Inject
	public ModuleLoader(Injector injector, LoggerUtil loggerUtil,
	                    @Named("modulePath") Path modulePath
	) {
		this.injector = injector;
		this.loggerUtil = loggerUtil;

		loggerUtil.trace("Initializing class: " + this);

		File moduleFile = modulePath.toFile();
		if (!moduleFile.exists()) {
			boolean isCreated = moduleFile.mkdir();
			loggerUtil.debug("Creating module dir");
			if (!isCreated) {
				loggerUtil.severe("Failed to create directory: " + moduleFile);
			}
		}
	}

	public void loadModules() {
		List<Class<? extends Module>> modules = Arrays.asList(
				ChatModule.class,
				BubbleChatModule.class,
				SwapperModule.class,
				AnnouncerModule.class,
				ChatMentionModule.class
		);

		for (Class<? extends Module> moduleClass : modules) {
			try {
				Module module = injector.getInstance(moduleClass);
				module.initialize();

				if (module.isEnabled()) {
					this.modules.add(module);
				}
			} catch (Exception e) {
				loggerUtil.severe(e.getMessage());
			}
		}
	}

	public void reloadModules() {
		loggerUtil.debug("Reloading modules");

		for (Module module : modules) {
			module.reload();
		}
	}

	public int getChatCount() {
		ChatModule chatModule = (ChatModule) modules.stream()
				.filter(module -> module instanceof ChatModule)
				.findFirst()
				.orElse(null);

		if (chatModule == null) {
			return 0;
		}

		return chatModule.getChats().size();
	}

	public int getSwapperCount() {
		SwapperModule swapperModule = (SwapperModule) modules.stream()
				.filter(module -> module instanceof SwapperModule)
				.findFirst()
				.orElse(null);

		if (swapperModule == null) {
			return 0;
		}

		return swapperModule.getSwappers().size();
	}

	public int getAnnouncementsCount() {
		AnnouncerModule announcerModule = (AnnouncerModule) modules.stream()
				.filter(module -> module instanceof AnnouncerModule)
				.findFirst()
				.orElse(null);

		if (announcerModule == null) {
			return 0;
		}

		return announcerModule.getAnnouncements().size();
	}
}
