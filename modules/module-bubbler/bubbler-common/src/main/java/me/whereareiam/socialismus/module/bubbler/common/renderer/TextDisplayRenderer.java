package me.whereareiam.socialismus.module.bubbler.common.renderer;

import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.Vector3f;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.model.position.Position;
import me.whereareiam.socialismus.module.bubbler.api.model.Vector;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.Bubble;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleLine;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerSettings;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.PassengerPacket;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.DestroyEntitiesPacket;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.display.TextDisplayPacket;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.display.metadata.InterpolationMetadataPacket;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.display.metadata.ScaleMetadataPacket;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.display.metadata.TranslationMetadataPacket;
import me.whereareiam.socialismus.module.bubbler.api.renderer.BubbleRenderer;
import me.whereareiam.socialismus.module.bubbler.api.renderer.RenderStrategy;
import me.whereareiam.socialismus.module.bubbler.api.renderer.RenderedLine;
import me.whereareiam.socialismus.module.bubbler.common.renderer.strategy.TextDisplayRenderStrategy;
import me.whereareiam.socialismus.module.bubbler.common.util.PacketUtil;
import me.whereareiam.socialismus.model.scheduler.DelayedRunnableTask;
import me.whereareiam.socialismus.service.Scheduler;

import java.util.Collection;
import java.util.List;

@Singleton
public class TextDisplayRenderer implements BubbleRenderer {
	private static final long TICK_MS = 50L;

	private final Provider<BubblerSettings> settings;
	private final Scheduler scheduler;
	private final RenderStrategy strategy;

	@Inject
	public TextDisplayRenderer(Provider<BubblerSettings> settings, Scheduler scheduler) {
		this.settings = settings;
		this.scheduler = scheduler;
		this.strategy = new TextDisplayRenderStrategy(this);
	}

	@Override
	public RenderedLine spawnLine(Bubble bubble, BubbleLine line, float yOffset, Position eyePos) {
		Bubble.Style style = bubble.getStyle();
		TextDisplayPacket packet = TextDisplayPacket.builder()
				.position(PacketUtil.toVector3d(eyePos))
				.text(line.getContent())
				.type(style.getDisplay())
				.backgroundColor(style.getBackground().getColor())
				.transparency(style.getBackground().getTransparency())
				.alignment(style.getText().getAlignment())
				.hasShadow(style.getText().isShadow())
				.isSeeThrough(style.isSeeThrough())
				.translation(new Vector3f(0, yOffset, 0))
				.build();

		return new TextDisplayRenderedLine(packet);
	}

	@Override
	public void sendSpawn(RenderedLine renderedLine, Collection<User> recipients) {
		TextDisplayRenderedLine line = (TextDisplayRenderedLine) renderedLine;
		recipients.forEach(r -> line.getPacket().send(r));
	}

	@Override
	public void attachAsPassenger(int vehicleId, RenderedLine line, Collection<User> recipients) {
		PassengerPacket packet = PassengerPacket.builder()
				.vehicleId(vehicleId)
				.passengerIds(new int[]{line.getPrimaryEntityId()})
				.build();
		recipients.forEach(packet::send);
	}

	@Override
	public void updatePosition(RenderedLine line, float yOffset, Position eyePos, Collection<User> recipients) {
		Vector3f translation = new Vector3f(0F, yOffset, 0F);
		TranslationMetadataPacket packet = TranslationMetadataPacket.builder()
				.entityId(line.getPrimaryEntityId())
				.translation(translation)
				.build();
		recipients.forEach(packet::send);
	}

	@Override
	public void destroy(RenderedLine line, Collection<User> recipients) {
		DestroyEntitiesPacket packet = DestroyEntitiesPacket.builder()
				.entityIds(line.getAllEntityIds().stream().mapToInt(Integer::intValue).toArray())
				.build();
		recipients.forEach(packet::send);
	}

