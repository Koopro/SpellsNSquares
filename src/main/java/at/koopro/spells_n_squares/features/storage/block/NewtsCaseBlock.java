package at.koopro.spells_n_squares.features.storage.block;

import at.koopro.spells_n_squares.features.building.block.BaseInteractiveBlock;
import at.koopro.spells_n_squares.features.storage.PocketDimensionData;
import at.koopro.spells_n_squares.features.storage.PocketDimensionManager;
import at.koopro.spells_n_squares.features.storage.block.NewtsCaseBlockItem;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

/**
 * Newt's Case block - a placeable block that provides access to a custom pocket dimension
 * with a structure schematic. Each case has its own unique dimension that persists.
 */
public class NewtsCaseBlock extends BaseInteractiveBlock implements EntityBlock {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final Property<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape CASE_SHAPE = Shapes.box(
        3.0D / 16.0D, 0.0D, 3.0D / 16.0D,
        13.0D / 16.0D, 9.0D / 16.0D, 13.0D / 16.0D
    );
    
    public NewtsCaseBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.stateDefinition.any()
            .setValue(OPEN, false)
            .setValue(FACING, Direction.NORTH));
    }
    
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // Face the block towards the player when placing
        Direction playerFacing = context.getHorizontalDirection().getOpposite();
        return this.defaultBlockState()
            .setValue(FACING, playerFacing)
            .setValue(OPEN, false);
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(OPEN, FACING);
    }
    
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        // Call parent - this will handle client/server split and call onServerInteract
        return super.use(state, level, pos, player, hand, hit);
    }
    
    @Override
    protected InteractionResult onServerInteract(BlockState state, Level level, BlockPos pos, 
                                                  ServerPlayer serverPlayer, InteractionHand hand, 
                                                  BlockHitResult hit) {
        ItemStack heldItem = serverPlayer.getItemInHand(hand);
        boolean isEmptyHand = heldItem.isEmpty();
        boolean isOpen = state.getValue(OPEN);

        // Check for upgrade item (gold ingot for now)
        if (!isEmptyHand && heldItem.is(net.minecraft.world.item.Items.GOLD_INGOT) && serverPlayer.isShiftKeyDown()) {
            return handleUpgrade(level, pos, serverPlayer, heldItem);
        }

        // Only allow interaction with empty hand or when holding the case item
        if (!isEmptyHand && !(heldItem.getItem() instanceof NewtsCaseBlockItem)) {
            return InteractionResult.PASS;
        }

        if (serverPlayer.isShiftKeyDown()) {
            // Shift-right-click: close if open, or pickup if closed
            if (isOpen) {
                setOpen(level, pos, state, false);
                return InteractionResult.SUCCESS;
            } else {
                return handlePickup(level, pos, serverPlayer);
            }
        }

        // Regular right-click
        if (!isOpen) {
            setOpen(level, pos, state, true);
            return InteractionResult.SUCCESS;
        }

        // Case is open - enter dimension (handleDimensionAccess checks if player is standing on it)
        LOGGER.info("[NewtsCaseBlock] Attempting dimension access at {}", pos);
        InteractionResult result = handleDimensionAccess(level, pos, serverPlayer);
        if (result != InteractionResult.SUCCESS && result != InteractionResult.CONSUME) {
            LOGGER.warn("[NewtsCaseBlock] Dimension access failed: {}", result);
        }
        return result;
    }
    
    /**
     * Handles upgrade attempt with gold ingot.
     */
    private InteractionResult handleUpgrade(Level level, BlockPos pos, ServerPlayer player, ItemStack upgradeItem) {
        if (!(level.getBlockEntity(pos) instanceof NewtsCaseBlockEntity blockEntity)) {
            return InteractionResult.FAIL;
        }
        
        PocketDimensionData.PocketDimensionComponent data = blockEntity.getDimensionData();
        
        // Check if player owns this case (for now, anyone can upgrade - could add ownership check later)
        int currentLevel = data.upgradeLevel();
        int requiredIngots = (currentLevel + 1) * 4; // 4, 8, 12, etc. ingots per level
        
        if (upgradeItem.getCount() < requiredIngots) {
            player.sendSystemMessage(Component.literal("§cUpgrade requires " + requiredIngots + " gold ingots (shift-right-click with ingots)"));
            return InteractionResult.FAIL;
        }
        
        // Consume ingots and upgrade
        upgradeItem.shrink(requiredIngots);
        PocketDimensionData.PocketDimensionComponent upgradedData = data.upgrade();
        blockEntity.setDimensionData(upgradedData);
        
        player.sendSystemMessage(Component.literal("§aNewt's Case upgraded to level " + upgradedData.upgradeLevel() + "! Size: " + upgradedData.size() + " blocks"));
        level.playSound(null, pos, net.minecraft.sounds.SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0f, 1.2f);
        
        return InteractionResult.SUCCESS;
    }
    
    /**
     * Handles shift-right-click to pick up the block.
     * Enforces that the case must be closed before pickup.
     * This should ONLY be called when the case is already closed.
     */
    private InteractionResult handlePickup(Level level, BlockPos pos, ServerPlayer player) {
        if (!(level.getBlockEntity(pos) instanceof NewtsCaseBlockEntity blockEntity)) {
            return InteractionResult.FAIL;
        }

        BlockState state = level.getBlockState(pos);
        
        // Double-check: case must be closed to pick up (safety check)
        if (state.hasProperty(OPEN) && state.getValue(OPEN)) {
            // This should never happen if called correctly, but close it as a safeguard
            LOGGER.warn("[NewtsCaseBlock] handlePickup called on open case at {} - closing instead", pos);
            setOpen(level, pos, state, false);
            player.sendSystemMessage(Component.translatable("message.spells_n_squares.pocket_dimension.case_must_be_closed"));
            return InteractionResult.SUCCESS;
        }
        
        // Case is confirmed closed - proceed with pickup
        LOGGER.info("[NewtsCaseBlock] Picking up case at {} by player {}", pos, player.getName().getString());
        
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
     * Requires the player to be standing on the opened case.
     */
    private InteractionResult handleDimensionAccess(Level level, BlockPos pos, ServerPlayer player) {
        if (!(level instanceof ServerLevel serverLevel)) {
            LOGGER.warn("[NewtsCaseBlock] handleDimensionAccess: level is not ServerLevel");
            return InteractionResult.FAIL;
        }

        BlockState state = level.getBlockState(pos);
        if (!state.getValue(OPEN)) {
            LOGGER.warn("[NewtsCaseBlock] handleDimensionAccess: case is closed");
            player.sendSystemMessage(Component.translatable("message.spells_n_squares.pocket_dimension.case_closed"));
            return InteractionResult.FAIL;
        }
        
        // Check if player is near the case (within 3 blocks horizontally, and at reasonable height)
        // This allows clicking from adjacent positions, similar to chests
        Vec3 playerPos = player.position();
        double dx = playerPos.x - (pos.getX() + 0.5);
        double dz = playerPos.z - (pos.getZ() + 0.5);
        double horizontalDist = Math.sqrt(dx * dx + dz * dz);
        
        // Allow interaction if player is within 3 blocks horizontally and at reasonable height
        // Height check: player should be at case level or above (not too far below)
        boolean isNearCase = horizontalDist <= 3.0 && 
                            playerPos.y >= pos.getY() - 1 && 
                            playerPos.y <= pos.getY() + 3;
        
        LOGGER.info("[NewtsCaseBlock] Player position: {}, Case position: {}, Distance: {:.2f}, Near case: {}", 
            playerPos, pos, String.format("%.2f", horizontalDist), isNearCase);
        
        if (!isNearCase) {
            player.sendSystemMessage(Component.literal("You must be near the opened case to enter the pocket dimension"));
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
        
        // Check access control (lock/whitelist)
        if (!data.hasAccess(player.getUUID())) {
            player.sendSystemMessage(Component.literal("§cYou do not have access to this case. It is locked."));
            return InteractionResult.FAIL;
        }
        
        // Require an additional click after opening before teleporting (enforce open-before-enter rule)
        if (blockEntity.wasJustOpened(level.getGameTime(), 20)) {
            // Case was just opened (within last second) - require explicit second click
            player.sendSystemMessage(Component.literal("§eThe case was just opened. Click again to enter the pocket dimension."));
            return InteractionResult.SUCCESS;
        }

        // Check if player is currently in the pocket dimension
        boolean inPocketDimension = serverLevel.dimension() == data.dimensionKey();
        
        if (inPocketDimension) {
            // Return to entry point
            return returnToEntryPoint(player, serverLevel, blockEntity, data);
        } else {
            // Enter pocket dimension - pass the case block position
            return enterPocketDimension(player, serverLevel, pos, blockEntity, data);
        }
    }
    
    /**
     * Teleports player to their pocket dimension.
     * @param casePos The position of the case block (used for exit validation)
     */
    private InteractionResult enterPocketDimension(ServerPlayer player, ServerLevel currentLevel, 
                                                   BlockPos casePos,
                                                   NewtsCaseBlockEntity blockEntity, 
                                                   PocketDimensionData.PocketDimensionComponent data) {
        // Store case block position as entry position (for exit validation)
        // This ensures we can check if the case is open when exiting
        data = data.withEntry(currentLevel.dimension(), casePos);
        blockEntity.setDimensionData(data);
        
        // Get or create pocket dimension level
        LOGGER.info("[NewtsCaseBlock] Getting or creating dimension: {}", data.dimensionKey());
        ServerLevel pocketLevel = PocketDimensionManager.getOrCreateDimension(
            currentLevel.getServer(), data.dimensionKey());
        if (pocketLevel == null) {
            LOGGER.error("[NewtsCaseBlock] Failed to get or create dimension: {}", data.dimensionKey());
            pocketLevel = currentLevel.getServer().getLevel(data.dimensionKey());
            if (pocketLevel == null) {
                LOGGER.error("[NewtsCaseBlock] Dimension does not exist: {}", data.dimensionKey());
                player.sendSystemMessage(Component.translatable("message.spells_n_squares.pocket_dimension.not_available"));
                return InteractionResult.FAIL;
            }
        }
        
        LOGGER.info("[NewtsCaseBlock] Dimension found/created: {}", pocketLevel.dimension());
        
        // Initialize Newt's Case dimension (loads structure if first time)
        // Pass upgrade level to scale the structure
        BlockPos spawnPos = PocketDimensionManager.initializeNewtsCaseDimension(pocketLevel, data.dimensionId(), data.upgradeLevel());
        LOGGER.info("[NewtsCaseBlock] Spawn position: {}, Upgrade level: {}", spawnPos, data.upgradeLevel());
        
        // Store player entry data for exit platform detection
        // Use case position as entry position so we can check if it's open on exit
        PocketDimensionManager.storePlayerEntry(
            player,
            currentLevel.dimension(),
            casePos,
            spawnPos,
            data.dimensionId()
        );
        
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
        
        // Check if the case block exists at entry position
        BlockState caseState = targetLevel.getBlockState(entryPos);
        BlockPos casePos = entryPos;
        boolean caseExists = caseState.getBlock() instanceof NewtsCaseBlock;
        
        if (!caseExists) {
            // Fallback: if entryPos is not the case, check positions below (legacy support)
            for (int yOffset = -1; yOffset >= -2; yOffset--) {
                BlockPos checkPos = entryPos.offset(0, yOffset, 0);
                BlockState checkState = targetLevel.getBlockState(checkPos);
                if (checkState.getBlock() instanceof NewtsCaseBlock) {
                    casePos = checkPos;
                    caseState = checkState;
                    caseExists = true;
                    break;
                }
            }
        }
        
        // If case doesn't exist, still allow exit to prevent trapping
        if (!caseExists) {
            player.sendSystemMessage(Component.literal("§eWarning: Case block not found. Exiting anyway."));
            // Continue with exit - teleport to safe location
        } else {
            // Case exists - check if it's closed
            if (caseState.hasProperty(OPEN) && !caseState.getValue(OPEN)) {
                // Case is closed - warn player but still allow exit to prevent trapping
                player.sendSystemMessage(Component.literal("§eWarning: The case is closed, but allowing exit to prevent trapping."));
                // Continue with exit - don't trap the player
            }
        }
        
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
        PocketDimensionManager.clearPlayerEntry(player);
        
        player.sendSystemMessage(Component.translatable("message.spells_n_squares.pocket_dimension.returned"));
        return InteractionResult.SUCCESS;
    }
    
    @Override
    public RenderShape getRenderShape(BlockState state) {
        // Use INVISIBLE for GeckoLib blocks - the BlockEntity renderer handles the actual rendering
        return RenderShape.INVISIBLE;
    }

    @Override
    public VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos, CollisionContext context) {
        return CASE_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos, CollisionContext context) {
        return CASE_SHAPE;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos, CollisionContext context) {
        return CASE_SHAPE;
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos) {
        return CASE_SHAPE;
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos) {
        // Use full block shape for interaction to improve user experience (easier to click)
        return Shapes.block();
    }
    
    @Override
    public boolean isPossibleToRespawnInThis(BlockState state) {
        return false;
    }
    
    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        // Return false to ensure proper interaction detection
        return false;
    }

    private void setOpen(Level level, BlockPos pos, BlockState state, boolean open) {
        BlockState newState = state.setValue(OPEN, open);
        level.setBlock(pos, newState, 3);
        
        // Notify BlockEntity of state change to trigger animation
        if (level.getBlockEntity(pos) instanceof NewtsCaseBlockEntity be) {
            be.onBlockStateChanged(newState);
            if (open) {
                be.markOpened(level.getGameTime());
            }
        }
        
        // Play sound effects
        if (open) {
            level.playSound(null, pos, SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 0.6f, 1.0f);
        } else {
            level.playSound(null, pos, SoundEvents.CHEST_CLOSE, SoundSource.BLOCKS, 0.6f, 1.0f);
        }
    }
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (StorageBlockEntities.NEWTS_CASE_BLOCK_ENTITY != null) {
            return new NewtsCaseBlockEntity(StorageBlockEntities.NEWTS_CASE_BLOCK_ENTITY.get(), pos, state);
        }
        return null;
    }
    
    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        LOGGER.info("[NewtsCaseBlock] Block placed at {} - Block type: {}", pos, level.getBlockState(pos).getBlock());
        LOGGER.info("[NewtsCaseBlock] Is this block? {}", level.getBlockState(pos).getBlock() == this);
    }
    
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, 
                                                                    BlockEntityType<T> type) {
        return null; // No ticker needed
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

