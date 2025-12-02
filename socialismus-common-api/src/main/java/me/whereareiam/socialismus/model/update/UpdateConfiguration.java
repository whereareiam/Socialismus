package me.whereareiam.socialismus.model.update;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateConfiguration {
	/**
	 * only present if you want release‐checks
	 */
	private UpdateSource release;

	/**
	 * only present if you want branch/dev‐checks
	 */
	private UpdateSource dev;
}

