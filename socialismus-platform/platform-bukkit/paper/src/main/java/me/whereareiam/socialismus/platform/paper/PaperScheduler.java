package me.whereareiam.socialismus.platform.paper;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.whereareiam.socialismus.api.model.scheduler.DelayedRunnableTask;
import me.whereareiam.socialismus.api.model.scheduler.PeriodicalRunnableTask;
import me.whereareiam.socialismus.api.model.scheduler.RunnableTask;
import me.whereareiam.socialismus.api.output.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Singleton
public class PaperScheduler implements Scheduler {
    private final Plugin plugin;
    private final Map<String, Map<Integer, ScheduledTask>> tasks = new HashMap<>();

    @Inject
    public PaperScheduler(Plugin plugin) {
        this.plugin = plugin;

        checkAsyncSchedulerAvailability();
    }

    private void checkAsyncSchedulerAvailability() {
        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.AsyncScheduler");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("AsyncScheduler not found. Please use Paper version 1.20 or newer.");
        }
    }

    @Override
    public void schedule(RunnableTask runnableTask) {
        ScheduledTask task = Bukkit.getAsyncScheduler().runNow(plugin, t -> runnableTask.getRunnable().run());
        tasks.put(runnableTask.getModule(), Map.of(runnableTask.getId(), task));
    }

    @Override
    public void schedule(DelayedRunnableTask runnableTask) {
        ScheduledTask task = Bukkit.getAsyncScheduler().runDelayed(plugin, t -> runnableTask.getRunnable().run(), runnableTask.getDelay(), TimeUnit.MILLISECONDS);
        tasks.put(runnableTask.getModule(), Map.of(runnableTask.getId(), task));
    }

    @Override
    public void schedule(PeriodicalRunnableTask runnableTask) {
        ScheduledTask task = Bukkit.getAsyncScheduler().runAtFixedRate(plugin, t -> runnableTask.getRunnable().run(), runnableTask.getDelay(), runnableTask.getPeriod(), TimeUnit.MILLISECONDS);
        tasks.put(runnableTask.getModule(), Map.of(runnableTask.getId(), task));
    }

    @Override
    public void schedule(RunnableTask runnableTask, boolean async) {
        schedule(runnableTask);
    }

    @Override
    public void schedule(DelayedRunnableTask runnableTask, boolean async) {
        schedule(runnableTask);
    }

    @Override
    public void schedule(PeriodicalRunnableTask runnableTask, boolean async) {
        schedule(runnableTask);
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
            tasks.get(module).values().forEach(ScheduledTask::cancel);
            tasks.remove(module);
        }
    }
}

