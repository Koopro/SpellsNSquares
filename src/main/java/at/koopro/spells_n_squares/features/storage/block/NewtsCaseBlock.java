package at.koopro.spells_n_squares.features.storage.block;

import at.koopro.spells_n_squares.features.building.block.BaseInteractiveBlock;
import at.koopro.spells_n_squares.features.storage.PocketDimensionData;
import at.koopro.spells_n_squares.features.storage.PocketDimensionManager;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Newt's Case block - a placeable block that provides access to a custom pocket dimension
 * with a fixed magical creature habitat layout.
 */
public class NewtsCaseBlock extends BaseInteractiveBlock implements EntityBlock {
    
    private static final int DEFAULT_SIZE = 32;
    
    public NewtsCaseBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, 
                                InteractionHand hand, BlockHitResult hit) {
        // Debug: Log that use method was called
        System.out.println("[NewtsCaseBlock] use() called directly at " + pos);
        System.out.println("[NewtsCaseBlock] Is client side: " + level.isClientSide());
        System.out.println("[NewtsCaseBlock] Player: " + (player != null ? player.getName().getString() : "null"));
        System.out.println("[NewtsCaseBlock] Hand: " + hand);
        System.out.println("[NewtsCaseBlock] Is shift down: " + (player != null ? player.isShiftKeyDown() : "N/A"));
        
        // Call parent implementation which handles client/server logic
        return super.use(state, level, pos, player, hand, hit);
    }
    
    @Override
    protected InteractionResult onServerInteract(BlockState state, Level level, BlockPos pos, 
                                                  ServerPlayer serverPlayer, InteractionHand hand, 
                                                  BlockHitResult hit) {
        // Debug: Log that interaction was called
        System.out.println("[NewtsCaseBlock] onServerInteract() called at " + pos);
        System.out.println("[NewtsCaseBlock] Block type: " + state.getBlock().getClass().getSimpleName());
        System.out.println("[NewtsCaseBlock] BlockEntity: " + (level.getBlockEntity(pos) != null ? level.getBlockEntity(pos).getClass().getSimpleName() : "null"));
        System.out.println("[NewtsCaseBlock] Shift check: " + serverPlayer.isShiftKeyDown());
        
        // Check if player is shift-clicking to pick up the block
        if (serverPlayer.isShiftKeyDown()) {
            System.out.println("[NewtsCaseBlock] Handling pickup");
            return handlePickup(level, pos, serverPlayer);
        } else {
            System.out.println("[NewtsCaseBlock] Handling dimension access");
            serverPlayer.sendSystemMessage(Component.literal("Newt's Case clicked!"));
            return handleDimensionAccess(level, pos, serverPlayer);
        }
    }
    
    /**
     * Handles shift-right-click to pick up the block.
     */
    private InteractionResult handlePickup(Level level, BlockPos pos, ServerPlayer player) {
        if (!(level.getBlockEntity(pos) instanceof NewtsCaseBlockEntity blockEntity)) {
            return InteractionResult.FAIL;
        }
        
        // Get dimension data from BlockEntity
        PocketDimensionData.PocketDimensionComponent dimensionData = blockEntity.getDimensionData();
        
        // Create ItemStack with preserved dimension data
        ItemStack itemStack = blockEntity.getItemStack();
        
        // Break the block
        level.removeBlock(pos, false);
        
        // Give item to player
        if (!player.getInventory().add(itemStack)) {
            // Inventory full, drop the item
            player.drop(itemStack, false);
        }
        
        level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.5f, 1.0f);
        return InteractionResult.SUCCESS;
    }
    
    /**
     * Handles right-click to access the dimension.
     */
    private InteractionResult handleDimensionAccess(Level level, BlockPos pos, ServerPlayer player) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.FAIL;
        }
        
        BlockEntity be = level.getBlockEntity(pos);
        NewtsCaseBlockEntity blockEntity;
        
        if (be instanceof NewtsCaseBlockEntity) {
            blockEntity = (NewtsCaseBlockEntity) be;
        } else {
            // BlockEntity might not exist - try to create it
            if (be == null) {
                BlockEntity newBE = newBlockEntity(pos, level.getBlockState(pos));
                if (newBE instanceof NewtsCaseBlockEntity) {
                    serverLevel.setBlockEntity(newBE);
                    be = level.getBlockEntity(pos);
                }
            }
            
            if (!(be instanceof NewtsCaseBlockEntity)) {
                player.sendSystemMessage(Component.literal("Error: BlockEntity not initialized. Try breaking and replacing the block."));
                return InteractionResult.FAIL;
            }
            blockEntity = (NewtsCaseBlockEntity) be;
        }
        
        PocketDimensionData.PocketDimensionComponent data = blockEntity.getDimensionData();
        
        // Check if player is currently in the pocket dimension
        boolean inPocketDimension = serverLevel.dimension() == data.dimensionKey();
        
        if (inPocketDimension) {
            // Return to entry point
            return returnToEntryPoint(player, serverLevel, blockEntity, data);
        } else {
            // Enter pocket dimension
            return enterPocketDimension(player, serverLevel, blockEntity, data);
        }
    }
    
    /**
     * Teleports player to their pocket dimension.
     */
    private InteractionResult enterPocketDimension(ServerPlayer player, ServerLevel currentLevel, 
                                                   NewtsCaseBlockEntity blockEntity, 
                                                   PocketDimensionData.PocketDimensionComponent data) {
        // Store entry position
        BlockPos entryPos = player.blockPosition();
        data = data.withEntry(currentLevel.dimension(), entryPos);
        blockEntity.setDimensionData(data);
        
        // Get or create pocket dimension level
        ServerLevel pocketLevel = PocketDimensionManager.getOrCreateDimension(
            currentLevel.getServer(), data.dimensionKey());
        if (pocketLevel == null) {
            pocketLevel = currentLevel.getServer().getLevel(data.dimensionKey());
            if (pocketLevel == null) {
                player.sendSystemMessage(Component.translatable("message.spells_n_squares.pocket_dimension.not_available"));
                return InteractionResult.FAIL;
            }
        }
        
        // Get spawn position for this pocket dimension
        net.minecraft.core.BlockPos spawnPos = PocketDimensionManager.getSpawnPosition(data.dimensionId(), data.size());
        
        // Store player entry data for exit platform detection
        PocketDimensionManager.storePlayerEntry(
            player.getUUID(),
            currentLevel.dimension(),
            entryPos,
            spawnPos,
            data.dimensionId()
        );
        
        // Initialize spawn area if needed (check if platform exists)
        if (pocketLevel.getBlockState(spawnPos.below()).isAir()) {
            PocketDimensionManager.initializeSpawnArea(pocketLevel, spawnPos, data.size(), data.type());
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
    private InteractionResult returnToEntryPoint(ServerPlayer player, ServerLevel pocketLevel, 
                                                 NewtsCaseBlockEntity blockEntity,
                                                 PocketDimensionData.PocketDimensionComponent data) {
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
        blockEntity.setDimensionData(data);
        
        // Clear player entry tracking
        PocketDimensionManager.clearPlayerEntry(player.getUUID());
        
        player.sendSystemMessage(Component.translatable("message.spells_n_squares.pocket_dimension.returned"));
        return InteractionResult.SUCCESS;
    }
    
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (StorageBlockEntities.NEWTS_CASE_BLOCK_ENTITY != null) {
            try {
                return new NewtsCaseBlockEntity(StorageBlockEntities.NEWTS_CASE_BLOCK_ENTITY.get(), pos, state);
            } catch (Exception e) {
                // BlockEntityType not ready yet
                return null;
            }
        }
        return null;
    }
    
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, 
                                                                    BlockEntityType<T> type) {
        return null; // No ticker needed
    }
    
    @Nullable
    @Override
    public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        // Return null to ensure the block's use() method is called instead of trying to open a menu
        // This allows empty-hand interactions to work properly
        return null;
    }
    
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        // Preserve dimension data when block is broken normally
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof NewtsCaseBlockEntity blockEntity) {
            // The dimension data will be preserved through the BlockEntity's getItemStack method
            // when the block is broken, but we need to ensure it's saved
            blockEntity.setChanged();
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
    
    @Override
    public java.util.List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
        // Override drops to preserve dimension data
        if (builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.BLOCK_ENTITY) instanceof NewtsCaseBlockEntity blockEntity) {
            ItemStack stack = blockEntity.getItemStack();
            return java.util.List.of(stack);
        }
        return super.getDrops(state, builder);
    }
}


