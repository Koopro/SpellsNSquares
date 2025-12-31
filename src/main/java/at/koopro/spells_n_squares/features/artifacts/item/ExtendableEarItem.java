package at.koopro.spells_n_squares.features.artifacts.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Extendable Ear - eavesdropping device from Weasley's Wizard Wheezes.
 * Allows listening to conversations from a distance.
 */
public class ExtendableEarItem extends Item {
    
    private static final double LISTEN_RANGE = 32.0;
    
    public ExtendableEarItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            Vec3 playerPos = player.position();
            AABB searchArea = new AABB(
                playerPos.x - LISTEN_RANGE, playerPos.y - LISTEN_RANGE, playerPos.z - LISTEN_RANGE,
                playerPos.x + LISTEN_RANGE, playerPos.y + LISTEN_RANGE, playerPos.z + LISTEN_RANGE
            );
            
            // Find nearby players (for eavesdropping on conversations)
            var players = level.getEntitiesOfClass(Player.class, searchArea,
                p -> p != player && p.isAlive());
            
            if (!players.isEmpty()) {
                // Show message about nearby players (eavesdropping is now automatic via chat handler)
                int count = players.size();
                serverPlayer.sendSystemMessage(Component.translatable(
                    "message.spells_n_squares.extendable_ear.listening",
                    count));
                serverPlayer.sendSystemMessage(Component.translatable(
                    "message.spells_n_squares.extendable_ear.active"));
                
                // Visual effect
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.NOTE,
                        playerPos.x, playerPos.y + 1.0, playerPos.z,
                        10, 0.3, 0.3, 0.3, 0.05);
                }
                
                level.playSound(null, playerPos.x, playerPos.y, playerPos.z,
                    SoundEvents.NOTE_BLOCK_PLING, SoundSource.PLAYERS, 0.5f, 1.5f);
                
                return InteractionResult.SUCCESS;
            } else {
                serverPlayer.sendSystemMessage(Component.translatable(
                    "message.spells_n_squares.extendable_ear.no_conversations"));
            }
        }
        
        return InteractionResult.PASS;
    }
}

















