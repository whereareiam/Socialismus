package me.whereareiam.socialismus.model.update;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.type.module.ProviderType;

@Data
@SuperBuilder
public class UpdateSource {
	private ProviderType provider;
	private String id;
}

