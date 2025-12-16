package at.koopro.spells_n_squares.features.quidditch;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.*;

/**
 * Represents a Quidditch match in progress.
 * Manages match state, teams, scoring, and game mechanics.
 */
public class QuidditchMatch {
    private final String matchId;
    private final QuidditchTeam team1;
    private final QuidditchTeam team2;
    private final QuidditchPitch pitch;
    private MatchState state;
    private int team1Score;
    private int team2Score;
    private long startTime;
    private long endTime;
    private ServerPlayer snitchHolder; // Player who caught the snitch
    
    public QuidditchMatch(String matchId, QuidditchTeam team1, QuidditchTeam team2, QuidditchPitch pitch) {
        this.matchId = matchId;
        this.team1 = team1;
        this.team2 = team2;
        this.pitch = pitch;
        this.state = MatchState.WAITING;
        this.team1Score = 0;
        this.team2Score = 0;
    }
    
    public enum MatchState {
        WAITING,    // Waiting for players to join
        STARTING,   // Match is starting (countdown)
        IN_PROGRESS, // Match is active
        PAUSED,     // Match is paused
        FINISHED    // Match has ended
    }
    
    /**
     * Starts the match.
     */
    public void startMatch() {
        if (state != MatchState.WAITING && state != MatchState.PAUSED) {
            return;
        }
        state = MatchState.IN_PROGRESS;
        startTime = System.currentTimeMillis();
    }
    
    /**
     * Ends the match.
     */
    public void endMatch() {
        state = MatchState.FINISHED;
        endTime = System.currentTimeMillis();
    }
    
    /**
     * Scores a goal for a team.
     * @param team The team that scored
     * @return true if the goal was valid
     */
    public boolean scoreGoal(QuidditchTeam team) {
        if (state != MatchState.IN_PROGRESS) {
            return false;
        }
        
        if (team == team1) {
            team1Score += 10; // 10 points per goal
        } else if (team == team2) {
            team2Score += 10;
        } else {
            return false;
        }
        
        return true;
    }
    
    /**
     * Catches the snitch.
     * @param player The player who caught it
     * @param team The team the player belongs to
     * @return true if the catch was valid
     */
    public boolean catchSnitch(ServerPlayer player, QuidditchTeam team) {
        if (state != MatchState.IN_PROGRESS || snitchHolder != null) {
            return false;
        }
        
        snitchHolder = player;
        
        // Add 150 points to the catching team
        if (team == team1) {
            team1Score += 150;
        } else if (team == team2) {
            team2Score += 150;
        } else {
            return false;
        }
        
        // End the match
        endMatch();
        return true;
    }
    
    /**
     * Gets the winning team.
     * @return The winning team, or null if tie
     */
    public QuidditchTeam getWinner() {
        if (team1Score > team2Score) {
            return team1;
        } else if (team2Score > team1Score) {
            return team2;
        }
        return null; // Tie
    }
    
    // Getters
    public String getMatchId() { return matchId; }
    public QuidditchTeam getTeam1() { return team1; }
    public QuidditchTeam getTeam2() { return team2; }
    public QuidditchPitch getPitch() { return pitch; }
    public MatchState getState() { return state; }
    public int getTeam1Score() { return team1Score; }
    public int getTeam2Score() { return team2Score; }
    public long getStartTime() { return startTime; }
    public long getEndTime() { return endTime; }
    public ServerPlayer getSnitchHolder() { return snitchHolder; }
    
    public void setState(MatchState state) { this.state = state; }
}



