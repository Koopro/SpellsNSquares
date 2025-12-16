package at.koopro.spells_n_squares.features.artifacts;

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
 * Decoy Detonator - a distraction device from Weasley's Wizard Wheezes.
 * Creates a loud noise and visual effect to distract enemies.
 */
public class DecoyDetonatorItem extends Item {
    
    public DecoyDetonatorItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            Vec3 pos = player.position();
            Vec3 lookVec = player.getLookAngle();
            Vec3 detonatePos = pos.add(lookVec.scale(3.0));
            
            // Create explosion effect (no damage)
            serverLevel.sendParticles(ParticleTypes.EXPLOSION,
                detonatePos.x, detonatePos.y, detonatePos.z,
                1, 0.0, 0.0, 0.0, 0.0);
            
            serverLevel.sendParticles(ParticleTypes.SMOKE,
                detonatePos.x, detonatePos.y, detonatePos.z,
                50, 1.0, 1.0, 1.0, 0.1);
            
            serverLevel.sendParticles(ParticleTypes.FLAME,
                detonatePos.x, detonatePos.y, detonatePos.z,
                20, 0.5, 0.5, 0.5, 0.05);
            
            // Loud sound
            level.playSound(null, detonatePos.x, detonatePos.y, detonatePos.z,
                SoundEvents.TNT_PRIMED, SoundSource.PLAYERS, 2.0f, 0.8f);
            
            level.playSound(null, detonatePos.x, detonatePos.y, detonatePos.z,
                SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.5f, 1.0f);
            
            // Consume item
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            
            return InteractionResult.SUCCESS;
        }
        
        return InteractionResult.PASS;
    }
}




