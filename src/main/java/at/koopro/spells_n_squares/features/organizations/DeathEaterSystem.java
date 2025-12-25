package at.koopro.spells_n_squares.features.organizations;

import at.koopro.spells_n_squares.features.playerclass.PlayerClass;
import at.koopro.spells_n_squares.features.playerclass.PlayerClassManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.*;

/**
 * Manages the Death Eater organization.
 * Handles member tracking, Dark Mark mechanics, recruitment, and dark missions.
 */
public final class DeathEaterSystem {
    private DeathEaterSystem() {
    }
    
    // Registry of Death Eaters (UUID -> DeathEater)
    private static final Map<UUID, DeathEater> deathEaters = new HashMap<>();
    
    // Active dark missions
    private static final Map<String, DarkMission> activeMissions = new HashMap<>();
    
    // Dark Mark summoning requests
    private static final Map<UUID, DarkMarkSummon> activeSummons = new HashMap<>();
    
    /**
     * Represents a Death Eater member.
     */
    public record DeathEater(
        UUID playerId,
        String darkMarkName,
        long joinDate,
        int missionsCompleted,
        int rank, // 0 = initiate, 1 = member, 2 = inner circle
        boolean hasDarkMark
    ) {}
    
    /**
     * Represents a dark mission.
     */
    public record DarkMission(
        String missionId,
        String title,
        String description,
        MissionType type,
        MissionStatus status,
        Set<UUID> assignedMembers,
        long startTime,
        long endTime
    ) {}
    
    /**
     * Dark mission types.
     */
    public enum MissionType {
        ASSASSINATION("Assassination", "Eliminate a target"),
        TERROR("Terror Attack", "Spread fear and chaos"),
        RECRUITMENT("Recruitment", "Recruit new Death Eaters"),
        THEFT("Theft", "Steal valuable items or information"),
        SABOTAGE("Sabotage", "Sabotage enemy operations");
        
        private final String displayName;
        private final String description;
        
        MissionType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
    }
    
