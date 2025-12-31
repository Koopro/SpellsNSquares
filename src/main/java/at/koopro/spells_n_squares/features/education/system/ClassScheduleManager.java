package at.koopro.spells_n_squares.features.education.system;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages class schedules for players.
 */
public final class ClassScheduleManager {
    private static final Map<Identifier, ClassSchedule> SCHEDULES = new HashMap<>();
    
    private ClassScheduleManager() {
    }
    
    /**
     * Gets the class schedule for a player.
     */
    public static ClassSchedule getSchedule(Player player) {
        Identifier playerId = Identifier.fromNamespaceAndPath("player", player.getUUID().toString());
        return SCHEDULES.getOrDefault(playerId, new ClassSchedule());
    }
    
    /**
     * Sets the class schedule for a player.
     */
    public static void setSchedule(Player player, ClassSchedule schedule) {
        Identifier playerId = Identifier.fromNamespaceAndPath("player", player.getUUID().toString());
        SCHEDULES.put(playerId, schedule);
    }
    
    /**
     * Represents a class schedule.
     */
    public static class ClassSchedule {
        private final List<ScheduledClass> classes = new ArrayList<>();
        
        public List<ScheduledClass> getClasses() {
            return new ArrayList<>(classes);
        }
        
        public void addClass(ScheduledClass scheduledClass) {
            classes.add(scheduledClass);
        }
        
        public void removeClass(Identifier classId) {
            classes.removeIf(c -> c.getClassId().equals(classId));
        }
    }
    
    /**
     * Represents a scheduled class.
     */
    public static class ScheduledClass {
        private final Identifier classId;
        private final long startTime; // Game time when class starts
        private final long endTime; // Game time when class ends
        
        public ScheduledClass(Identifier classId, long startTime, long endTime) {
            this.classId = classId;
            this.startTime = startTime;
            this.endTime = endTime;
        }
        
        public Identifier getClassId() {
            return classId;
        }
        
        public long getStartTime() {
            return startTime;
        }
        
        public long getEndTime() {
            return endTime;
        }
        
        public boolean isActive(long currentTime) {
            return currentTime >= startTime && currentTime <= endTime;
        }
    }
}

