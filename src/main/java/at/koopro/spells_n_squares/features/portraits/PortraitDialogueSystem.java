package at.koopro.spells_n_squares.features.portraits;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

/**
 * Manages portrait conversations and dialogue.
 */
public final class PortraitDialogueSystem {
    private PortraitDialogueSystem() {
    }
    
    /**
     * Initiates a conversation with a portrait.
     */
    public static void startConversation(ServerPlayer player, PortraitData.PortraitComponent portrait) {
        // Get greeting based on personality
        String greeting = getGreeting(portrait);
        player.sendSystemMessage(net.minecraft.network.chat.Component.translatable(
            "message.spells_n_squares.portrait.greeting", portrait.name(), greeting));
        
        // Record interaction
        recordInteraction(portrait, player.getUUID());
    }
    
    /**
     * Gets a greeting message based on portrait personality.
     */
    private static String getGreeting(PortraitData.PortraitComponent portrait) {
        return switch (portrait.personality()) {
            case FRIENDLY -> "Hello there! How can I help you?";
            case GRUMPY -> "What do you want?";
            case WISE -> "Greetings, seeker of knowledge.";
            case MISCHIEVOUS -> "Well, well, what have we here?";
            case GUARDIAN -> "State your business.";
            case NEUTRAL -> "Hello.";
        };
    }
    
    /**
     * Sends a message through a portrait network.
     * Portraits can deliver messages between locations.
     */
    public static boolean sendMessage(ServerPlayer sender, PortraitData.PortraitComponent sourcePortrait,
                                     PortraitData.PortraitComponent targetPortrait, String message) {
        if (!sourcePortrait.isAwakened() || !targetPortrait.isAwakened()) {
            return false; // Both portraits must be awakened
        }
        
        // Add message to target portrait's dialogue history
        long timestamp = System.currentTimeMillis();
        PortraitData.PortraitComponent updated = targetPortrait.addDialogue(
            "Message from " + sourcePortrait.name() + ": " + message,
            sender.getUUID(),
            timestamp
        );
        
        // TODO: Update portrait data in storage
        
        sender.sendSystemMessage(net.minecraft.network.chat.Component.translatable(
            "message.spells_n_squares.portrait.message_sent", targetPortrait.name()));
        
        return true;
    }
    
    /**
     * Records an interaction with a portrait.
     */
    private static void recordInteraction(PortraitData.PortraitComponent portrait, UUID playerId) {
        long timestamp = System.currentTimeMillis();
        PortraitData.PortraitComponent updated = portrait.addDialogue(
            "Player interacted",
            playerId,
            timestamp
        );
        
        // TODO: Update portrait data in storage
    }
    
    /**
     * Gets dialogue history for a portrait.
     */
    public static java.util.List<PortraitData.DialogueEntry> getDialogueHistory(
            PortraitData.PortraitComponent portrait) {
        return portrait.dialogueHistory();
    }
    
    /**
     * Makes a portrait remember a fact.
     */
    public static void rememberFact(PortraitData.PortraitComponent portrait, String key, String value) {
        PortraitData.PortraitComponent updated = portrait.rememberFact(key, value);
        // TODO: Update portrait data in storage
    }
    
    /**
     * Gets a remembered fact from a portrait.
     */
    public static String getRememberedFact(PortraitData.PortraitComponent portrait, String key) {
        return portrait.rememberedFacts().getOrDefault(key, null);
    }
}



