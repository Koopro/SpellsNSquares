package at.koopro.spells_n_squares.features.misc;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Dungbomb - A prank item that creates a smelly explosion.
 */
public class DungbombItem extends Item {
    
    public DungbombItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.FAIL;
        }
        
        Vec3 pos = player.position().add(player.getLookAngle().scale(1.5));
        pos = pos.add(0, 0.5, 0);
        
        // Create explosion effect
        serverLevel.sendParticles(
            ParticleTypes.CLOUD,
            pos.x, pos.y, pos.z,
            50,
            1.5, 1.5, 1.5,
            0.2
        );
        
        // Audio feedback
        level.playSound(null, pos.x, pos.y, pos.z,
            SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.5f, 0.8f);
        
        // Consume item
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        
        return InteractionResult.SUCCESS;
    }
}













