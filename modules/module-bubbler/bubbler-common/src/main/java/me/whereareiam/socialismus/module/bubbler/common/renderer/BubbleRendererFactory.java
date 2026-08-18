package me.whereareiam.socialismus.module.bubbler.common.renderer;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerSettings;
import me.whereareiam.socialismus.module.bubbler.api.renderer.BubbleRenderer;
import me.whereareiam.socialismus.module.bubbler.api.type.BubbleType;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class BubbleRendererFactory {
	private final Provider<BubblerSettings> settings;
	private final TextDisplayRenderer textDisplayRenderer;
	@Getter
	private final ArmorStandRenderer armorStandRenderer;

	public BubbleRenderer getRenderer() {
		BubbleType type = settings.get().getBubbleType();
		return getRenderer(type);
	}

	public BubbleRenderer getRenderer(BubbleType type) {
		return switch (type) {
			case TEXT_DISPLAY -> textDisplayRenderer;
			case ARMOR_STAND -> armorStandRenderer;
		};
	}
}
