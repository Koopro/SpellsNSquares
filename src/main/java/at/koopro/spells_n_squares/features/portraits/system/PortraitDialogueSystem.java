package at.koopro.spells_n_squares.features.portraits.system;

import at.koopro.spells_n_squares.features.portraits.PortraitData;
import net.minecraft.server.level.ServerPlayer;

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
    public static void startConversation(ServerPlayer player, PortraitData.PortraitComponent portrait, 
                                        net.minecraft.core.BlockPos pos) {
        // Get greeting based on personality
        String greeting = getGreeting(portrait);
        player.sendSystemMessage(net.minecraft.network.chat.Component.translatable(
            "message.spells_n_squares.portrait.greeting", portrait.name(), greeting));
        
        // Record interaction
        recordInteraction(player, portrait, player.getUUID(), pos);
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
                                     PortraitData.PortraitComponent targetPortrait, String message,
                                     net.minecraft.core.BlockPos targetPos) {
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
        
        // Update portrait data in storage
        net.minecraft.world.level.Level level = sender.level();
        if (level instanceof net.minecraft.server.level.ServerLevel) {
            at.koopro.spells_n_squares.features.portraits.block.MagicalPortraitBlock.setPortraitData(
                level, targetPos, updated);
        }
        
        sender.sendSystemMessage(net.minecraft.network.chat.Component.translatable(
            "message.spells_n_squares.portrait.message_sent", targetPortrait.name()));
        
        return true;
    }
    
    /**
     * Records an interaction with a portrait.
     */
    private static void recordInteraction(ServerPlayer player, PortraitData.PortraitComponent portrait, 
                                         UUID playerId, net.minecraft.core.BlockPos pos) {
        long timestamp = System.currentTimeMillis();
        PortraitData.PortraitComponent updated = portrait.addDialogue(
            "Player interacted",
            playerId,
            timestamp
        );
        
        // Update portrait data in storage
        net.minecraft.world.level.Level level = player.level();
        if (level instanceof net.minecraft.server.level.ServerLevel) {
            at.koopro.spells_n_squares.features.portraits.block.MagicalPortraitBlock.setPortraitData(
                level, pos, updated);
        }
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
    public static void rememberFact(ServerPlayer player, PortraitData.PortraitComponent portrait, 
                                   String key, String value, net.minecraft.core.BlockPos pos) {
        PortraitData.PortraitComponent updated = portrait.rememberFact(key, value);
        // Update portrait data in storage
        net.minecraft.world.level.Level level = player.level();
        if (level instanceof net.minecraft.server.level.ServerLevel) {
            at.koopro.spells_n_squares.features.portraits.block.MagicalPortraitBlock.setPortraitData(
                level, pos, updated);
        }
    }
    
    /**
     * Gets a remembered fact from a portrait.
     */
    public static String getRememberedFact(PortraitData.PortraitComponent portrait, String key) {
        return portrait.rememberedFacts().getOrDefault(key, null);
    }
}













