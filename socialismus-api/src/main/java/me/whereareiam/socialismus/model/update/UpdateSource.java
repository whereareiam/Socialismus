package me.whereareiam.socialismus.model.update;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.type.module.ProviderType;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSource {
	private ProviderType provider;
	private String id;
}

