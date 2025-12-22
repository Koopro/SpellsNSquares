package at.koopro.spells_n_squares.features.artifacts;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Foe-Glass - A magical mirror that shows enemies approaching.
 */
public class FoeGlassItem extends Item {
    
    private static final double DETECTION_RANGE = 32.0;
    
    public FoeGlassItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.FAIL;
        }
        
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.FAIL;
        }
        
        // Find hostile entities nearby
        Vec3 pos = player.position();
        AABB searchBox = new AABB(pos, pos).inflate(DETECTION_RANGE);
        List<LivingEntity> hostiles = level.getEntitiesOfClass(
            LivingEntity.class,
            searchBox,
            entity -> entity instanceof Monster && entity.isAlive() && !entity.isSpectator()
        );
        
        if (hostiles.isEmpty()) {
            serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.foe_glass.no_enemies"));
        } else {
            serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.foe_glass.enemies_detected", hostiles.size()));
            
            // Visual effect
            serverLevel.sendParticles(
                ParticleTypes.ANGRY_VILLAGER,
                pos.x, pos.y + player.getEyeHeight(), pos.z,
                10,
                0.5, 0.5, 0.5,
                0.1
            );
            
            // Audio feedback
            level.playSound(null, pos.x, pos.y, pos.z,
                SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.5f, 0.8f);
        }
        
        return InteractionResult.SUCCESS;
    }
}




