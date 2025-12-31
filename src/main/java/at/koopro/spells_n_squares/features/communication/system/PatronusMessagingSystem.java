package at.koopro.spells_n_squares.features.communication.system;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.*;

/**
 * System for sending messages via Patronus.
 */
public final class PatronusMessagingSystem {
    private PatronusMessagingSystem() {
    }
    
    // Map of pending messages (player UUID -> list of messages)
    // Using HashMap - order doesn't matter, O(1) lookup needed
    private static final Map<UUID, List<PatronusMessage>> pendingMessages = new HashMap<>();
    
    /**
     * Represents a Patronus message.
     */
    public record PatronusMessage(
        Component message,
        Vec3 deliveryPosition,
        long deliveryTick
    ) {
    }
    
    /**
     * Sends a message via Patronus to a target player.
     */
    public static void sendMessage(Player sender, Player recipient, Component message) {
        if (sender.level() instanceof ServerLevel serverLevel) {
            Vec3 targetPos = recipient.position();
            
            // Create message
            PatronusMessage patronusMessage = new PatronusMessage(
                message,
                targetPos,
                serverLevel.getGameTime() + 40 // Deliver in 2 seconds
            );
            
            // Add to pending messages
            pendingMessages.computeIfAbsent(recipient.getUUID(), k -> new ArrayList<>()).add(patronusMessage);
            
            // Visual effect at sender
            Vec3 senderPos = sender.position();
            serverLevel.sendParticles(ParticleTypes.END_ROD, senderPos.x, senderPos.y + 1.0, senderPos.z,
                20, 0.3, 0.3, 0.3, 0.1);
        }
    }
    
    /**
     * Delivers pending messages (called from tick handler).
     */
    public static void deliverMessages(ServerLevel level, Player player) {
        List<PatronusMessage> messages = pendingMessages.get(player.getUUID());
        if (messages == null || messages.isEmpty()) {
            return;
        }
        
        long currentTick = level.getGameTime();
        List<PatronusMessage> toDeliver = new ArrayList<>();
        
        for (PatronusMessage message : messages) {
            if (currentTick >= message.deliveryTick()) {
                toDeliver.add(message);
            }
        }
        
        for (PatronusMessage message : toDeliver) {
            if (player instanceof ServerPlayer serverPlayer) {
                // Visual effect: patronus delivers message
                Vec3 pos = message.deliveryPosition();
                level.sendParticles(ParticleTypes.END_ROD, pos.x, pos.y + 1.0, pos.z,
                    30, 0.5, 0.5, 0.5, 0.1);
                level.sendParticles(ParticleTypes.HEART, pos.x, pos.y + 1.0, pos.z,
                    10, 0.2, 0.2, 0.2, 0.05);
                
                serverPlayer.sendSystemMessage(Component.literal("[Patronus] ").append(message.message()));
            }
            messages.remove(message);
        }
        
        if (messages.isEmpty()) {
            pendingMessages.remove(player.getUUID());
        }
    }
}

