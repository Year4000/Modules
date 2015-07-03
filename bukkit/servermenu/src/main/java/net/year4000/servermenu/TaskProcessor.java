package net.year4000.servermenu;

import lombok.Setter;
import net.year4000.ducktape.bukkit.utils.SchedulerUtil;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public final class TaskProcessor<T extends Runnable> implements Runnable {
    private Queue<T> tasks = new ConcurrentLinkedQueue<>();
    private AtomicBoolean process = new AtomicBoolean(true);
    @Setter
    private Supplier<Boolean> wait = () -> false;

    /** Add the collection of tasks */
    public TaskProcessor(Collection<T> tasks) {
        this.tasks.addAll(tasks);
    }

    /** Start to process the tasks */
    public void process() {
        SchedulerUtil.runAsync(this);
    }

    /** End future pending tasks */
    public void end() {
        process.set(false);
    }

    @Override
    public void run() {
        while (process.get()) {
            while (tasks.peek() != null) {
                T task = tasks.poll();
                String taskName = task.getClass().getSimpleName() + "@" + Integer.toHexString(task.hashCode());
                ServerMenu.debug("Processing Task: " + taskName);

                try {
                    task.run();
                    Thread.sleep(Byte.MAX_VALUE);

                    // Do we need to have this thread pause at specific events
                    while (wait.get()) {
                        Thread.sleep(Byte.MAX_VALUE);
                    }
                }
                catch (Exception e) {
                    ServerMenu.log(e, false);
                }
                finally {
                    tasks.add(task);
                }
            }
        }
    }
}
