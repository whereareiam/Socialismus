package me.whereareiam.socialismus.api.model.scheduler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Represents a scheduled task with unique identification and module association.
 * This class is used to wrap Runnable tasks with additional metadata for task management.
 *
 * <p>Each task contains:</p>
 * <ul>
 *   <li>A unique identifier</li>
 *   <li>A module name for task origin tracking</li>
 *   <li>The actual runnable task to be executed</li>
 * </ul>
 */
@Getter
@ToString
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class RunnableTask {
		/** Unique identifier for the task */
		private final int id;

		/** Name of the module that created this task */
		private final String module;

		/** The actual task to be executed */
		private final Runnable runnable;
}