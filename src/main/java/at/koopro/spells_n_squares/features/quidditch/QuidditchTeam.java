package at.koopro.spells_n_squares.features.quidditch;

import net.minecraft.server.level.ServerPlayer;

import java.util.*;

/**
 * Represents a Quidditch team with players in specific positions.
 */
public class QuidditchTeam {
    private final String teamName;
    private final Map<QuidditchPosition, ServerPlayer> positions;
    private final Set<ServerPlayer> players;
    
    public QuidditchTeam(String teamName) {
        this.teamName = teamName;
        this.positions = new HashMap<>();
        this.players = new HashSet<>();
    }
    
    /**
     * Quidditch positions.
     */
    public enum QuidditchPosition {
        KEEPER("Keeper", "Defends the goal hoops"),
        SEEKER("Seeker", "Tries to catch the Golden Snitch"),
        CHASER("Chaser", "Scores goals with the Quaffle"),
        BEATER("Beater", "Uses Bludgers to disrupt opponents");
        
        private final String displayName;
        private final String description;
        
        QuidditchPosition(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
    }
    
    /**
     * Assigns a player to a position.
     */
    public boolean assignPlayer(ServerPlayer player, QuidditchPosition position) {
        if (players.size() >= 7) {
            return false; // Team is full
        }
        
        // Remove player from any existing position
        positions.values().remove(player);
        players.remove(player);
        
        // Check if position is already taken
        if (positions.containsKey(position)) {
            return false;
        }
        
        positions.put(position, player);
        players.add(player);
        return true;
    }
    
    /**
     * Removes a player from the team.
     */
    public boolean removePlayer(ServerPlayer player) {
        if (!players.contains(player)) {
            return false;
        }
        
        positions.values().remove(player);
        players.remove(player);
        return true;
    }
    
    /**
     * Gets the player in a specific position.
     */
    public ServerPlayer getPlayer(QuidditchPosition position) {
        return positions.get(position);
    }
    
    /**
     * Gets all players in the team.
     */
    public Set<ServerPlayer> getPlayers() {
        return new HashSet<>(players);
    }
    
    /**
     * Gets all positions.
     */
    public Map<QuidditchPosition, ServerPlayer> getPositions() {
        return new HashMap<>(positions);
    }
    
    /**
     * Checks if the team is complete (has all 7 players).
     */
    public boolean isComplete() {
        return players.size() == 7 && 
               positions.containsKey(QuidditchPosition.KEEPER) &&
               positions.containsKey(QuidditchPosition.SEEKER) &&
               positions.values().stream().filter(p -> positions.entrySet().stream()
                   .filter(e -> e.getValue() == p && e.getKey() == QuidditchPosition.CHASER)
                   .count() > 0).count() >= 3 &&
               positions.values().stream().filter(p -> positions.entrySet().stream()
                   .filter(e -> e.getValue() == p && e.getKey() == QuidditchPosition.BEATER)
                   .count() > 0).count() >= 2;
    }
    
    public String getTeamName() { return teamName; }
}
















