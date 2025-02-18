package me.whereareiam.socialismus.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.function.Function;

/**
 * Represents a worker that performs a transformation on data of type T.
 * Workers can be prioritized, cancelled, and marked as removable.
 *
 * <p>Workers are used in the processing pipeline to modify or filter data
 * before it reaches its final destination. Each worker contains a function
 * that transforms the input data.</p>
 *
 * @param <T> the type of data this worker processes
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class Worker<T> {
		/** The function that transforms the input data */
		private final Function<T, T> function;

		/** The priority of this worker in the processing pipeline */
		private final int priority;

		/** Indicates if this worker can be removed from the pipeline */
		private boolean removable;

		/** Indicates if this worker's processing is cancelled */
		private boolean cancelled;
}