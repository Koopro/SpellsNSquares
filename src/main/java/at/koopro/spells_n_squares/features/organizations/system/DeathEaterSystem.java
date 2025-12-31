package at.koopro.spells_n_squares.features.organizations.system;

import at.koopro.spells_n_squares.features.organizations.base.OrganizationMember;
import at.koopro.spells_n_squares.features.organizations.base.OrganizationMission;
import at.koopro.spells_n_squares.features.organizations.base.OrganizationSystem;
import at.koopro.spells_n_squares.features.playerclass.PlayerClass;
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
    
    // Internal implementation using OrganizationSystem base class
    private static final DeathEaterSystemImpl impl = new DeathEaterSystemImpl();
    
    // Dark Mark summoning requests (organization-specific feature)
    private static final Map<UUID, DarkMarkSummon> activeSummons = new HashMap<>();
    
    /**
     * Internal implementation that extends OrganizationSystem.
     */
    private static class DeathEaterSystemImpl extends OrganizationSystem<DeathEater, DarkMission> {
        @Override
        protected PlayerClass getOrganizationClass() {
            return PlayerClass.DEATH_EATER;
        }
        
        @Override
        protected DeathEater createMember(UUID playerId, ServerPlayer player, String... args) {
            String darkMarkName = args.length > 0 ? args[0] : "Death Eater";
            return new DeathEater(
                playerId,
                darkMarkName,
                System.currentTimeMillis(),
                0,
                0,
                true // Has Dark Mark
            );
        }
        
        @Override
        protected DarkMission buildMission(String missionId, String title, String description, Object... args) {
            MissionType type = args.length > 0 && args[0] instanceof MissionType ? (MissionType) args[0] : MissionType.ASSASSINATION;
            return new DarkMission(
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
        protected DeathEater updateMemberOnMissionComplete(DeathEater member) {
            return new DeathEater(
                member.playerId(),
                member.darkMarkName(),
                member.joinDate(),
                member.missionsCompleted() + 1,
                member.rank(),
                member.hasDarkMark()
            );
        }
        
        @Override
        protected DarkMission updateMissionMembers(DarkMission mission, Set<UUID> newMembers) {
            return new DarkMission(
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
        protected DarkMission updateMissionStatus(DarkMission mission, long endTime) {
            return new DarkMission(
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
     * Represents a Death Eater member.
     */
    public record DeathEater(
        UUID playerId,
        String darkMarkName,
        long joinDate,
        int missionsCompleted,
        int rank, // 0 = initiate, 1 = member, 2 = inner circle
        boolean hasDarkMark
    ) implements OrganizationMember {
        @Override
        public UUID getPlayerId() {
            return playerId;
        }
    }
    
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
        return impl.addMember(player, darkMarkName);
    }
    
    /**
     * Removes a player from the Death Eaters.
     */
    public static boolean removeDeathEater(ServerPlayer player) {
        return impl.removeMember(player);
    }
    
    /**
     * Checks if a player is a Death Eater.
     */
    public static boolean isDeathEater(Player player) {
        return impl.isMember(player);
    }
    
    /**
     * Gets the Death Eater data for a player.
     */
    public static DeathEater getDeathEater(Player player) {
        return impl.getMember(player);
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
            // Convert player position to BlockPos for Dark Mark display
            net.minecraft.core.BlockPos markPos = summoner.blockPosition();
            
            at.koopro.spells_n_squares.features.organizations.network.DarkMarkPayload payload = 
                new at.koopro.spells_n_squares.features.organizations.network.DarkMarkPayload(
                    markPos,
                    reason,
                    summoner.getName().getString()
                );
            
            for (DeathEater deathEater : impl.getAllMembers()) {
                net.minecraft.server.level.ServerPlayer member = serverLevel.getServer().getPlayerList().getPlayer(deathEater.playerId());
                if (member != null && !member.getUUID().equals(summonerId)) {
                    // Send network packet to show Dark Mark in sky
                    member.connection.send(payload);
                    
                    // Send notification message
                    member.sendSystemMessage(
                        net.minecraft.network.chat.Component.translatable(
                            "message.spells_n_squares.death_eater.dark_mark_summoned",
                            summoner.getName(),
                            reason
                        )
                    );
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
     * Gets all active missions.
     */
    public static Collection<DarkMission> getActiveMissions() {
        return impl.getActiveMissions();
    }
    
    /**
     * Gets all Death Eaters.
     */
    public static Collection<DeathEater> getAllDeathEaters() {
        return impl.getAllMembers();
    }
}
















