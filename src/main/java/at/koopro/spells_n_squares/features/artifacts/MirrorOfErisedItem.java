package at.koopro.spells_n_squares.features.artifacts;

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
import net.minecraft.world.phys.Vec3;

/**
 * Mirror of Erised - shows the user's deepest desires.
 * Displays a message about what the player desires most.
 */
public class MirrorOfErisedItem extends Item {
    
    public MirrorOfErisedItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
            Vec3 pos = player.position();
            
            // Visual effect
            serverLevel.sendParticles(ParticleTypes.ENCHANT,
                pos.x, pos.y + 1.5, pos.z,
                30, 1.0, 1.0, 1.0, 0.1);
            
            serverLevel.sendParticles(ParticleTypes.END_ROD,
                pos.x, pos.y + 1.5, pos.z,
                20, 0.5, 0.5, 0.5, 0.05);
            
            level.playSound(null, pos.x, pos.y, pos.z,
                SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0f, 0.8f);
            
            // Show desire message (could be expanded with actual desire tracking)
            Component message = Component.translatable("message.spells_n_squares.mirror_erised.desire",
                player.getDisplayName());
            serverPlayer.sendSystemMessage(message);
            
            return InteractionResult.SUCCESS;
        }
        
        return InteractionResult.PASS;
    }
}




