package at.koopro.spells_n_squares.features.artifacts;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Deluminator artifact that can capture and release light.
 */
public class DeluminatorItem extends Item {
    
    public DeluminatorItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        
        if (!(level instanceof ServerLevel serverLevel) || !(context.getPlayer() instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.FAIL;
        }
        
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        ItemStack stack = context.getItemInHand();
        DeluminatorData.DeluminatorComponent component = getDeluminatorData(stack);
        
        // Check if we're trying to capture light
        if (isLightSource(state)) {
            // Capture mode: store the light and remove it
            if (component.hasCapturedLight()) {
                serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.deluminator.already_has_light"));
                return InteractionResult.FAIL;
            }
            
            // Store the captured light
            DeluminatorData.DeluminatorComponent newComponent = component.withCapturedLight(state);
            stack.set(DeluminatorData.DELUMINATOR_DATA.get(), newComponent);
            
            // Remove the light block
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            
            // Visual and audio feedback
            Vec3 center = Vec3.atCenterOf(pos);
            serverLevel.sendParticles(ParticleTypes.END_ROD, center.x, center.y, center.z, 
                20, 0.3, 0.3, 0.3, 0.1);
            serverLevel.sendParticles(ParticleTypes.ENCHANT, center.x, center.y, center.z, 
                15, 0.2, 0.2, 0.2, 0.05);
            level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 
                0.7f, 1.2f);
            
            serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.deluminator.light_captured"));
            return InteractionResult.SUCCESS;
        }
        
        // Release mode: check if we have captured light
        if (component.hasCapturedLight()) {
            BlockState capturedLight = component.getCapturedLight();
            BlockPos targetPos = pos;
            
            // If clicking on a solid block, place light on adjacent face
            if (!state.isAir() && !state.canBeReplaced()) {
                targetPos = pos.relative(context.getClickedFace());
                state = level.getBlockState(targetPos);
            }
            
            // Check if target position is valid
            if (!state.isAir() && !state.canBeReplaced()) {
                serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.deluminator.cannot_place"));
                return InteractionResult.FAIL;
            }
            
            // Place the captured light
            level.setBlock(targetPos, capturedLight, 3);
            
            // Clear the captured light from item
            DeluminatorData.DeluminatorComponent newComponent = component.clearCapturedLight();
            stack.set(DeluminatorData.DELUMINATOR_DATA.get(), newComponent);
            
            // Visual and audio feedback
            Vec3 center = Vec3.atCenterOf(targetPos);
            serverLevel.sendParticles(ParticleTypes.END_ROD, center.x, center.y, center.z, 
                20, 0.3, 0.3, 0.3, 0.1);
            serverLevel.sendParticles(ParticleTypes.ENCHANT, center.x, center.y, center.z, 
                15, 0.2, 0.2, 0.2, 0.05);
            level.playSound(null, targetPos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 
                0.7f, 0.9f);
            
            serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.deluminator.light_released"));
            return InteractionResult.SUCCESS;
        }
        
        // No light source clicked and no captured light
        serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.deluminator.no_light"));
        return InteractionResult.PASS;
    }
    
    /**
     * Gets the deluminator data component from an item stack.
     */
    public static DeluminatorData.DeluminatorComponent getDeluminatorData(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof DeluminatorItem)) {
            return new DeluminatorData.DeluminatorComponent();
        }
        
        DeluminatorData.DeluminatorComponent data = stack.get(DeluminatorData.DELUMINATOR_DATA.get());
        if (data == null) {
            data = new DeluminatorData.DeluminatorComponent();
            stack.set(DeluminatorData.DELUMINATOR_DATA.get(), data);
        }
        return data;
    }
    
    /**
     * Checks if a block state is a light source that can be captured.
     */
    private boolean isLightSource(BlockState state) {
        // Check specific light source blocks
        if (state.is(Blocks.TORCH) ||
            state.is(Blocks.SOUL_TORCH) ||
            state.is(Blocks.REDSTONE_TORCH) ||
            state.is(Blocks.LANTERN) ||
            state.is(Blocks.SOUL_LANTERN) ||
            state.is(Blocks.GLOWSTONE) ||
            state.is(Blocks.SEA_LANTERN) ||
            state.is(Blocks.SHROOMLIGHT) ||
            state.is(Blocks.JACK_O_LANTERN) ||
            state.is(Blocks.END_ROD) ||
            state.is(Blocks.BEACON) ||
            state.is(Blocks.CAMPFIRE) ||
            state.is(Blocks.SOUL_CAMPFIRE) ||
            state.is(Blocks.REDSTONE_LAMP)) {
            return true;
        }
        
        // Check if block emits light (light level > 0)
        // Note: getLightEmission() is deprecated but still functional
        // In future versions, use state.getLightEmission(level, pos) instead
        @SuppressWarnings("deprecation")
        int lightEmission = state.getLightEmission();
        return lightEmission > 0;
    }
}
















