package me.whereareiam.socialismus.api.model.module;

import lombok.*;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.api.type.module.ChannelType;
import me.whereareiam.socialismus.api.type.module.ProviderType;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSpecification {
	private ProviderType provider;
	private String id;
	@Builder.Default
	private ChannelType channel = ChannelType.RELEASE;
}
