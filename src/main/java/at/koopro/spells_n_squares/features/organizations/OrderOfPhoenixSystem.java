package at.koopro.spells_n_squares.features.organizations;

import at.koopro.spells_n_squares.features.playerclass.PlayerClass;
import at.koopro.spells_n_squares.features.playerclass.PlayerClassManager;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

/**
 * Manages the Order of the Phoenix organization.
 * Handles member tracking, missions, safe houses, and communication.
 */
public final class OrderOfPhoenixSystem {
    private OrderOfPhoenixSystem() {
    }
    
    // Registry of Order members (UUID -> OrderMember)
    private static final Map<UUID, OrderMember> members = new HashMap<>();
    
    // Active missions
    private static final Map<String, OrderMission> activeMissions = new HashMap<>();
    
    // Safe houses (location name -> SafeHouse)
    private static final Map<String, SafeHouse> safeHouses = new HashMap<>();
    
    /**
     * Represents an Order member.
     */
    public record OrderMember(
        UUID playerId,
        String codeName,
        long joinDate,
        int missionsCompleted,
        int rank // 0 = member, 1 = senior member, 2 = leader
    ) {}
    
    /**
     * Represents an Order mission.
     */
    public record OrderMission(
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
     * Mission types.
     */
    public enum MissionType {
        RESCUE("Rescue Mission", "Rescue someone in danger"),
        INTELLIGENCE("Intelligence Gathering", "Gather information about enemy activities"),
        PROTECTION("Protection Detail", "Protect a target from harm"),
        RECRUITMENT("Recruitment", "Recruit new members"),
        DEFENSE("Defense Mission", "Defend a location or person");
        
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
     * Represents a safe house.
     */
    public record SafeHouse(
        String name,
        String dimension,
        int x, int y, int z,
        Set<UUID> authorizedMembers,
        boolean isActive
    ) {}
    
    /**
     * Adds a player to the Order of the Phoenix.
     */
    public static boolean addMember(ServerPlayer player, String codeName) {
        UUID playerId = player.getUUID();
        
        if (members.containsKey(playerId)) {
            return false; // Already a member
        }
        
        // Add ORDER_MEMBER player class
        PlayerClassManager.addPlayerClass(player, PlayerClass.ORDER_MEMBER);
        
        members.put(playerId, new OrderMember(
            playerId,
            codeName,
            System.currentTimeMillis(),
            0,
            0
        ));
        
        return true;
    }
    
    /**
     * Removes a player from the Order.
     */
    public static boolean removeMember(ServerPlayer player) {
        UUID playerId = player.getUUID();
        
        if (!members.containsKey(playerId)) {
            return false;
        }
        
        // Remove ORDER_MEMBER player class
        PlayerClassManager.removePlayerClass(player, PlayerClass.ORDER_MEMBER);
        
        members.remove(playerId);
        
        // Remove from all missions
        for (OrderMission mission : activeMissions.values()) {
            if (mission.assignedMembers().contains(playerId)) {
                Set<UUID> newMembers = new HashSet<>(mission.assignedMembers());
                newMembers.remove(playerId);
                activeMissions.put(mission.missionId(), new OrderMission(
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
     * Checks if a player is an Order member.
     */
    public static boolean isMember(ServerPlayer player) {
        return members.containsKey(player.getUUID());
    }
    
    /**
     * Gets the Order member data for a player.
     */
    public static OrderMember getMember(ServerPlayer player) {
        return members.get(player.getUUID());
    }
    
    /**
     * Creates a new mission.
     */
    public static OrderMission createMission(String missionId, String title, String description, MissionType type) {
        if (activeMissions.containsKey(missionId)) {
            return null; // Mission ID already exists
        }
        
        OrderMission mission = new OrderMission(
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
        OrderMission mission = activeMissions.get(missionId);
        if (mission == null || !isMember(player)) {
            return false;
        }
        
        Set<UUID> newMembers = new HashSet<>(mission.assignedMembers());
        newMembers.add(player.getUUID());
        
        activeMissions.put(missionId, new OrderMission(
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
        OrderMission mission = activeMissions.get(missionId);
        if (mission == null) {
            return false;
        }
        
        activeMissions.put(missionId, new OrderMission(
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
            OrderMember member = members.get(memberId);
            if (member != null) {
                members.put(memberId, new OrderMember(
                    member.playerId(),
                    member.codeName(),
                    member.joinDate(),
                    member.missionsCompleted() + 1,
                    member.rank()
                ));
            }
        }
        
        return true;
    }
    
    /**
     * Registers a safe house.
     */
    public static void registerSafeHouse(String name, String dimension, int x, int y, int z) {
        safeHouses.put(name, new SafeHouse(
            name,
            dimension,
            x, y, z,
            new HashSet<>(),
            true
        ));
    }
    
    /**
     * Gets a safe house.
     */
    public static SafeHouse getSafeHouse(String name) {
        return safeHouses.get(name);
    }
    
    /**
     * Checks if a player has access to a safe house.
     */
    public static boolean hasSafeHouseAccess(ServerPlayer player, String safeHouseName) {
        SafeHouse safeHouse = safeHouses.get(safeHouseName);
        if (safeHouse == null || !isMember(player)) {
            return false;
        }
        
        // All members have access by default, or check authorized list
        return safeHouse.authorizedMembers().isEmpty() || 
               safeHouse.authorizedMembers().contains(player.getUUID());
    }
    
    /**
     * Gets all active missions.
     */
    public static Collection<OrderMission> getActiveMissions() {
        return new ArrayList<>(activeMissions.values());
    }
    
    /**
     * Gets all members.
     */
    public static Collection<OrderMember> getAllMembers() {
        return new ArrayList<>(members.values());
    }
}






