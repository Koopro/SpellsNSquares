package at.koopro.spells_n_squares.features.ghosts;

import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

/**
 * Manages ghost conversations and dialogue.
 */
public final class GhostDialogueSystem {
    private GhostDialogueSystem() {
    }
    
    /**
     * Initiates a conversation with a ghost.
     */
    public static void startConversation(ServerPlayer player, GhostData.GhostComponent ghost) {
        // Get greeting based on ghost type
        String greeting = getGreeting(ghost);
        player.sendSystemMessage(net.minecraft.network.chat.Component.translatable(
            "message.spells_n_squares.ghost.greeting", ghost.name(), greeting));
        
        // Record interaction
        recordInteraction(ghost, player.getUUID());
    }
    
    /**
     * Gets a greeting message based on ghost type.
     */
    private static String getGreeting(GhostData.GhostComponent ghost) {
        return switch (ghost.ghostType()) {
            case HOUSE_GHOST -> {
                String houseName = ghost.houseAssociation().name();
                yield "Greetings from " + houseName + "!";
            }
            case COMMON_GHOST -> "Hello, mortal.";
            case POLTERGEIST -> "What mischief can we cause today?";
            case SPIRIT -> "The spirits speak...";
        };
    }
    
    /**
     * Gets available quests from a ghost.
     */
    public static java.util.List<String> getAvailableQuests(GhostData.GhostComponent ghost) {
        if (!ghost.canProvideQuests()) {
            return java.util.List.of();
        }
        return ghost.availableQuests();
    }
    
    /**
     * Records an interaction with a ghost.
     */
    private static void recordInteraction(GhostData.GhostComponent ghost, UUID playerId) {
        GhostData.GhostComponent updated = ghost.recordInteraction(playerId);
        // TODO: Update ghost data in storage
    }
    
    /**
     * Gets dialogue history for a ghost.
     */
    public static java.util.List<GhostData.DialogueEntry> getDialogueHistory(
            GhostData.GhostComponent ghost) {
        return ghost.dialogueHistory();
    }
    
    /**
     * Adds dialogue to a ghost's history.
     */
    public static void addDialogue(GhostData.GhostComponent ghost, String text, UUID speakerId) {
        long timestamp = System.currentTimeMillis();
        GhostData.GhostComponent updated = ghost.addDialogue(text, speakerId, timestamp);
        // TODO: Update ghost data in storage
    }
}











