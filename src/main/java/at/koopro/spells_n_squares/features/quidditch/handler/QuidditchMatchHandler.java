package at.koopro.spells_n_squares.features.quidditch.handler;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.features.quidditch.QuidditchMatch;
import at.koopro.spells_n_squares.features.quidditch.QuidditchTeam;
import at.koopro.spells_n_squares.features.quidditch.system.QuidditchTeamManager;
import at.koopro.spells_n_squares.core.util.SafeEventHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles Quidditch match ticking and updates.
 * Manages match state, timeouts, and cleanup.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class QuidditchMatchHandler {
    
    private static final long MATCH_TIMEOUT_MS = 3600000; // 1 hour timeout
    private static final int TICK_INTERVAL = 20; // Check every second
    
    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }
        
        // Only check periodically
        if (serverLevel.getGameTime() % TICK_INTERVAL != 0) {
            return;
        }
        
        SafeEventHandler.execute(() -> {
            // Get all active matches
            List<QuidditchMatch> matches = new ArrayList<>(QuidditchTeamManager.getActiveMatches());
            
            for (QuidditchMatch match : matches) {
                if (match.getState() == QuidditchMatch.MatchState.IN_PROGRESS) {
                    // Check for match timeout
                    long currentTime = System.currentTimeMillis();
                    if (match.getStartTime() > 0 && (currentTime - match.getStartTime()) > MATCH_TIMEOUT_MS) {
                        // Match timeout - end the match
                        match.endMatch();
                        
                        // Notify all players
                        notifyMatchEnd(match, "Match timed out after 1 hour");
                    }
                } else if (match.getState() == QuidditchMatch.MatchState.FINISHED) {
                    // Clean up finished matches after a delay
                    long endTime = match.getEndTime();
                    if (endTime > 0 && (System.currentTimeMillis() - endTime) > 60000) { // 1 minute after end
                        QuidditchTeamManager.removeMatch(match.getMatchId());
                    }
                }
            }
        }, "quidditch match ticking");
    }
    
    /**
     * Notifies all players in a match about match end.
     */
    private static void notifyMatchEnd(QuidditchMatch match, String reason) {
        QuidditchTeam winner = match.getWinner();
        
        for (ServerPlayer player : match.getTeam1().getPlayers()) {
            if (winner != null) {
                if (winner == match.getTeam1()) {
                    player.sendSystemMessage(
                        net.minecraft.network.chat.Component.translatable("quidditch.match.won", match.getTeam1().getTeamName())
                    );
                } else {
                    player.sendSystemMessage(
                        net.minecraft.network.chat.Component.translatable("quidditch.match.lost", match.getTeam2().getTeamName())
                    );
                }
            } else {
                player.sendSystemMessage(
                    net.minecraft.network.chat.Component.translatable("quidditch.match.tie")
                );
            }
            player.sendSystemMessage(
                net.minecraft.network.chat.Component.translatable("quidditch.match.final_score",
                    match.getTeam1().getTeamName(), match.getTeam1Score(),
                    match.getTeam2().getTeamName(), match.getTeam2Score())
            );
        }
        
        for (ServerPlayer player : match.getTeam2().getPlayers()) {
            if (winner != null) {
                if (winner == match.getTeam2()) {
                    player.sendSystemMessage(
                        net.minecraft.network.chat.Component.translatable("quidditch.match.won", match.getTeam2().getTeamName())
                    );
                } else {
                    player.sendSystemMessage(
                        net.minecraft.network.chat.Component.translatable("quidditch.match.lost", match.getTeam1().getTeamName())
                    );
                }
            } else {
                player.sendSystemMessage(
                    net.minecraft.network.chat.Component.translatable("quidditch.match.tie")
                );
            }
            player.sendSystemMessage(
                net.minecraft.network.chat.Component.translatable("quidditch.match.final_score",
                    match.getTeam1().getTeamName(), match.getTeam1Score(),
                    match.getTeam2().getTeamName(), match.getTeam2Score())
            );
        }
    }
}

