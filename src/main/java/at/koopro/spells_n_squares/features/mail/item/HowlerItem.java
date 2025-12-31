package at.koopro.spells_n_squares.features.mail.item;

import at.koopro.spells_n_squares.core.registry.ModDataComponents;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Howler - A screaming letter that delivers an angry message loudly.
 */
public class HowlerItem extends Item {
    
    public HowlerItem(Properties properties) {
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
        
        // Get message from item data (if stored)
        String message = getMessage(stack);
        if (message == null || message.isEmpty()) {
            message = "You have received a Howler!";
        }
        
        // Play loud screaming sound
        Vec3 pos = player.position();
        serverLevel.playSound(null, pos.x, pos.y, pos.z,
            SoundEvents.VILLAGER_AMBIENT, SoundSource.PLAYERS, 2.0f, 0.3f);
        
        // Visual effects - red particles
        serverLevel.sendParticles(
            ParticleTypes.ANGRY_VILLAGER,
            pos.x, pos.y + 1.5, pos.z,
            50,
            1.0, 1.0, 1.0,
            0.2
        );
        
        // Display message loudly (in all caps)
        serverPlayer.sendSystemMessage(Component.literal("§c§l" + message.toUpperCase()));
        
        // Consume the Howler after use
        stack.shrink(1);
        
        return InteractionResult.SUCCESS;
    }
    
    /**
     * Sets the message for this Howler.
     */
    public static void setMessage(ItemStack stack, String message) {
        stack.set(ModDataComponents.HOWLER_MESSAGE.get(), message);
    }
    
    /**
     * Gets the message from this Howler.
     */
    public static String getMessage(ItemStack stack) {
        String message = stack.get(ModDataComponents.HOWLER_MESSAGE.get());
        return message != null ? message : null;
    }
}
