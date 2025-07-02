package me.whereareiam.socialismus.api.model.module;

import lombok.*;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.api.type.module.ProviderType;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSpecification {
	/**
	 * only present if you want release‐checks
	 */
	private Spec release;

	/**
	 * only present if you want branch/dev‐checks
	 */
	private Spec dev;

	@Getter
	@Setter
	@ToString
	@SuperBuilder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Spec {
		private ProviderType provider;
		private String id;
	}
}
