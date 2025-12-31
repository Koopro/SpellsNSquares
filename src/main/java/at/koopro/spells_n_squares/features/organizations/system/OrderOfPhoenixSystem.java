package at.koopro.spells_n_squares.features.organizations.system;

import at.koopro.spells_n_squares.features.organizations.base.OrganizationMember;
import at.koopro.spells_n_squares.features.organizations.base.OrganizationMission;
import at.koopro.spells_n_squares.features.organizations.base.OrganizationSystem;
import at.koopro.spells_n_squares.features.playerclass.PlayerClass;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

/**
 * Manages the Order of the Phoenix organization.
 * Handles member tracking, missions, safe houses, and communication.
 */
public final class OrderOfPhoenixSystem {
    private OrderOfPhoenixSystem() {
    }
    
    // Internal implementation using OrganizationSystem base class
    private static final OrderOfPhoenixSystemImpl impl = new OrderOfPhoenixSystemImpl();
    
    // Safe houses (location name -> SafeHouse) - organization-specific feature
    private static final Map<String, SafeHouse> safeHouses = new HashMap<>();
    
    /**
     * Internal implementation that extends OrganizationSystem.
     */
    private static class OrderOfPhoenixSystemImpl extends OrganizationSystem<OrderMember, OrderMission> {
        @Override
        protected PlayerClass getOrganizationClass() {
            return PlayerClass.ORDER_MEMBER;
        }
        
        @Override
        protected OrderMember createMember(UUID playerId, ServerPlayer player, String... args) {
            String codeName = args.length > 0 ? args[0] : "Order Member";
            return new OrderMember(
                playerId,
                codeName,
                System.currentTimeMillis(),
                0,
                0
            );
        }
        
        @Override
        protected OrderMission buildMission(String missionId, String title, String description, Object... args) {
            MissionType type = args.length > 0 && args[0] instanceof MissionType ? (MissionType) args[0] : MissionType.RESCUE;
            return new OrderMission(
                missionId,
                title,
                description,
                type,
                MissionStatus.PENDING,
                new HashSet<>(),
                System.currentTimeMillis(),
                0
            );
        }
        
        @Override
        protected OrderMember updateMemberOnMissionComplete(OrderMember member) {
            return new OrderMember(
                member.playerId(),
                member.codeName(),
                member.joinDate(),
                member.missionsCompleted() + 1,
                member.rank()
            );
        }
        
        @Override
        protected OrderMission updateMissionMembers(OrderMission mission, Set<UUID> newMembers) {
            return new OrderMission(
                mission.missionId(),
                mission.title(),
                mission.description(),
                mission.type(),
                mission.status(),
                newMembers,
                mission.startTime(),
                mission.endTime()
            );
        }
        
        @Override
        protected OrderMission updateMissionStatus(OrderMission mission, long endTime) {
            return new OrderMission(
                mission.missionId(),
                mission.title(),
                mission.description(),
                mission.type(),
                MissionStatus.COMPLETED,
                mission.assignedMembers(),
                mission.startTime(),
                endTime
            );
        }
    }
    
    /**
     * Represents an Order member.
     */
    public record OrderMember(
        UUID playerId,
        String codeName,
        long joinDate,
        int missionsCompleted,
        int rank // 0 = member, 1 = senior member, 2 = leader
    ) implements OrganizationMember {
        @Override
        public UUID getPlayerId() {
            return playerId;
        }
    }
    
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
    ) implements OrganizationMission {
        @Override
        public String getMissionId() {
            return missionId;
        }
        
        @Override
        public Set<UUID> getAssignedMembers() {
            return assignedMembers;
        }
    }
    
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
        return impl.addMember(player, codeName);
    }
    
    /**
     * Removes a player from the Order.
     */
    public static boolean removeMember(ServerPlayer player) {
        return impl.removeMember(player);
    }
    
    /**
     * Checks if a player is an Order member.
     */
    public static boolean isMember(ServerPlayer player) {
        return impl.isMember(player);
    }
    
    /**
     * Gets the Order member data for a player.
     */
    public static OrderMember getMember(ServerPlayer player) {
        return impl.getMember(player);
    }
    
    /**
     * Creates a new mission.
     */
    public static OrderMission createMission(String missionId, String title, String description, MissionType type) {
        return impl.createMission(missionId, title, description, type);
    }
    
    /**
     * Assigns a member to a mission.
     */
    public static boolean assignMemberToMission(ServerPlayer player, String missionId) {
        return impl.assignMemberToMission(player, missionId);
    }
    
    /**
     * Completes a mission.
     */
    public static boolean completeMission(String missionId) {
        return impl.completeMission(missionId);
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
        return impl.getActiveMissions();
    }
    
    /**
     * Gets all members.
     */
    public static Collection<OrderMember> getAllMembers() {
        return impl.getAllMembers();
    }
}
















