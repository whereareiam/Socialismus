package me.whereareiam.socialismus.module.network.common.lease;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A lease as it is replicated: who holds the key, with which priority.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class LeaseRecord {
	private String holder;
	private int priority;
}
