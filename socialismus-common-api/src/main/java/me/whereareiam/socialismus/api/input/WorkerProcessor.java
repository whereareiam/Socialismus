package me.whereareiam.socialismus.api.input;

import me.whereareiam.socialismus.api.model.Worker;

import java.util.LinkedList;

/**
 * Processor interface for managing workers that transform data of type T.
 * Provides methods to manage the worker pipeline by adding, removing,
 * and retrieving workers.
 *
 * <p>Worker processors maintain an ordered list of workers that can be used
 * to transform or filter data in a sequential processing pipeline.</p>
 *
 * @param <T> the type of data processed by the workers
 */
public interface WorkerProcessor<T> {
		/**
		 * Gets the list of workers in this processor.
		 * Workers are ordered by their priority in the processing pipeline.
		 *
		 * @return a LinkedList containing all registered workers
		 */
		LinkedList<Worker<T>> getWorkers();

		/**
		 * Removes a worker from the processing pipeline.
		 * Only removable workers can be removed from the pipeline.
		 *
		 * @param worker the worker to remove
		 * @return true if the worker was removed successfully
		 */
		boolean removeWorker(Worker<T> worker);

		/**
		 * Adds a new worker to the processing pipeline.
		 * Workers are automatically sorted by their priority.
		 *
		 * @param worker the worker to add to the pipeline
		 */
		void addWorker(Worker<T> worker);
}