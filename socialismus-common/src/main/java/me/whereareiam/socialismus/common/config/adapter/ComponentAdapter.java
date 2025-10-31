package me.whereareiam.socialismus.common.config.adapter;

import me.whereareiam.configura.TypeAdapter;
import me.whereareiam.socialismus.api.util.ComponentUtil;
import net.kyori.adventure.text.Component;

public class ComponentAdapter implements TypeAdapter<Component> {
	@Override
	public Component deserialize(String value) {
		if (value == null || value.isEmpty()) return null;
		return ComponentUtil.toGson(value);
	}

	@Override
	public String serialize(Component value) {
		if (value == null) return null;
		return ComponentUtil.toGson(value);
	}
}