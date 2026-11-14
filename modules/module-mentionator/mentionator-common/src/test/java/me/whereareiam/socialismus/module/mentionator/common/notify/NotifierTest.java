package me.whereareiam.socialismus.module.mentionator.common.notify;

import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.mentionator.api.MentionatorService;
import me.whereareiam.socialismus.module.mentionator.api.model.config.MentionatorSettings;
import me.whereareiam.socialismus.module.mentionator.api.model.notification.Notification;
import me.whereareiam.socialismus.module.mentionator.api.model.notification.content.ActionbarContent;
import me.whereareiam.socialismus.module.mentionator.api.model.notification.content.ChatContent;
import me.whereareiam.socialismus.module.mentionator.api.model.notification.content.SoundContent;
import me.whereareiam.socialismus.module.mentionator.api.type.Mode;
import me.whereareiam.socialismus.module.mentionator.common.condition.MentionatorConditions;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.util.EventUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static me.whereareiam.socialismus.module.mentionator.common.TestMentionator.conditions;
import static me.whereareiam.socialismus.module.mentionator.common.TestMentionator.phrases;
import static me.whereareiam.socialismus.module.mentionator.common.TestMentionator.player;
import static me.whereareiam.socialismus.module.mentionator.common.TestMentionator.useMiniMessage;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotifierTest {
	private final SocialismusPlayer alex = player("Alex");
	private final SocialismusPlayer steve = player("Steve");
	private final MentionatorService service = mock(MentionatorService.class);
	private final DeliveryLimits limits = mock(DeliveryLimits.class);
	private final NotificationSender sender = mock(NotificationSender.class);
	private final List<Notification> notifications = List.of(
			new Notification("ping", List.of(
					ChatContent.builder().chat("{sender} tagged you").build(),
					ActionbarContent.builder().actionbar("{sender} tagged you").build()
			)),
			new Notification("alert", List.of(
					SoundContent.builder().sound("minecraft:block.note_block.pling").build()
			))
	);
	private final RemoteTagging remote = mock(RemoteTagging.class);
	private Notifier notifier;

	@BeforeEach
	void setUp() {
		useMiniMessage();
		EventUtil.initialize(mock(EventManager.class));
		when(limits.admit(any(), any(), anyBoolean())).thenReturn(true);
		when(service.getMode(any())).thenReturn(Mode.ALL);

		MentionatorConditions conditions = new MentionatorConditions(conditions(), phrases(), List::of, () -> notifications, MentionatorSettings::new);
		conditions.bind();
		RemoteTaggings remoteTaggings = new RemoteTaggings();
		remoteTaggings.use(remote);
		notifier = new Notifier(conditions, mock(PlayerRegistry.class), remoteTaggings, limits, service, sender);
	}

	@Test
	void aPlayerTaggedTwiceGetsEachNotificationOnce() {
		notifier.deliver(steve, alex, delivery(ping("player", true, false), ping("everyone", false, false)));

		verify(sender, times(2)).send(eq(steve), any(), any(), any());
	}

	@Test
	void aPlayerWhoHearsOnlyDirectTagsIsNotNotifiedOfAGroupTag() {
		when(service.getMode(steve.getUniqueId())).thenReturn(Mode.DIRECT);

		notifier.deliver(steve, alex, delivery(ping("everyone", false, false)));
		verify(sender, never()).send(any(), any(), any(), any());

		notifier.deliver(steve, alex, delivery(ping("player", true, false)));
		verify(sender, times(2)).send(eq(steve), any(), any(), any());
	}

	@Test
	void aPlayerWhoTurnedTagsOffHearsOnlyTagsThatBypass() {
		when(service.getMode(steve.getUniqueId())).thenReturn(Mode.NONE);

		notifier.deliver(steve, alex, delivery(ping("player", true, false)));
		verify(sender, never()).send(any(), any(), any(), any());

		notifier.deliver(steve, alex, delivery(ping("staff", false, true)));
		verify(sender, times(2)).send(eq(steve), any(), any(), any());
	}

	@Test
	void theLimitsKeepAPlayerSilent() {
		when(limits.admit(any(), any(), anyBoolean())).thenReturn(false);

		notifier.deliver(steve, alex, delivery(ping("player", true, false)));

		verify(sender, never()).send(any(), any(), any(), any());
		verify(steve, never()).playSound(any(), anyFloat(), anyFloat());
	}

	@Test
	void aProxyHandsSoundsToTheServerThePlayerIsOn() {
		// Tests run on no game server, as a proxy does.
		notifier.deliver(steve, alex, delivery(new Ping("staff", "Steve", List.of("alert"), false, false)));

		verify(sender, never()).send(any(), any(), any(), any());
		verify(remote).playSounds(eq(steve.getUniqueId()), eq(List.of(new SoundCue("minecraft:block.note_block.pling", 1.0f, 1.0f))));
	}

	private static Ping ping(String tag, boolean direct, boolean bypasses) {
		return new Ping(tag, "Steve", List.of("ping"), direct, bypasses);
	}

	private TagDelivery delivery(Ping... pings) {
		return new TagDelivery(alex.getUniqueId(), "Alex", "lobby", "global", "hi @Steve", false, List.of(pings));
	}

}
