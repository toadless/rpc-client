package net.toadless.rpc.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.toadless.rpc.objects.RPCTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskHandler.class);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
    private final List<RPCTask> tasks = new ArrayList<>();
    private final List<UUID> currentUUIDs = new ArrayList<>();

    public TaskHandler()
    {
        LOGGER.info("Started TaskHandler.");
    }

    public RPCTask addTask(Runnable task, TimeUnit unit, long time)
    {
        String taskName = getTaskName();
        RPCTask rpcTask = new RPCTask(scheduler.schedule(task, time, unit), taskName, time, unit);
        tasks.add(rpcTask);
        scheduleDeletion(rpcTask);
        LOGGER.info("Added new task with name " + taskName + " expires in " + rpcTask.getExpiresAt() + " " + rpcTask.getUnit());
        return rpcTask;
    }

    public RPCTask addTask(Runnable task, String taskName, TimeUnit unit, long time)
    {
        RPCTask rpcTask = new RPCTask(scheduler.schedule(task, time, unit), taskName, time, unit);
        tasks.add(rpcTask);
        scheduleDeletion(rpcTask);
        LOGGER.info("Added new task with name " + taskName + " expires in " + rpcTask.getExpiresAt() + " " + rpcTask.getUnit());
        return rpcTask;
    }

    public RPCTask addTask(Callable<?> task, String taskName, TimeUnit unit, long time)
    {
        RPCTask rpcTask = new RPCTask(scheduler.schedule(task, time, unit), taskName, time, unit);
        tasks.add(rpcTask);
        scheduleDeletion(rpcTask);
        LOGGER.info("Added new task with name " + taskName + " expires in " + rpcTask.getExpiresAt() + " " + rpcTask.getUnit());
        return rpcTask;
    }

    public RPCTask addRepeatingTask(Runnable task, String taskName, long initialDelay, TimeUnit unit, long period)
    {
        RPCTask rpcTask = new RPCTask(scheduler.scheduleAtFixedRate(task, initialDelay, period, unit), taskName, period + initialDelay, unit);
        tasks.add(rpcTask);
        LOGGER.info("Added new repeating task with name " + taskName + "!");
        return rpcTask;
    }

    public RPCTask addRepeatingTask(Runnable task, String taskName, TimeUnit unit, long time)
    {
        return addRepeatingTask(task, taskName, 0, unit, time);
    }

    public RPCTask addRepeatingTask(Runnable task, TimeUnit unit, long time)
    {
        return addRepeatingTask(task, "" + System.currentTimeMillis(), 0, unit, time);
    }

    public RPCTask getTask(String taskName)
    {
        for (RPCTask task : tasks)
        {
            if (task.getName().equalsIgnoreCase(taskName))
            {
                return task;
            }
        }
        return null;
    }

    public void cancelTask(String taskName, boolean shouldInterrupt)
    {
        LOGGER.debug("Cancelling task " + taskName);
        for (RPCTask task : tasks)
        {
            if (task.getName().equalsIgnoreCase(taskName))
            {
                LOGGER.info("Cancelled task " + taskName);
                task.getTask().cancel(shouldInterrupt);
                return;
            }
        }
        LOGGER.info("Task " + taskName + " could not be found");
    }

    public void close()
    {
        LOGGER.info("Closing TaskHandler");
        for (RPCTask task : tasks)
        {
            task.cancel(false);
        }
        LOGGER.info("TaskHandler closed");
    }

    public String getTaskName()
    {
        UUID uuid = UUID.randomUUID();
        if (!currentUUIDs.contains(uuid))
        {
            currentUUIDs.add(uuid);
            return uuid.toString();
        }
        else
        {
            return getTaskName();
        }
    }

    public List<RPCTask> getTasks()
    {
        return tasks;
    }

    private void scheduleDeletion(RPCTask task)
    {
        LOGGER.info("Task " + task.getName() + " scheduled for deletion in " + task.getExpiresAt() + " " + task.getUnit());
        scheduler.schedule(() -> tasks.remove(task), task.getExpiresAt(), task.getUnit());
    }
}