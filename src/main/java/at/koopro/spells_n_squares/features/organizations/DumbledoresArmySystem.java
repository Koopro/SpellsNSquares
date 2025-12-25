package at.koopro.spells_n_squares.features.organizations;

import net.minecraft.server.level.ServerPlayer;

import java.util.*;

/**
 * Manages Dumbledore's Army training system.
 * Handles spell practice sessions, training areas, and skill progression.
 */
public final class DumbledoresArmySystem {
    private DumbledoresArmySystem() {
    }
    
    // Registry of DA members (UUID -> DAMember)
    private static final Map<UUID, DAMember> members = new HashMap<>();
    
    // Active training sessions
    private static final Map<String, TrainingSession> activeSessions = new HashMap<>();
    
    /**
     * Represents a DA member.
     */
    public record DAMember(
        UUID playerId,
        long joinDate,
        int sessionsAttended,
        Map<String, Integer> spellPracticeCounts, // spellId -> practice count
        int skillLevel
    ) {}
    
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
    ) {}
    
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
        UUID playerId = player.getUUID();
        
        if (members.containsKey(playerId)) {
            return false; // Already a member
        }
        
        members.put(playerId, new DAMember(
            playerId,
            System.currentTimeMillis(),
            0,
            new HashMap<>(),
            1 // Start at level 1
        ));
        
        return true;
    }
    
    /**
     * Removes a player from Dumbledore's Army.
     */
    public static boolean removeMember(ServerPlayer player) {
        return members.remove(player.getUUID()) != null;
    }
    
    /**
     * Checks if a player is a DA member.
     */
    public static boolean isMember(ServerPlayer player) {
        return members.containsKey(player.getUUID());
    }
    
    /**
     * Gets the DA member data for a player.
     */
    public static DAMember getMember(ServerPlayer player) {
        return members.get(player.getUUID());
    }
    
    /**
     * Creates a new training session.
     */
    public static TrainingSession createSession(String sessionId, String instructorName, String focusSpell) {
        if (activeSessions.containsKey(sessionId)) {
            return null; // Session ID already exists
        }
        
        TrainingSession session = new TrainingSession(
            sessionId,
            instructorName,
            new HashSet<>(),
            focusSpell,
            System.currentTimeMillis(),
            0,
            TrainingStatus.SCHEDULED
        );
        
        activeSessions.put(sessionId, session);
        return session;
    }
    
    /**
     * Joins a player to a training session.
     */
    public static boolean joinSession(ServerPlayer player, String sessionId) {
        TrainingSession session = activeSessions.get(sessionId);
        if (session == null || !isMember(player)) {
            return false;
        }
        
        Set<UUID> newParticipants = new HashSet<>(session.participants());
        newParticipants.add(player.getUUID());
        
        activeSessions.put(sessionId, new TrainingSession(
            session.sessionId(),
            session.instructorName(),
            newParticipants,
            session.focusSpell(),
            session.startTime(),
            session.endTime(),
            session.status()
        ));
        
        return true;
    }
    
    /**
     * Records spell practice for a player.
     */
    public static void recordSpellPractice(ServerPlayer player, String spellId) {
        DAMember member = members.get(player.getUUID());
        if (member == null) {
            return;
        }
        
        Map<String, Integer> practiceCounts = new HashMap<>(member.spellPracticeCounts());
        practiceCounts.put(spellId, practiceCounts.getOrDefault(spellId, 0) + 1);
        
        // Update skill level based on total practice
        int totalPractice = practiceCounts.values().stream().mapToInt(Integer::intValue).sum();
        int newSkillLevel = Math.min(10, 1 + (totalPractice / 10)); // Max level 10
        
        members.put(player.getUUID(), new DAMember(
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
        TrainingSession session = activeSessions.get(sessionId);
        if (session == null) {
            return false;
        }
        
        activeSessions.put(sessionId, new TrainingSession(
            session.sessionId(),
            session.instructorName(),
            session.participants(),
            session.focusSpell(),
            session.startTime(),
            System.currentTimeMillis(),
            TrainingStatus.COMPLETED
        ));
        
        // Update member stats
        for (UUID participantId : session.participants()) {
            DAMember member = members.get(participantId);
            if (member != null) {
                members.put(participantId, new DAMember(
                    member.playerId(),
                    member.joinDate(),
                    member.sessionsAttended() + 1,
                    member.spellPracticeCounts(),
                    member.skillLevel()
                ));
            }
        }
        
        return true;
    }
    
    /**
     * Gets all active training sessions.
     */
    public static Collection<TrainingSession> getActiveSessions() {
        return new ArrayList<>(activeSessions.values());
    }
}











