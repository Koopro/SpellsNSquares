package at.koopro.spells_n_squares.features.quidditch;

import net.minecraft.server.level.ServerPlayer;

import java.util.*;

/**
 * Manages Quidditch teams and match scheduling.
 */
public final class QuidditchTeamManager {
    private QuidditchTeamManager() {
    }
    
    // Active teams
    private static final Map<String, QuidditchTeam> teams = new HashMap<>();
    
    // Active matches
    private static final Map<String, QuidditchMatch> activeMatches = new HashMap<>();
    
    // Player to team mapping
    private static final Map<ServerPlayer, QuidditchTeam> playerTeams = new HashMap<>();
    
    /**
     * Creates a new team.
     */
    public static QuidditchTeam createTeam(String teamName) {
        if (teams.containsKey(teamName)) {
            return null; // Team already exists
        }
        
        QuidditchTeam team = new QuidditchTeam(teamName);
        teams.put(teamName, team);
        return team;
    }
    
    /**
     * Gets a team by name.
     */
    public static QuidditchTeam getTeam(String teamName) {
        return teams.get(teamName);
    }
    
    /**
     * Gets the team a player belongs to.
     */
    public static QuidditchTeam getPlayerTeam(ServerPlayer player) {
        return playerTeams.get(player);
    }
    
    /**
     * Adds a player to a team.
     */
    public static boolean addPlayerToTeam(ServerPlayer player, QuidditchTeam team, QuidditchTeam.QuidditchPosition position) {
        // Remove from old team first
        QuidditchTeam oldTeam = playerTeams.get(player);
        if (oldTeam != null) {
            oldTeam.removePlayer(player);
        }
        
        if (team.assignPlayer(player, position)) {
            playerTeams.put(player, team);
            return true;
        }
        return false;
    }
    
    /**
     * Removes a player from their team.
     */
    public static boolean removePlayerFromTeam(ServerPlayer player) {
        QuidditchTeam team = playerTeams.remove(player);
        if (team != null) {
            return team.removePlayer(player);
        }
        return false;
    }
    
    /**
     * Creates a new match.
     */
    public static QuidditchMatch createMatch(String matchId, QuidditchTeam team1, QuidditchTeam team2, QuidditchPitch pitch) {
        if (activeMatches.containsKey(matchId)) {
            return null; // Match ID already exists
        }
        
        QuidditchMatch match = new QuidditchMatch(matchId, team1, team2, pitch);
        activeMatches.put(matchId, match);
        return match;
    }
    
    /**
     * Gets an active match.
     */
    public static QuidditchMatch getMatch(String matchId) {
        return activeMatches.get(matchId);
    }
    
    /**
     * Removes a match (when it ends).
     */
    public static void removeMatch(String matchId) {
        QuidditchMatch match = activeMatches.remove(matchId);
        if (match != null) {
            // Remove players from teams
            for (ServerPlayer player : match.getTeam1().getPlayers()) {
                playerTeams.remove(player);
            }
            for (ServerPlayer player : match.getTeam2().getPlayers()) {
                playerTeams.remove(player);
            }
        }
    }
    
    /**
     * Gets all active matches.
     */
    public static Collection<QuidditchMatch> getActiveMatches() {
        return new ArrayList<>(activeMatches.values());
    }
}















