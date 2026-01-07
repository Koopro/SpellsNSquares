package at.koopro.spells_n_squares.features.social.guild;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages guild/group system for players.
 * Allows players to form groups and share resources/benefits.
 */
public final class GuildSystem {
    
    private static final Map<UUID, UUID> PLAYER_GUILDS = new ConcurrentHashMap<>();
    private static final Map<UUID, GuildData> GUILDS = new ConcurrentHashMap<>();
    
    private GuildSystem() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Represents guild data.
     */
    public record GuildData(
        UUID guildId,
        String name,
        UUID leaderId,
        Set<UUID> members,
        long createdTimestamp,
        Map<String, String> properties
    ) {
        public GuildData {
            if (members == null) {
                members = new HashSet<>();
            }
            if (properties == null) {
                properties = new HashMap<>();
            }
        }
    }
    
    /**
     * Creates a new guild.
     * 
     * @param leaderId The leader's UUID
     * @param guildName The guild name
     * @return The created guild ID, or null if creation failed
     */
    public static UUID createGuild(UUID leaderId, String guildName) {
        if (leaderId == null || guildName == null || guildName.trim().isEmpty()) {
            return null;
        }
        
        // Check if player is already in a guild
        if (PLAYER_GUILDS.containsKey(leaderId)) {
            return null;
        }
        
        UUID guildId = UUID.randomUUID();
        Set<UUID> members = new HashSet<>();
        members.add(leaderId);
        
        GuildData guild = new GuildData(
            guildId,
            guildName.trim(),
            leaderId,
            members,
            System.currentTimeMillis(),
            new HashMap<>()
        );
        
        GUILDS.put(guildId, guild);
        PLAYER_GUILDS.put(leaderId, guildId);
        
        DevLogger.logStateChange(GuildSystem.class, "createGuild",
            "Guild created: " + guildName + " by " + leaderId);
        
        return guildId;
    }
    
    /**
     * Adds a member to a guild.
     * 
     * @param guildId The guild ID
     * @param memberId The member's UUID
     * @return true if member was added
     */
    public static boolean addMember(UUID guildId, UUID memberId) {
        if (guildId == null || memberId == null) {
            return false;
        }
        
        GuildData guild = GUILDS.get(guildId);
        if (guild == null) {
            return false;
        }
        
        // Check if player is already in a guild
        if (PLAYER_GUILDS.containsKey(memberId)) {
            return false;
        }
        
        Set<UUID> members = new HashSet<>(guild.members());
        members.add(memberId);
        
        GUILDS.put(guildId, new GuildData(
            guild.guildId(),
            guild.name(),
            guild.leaderId(),
            members,
            guild.createdTimestamp(),
            guild.properties()
        ));
        
        PLAYER_GUILDS.put(memberId, guildId);
        
        return true;
    }
    
    /**
     * Removes a member from a guild.
     * 
     * @param guildId The guild ID
     * @param memberId The member's UUID
     * @return true if member was removed
     */
    public static boolean removeMember(UUID guildId, UUID memberId) {
        if (guildId == null || memberId == null) {
            return false;
        }
        
        GuildData guild = GUILDS.get(guildId);
        if (guild == null) {
            return false;
        }
        
        // Cannot remove the leader
        if (guild.leaderId().equals(memberId)) {
            return false;
        }
        
        Set<UUID> members = new HashSet<>(guild.members());
        boolean removed = members.remove(memberId);
        
        if (removed) {
            GUILDS.put(guildId, new GuildData(
                guild.guildId(),
                guild.name(),
                guild.leaderId(),
                members,
                guild.createdTimestamp(),
                guild.properties()
            ));
            
            PLAYER_GUILDS.remove(memberId);
        }
        
        return removed;
    }
    
    /**
     * Gets the guild a player belongs to.
     * 
     * @param playerId The player's UUID
     * @return Guild data, or null if not in a guild
     */
    public static GuildData getPlayerGuild(UUID playerId) {
        if (playerId == null) {
            return null;
        }
        
        UUID guildId = PLAYER_GUILDS.get(playerId);
        if (guildId == null) {
            return null;
        }
        
        return GUILDS.get(guildId);
    }
    
    /**
     * Gets all members of a guild.
     * 
     * @param guildId The guild ID
     * @return Set of member UUIDs
     */
    public static Set<UUID> getGuildMembers(UUID guildId) {
        if (guildId == null) {
            return Collections.emptySet();
        }
        
        GuildData guild = GUILDS.get(guildId);
        return guild != null ? new HashSet<>(guild.members()) : Collections.emptySet();
    }
    
    /**
     * Disbands a guild.
     * 
     * @param guildId The guild ID
     * @param leaderId The leader's UUID (must match)
     * @return true if guild was disbanded
     */
    public static boolean disbandGuild(UUID guildId, UUID leaderId) {
        if (guildId == null || leaderId == null) {
            return false;
        }
        
        GuildData guild = GUILDS.get(guildId);
        if (guild == null || !guild.leaderId().equals(leaderId)) {
            return false;
        }
        
        // Remove all members from guild mapping
        for (UUID memberId : guild.members()) {
            PLAYER_GUILDS.remove(memberId);
        }
        
        GUILDS.remove(guildId);
        
        DevLogger.logStateChange(GuildSystem.class, "disbandGuild",
            "Guild disbanded: " + guild.name());
        
        return true;
    }
}

