package at.koopro.spells_n_squares.features.artifacts.item;

import at.koopro.spells_n_squares.core.data.ItemDataHelper;
import at.koopro.spells_n_squares.features.artifacts.data.MirrorOfErisedData;
import at.koopro.spells_n_squares.core.util.effect.EffectUtils;
import at.koopro.spells_n_squares.features.artifacts.base.BaseArtifactItem;
import at.koopro.spells_n_squares.features.artifacts.util.ArtifactDataHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Mirror of Erised - shows the user's deepest desires.
 * Displays a message about what the player desires most.
 */
public class MirrorOfErisedItem extends BaseArtifactItem {
    
    public MirrorOfErisedItem(Properties properties) {
        super(properties);
    }
    
    @Override
    protected InteractionResult onArtifactUse(Level level, ServerPlayer player, InteractionHand hand, ItemStack stack) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.FAIL;
        }
        
        Vec3 pos = player.position().add(0, 1.5, 0);
        
        // Get or create desire data
        MirrorOfErisedData.MirrorOfErisedComponent component = ArtifactDataHelper.getMirrorData(stack);
        
        // Visual effect (custom particles and sound)
        EffectUtils.spawnParticles(serverLevel, pos, net.minecraft.core.particles.ParticleTypes.ENCHANT, 30, 1.0, 1.0, 1.0, 0.1);
        EffectUtils.spawnParticles(serverLevel, pos, net.minecraft.core.particles.ParticleTypes.END_ROD, 20, 0.5, 0.5, 0.5, 0.05);
        serverLevel.playSound(null, pos.x, pos.y, pos.z,
            SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0f, 0.8f);
        
        // Show desire message
        MirrorOfErisedData.Desire primaryDesire = component.getPrimaryDesire();
        if (primaryDesire != null) {
            Component message = Component.translatable("message.spells_n_squares.mirror_erised.desire",
                player.getDisplayName(), primaryDesire.description());
            sendMessage(player, message);
        } else {
            // Default message if no desires tracked yet
            Component message = Component.translatable("message.spells_n_squares.mirror_erised.no_desire",
                player.getDisplayName());
            sendMessage(player, message);
        }
        
        return InteractionResult.SUCCESS;
    }
    
    @Override
    protected void spawnArtifactEffects(net.minecraft.server.level.ServerLevel level, ServerPlayer player) {
        // Effects already spawned in onArtifactUse with custom sound
        // Override to prevent double spawning
    }
    
    /**
     * Gets the Mirror of Erised data component from an item stack.
     * @deprecated Use {@link ArtifactDataHelper#getMirrorData(ItemStack)} instead
     */
    @Deprecated(forRemoval = false) // Keep for backward compatibility
    public static MirrorOfErisedData.MirrorOfErisedComponent getMirrorData(ItemStack stack) {
        return ArtifactDataHelper.getMirrorData(stack);
    }
    
    /**
     * Adds a desire to the mirror (can be called from other systems).
     */
    public static void addDesire(ItemStack stack, String description, MirrorOfErisedData.DesireType type, long timestamp) {
        MirrorOfErisedData.MirrorOfErisedComponent component = ArtifactDataHelper.getMirrorData(stack);
        component = component.addDesire(description, type, timestamp);
        ItemDataHelper.setData(stack, MirrorOfErisedData.MIRROR_OF_ERISED_DATA.get(), component);
    }
}

















