package at.koopro.spells_n_squares.features.storage.item;

import at.koopro.spells_n_squares.features.storage.data.PocketDimensionData;
import at.koopro.spells_n_squares.features.storage.system.PocketDimensionManager;
import net.minecraft.core.BlockPos;
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
 * Portable storage item that provides access to a pocket dimension.
 */
public class PocketDimensionItem extends Item {
    
    private static final int DEFAULT_SIZE = 16; // 16x16 blocks
    
    public PocketDimensionItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        
        if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.FAIL;
        }
        
        PocketDimensionData.PocketDimensionComponent data = getPocketDimension(stack);
        if (data == null) {
            return InteractionResult.FAIL;
        }
        
        // Check if player is currently in the pocket dimension
        boolean inPocketDimension = serverLevel.dimension() == data.dimensionKey();
        
        if (inPocketDimension) {
            // Return to entry point
            return returnToEntryPoint(serverPlayer, serverLevel, stack, data);
        } else {
            // Enter pocket dimension
            return enterPocketDimension(serverPlayer, serverLevel, stack, data);
        }
    }
    
    /**
     * Teleports player to their pocket dimension.
     */
    private InteractionResult enterPocketDimension(ServerPlayer player, ServerLevel currentLevel, ItemStack stack, PocketDimensionData.PocketDimensionComponent data) {
        // Store entry position
        BlockPos entryPos = player.blockPosition();
        data = data.withEntry(currentLevel.dimension(), entryPos);
        stack.set(PocketDimensionData.POCKET_DIMENSION.get(), data);
        
        // Get or create pocket dimension level
        ServerLevel pocketLevel = PocketDimensionManager.getOrCreateDimension(
            currentLevel.getServer(), data.dimensionKey());
        if (pocketLevel == null) {
            // Dimension doesn't exist - try to get it directly from server
            pocketLevel = currentLevel.getServer().getLevel(data.dimensionKey());
            if (pocketLevel == null) {
                player.sendSystemMessage(Component.translatable("message.spells_n_squares.pocket_dimension.not_available"));
                return InteractionResult.FAIL;
            }
        }
        
        // Get spawn position for this pocket dimension
        BlockPos spawnPos = PocketDimensionManager.getSpawnPosition(data.dimensionId(), data.size());
        
        // Initialize spawn area if needed (check if platform exists)
        if (pocketLevel.getBlockState(spawnPos.below()).isAir()) {
            PocketDimensionManager.initializeSpawnArea(pocketLevel, spawnPos, data.size());
        }
        
        // Visual effect at origin
        Vec3 origin = player.position();
        currentLevel.sendParticles(ParticleTypes.PORTAL,
            origin.x, origin.y, origin.z,
            30, 0.5, 0.5, 0.5, 0.1);
        currentLevel.sendParticles(ParticleTypes.END_ROD,
            origin.x, origin.y, origin.z,
            20, 0.3, 0.3, 0.3, 0.05);
        
        currentLevel.playSound(null, origin.x, origin.y, origin.z,
            SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
        
        // Teleport to pocket dimension
        player.teleportTo(pocketLevel, spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
            java.util.Set.of(), player.getYRot(), player.getXRot(), false);
        
        // Visual effect at destination
        Vec3 dest = Vec3.atCenterOf(spawnPos);
        pocketLevel.sendParticles(ParticleTypes.PORTAL,
            dest.x, dest.y, dest.z,
            30, 0.5, 0.5, 0.5, 0.1);
        pocketLevel.sendParticles(ParticleTypes.END_ROD,
            dest.x, dest.y, dest.z,
            20, 0.3, 0.3, 0.3, 0.05);
        
        pocketLevel.playSound(null, dest.x, dest.y, dest.z,
            SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
        
        player.sendSystemMessage(Component.translatable("message.spells_n_squares.pocket_dimension.entered"));
        return InteractionResult.SUCCESS;
    }
    
    /**
     * Teleports player back to their entry point.
     */
    private InteractionResult returnToEntryPoint(ServerPlayer player, ServerLevel pocketLevel, ItemStack stack, PocketDimensionData.PocketDimensionComponent data) {
        if (data.entryDimension().isEmpty() || data.entryPosition().isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.spells_n_squares.pocket_dimension.no_entry_point"));
            return InteractionResult.FAIL;
        }
        
        ServerLevel targetLevel = pocketLevel.getServer().getLevel(data.entryDimension().get());
        if (targetLevel == null) {
            player.sendSystemMessage(Component.translatable("message.spells_n_squares.pocket_dimension.cannot_return"));
            return InteractionResult.FAIL;
        }
        
        BlockPos entryPos = data.entryPosition().get();
        
        // Visual effect at origin (pocket dimension)
        Vec3 origin = player.position();
        pocketLevel.sendParticles(ParticleTypes.PORTAL,
            origin.x, origin.y, origin.z,
            30, 0.5, 0.5, 0.5, 0.1);
        pocketLevel.sendParticles(ParticleTypes.END_ROD,
            origin.x, origin.y, origin.z,
            20, 0.3, 0.3, 0.3, 0.05);
        
        pocketLevel.playSound(null, origin.x, origin.y, origin.z,
            SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
        
        // Teleport back
        player.teleportTo(targetLevel, entryPos.getX() + 0.5, entryPos.getY(), entryPos.getZ() + 0.5,
            java.util.Set.of(), player.getYRot(), player.getXRot(), false);
        
        // Visual effect at destination
        Vec3 dest = Vec3.atCenterOf(entryPos);
        targetLevel.sendParticles(ParticleTypes.PORTAL,
            dest.x, dest.y, dest.z,
            30, 0.5, 0.5, 0.5, 0.1);
        targetLevel.sendParticles(ParticleTypes.END_ROD,
            dest.x, dest.y, dest.z,
            20, 0.3, 0.3, 0.3, 0.05);
        
        targetLevel.playSound(null, dest.x, dest.y, dest.z,
            SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
        
        // Clear entry data
        data = data.clearEntry();
        stack.set(PocketDimensionData.POCKET_DIMENSION.get(), data);
        
        player.sendSystemMessage(Component.translatable("message.spells_n_squares.pocket_dimension.returned"));
        return InteractionResult.SUCCESS;
    }
    
    public static PocketDimensionData.PocketDimensionComponent getPocketDimension(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof PocketDimensionItem)) {
            return null;
        }
        
        PocketDimensionData.PocketDimensionComponent data = stack.get(PocketDimensionData.POCKET_DIMENSION.get());
        if (data == null) {
            data = PocketDimensionData.PocketDimensionComponent.createDefault(DEFAULT_SIZE);
            stack.set(PocketDimensionData.POCKET_DIMENSION.get(), data);
        }
        return data;
    }
}









