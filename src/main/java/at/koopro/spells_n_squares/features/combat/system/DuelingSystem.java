package at.koopro.spells_n_squares.features.combat.system;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * System for managing duels between players.
 */
public final class DuelingSystem {
    private static final Map<UUID, DuelState> ACTIVE_DUELS = new HashMap<>();
    
    private DuelingSystem() {
    }
    
    /**
     * Starts a duel between two players.
     */
    public static boolean startDuel(Player challenger, Player opponent) {
        if (isInDuel(challenger) || isInDuel(opponent)) {
            return false; // Already in a duel
        }
        
        DuelState duel = new DuelState(challenger.getUUID(), opponent.getUUID());
        ACTIVE_DUELS.put(challenger.getUUID(), duel);
        ACTIVE_DUELS.put(opponent.getUUID(), duel);
        
        // Notify players
        if (challenger instanceof ServerPlayer serverChallenger) {
            serverChallenger.sendSystemMessage(Component.translatable("message.spells_n_squares.duel.started", opponent.getName()));
        }
        if (opponent instanceof ServerPlayer serverOpponent) {
            serverOpponent.sendSystemMessage(Component.translatable("message.spells_n_squares.duel.challenged", challenger.getName()));
        }
        
        return true;
    }
    
    /**
     * Ends a duel.
     */
    public static void endDuel(Player player) {
        DuelState duel = ACTIVE_DUELS.remove(player.getUUID());
        if (duel != null) {
            ACTIVE_DUELS.remove(duel.getChallengerId());
            ACTIVE_DUELS.remove(duel.getOpponentId());
        }
    }
    
    /**
     * Checks if a player is in a duel.
     */
    public static boolean isInDuel(Player player) {
        return ACTIVE_DUELS.containsKey(player.getUUID());
    }
    
    /**
     * Gets the opponent in a duel.
     */
    public static Player getOpponent(Player player) {
        DuelState duel = ACTIVE_DUELS.get(player.getUUID());
        if (duel == null) {
            return null;
        }
        
        UUID opponentId = duel.getChallengerId().equals(player.getUUID()) 
            ? duel.getOpponentId() 
            : duel.getChallengerId();
        
        if (player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            return serverLevel.getPlayerByUUID(opponentId);
        }
        return null;
    }
    
    /**
     * Represents the state of an active duel.
     */
    public static class DuelState {
        private final UUID challengerId;
        private final UUID opponentId;
        private final long startTime;
        private int challengerScore = 0;
        private int opponentScore = 0;
        
        public DuelState(UUID challengerId, UUID opponentId) {
            this.challengerId = challengerId;
            this.opponentId = opponentId;
            this.startTime = System.currentTimeMillis();
        }
        
        public UUID getChallengerId() {
            return challengerId;
        }
        
        public UUID getOpponentId() {
            return opponentId;
        }
        
        public long getStartTime() {
            return startTime;
        }
        
        public int getChallengerScore() {
            return challengerScore;
        }
        
        public int getOpponentScore() {
            return opponentScore;
        }
        
        public void addScore(UUID playerId, int points) {
            if (playerId.equals(challengerId)) {
                challengerScore += points;
            } else if (playerId.equals(opponentId)) {
                opponentScore += points;
            }
        }
    }
}












