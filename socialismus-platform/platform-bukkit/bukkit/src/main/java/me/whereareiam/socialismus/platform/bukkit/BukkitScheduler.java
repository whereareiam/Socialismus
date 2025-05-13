package me.whereareiam.socialismus.platform.bukkit;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.scheduler.DelayedRunnableTask;
import me.whereareiam.socialismus.api.model.scheduler.PeriodicalRunnableTask;
import me.whereareiam.socialismus.api.model.scheduler.RunnableTask;
import me.whereareiam.socialismus.api.output.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class BukkitScheduler implements Scheduler {
    private static final long MS_TO_TICKS = 20L;
    private final Plugin plugin;
    private final Map<String, Map<Integer, BukkitTask>> tasks = new HashMap<>();

    @Inject
    public BukkitScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void schedule(RunnableTask runnableTask) {
        schedule(runnableTask, false);
    }

    @Override
    public void schedule(DelayedRunnableTask runnableTask) {
        schedule(runnableTask, false);
    }

    @Override
    public void schedule(PeriodicalRunnableTask runnableTask) {
        schedule(runnableTask, false);
    }

    @Override
    public void schedule(RunnableTask runnableTask, boolean async) {
        BukkitTask task;
        if (async) {
            task = Bukkit.getScheduler().runTaskAsynchronously(plugin, runnableTask.getRunnable());
        } else {
            task = Bukkit.getScheduler().runTask(plugin, runnableTask.getRunnable());
        }

        tasks.put(runnableTask.getModule(), Map.of(runnableTask.getId(), task));
    }

    @Override
    public void schedule(DelayedRunnableTask runnableTask, boolean async) {
        BukkitTask task;
        long delayInTicks = runnableTask.getDelay() / MS_TO_TICKS;

        if (async) {
            task = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnableTask.getRunnable(), delayInTicks);
        } else {
            task = Bukkit.getScheduler().runTaskLater(plugin, runnableTask.getRunnable(), delayInTicks);
        }

        tasks.put(runnableTask.getModule(), Map.of(runnableTask.getId(), task));
    }

    @Override
    public void schedule(PeriodicalRunnableTask runnableTask, boolean async) {
        BukkitTask task;

        long delayInTicks = runnableTask.getDelay() / MS_TO_TICKS;
        long periodInTicks = runnableTask.getPeriod() / MS_TO_TICKS;

        if (async) {
            task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnableTask.getRunnable(), delayInTicks, periodInTicks);
        } else {
            task = Bukkit.getScheduler().runTaskTimer(plugin, runnableTask.getRunnable(), delayInTicks, periodInTicks);
        }

        tasks.put(runnableTask.getModule(), Map.of(runnableTask.getId(), task));
    }

    @Override
    public void cancel(RunnableTask runnableTask) {
        if (tasks.containsKey(runnableTask.getModule()) && tasks.get(runnableTask.getModule()).containsKey(runnableTask.getId())) {
            tasks.get(runnableTask.getModule()).get(runnableTask.getId()).cancel();
            tasks.get(runnableTask.getModule()).remove(runnableTask.getId());
        }
    }

    @Override
    public void cancel(DelayedRunnableTask runnableTask) {
        cancel((RunnableTask) runnableTask);
    }

    @Override
    public void cancelByModule(String module) {
        if (tasks.containsKey(module)) {
            tasks.get(module).values().forEach(BukkitTask::cancel);
            tasks.remove(module);
        }
    }
}

