package me.whereareiam.socialismus.api.output;

import me.whereareiam.socialismus.api.model.scheduler.DelayedRunnableTask;
import me.whereareiam.socialismus.api.model.scheduler.PeriodicalRunnableTask;
import me.whereareiam.socialismus.api.model.scheduler.RunnableTask;

/**
 * Interface for managing scheduled tasks within the system.
 * Provides methods to schedule, execute, and cancel different types of tasks.
 *
 * <p>Supports three types of tasks:</p>
 * <ul>
 *   <li>Regular tasks ({@link RunnableTask}): Execute once immediately</li>
 *   <li>Delayed tasks ({@link DelayedRunnableTask}): Execute once after a delay</li>
 *   <li>Periodical tasks ({@link PeriodicalRunnableTask}): Execute repeatedly at fixed intervals</li>
 * </ul>
 */
public interface Scheduler {
    /**
     * Schedules a regular task for immediate execution.
     * @param runnableTask the task to schedule
     */
    void schedule(RunnableTask runnableTask);

    /**
     * Schedules a task to execute after a specified delay.
     * @param runnableTask the delayed task to schedule
     */
    void schedule(DelayedRunnableTask runnableTask);

    /**
     * Schedules a task to execute periodically.
     * @param runnableTask the periodical task to schedule
     */
    void schedule(PeriodicalRunnableTask runnableTask);

    /**
     * Schedules a regular task with optional async execution.
     * @param runnableTask the task to schedule
     * @param async true to run asynchronously, false for synchronous execution
     */
    void schedule(RunnableTask runnableTask, boolean async);

    /**
     * Schedules a delayed task with optional async execution.
     * @param runnableTask the delayed task to schedule
     * @param async true to run asynchronously, false for synchronous execution
     */
    void schedule(DelayedRunnableTask runnableTask, boolean async);

    /**
     * Schedules a periodical task with optional async execution.
     * @param runnableTask the periodical task to schedule
     * @param async true to run asynchronously, false for synchronous execution
     */
    void schedule(PeriodicalRunnableTask runnableTask, boolean async);

    /**
     * Cancels a regular task.
     * @param runnableTask the task to cancel
     */
    void cancel(RunnableTask runnableTask);

    /**
     * Cancels a delayed task.
     * @param runnableTask the delayed task to cancel
     */
    void cancel(DelayedRunnableTask runnableTask);

    /**
     * Cancels all tasks associated with a specific module.
     * @param module the name of the module whose tasks should be cancelled
     */
    void cancelByModule(String module);
}