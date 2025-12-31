package at.koopro.spells_n_squares.features.organizations.base;

import at.koopro.spells_n_squares.features.playerclass.PlayerClass;
import at.koopro.spells_n_squares.features.playerclass.system.PlayerClassManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.*;

/**
 * Base class for organization systems that manage members and missions/sessions.
 * Provides common functionality for member management, mission/session tracking,
 * and record update patterns.
 * 
 * <p>This class eliminates code duplication across organization systems like
 * DeathEaterSystem, OrderOfPhoenixSystem, and DumbledoresArmySystem.
 * 
 * <p><b>Thread Safety:</b> The static collections are accessed only from the main server thread
 * during game ticks. Thread safety is not required as Minecraft's game logic runs on a single thread.
 * 
 * @param <TMember> The member record type for this organization
 * @param <TMission> The mission/session record type for this organization
 */
public abstract class OrganizationSystem<TMember extends OrganizationMember, TMission extends OrganizationMission> {
    
    // Registry of members (UUID -> Member)
    protected final Map<UUID, TMember> members = new HashMap<>();
    
    // Active missions/sessions (ID -> Mission/Session)
    protected final Map<String, TMission> activeMissions = new HashMap<>();
    
    /**
     * Gets the PlayerClass associated with this organization, if any.
     * @return The PlayerClass, or null if this organization doesn't use a PlayerClass
     */
    protected abstract PlayerClass getOrganizationClass();
    
    /**
     * Creates a new member record for a player.
     * @param playerId The player's UUID
     * @param player The player
     * @param args Additional arguments for member creation (e.g., codeName, darkMarkName)
     * @return The created member record
     */
    protected abstract TMember createMember(UUID playerId, ServerPlayer player, String... args);
    
    /**
     * Creates a new mission/session record.
     * @param missionId The mission/session ID
     * @param title The title
     * @param description The description
     * @param args Additional arguments for mission creation (e.g., MissionType, focusSpell)
     * @return The created mission/session record
     */
    protected abstract TMission buildMission(String missionId, String title, String description, Object... args);
    
    /**
     * Updates a member record when a mission is completed.
     * @param member The current member record
     * @return The updated member record with incremented mission count
     */
    protected abstract TMember updateMemberOnMissionComplete(TMember member);
    
    /**
     * Updates a mission record with new assigned members.
     * @param mission The current mission record
     * @param newMembers The new set of assigned member UUIDs
     * @return The updated mission record
     */
    protected abstract TMission updateMissionMembers(TMission mission, Set<UUID> newMembers);
    
    /**
     * Updates a mission record with a new status.
     * @param mission The current mission record
     * @param endTime The end time (current time if completing)
     * @return The updated mission record
     */
    protected abstract TMission updateMissionStatus(TMission mission, long endTime);
    
    /**
     * Adds a player to the organization.
     * @param player The player to add
     * @param args Additional arguments for member creation
     * @return true if the player was added, false if already a member
     */
    public boolean addMember(ServerPlayer player, String... args) {
        UUID playerId = player.getUUID();
        
        if (members.containsKey(playerId)) {
            return false; // Already a member
        }
        
        // Add organization PlayerClass if applicable
        PlayerClass orgClass = getOrganizationClass();
        if (orgClass != null) {
            PlayerClassManager.addPlayerClass(player, orgClass);
        }
        
        TMember member = createMember(playerId, player, args);
        members.put(playerId, member);
        
        return true;
    }
    
    /**
     * Removes a player from the organization.
     * @param player The player to remove
     * @return true if the player was removed, false if not a member
     */
    public boolean removeMember(ServerPlayer player) {
        UUID playerId = player.getUUID();
        
        if (!members.containsKey(playerId)) {
            return false;
        }
        
        // Remove organization PlayerClass if applicable
        PlayerClass orgClass = getOrganizationClass();
        if (orgClass != null) {
            PlayerClassManager.removePlayerClass(player, orgClass);
        }
        
        members.remove(playerId);
        
        // Remove from all missions
        for (TMission mission : activeMissions.values()) {
            if (mission.getAssignedMembers().contains(playerId)) {
                Set<UUID> newMembers = new HashSet<>(mission.getAssignedMembers());
                newMembers.remove(playerId);
                activeMissions.put(mission.getMissionId(), updateMissionMembers(mission, newMembers));
            }
        }
        
        return true;
    }
    
    /**
     * Checks if a player is a member of the organization.
     * @param player The player to check
     * @return true if the player is a member
     */
    public boolean isMember(Player player) {
        return members.containsKey(player.getUUID());
    }
    
    /**
     * Gets the member data for a player.
     * @param player The player
     * @return The member record, or null if not a member
     */
    public TMember getMember(Player player) {
        return members.get(player.getUUID());
    }
    
    /**
     * Creates a new mission/session.
     * @param missionId The mission/session ID
     * @param title The title
     * @param description The description
     * @param args Additional arguments for mission creation
     * @return The created mission/session, or null if ID already exists
     */
    public TMission createMission(String missionId, String title, String description, Object... args) {
        if (activeMissions.containsKey(missionId)) {
            return null; // Mission ID already exists
        }
        
        TMission mission = buildMission(missionId, title, description, args);
        activeMissions.put(missionId, mission);
        return mission;
    }
    
    /**
     * Assigns a member to a mission/session.
     * @param player The player to assign
     * @param missionId The mission/session ID
     * @return true if the member was assigned, false if mission doesn't exist or player is not a member
     */
    public boolean assignMemberToMission(ServerPlayer player, String missionId) {
        TMission mission = activeMissions.get(missionId);
        if (mission == null || !isMember(player)) {
            return false;
        }
        
        Set<UUID> newMembers = new HashSet<>(mission.getAssignedMembers());
        newMembers.add(player.getUUID());
        
        activeMissions.put(missionId, updateMissionMembers(mission, newMembers));
        return true;
    }
    
    /**
     * Completes a mission/session.
     * @param missionId The mission/session ID
     * @return true if the mission was completed, false if mission doesn't exist
     */
    public boolean completeMission(String missionId) {
        TMission mission = activeMissions.get(missionId);
        if (mission == null) {
            return false;
        }
        
        long endTime = System.currentTimeMillis();
        activeMissions.put(missionId, updateMissionStatus(mission, endTime));
        
        // Update member stats
        for (UUID memberId : mission.getAssignedMembers()) {
            TMember member = members.get(memberId);
            if (member != null) {
                members.put(memberId, updateMemberOnMissionComplete(member));
            }
        }
        
        return true;
    }
    
    /**
     * Gets all active missions/sessions.
     * @return A collection of all active missions/sessions
     */
    public Collection<TMission> getActiveMissions() {
        return new ArrayList<>(activeMissions.values());
    }
    
    /**
     * Gets all members.
     * @return A collection of all members
     */
    public Collection<TMember> getAllMembers() {
        return new ArrayList<>(members.values());
    }
    
    /**
     * Updates a member's data.
     * Used for organization-specific member updates (e.g., spell practice tracking).
     * @param playerId The player's UUID
     * @param member The updated member record
     */
    public void updateMember(UUID playerId, TMember member) {
        members.put(playerId, member);
    }
}

