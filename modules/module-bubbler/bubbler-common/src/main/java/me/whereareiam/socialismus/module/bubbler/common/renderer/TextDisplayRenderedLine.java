package me.whereareiam.socialismus.module.bubbler.common.renderer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.display.TextDisplayPacket;
import me.whereareiam.socialismus.module.bubbler.api.renderer.RenderedLine;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class TextDisplayRenderedLine implements RenderedLine {
	private final TextDisplayPacket packet;

	@Override
	public List<Integer> getAllEntityIds() {
		return List.of(packet.getEntityId());
	}

	@Override
	public int getPrimaryEntityId() {
		return packet.getEntityId();
	}
}