    /**
     * Mission status.
     */
    public enum MissionStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        FAILED,
        CANCELLED
    }
    
    /**
     * Represents a Dark Mark summoning.
     */
    public record DarkMarkSummon(
        UUID summonerId,
        String location,
        String reason,
        long summonTime,
        Set<UUID> respondedMembers
    ) {}
    
    /**
     * Adds a player to the Death Eaters.
     */
    public static boolean addDeathEater(ServerPlayer player, String darkMarkName) {
        UUID playerId = player.getUUID();
        
        if (deathEaters.containsKey(playerId)) {
            return false; // Already a Death Eater
        }
        
        // Add DEATH_EATER player class
        PlayerClassManager.addPlayerClass(player, PlayerClass.DEATH_EATER);
        
        deathEaters.put(playerId, new DeathEater(
            playerId,
            darkMarkName,
            System.currentTimeMillis(),
            0,
            0,
            true // Has Dark Mark
        ));
        
        return true;
    }
    
    /**
     * Removes a player from the Death Eaters.
     */
    public static boolean removeDeathEater(ServerPlayer player) {
        UUID playerId = player.getUUID();
        
        if (!deathEaters.containsKey(playerId)) {
            return false;
        }
        
        // Remove DEATH_EATER player class
        PlayerClassManager.removePlayerClass(player, PlayerClass.DEATH_EATER);
        
        deathEaters.remove(playerId);
        
        // Remove from all missions
        for (DarkMission mission : activeMissions.values()) {
            if (mission.assignedMembers().contains(playerId)) {
                Set<UUID> newMembers = new HashSet<>(mission.assignedMembers());
                newMembers.remove(playerId);
                activeMissions.put(mission.missionId(), new DarkMission(
                    mission.missionId(),
                    mission.title(),
                    mission.description(),
                    mission.type(),
                    mission.status(),
                    newMembers,
                    mission.startTime(),
                    mission.endTime()
                ));
            }
        }
        
        return true;
    }
    
    /**
     * Checks if a player is a Death Eater.
     */
    public static boolean isDeathEater(Player player) {
        return deathEaters.containsKey(player.getUUID());
    }
    
    /**
     * Gets the Death Eater data for a player.
     */
    public static DeathEater getDeathEater(Player player) {
        return deathEaters.get(player.getUUID());
    }
    
    /**
     * Summons all Death Eaters via Dark Mark.
     */
    public static DarkMarkSummon summonDeathEaters(ServerPlayer summoner, String location, String reason) {
        UUID summonerId = summoner.getUUID();
        
        if (!isDeathEater(summoner)) {
            return null; // Only Death Eaters can summon
        }
        
        DarkMarkSummon summon = new DarkMarkSummon(
            summonerId,
            location,
            reason,
            System.currentTimeMillis(),
            new HashSet<>()
        );
        
        activeSummons.put(summonerId, summon);
        
        // Notify all Death Eaters
        if (summoner.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            for (DeathEater deathEater : deathEaters.values()) {
                net.minecraft.server.level.ServerPlayer member = serverLevel.getServer().getPlayerList().getPlayer(deathEater.playerId());
                if (member != null && !member.getUUID().equals(summonerId)) {
                    // TODO: Send network packet to show Dark Mark in sky
                    // TODO: Send notification message
                }
            }
        }
        
        return summon;
    }
    
    /**
     * Responds to a Dark Mark summon.
     */
    public static boolean respondToSummon(ServerPlayer player, UUID summonerId) {
        DarkMarkSummon summon = activeSummons.get(summonerId);
        if (summon == null || !isDeathEater(player)) {
            return false;
        }
        
        Set<UUID> responded = new HashSet<>(summon.respondedMembers());
        responded.add(player.getUUID());
        
        activeSummons.put(summonerId, new DarkMarkSummon(
            summon.summonerId(),
            summon.location(),
            summon.reason(),
            summon.summonTime(),
            responded
        ));
        
        return true;
    }
    
    /**
     * Creates a new dark mission.
     */
    public static DarkMission createMission(String missionId, String title, String description, MissionType type) {
        if (activeMissions.containsKey(missionId)) {
            return null; // Mission ID already exists
        }
        
        DarkMission mission = new DarkMission(
            missionId,
            title,
            description,
            type,
            MissionStatus.PENDING,
            new HashSet<>(),
            System.currentTimeMillis(),
            0
        );
        
        activeMissions.put(missionId, mission);
        return mission;
    }
    
    /**
     * Assigns a member to a mission.
     */
    public static boolean assignMemberToMission(ServerPlayer player, String missionId) {
        DarkMission mission = activeMissions.get(missionId);
        if (mission == null || !isDeathEater(player)) {
            return false;
        }
        
        Set<UUID> newMembers = new HashSet<>(mission.assignedMembers());
        newMembers.add(player.getUUID());
        
        activeMissions.put(missionId, new DarkMission(
            mission.missionId(),
            mission.title(),
            mission.description(),
            mission.type(),
            mission.status(),
            newMembers,
            mission.startTime(),
            mission.endTime()
        ));
        
        return true;
    }
    
    /**
     * Completes a mission.
     */
    public static boolean completeMission(String missionId) {
        DarkMission mission = activeMissions.get(missionId);
        if (mission == null) {
            return false;
        }
        
        activeMissions.put(missionId, new DarkMission(
            mission.missionId(),
            mission.title(),
            mission.description(),
            mission.type(),
            MissionStatus.COMPLETED,
            mission.assignedMembers(),
            mission.startTime(),
            System.currentTimeMillis()
        ));
        
        // Update member stats
        for (UUID memberId : mission.assignedMembers()) {
            DeathEater deathEater = deathEaters.get(memberId);
            if (deathEater != null) {
                deathEaters.put(memberId, new DeathEater(
                    deathEater.playerId(),
                    deathEater.darkMarkName(),
                    deathEater.joinDate(),
                    deathEater.missionsCompleted() + 1,
                    deathEater.rank(),
                    deathEater.hasDarkMark()
                ));
            }
        }
        
        return true;
    }
    
    /**
     * Gets all active missions.
     */
    public static Collection<DarkMission> getActiveMissions() {
        return new ArrayList<>(activeMissions.values());
    }
    
    /**
     * Gets all Death Eaters.
     */
    public static Collection<DeathEater> getAllDeathEaters() {
        return new ArrayList<>(deathEaters.values());
    }
}











