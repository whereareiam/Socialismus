package me.whereareiam.socialismus.module.bubbler.common.renderer;

import lombok.Getter;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.AreaEffectCloudPacket;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.living.ArmorStandPacket;
import me.whereareiam.socialismus.module.bubbler.api.renderer.RenderedLine;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ArmorStandRenderedLine implements RenderedLine {
	private final ArmorStandPacket armorStandPacket;
	private final AreaEffectCloudPacket spacerPacket;

	public ArmorStandRenderedLine(ArmorStandPacket armorStandPacket, AreaEffectCloudPacket spacerPacket) {
		this.armorStandPacket = armorStandPacket;
		this.spacerPacket = spacerPacket;
	}

	public ArmorStandRenderedLine(ArmorStandPacket armorStandPacket) {
		this(armorStandPacket, null);
	}

	public boolean hasSpacer() {
		return spacerPacket != null;
	}

	@Override
	public List<Integer> getAllEntityIds() {
		List<Integer> ids = new ArrayList<>();
		ids.add(armorStandPacket.getEntityId());
		if (spacerPacket != null) {
			ids.add(spacerPacket.getEntityId());
		}
		return ids;
	}

	@Override
	public int getPrimaryEntityId() {
		return armorStandPacket.getEntityId();
	}

	public int getSpacerId() {
		return spacerPacket != null ? spacerPacket.getEntityId() : -1;
	}
}