	@Override
	public void destroy(List<RenderedLine> lines, Collection<User> recipients) {
		int[] ids = lines.stream()
				.flatMap(l -> l.getAllEntityIds().stream())
				.mapToInt(Integer::intValue)
				.toArray();
		DestroyEntitiesPacket packet = DestroyEntitiesPacket.builder()
				.entityIds(ids)
				.build();
		recipients.forEach(packet::send);
	}

	@Override
	public void applyInitialScale(RenderedLine renderedLine, Bubble bubble, Collection<User> recipients) {
		// This is called AFTER sendSpawn, so we send metadata to override the default scale
		TextDisplayRenderedLine line = (TextDisplayRenderedLine) renderedLine;
		float startScale = settings.get().getAnimation().getExpansion().getStartScale();
		Vector3f scale = new Vector3f(startScale, startScale, startScale);

		// Send scale metadata packet to set initial small scale
		ScaleMetadataPacket scalePacket = ScaleMetadataPacket.builder()
				.entityId(line.getPrimaryEntityId())
				.scale(scale)
				.build();

		recipients.forEach(scalePacket::send);
	}

	@Override
	public void animateSpawn(RenderedLine line, Bubble bubble, Collection<User> recipients) {
		long durationMs = settings.get().getAnimation().getExpansion().getDuration();
		int ticks = Math.max(1, (int) Math.ceil(durationMs / (double) TICK_MS));

		InterpolationMetadataPacket interpolation = InterpolationMetadataPacket.builder()
				.entityId(line.getPrimaryEntityId())
				.startDelayTicks(0)
				.transformDurationTicks(ticks)
				.positionRotationDurationTicks(0)
				.build();
		recipients.forEach(interpolation::send);

		Vector3f targetScale = toVector3f(bubble.getStyle().getScale());
		scheduler.schedule(DelayedRunnableTask.builder()
				.module("bubbler")
				.delay(TICK_MS)
				.runnable(() -> {
					ScaleMetadataPacket scale = ScaleMetadataPacket.builder()
							.entityId(line.getPrimaryEntityId())
							.scale(targetScale)
							.build();
					recipients.forEach(scale::send);
				})
				.build());
	}

	@Override
	public void animateRemoval(RenderedLine line, Bubble bubble, Collection<User> recipients, Runnable onComplete) {
		long durationMs = settings.get().getAnimation().getContraction().getDuration();
		int ticks = Math.max(1, (int) Math.ceil(durationMs / (double) TICK_MS));

		InterpolationMetadataPacket interpolation = InterpolationMetadataPacket.builder()
				.entityId(line.getPrimaryEntityId())
				.startDelayTicks(0)
				.transformDurationTicks(ticks)
				.positionRotationDurationTicks(0)
				.build();
		recipients.forEach(interpolation::send);

		float endScale = settings.get().getAnimation().getContraction().getEndScale();
		Vector3f scale = new Vector3f(endScale, endScale, endScale);

		scheduler.schedule(DelayedRunnableTask.builder()
				.module("bubbler")
				.delay(TICK_MS)
				.runnable(() -> {
					ScaleMetadataPacket scalePacket = ScaleMetadataPacket.builder()
							.entityId(line.getPrimaryEntityId())
							.scale(scale)
							.build();
					recipients.forEach(scalePacket::send);
				})
				.build());

		scheduler.schedule(DelayedRunnableTask.builder()
				.module("bubbler")
				.delay(TICK_MS + durationMs)
				.runnable(() -> {
					destroy(line, recipients);
					onComplete.run();
				})
				.build());
	}

	@Override
	public int getPassengerAnchorId(RenderedLine line) {
		return line.getPrimaryEntityId();
	}

	@Override
	public RenderStrategy getStrategy() {
		return strategy;
	}

	private Vector3f toVector3f(Vector v) {
		return new Vector3f(v.getX(), v.getY(), v.getZ());
	}
}
