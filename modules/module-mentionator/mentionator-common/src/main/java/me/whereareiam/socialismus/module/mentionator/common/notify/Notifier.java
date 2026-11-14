package me.whereareiam.socialismus.module.mentionator.common.notify;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.mentionator.api.MentionatorService;
import me.whereareiam.socialismus.module.mentionator.api.event.PlayerTaggedEvent;
import me.whereareiam.socialismus.module.mentionator.api.model.notification.Notification;
import me.whereareiam.socialismus.module.mentionator.api.model.notification.NotificationContent;
import me.whereareiam.socialismus.module.mentionator.api.model.notification.content.SoundContent;
import me.whereareiam.socialismus.module.mentionator.api.model.tag.Tag;
import me.whereareiam.socialismus.module.mentionator.api.model.tagging.TagUse;
import me.whereareiam.socialismus.module.mentionator.api.model.tagging.Tagging;
import me.whereareiam.socialismus.module.mentionator.api.type.Mode;
import me.whereareiam.socialismus.module.mentionator.common.condition.Catalog;
import me.whereareiam.socialismus.module.mentionator.common.condition.MentionatorConditions;
import me.whereareiam.socialismus.module.mentionator.common.tagging.ContentEdits;
import me.whereareiam.socialismus.module.mentionator.common.tagging.Form;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.type.PlatformType;
import me.whereareiam.socialismus.util.EventUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Notifies the players a message tags, once it went out. The server the message was written on gathers the tags of
 * each tagged player; the server that hosts the player decides, by their mode and the limits, whether they hear it,
 * and gives them each notification once however often they were tagged.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class Notifier {
	private final MentionatorConditions conditions;
	private final PlayerRegistry players;
	private final RemoteTaggings remote;
	private final DeliveryLimits limits;
	private final MentionatorService service;
	private final NotificationSender sender;

	/**
	 * Notifies the players a message written on this server tags: those hosted here at once, the others through
	 * their own server.
	 *
	 * @param message the message, sent
	 * @param tagging its tags
	 */
	public void dispatch(@NotNull ChatMessage message, @NotNull Tagging tagging) {
		Catalog catalog = conditions.catalog();
		SocialismusPlayer author = message.getSender();
		Map<UUID, List<Ping>> pings = new LinkedHashMap<>();
		for (TagUse use : tagging.uses()) {
			Optional<Tag> tag = catalog.tag(use.tag());
			if (tag.isEmpty()) continue;

			Ping ping = new Ping(
					use.tag(),
					use.match(),
					tag.get().getNotify() == null ? List.of() : tag.get().getNotify(),
					direct(tag.get()),
					catalog.bypasses(tag.get(), author, message)
			);
			use.targets().forEach(target -> pings.computeIfAbsent(target, ignored -> new ArrayList<>()).add(ping));
		}

		boolean bypassesReceived = catalog.bypassesReceived(author, message);
		String text = message.getContent() == null ? "" : ContentEdits.plain(message.getContent());
		pings.forEach((target, heard) -> {
			TagDelivery delivery = new TagDelivery(
					author.getUniqueId(),
					author.getUsername(),
					author.getServer(),
					message.getChat() == null ? null : message.getChat().getId(),
					text,
					bypassesReceived,
					heard
			);
			Optional<SocialismusPlayer> hosted = players.getPlayerData(target);
			if (hosted.isPresent()) {
				deliver(hosted.get(), author, delivery);
				return;
			}

			remote.current().ifPresent(other -> other.send(target, delivery));
		});
	}

	/**
	 * Notifies a tagged player this server hosts, as their mode and the limits allow.
	 *
	 * @param player   the tagged player
	 * @param author   the player who tagged them, as this server knows them
	 * @param delivery what tagged them
	 */
	public void deliver(@NotNull SocialismusPlayer player, @NotNull SocialismusPlayer author, @NotNull TagDelivery delivery) {
		Mode mode = service.getMode(player.getUniqueId());
		List<Ping> heard = delivery.pings().stream().filter(ping -> hears(mode, ping)).toList();
		if (heard.isEmpty()) return;
		if (!limits.admit(delivery.sender(), player.getUniqueId(), delivery.bypassesReceived())) return;

		// Each notification once, for the first tag that asked for it.
		Map<String, Ping> byNotification = new LinkedHashMap<>();
		heard.forEach(ping -> ping.notifications().forEach(id -> byNotification.putIfAbsent(id, ping)));
		List<String> tags = heard.stream().map(Ping::tag).distinct().toList();

		PlayerTaggedEvent event = new PlayerTaggedEvent(
				player,
				delivery.sender(),
				delivery.senderName(),
				tags,
				new ArrayList<>(byNotification.keySet())
		);
		EventUtil.callEvent(event, () -> send(player, author, delivery, event.getNotifications(), byNotification, heard.getFirst()));
	}

	private void send(
			SocialismusPlayer player,
			SocialismusPlayer author,
			TagDelivery delivery,
			List<String> notifications,
			Map<String, Ping> byNotification,
			Ping first
	) {
		Catalog catalog = conditions.catalog();
		List<SoundCue> handedOver = new ArrayList<>();
		for (String id : notifications) {
			Optional<Notification> notification = catalog.notification(id);
			if (notification.isEmpty() || notification.get().getContents() == null) continue;

			Ping ping = byNotification.getOrDefault(id, first);
			for (NotificationContent content : notification.get().getContents()) {
				if (content == null || !catalog.receives(content, player, author)) continue;
				if (content instanceof SoundContent sound && !PlatformType.isGameServer()) {
					handedOver.add(new SoundCue(sound.getSound(), sound.getParameters().getVolume(), sound.getParameters().getPitch()));
					continue;
				}

				sender.send(player, content, delivery, ping);
			}
		}

		// A proxy plays no sounds; the server the player is on does.
		if (!handedOver.isEmpty())
			remote.current().ifPresent(tagging -> tagging.playSounds(player.getUniqueId(), handedOver));
	}

	/**
	 * Plays sounds a proxy handed over to a player this server hosts.
	 *
	 * @param player the player's unique id
	 * @param sounds the sounds
	 */
	public void playSounds(@NotNull UUID player, @NotNull List<SoundCue> sounds) {
		players.getPlayerData(player).ifPresent(hosted -> sounds.forEach(sound -> hosted.playSound(sound.sound(), sound.volume(), sound.pitch())));
	}

	private static boolean hears(Mode mode, Ping ping) {
		if (ping.bypasses() || mode == Mode.ALL) return true;

		return mode == Mode.DIRECT && ping.direct();
	}

	/**
	 * Returns whether a tag names the player it tags: its {@code direct}, or else whether a form holds
	 * {@code {username}}.
	 *
	 * @param tag the tag
	 * @return whether it is direct
	 */
	public static boolean direct(@NotNull Tag tag) {
		Boolean direct = tag.getParameters() == null ? null : tag.getParameters().getDirect();
		if (direct != null) return direct;

		return tag.getWritten().stream().map(Form::of).anyMatch(Form::username);
	}
}
