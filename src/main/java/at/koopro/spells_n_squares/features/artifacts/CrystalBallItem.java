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
 * Crystal Ball - divination tool for seeing glimpses of the future.
 * Provides random hints or predictions.
 */
public class CrystalBallItem extends Item {
    
    private static final String[] PREDICTIONS = {
        "You will find something valuable soon",
        "A friend will need your help",
        "Danger approaches from the east",
        "Great fortune awaits you",
        "Beware of false promises",
        "Your path will cross with an old enemy",
        "A new opportunity will arise",
        "Trust your instincts"
    };
    
    public CrystalBallItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
            Vec3 pos = player.position();
            
            // Visual effect
            serverLevel.sendParticles(ParticleTypes.ENCHANT,
                pos.x, pos.y + 1.5, pos.z,
                25, 0.8, 0.8, 0.8, 0.1);
            
            serverLevel.sendParticles(ParticleTypes.END_ROD,
                pos.x, pos.y + 1.5, pos.z,
                15, 0.3, 0.3, 0.3, 0.03);
            
            level.playSound(null, pos.x, pos.y, pos.z,
                SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.8f, 1.2f);
            
            // Show random prediction
            String prediction = PREDICTIONS[serverLevel.random.nextInt(PREDICTIONS.length)];
            Component message = Component.translatable("message.spells_n_squares.crystal_ball.prediction", prediction);
            serverPlayer.sendSystemMessage(message);
            
            return InteractionResult.SUCCESS;
        }
        
        return InteractionResult.PASS;
    }
}







