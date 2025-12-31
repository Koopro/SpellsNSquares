package at.koopro.spells_n_squares.features.organizations.system;

import at.koopro.spells_n_squares.features.organizations.base.OrganizationMember;
import at.koopro.spells_n_squares.features.organizations.base.OrganizationMission;
import at.koopro.spells_n_squares.features.organizations.base.OrganizationSystem;
import at.koopro.spells_n_squares.features.playerclass.PlayerClass;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

/**
 * Manages Dumbledore's Army training system.
 * Handles spell practice sessions, training areas, and skill progression.
 */
public final class DumbledoresArmySystem {
    private DumbledoresArmySystem() {
    }
    
    // Internal implementation using OrganizationSystem base class
    private static final DumbledoresArmySystemImpl impl = new DumbledoresArmySystemImpl();
    
    /**
     * Internal implementation that extends OrganizationSystem.
     */
    private static class DumbledoresArmySystemImpl extends OrganizationSystem<DAMember, TrainingSession> {
        @Override
        protected PlayerClass getOrganizationClass() {
            return null; // DA doesn't use a PlayerClass
        }
        
        @Override
        protected DAMember createMember(UUID playerId, ServerPlayer player, String... args) {
            return new DAMember(
                playerId,
                System.currentTimeMillis(),
                0,
                new HashMap<>(),
                1 // Start at level 1
            );
        }
        
        @Override
        protected TrainingSession buildMission(String missionId, String title, String description, Object... args) {
            String instructorName = args.length > 0 && args[0] instanceof String ? (String) args[0] : "Instructor";
            String focusSpell = args.length > 1 && args[1] instanceof String ? (String) args[1] : "";
            return new TrainingSession(
                missionId,
                instructorName,
                new HashSet<>(),
                focusSpell,
                System.currentTimeMillis(),
                0,
                TrainingStatus.SCHEDULED
            );
        }
        
        @Override
        protected DAMember updateMemberOnMissionComplete(DAMember member) {
            return new DAMember(
                member.playerId(),
                member.joinDate(),
                member.sessionsAttended() + 1,
                member.spellPracticeCounts(),
                member.skillLevel()
            );
        }
        
        @Override
        protected TrainingSession updateMissionMembers(TrainingSession session, Set<UUID> newMembers) {
            return new TrainingSession(
                session.sessionId(),
                session.instructorName(),
                newMembers,
                session.focusSpell(),
                session.startTime(),
                session.endTime(),
                session.status()
            );
        }
        
        @Override
        protected TrainingSession updateMissionStatus(TrainingSession session, long endTime) {
            return new TrainingSession(
                session.sessionId(),
                session.instructorName(),
                session.participants(),
                session.focusSpell(),
                session.startTime(),
                endTime,
                TrainingStatus.COMPLETED
            );
        }
    }
    
    /**
     * Represents a DA member.
     */
    public record DAMember(
        UUID playerId,
        long joinDate,
        int sessionsAttended,
        Map<String, Integer> spellPracticeCounts, // spellId -> practice count
        int skillLevel
    ) implements OrganizationMember {
        @Override
        public UUID getPlayerId() {
            return playerId;
        }
    }
    
    /**
     * Represents a training session.
     */
    public record TrainingSession(
        String sessionId,
        String instructorName,
        Set<UUID> participants,
        String focusSpell, // Spell being practiced
        long startTime,
        long endTime,
        TrainingStatus status
    ) implements OrganizationMission {
        @Override
        public String getMissionId() {
            return sessionId;
        }
        
        @Override
        public Set<UUID> getAssignedMembers() {
            return participants;
        }
    }
    
    /**
     * Training session status.
     */
    public enum TrainingStatus {
        SCHEDULED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }
    
    /**
     * Adds a player to Dumbledore's Army.
     */
    public static boolean addMember(ServerPlayer player) {
        return impl.addMember(player);
    }
    
    /**
     * Removes a player from Dumbledore's Army.
     */
    public static boolean removeMember(ServerPlayer player) {
        return impl.removeMember(player);
    }
    
    /**
     * Checks if a player is a DA member.
     */
    public static boolean isMember(ServerPlayer player) {
        return impl.isMember(player);
    }
    
    /**
     * Gets the DA member data for a player.
     */
    public static DAMember getMember(ServerPlayer player) {
        return impl.getMember(player);
    }
    
    /**
     * Creates a new training session.
     */
    public static TrainingSession createSession(String sessionId, String instructorName, String focusSpell) {
        return impl.createMission(sessionId, "Training Session", "Practice " + focusSpell, instructorName, focusSpell);
    }
    
    /**
     * Joins a player to a training session.
     */
    public static boolean joinSession(ServerPlayer player, String sessionId) {
        return impl.assignMemberToMission(player, sessionId);
    }
    
    /**
     * Records spell practice for a player.
     * This is organization-specific functionality, so it accesses the implementation directly.
     */
    public static void recordSpellPractice(ServerPlayer player, String spellId) {
        DAMember member = impl.getMember(player);
        if (member == null) {
            return;
        }
        
        Map<String, Integer> practiceCounts = new HashMap<>(member.spellPracticeCounts());
        practiceCounts.put(spellId, practiceCounts.getOrDefault(spellId, 0) + 1);
        
        // Update skill level based on total practice
        int totalPractice = practiceCounts.values().stream().mapToInt(Integer::intValue).sum();
        int newSkillLevel = Math.min(10, 1 + (totalPractice / 10)); // Max level 10
        
        // Update member through base class method
        impl.updateMember(player.getUUID(), new DAMember(
            member.playerId(),
            member.joinDate(),
            member.sessionsAttended(),
            practiceCounts,
            newSkillLevel
        ));
    }
    
    /**
     * Completes a training session.
     */
    public static boolean completeSession(String sessionId) {
        return impl.completeMission(sessionId);
    }
    
    /**
     * Gets all active training sessions.
     */
    public static Collection<TrainingSession> getActiveSessions() {
        return impl.getActiveMissions();
    }
}
















