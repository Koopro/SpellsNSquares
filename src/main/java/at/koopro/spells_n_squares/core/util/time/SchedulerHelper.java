package at.koopro.spells_n_squares.core.util.time;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import net.minecraft.server.level.ServerLevel;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Helper class for scheduling tasks with delays and periodic execution.
 * Provides server-side task scheduling for delayed and periodic operations.
 */
public final class SchedulerHelper {
    
    private static final Map<UUID, ScheduledTask> TASKS = new ConcurrentHashMap<>();
    private static final Map<net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level>, List<UUID>> LEVEL_TASKS = new ConcurrentHashMap<>();
    
    private SchedulerHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Represents a scheduled task.
     */
    public static class ScheduledTask {
        private final UUID taskId;
        private final Consumer<ServerLevel> task;
        private final long scheduledTime;
        private final long delay;
        private final long period;
        private final boolean periodic;
        private final net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> levelId;
        private int executionCount;
        private final int maxExecutions;
        
        private ScheduledTask(UUID taskId, Consumer<ServerLevel> task, long scheduledTime, 
                             long delay, long period, boolean periodic, 
                             net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> levelId, int maxExecutions) {
            this.taskId = taskId;
            this.task = task;
            this.scheduledTime = scheduledTime;
            this.delay = delay;
            this.period = period;
            this.periodic = periodic;
            this.levelId = levelId;
            this.executionCount = 0;
            this.maxExecutions = maxExecutions;
        }
        
        public UUID getTaskId() {
            return taskId;
        }
        
        public boolean shouldExecute(long currentTime) {
            if (maxExecutions > 0 && executionCount >= maxExecutions) {
                return false;
            }
            return (currentTime - scheduledTime) >= delay;
        }
        
        public boolean execute(ServerLevel level) {
            if (level == null || !level.dimension().equals(levelId)) {
                return false;
            }
            
            try {
                task.accept(level);
                executionCount++;
                return true;
            } catch (Exception e) {
                DevLogger.logWarn(SchedulerHelper.class, "execute", 
                    "Error executing scheduled task: " + e.getMessage());
                return false;
            }
        }
        
        public void reschedule(long currentTime) {
            if (periodic && (maxExecutions <= 0 || executionCount < maxExecutions)) {
                // Reschedule for next period
                // This is handled by updating the scheduled time
            }
        }
        
        public boolean isComplete() {
            return maxExecutions > 0 && executionCount >= maxExecutions;
        }
    }
    
    /**
     * Schedules a task to execute after a delay.
     * 
     * @param level The server level
     * @param task The task to execute
     * @param delayTicks The delay in ticks
     * @return The task ID
     */
    public static UUID scheduleDelayed(ServerLevel level, Consumer<ServerLevel> task, long delayTicks) {
        if (level == null || task == null) {
            return null;
        }
        
        UUID taskId = UUID.randomUUID();
        long currentTime = level.getGameTime();
        long scheduledTime = currentTime + delayTicks;
        
        var dimensionKey = level.dimension();
        ScheduledTask scheduledTask = new ScheduledTask(
            taskId, task, scheduledTime, delayTicks, 0, false,
            dimensionKey, 1
        );
        
        TASKS.put(taskId, scheduledTask);
        LEVEL_TASKS.computeIfAbsent(dimensionKey, k -> new ArrayList<>()).add(taskId);
        
        DevLogger.logStateChange(SchedulerHelper.class, "scheduleDelayed",
            "Scheduled task: " + taskId + " for " + delayTicks + " ticks");
        
        return taskId;
    }
    
    /**
     * Schedules a periodic task that executes repeatedly.
     * 
     * @param level The server level
     * @param task The task to execute
     * @param delayTicks Initial delay in ticks
     * @param periodTicks Period between executions in ticks
     * @param maxExecutions Maximum number of executions (0 for infinite)
     * @return The task ID
     */
    public static UUID schedulePeriodic(ServerLevel level, Consumer<ServerLevel> task, 
                                       long delayTicks, long periodTicks, int maxExecutions) {
        if (level == null || task == null) {
            return null;
        }
        
        UUID taskId = UUID.randomUUID();
        long currentTime = level.getGameTime();
        long scheduledTime = currentTime + delayTicks;
        
        var dimensionKey = level.dimension();
        ScheduledTask scheduledTask = new ScheduledTask(
            taskId, task, scheduledTime, delayTicks, periodTicks, true,
            dimensionKey, maxExecutions
        );
        
        TASKS.put(taskId, scheduledTask);
        LEVEL_TASKS.computeIfAbsent(dimensionKey, k -> new ArrayList<>()).add(taskId);
        
        DevLogger.logStateChange(SchedulerHelper.class, "schedulePeriodic",
            "Scheduled periodic task: " + taskId + " every " + periodTicks + " ticks");
        
        return taskId;
    }
    
    /**
     * Cancels a scheduled task.
     * 
     * @param taskId The task ID
     * @return true if task was cancelled
     */
    public static boolean cancelTask(UUID taskId) {
        ScheduledTask task = TASKS.remove(taskId);
        if (task != null) {
            LEVEL_TASKS.values().forEach(list -> list.remove(taskId));
            DevLogger.logStateChange(SchedulerHelper.class, "cancelTask",
                "Cancelled task: " + taskId);
            return true;
        }
        return false;
    }
    
    /**
     * Processes all scheduled tasks for a level.
     * Should be called each tick on the server.
     * 
     * @param level The server level
     */
    public static void processTasks(ServerLevel level) {
        if (level == null) {
            return;
        }
        
        var levelId = level.dimension();
        List<UUID> taskIds = LEVEL_TASKS.get(levelId);
        if (taskIds == null || taskIds.isEmpty()) {
            return;
        }
        
        long currentTime = level.getGameTime();
        List<UUID> toRemove = new ArrayList<>();
        
        for (UUID taskId : new ArrayList<>(taskIds)) {
            ScheduledTask task = TASKS.get(taskId);
            if (task == null) {
                toRemove.add(taskId);
                continue;
            }
            
            if (task.shouldExecute(currentTime)) {
                boolean executed = task.execute(level);
                
                if (task.isComplete() || !executed) {
                    toRemove.add(taskId);
                } else if (task.periodic) {
                    // Reschedule for next period
                    ScheduledTask rescheduled = new ScheduledTask(
                        task.taskId, task.task, currentTime, task.period, task.period,
                        true, task.levelId, task.maxExecutions
                    );
                    rescheduled.executionCount = task.executionCount;
                    TASKS.put(taskId, rescheduled);
                } else {
                    toRemove.add(taskId);
                }
            }
        }
        
        // Remove completed tasks
        for (UUID taskId : toRemove) {
            TASKS.remove(taskId);
            taskIds.remove(taskId);
        }
    }
    
    /**
     * Clears all tasks for a level.
     * 
     * @param level The server level
     */
    public static void clearLevelTasks(ServerLevel level) {
        if (level == null) {
            return;
        }
        
        var levelId = level.dimension();
        List<UUID> taskIds = LEVEL_TASKS.remove(levelId);
        if (taskIds != null) {
            for (UUID taskId : taskIds) {
                TASKS.remove(taskId);
            }
        }
    }
}

