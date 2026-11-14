package me.whereareiam.socialismus.module.mentionator.common.notify;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.model.SerializerContent;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.scheduler.DelayedRunnableTask;
import me.whereareiam.socialismus.module.mentionator.api.model.notification.NotificationContent;
import me.whereareiam.socialismus.module.mentionator.api.model.notification.content.ActionbarContent;
import me.whereareiam.socialismus.module.mentionator.api.model.notification.content.BossbarContent;
import me.whereareiam.socialismus.module.mentionator.api.model.notification.content.ChatContent;
import me.whereareiam.socialismus.module.mentionator.api.model.notification.content.SoundContent;
import me.whereareiam.socialismus.module.mentionator.api.model.notification.content.TitleContent;
import me.whereareiam.socialismus.module.mentionator.common.tagging.TagRenderer;
import me.whereareiam.socialismus.service.Scheduler;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Shows a tagged player one content of a notification: a chat line, an action bar text, a title, a sound or a boss
 * bar.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class NotificationSender {
	/**
	 * The module name Mentionator's scheduled tasks carry, so they stop with it.
	 */
	public static final String MODULE = "mentionator";

	private static final long TICK_MILLIS = 50;
	private static final int BOSSBAR_STEPS = 20;

	private final Scheduler scheduler;

	/**
	 * Shows a content to a player.
	 *
	 * @param player   the tagged player, on this server
	 * @param content  the content; a sound is played here
	 * @param delivery what tagged them
	 * @param ping     the tag the notification is for
	 */
	public void send(@NotNull SocialismusPlayer player, @NotNull NotificationContent content, @NotNull TagDelivery delivery, @NotNull Ping ping) {
		Audience audience = player.getAudience();
		switch (content) {
			case ChatContent chat -> player.sendMessage(render(player, chat.getChat(), delivery, ping));
			case ActionbarContent actionbar -> audience.sendActionBar(render(player, actionbar.getActionbar(), delivery, ping));
			case TitleContent title -> audience.showTitle(title(player, title, delivery, ping));
			case SoundContent sound -> player.playSound(sound.getSound(), sound.getParameters().getVolume(), sound.getParameters().getPitch());
			case BossbarContent bossbar -> show(audience, bossbar, render(player, bossbar.getBossbar(), delivery, ping));
			default -> {
			}
		}
	}

	private Title title(SocialismusPlayer player, TitleContent content, TagDelivery delivery, Ping ping) {
		Component title = Component.text().color(NamedTextColor.GOLD).append(render(player, content.getTitle(), delivery, ping)).build();
		Component subtitle = content.getSubtitle() == null
				? Component.empty()
				: Component.text().color(NamedTextColor.DARK_GRAY).append(render(player, content.getSubtitle(), delivery, ping)).build();
		TitleContent.Parameters parameters = content.getParameters();

		return Title.title(title, subtitle, Title.Times.times(
				ticks(parameters.getFadeIn()),
				ticks(parameters.getStay()),
				ticks(parameters.getFadeOut())
		));
	}

	// The bar empties in steps over its duration, then goes.
	private void show(Audience audience, BossbarContent content, Component text) {
		BossbarContent.Parameters parameters = content.getParameters();
		BossBar bar = BossBar.bossBar(text, 1.0f, parameters.getColor(), parameters.getOverlay());
		audience.showBossBar(bar);

		long duration = Math.max(TICK_MILLIS, parameters.getDuration().toMillis());
		for (int step = 1; step < BOSSBAR_STEPS; step++) {
			float progress = 1.0f - (float) step / BOSSBAR_STEPS;
			later(duration * step / BOSSBAR_STEPS, () -> bar.progress(progress));
		}
		later(duration, () -> audience.hideBossBar(bar));
	}

	private void later(long delay, Runnable runnable) {
		scheduler.schedule(DelayedRunnableTask.builder()
				.module(MODULE)
				.delay(delay)
				.runnable(runnable)
				.build());
	}

	/**
	 * Renders a text of a notification for its player. The message goes in after the text is parsed, so what the
	 * sender wrote is shown as written and never read as formatting.
	 */
	static Component render(SocialismusPlayer player, @Nullable String text, TagDelivery delivery, Ping ping) {
		if (text == null) return Component.empty();

		Map<String, String> placeholders = new HashMap<>();
		placeholders.put("sender", delivery.senderName());
		placeholders.put("tag", ping.tag());
		placeholders.put("match", TagRenderer.escape(ping.match()));
		placeholders.put("chat", delivery.chat() == null ? "" : delivery.chat());
		placeholders.put("server", delivery.senderServer() == null ? "" : delivery.senderServer());
		Component rendered = Serializer.serialize(SerializerContent.builder()
				.receiver(player)
				.message(text)
				.placeholders(placeholders)
				.build());

		return rendered.replaceText(TextReplacementConfig.builder()
				.matchLiteral(Serializer.placeholder("message"))
				.replacement(Component.text(delivery.message()))
				.build());
	}

	private static Duration ticks(int ticks) {
		return Duration.ofMillis(Math.max(0, ticks) * TICK_MILLIS);
	}
}
