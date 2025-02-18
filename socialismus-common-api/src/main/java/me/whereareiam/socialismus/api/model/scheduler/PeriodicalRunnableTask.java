package me.whereareiam.socialismus.api.model.scheduler;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Represents a task that runs periodically at fixed intervals in the Socialismus plugin system.
 * This class extends {@link RunnableTask} to add timing parameters for periodic execution.
 *
 * <p>This class uses Lombok annotations for boilerplate code generation
 * and implements the Builder pattern for flexible object creation.</p>
 */
@Getter
@ToString
@SuperBuilder(toBuilder = true)
public class PeriodicalRunnableTask extends RunnableTask {
		/**
		 * The initial delay before the first task execution (in milliseconds)
		 */
		private final long delay;

		/**
		 * The time period between consecutive task executions (in milliseconds)
		 */
		private final long period;
}