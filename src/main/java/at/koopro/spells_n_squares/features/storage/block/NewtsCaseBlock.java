package at.koopro.spells_n_squares.features.storage.block;

import at.koopro.spells_n_squares.core.base.block.BaseGeoBlock;
import at.koopro.spells_n_squares.core.fx.ParticlePool;
import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import at.koopro.spells_n_squares.core.util.rendering.ColorUtils;
import at.koopro.spells_n_squares.features.storage.PocketDimensionData;
import at.koopro.spells_n_squares.features.storage.PocketDimensionManager;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
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
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
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
public class NewtsCaseBlock extends BaseGeoBlock {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final Property<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape CASE_SHAPE = Shapes.box(
        3.0D / 16.0D, 0.0D, 3.0D / 16.0D,
        13.0D / 16.0D, 9.0D / 16.0D, 13.0D / 16.0D
    );
    
    public NewtsCaseBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    protected BlockState createDefaultState() {
        return this.stateDefinition.any()
            .setValue(OPEN, false)
            .setValue(FACING, Direction.NORTH);
    }
    
    @Override
    protected void addStateProperties(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(OPEN, FACING);
    }
    
    @Override
    protected BlockState customizePlacementState(BlockState state, BlockPlaceContext context) {
        // Face the block towards the player when placing
        Direction playerFacing = context.getHorizontalDirection().getOpposite();
        return state
            .setValue(FACING, playerFacing)
            .setValue(OPEN, false);
    }
    
    /**
     * Public method to handle block interaction.
     * Can be called from BlockItem or event handlers.
     * 
     * @param state The block state
     * @param level The level
     * @param pos The block position
     * @param player The player
     * @param hand The interaction hand
     * @param hit The hit result
     * @return The interaction result
     */
    public InteractionResult handleInteraction(BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        DevLogger.logBlockInteraction(this, "handleInteraction", player, pos, state);
        DevLogger.logMethodEntry(this, "handleInteraction", 
            "pos=" + DevLogger.formatPos(pos) + 
            ", player=" + (player != null ? player.getName().getString() : "null") +
            ", hand=" + hand);
        
        if (level.isClientSide()) {
            DevLogger.logMethodExit(this, "handleInteraction", InteractionResult.SUCCESS);
            return InteractionResult.SUCCESS;
        }
        
        if (!(player instanceof ServerPlayer serverPlayer)) {
            DevLogger.logMethodExit(this, "handleInteraction", InteractionResult.FAIL);
            return InteractionResult.FAIL;
        }
        
        InteractionResult result = onServerInteract(state, level, pos, serverPlayer, hand, hit);
        DevLogger.logMethodExit(this, "handleInteraction", result);
        return result;
    }
    
    @Override
    protected InteractionResult onServerInteract(BlockState state, Level level, BlockPos pos, 
                                                  ServerPlayer serverPlayer, InteractionHand hand, 
                                                  BlockHitResult hit) {
        DevLogger.logMethodEntry(this, "onServerInteract", 
            "pos=" + DevLogger.formatPos(pos) + 
            ", player=" + (serverPlayer != null ? serverPlayer.getName().getString() : "null") +
            ", hand=" + hand);
        
        ItemStack heldItem = serverPlayer.getItemInHand(hand);
        boolean isEmptyHand = heldItem.isEmpty();
        boolean isOpen = state.getValue(OPEN);
        
        DevLogger.logParameter(this, "onServerInteract", "isEmptyHand", isEmptyHand);
        DevLogger.logParameter(this, "onServerInteract", "isOpen", isOpen);
        DevLogger.logParameter(this, "onServerInteract", "heldItem", 
            heldItem.isEmpty() ? "empty" : heldItem.getItem().getDescriptionId());

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
        DevLogger.logDebug(this, "onServerInteract", "Attempting dimension access");
        InteractionResult result = handleDimensionAccess(level, pos, serverPlayer);
        if (result != InteractionResult.SUCCESS && result != InteractionResult.CONSUME) {
            LOGGER.warn("[NewtsCaseBlock] Dimension access failed: {}", result);
            DevLogger.logWarn(this, "onServerInteract", "Dimension access failed: " + result);
        }
        DevLogger.logMethodExit(this, "onServerInteract", result);
        return result;
    }
    
    /**
     * Handles upgrade attempt with gold ingot.
     */
    private InteractionResult handleUpgrade(Level level, BlockPos pos, ServerPlayer player, ItemStack upgradeItem) {
        DevLogger.logMethodEntry(this, "handleUpgrade", 
            "pos=" + DevLogger.formatPos(pos) + 
            ", player=" + (player != null ? player.getName().getString() : "null") +
            ", ingotCount=" + upgradeItem.getCount());
        
        if (!(level.getBlockEntity(pos) instanceof NewtsCaseBlockEntity blockEntity)) {
            DevLogger.logMethodExit(this, "handleUpgrade", InteractionResult.FAIL);
            return InteractionResult.FAIL;
        }
        
        PocketDimensionData.PocketDimensionComponent data = blockEntity.getDimensionData();
        
        // Check if player owns this case (for now, anyone can upgrade - could add ownership check later)
        int currentLevel = data.upgradeLevel();
        int requiredIngots = (currentLevel + 1) * 4; // 4, 8, 12, etc. ingots per level
        
        DevLogger.logParameter(this, "handleUpgrade", "currentLevel", currentLevel);
        DevLogger.logParameter(this, "handleUpgrade", "requiredIngots", requiredIngots);
        
        if (upgradeItem.getCount() < requiredIngots) {
            player.sendSystemMessage(at.koopro.spells_n_squares.core.util.rendering.ColorUtils.coloredText("Upgrade requires " + requiredIngots + " gold ingots (shift-right-click with ingots)", at.koopro.spells_n_squares.core.util.rendering.ColorUtils.SPELL_RED));
            DevLogger.logMethodExit(this, "handleUpgrade", InteractionResult.FAIL);
            return InteractionResult.FAIL;
        }
        
        // Consume ingots and upgrade
        upgradeItem.shrink(requiredIngots);
        PocketDimensionData.PocketDimensionComponent upgradedData = data.upgrade();
        blockEntity.setDimensionData(upgradedData);
        
        DevLogger.logStateChange(this, "handleUpgrade", 
            "upgraded to level " + upgradedData.upgradeLevel() + 
            ", size=" + upgradedData.size() + 
            ", pos=" + DevLogger.formatPos(pos));
        
        player.sendSystemMessage(at.koopro.spells_n_squares.core.util.rendering.ColorUtils.coloredText("Newt's Case upgraded to level " + upgradedData.upgradeLevel() + "! Size: " + upgradedData.size() + " blocks", at.koopro.spells_n_squares.core.util.rendering.ColorUtils.SPELL_GREEN));
        level.playSound(null, pos, net.minecraft.sounds.SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0f, 1.2f);
        
        DevLogger.logMethodExit(this, "handleUpgrade", InteractionResult.SUCCESS);
        return InteractionResult.SUCCESS;
    }
    
    /**
     * Handles shift-right-click to pick up the block.
     * Enforces that the case must be closed before pickup.
     * This should ONLY be called when the case is already closed.
     */
    private InteractionResult handlePickup(Level level, BlockPos pos, ServerPlayer player) {
        DevLogger.logMethodEntry(this, "handlePickup", 
            "pos=" + DevLogger.formatPos(pos) + 
            ", player=" + (player != null ? player.getName().getString() : "null"));
        
        if (!(level.getBlockEntity(pos) instanceof NewtsCaseBlockEntity blockEntity)) {
            DevLogger.logMethodExit(this, "handlePickup", InteractionResult.FAIL);
            return InteractionResult.FAIL;
        }

        BlockState state = level.getBlockState(pos);
        
        // Double-check: case must be closed to pick up (safety check)
        if (state.hasProperty(OPEN) && state.getValue(OPEN)) {
            // This should never happen if called correctly, but close it as a safeguard
            LOGGER.warn("[NewtsCaseBlock] handlePickup called on open case at {} - closing instead", pos);
            DevLogger.logWarn(this, "handlePickup", "Called on open case - closing instead");
            setOpen(level, pos, state, false);
            player.sendSystemMessage(Component.translatable("message.spells_n_squares.pocket_dimension.case_must_be_closed"));
            DevLogger.logMethodExit(this, "handlePickup", InteractionResult.SUCCESS);
            return InteractionResult.SUCCESS;
        }
        
        // Case is confirmed closed - proceed with pickup
        LOGGER.info("[NewtsCaseBlock] Picking up case at {} by player {}", pos, player.getName().getString());
        DevLogger.logStateChange(this, "handlePickup", "Picking up case, pos=" + DevLogger.formatPos(pos));
        
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
        DevLogger.logMethodExit(this, "handlePickup", InteractionResult.SUCCESS);
        return InteractionResult.SUCCESS;
    }
    
    /**
     * Handles right-click to access the dimension.
     * Requires the player to be standing on the opened case.
     */
    private InteractionResult handleDimensionAccess(Level level, BlockPos pos, ServerPlayer player) {
        DevLogger.logMethodEntry(this, "handleDimensionAccess", 
            "pos=" + DevLogger.formatPos(pos) + 
            ", player=" + (player != null ? player.getName().getString() : "null"));
        
        if (!(level instanceof ServerLevel serverLevel)) {
            LOGGER.warn("[NewtsCaseBlock] handleDimensionAccess: level is not ServerLevel");
            DevLogger.logWarn(this, "handleDimensionAccess", "level is not ServerLevel");
            DevLogger.logMethodExit(this, "handleDimensionAccess", InteractionResult.FAIL);
            return InteractionResult.FAIL;
        }

        BlockState state = level.getBlockState(pos);
        if (!state.getValue(OPEN)) {
            LOGGER.warn("[NewtsCaseBlock] handleDimensionAccess: case is closed");
            DevLogger.logWarn(this, "handleDimensionAccess", "case is closed");
            player.sendSystemMessage(Component.translatable("message.spells_n_squares.pocket_dimension.case_closed"));
            DevLogger.logMethodExit(this, "handleDimensionAccess", InteractionResult.FAIL);
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
            player.sendSystemMessage(ColorUtils.coloredText("You must be near the opened case to enter the pocket dimension", ColorUtils.SPELL_RED));
            DevLogger.logWarn(this, "handleDimensionAccess", "Player not near case");
            DevLogger.logMethodExit(this, "handleDimensionAccess", InteractionResult.FAIL);
            return InteractionResult.FAIL;
        }
        
        BlockEntity be = level.getBlockEntity(pos);
        NewtsCaseBlockEntity blockEntity;
        
        if (be instanceof NewtsCaseBlockEntity) {
            blockEntity = (NewtsCaseBlockEntity) be;
        } else {
            // BlockEntity might not exist - try to create it
            if (be == null) {
                DevLogger.logDebug(this, "handleDimensionAccess", "Creating missing BlockEntity");
                BlockEntity newBE = newBlockEntity(pos, level.getBlockState(pos));
                if (newBE instanceof NewtsCaseBlockEntity) {
                    serverLevel.setBlockEntity(newBE);
                    be = level.getBlockEntity(pos);
                }
            }
            
            if (!(be instanceof NewtsCaseBlockEntity)) {
                player.sendSystemMessage(ColorUtils.coloredText("Error: BlockEntity not initialized. Try breaking and replacing the block.", ColorUtils.SPELL_RED));
                DevLogger.logError(this, "handleDimensionAccess", "BlockEntity not initialized", null);
                DevLogger.logMethodExit(this, "handleDimensionAccess", InteractionResult.FAIL);
                return InteractionResult.FAIL;
            }
            blockEntity = (NewtsCaseBlockEntity) be;
        }
        
        PocketDimensionData.PocketDimensionComponent data = blockEntity.getDimensionData();
        
        // Check access control (lock/whitelist)
        if (!data.hasAccess(player.getUUID())) {
            player.sendSystemMessage(ColorUtils.coloredText("You do not have access to this case. It is locked.", ColorUtils.SPELL_RED));
            DevLogger.logWarn(this, "handleDimensionAccess", "Player does not have access");
            DevLogger.logMethodExit(this, "handleDimensionAccess", InteractionResult.FAIL);
            return InteractionResult.FAIL;
        }
        
        // Require an additional click after opening before teleporting (enforce open-before-enter rule)
        if (blockEntity.wasJustOpened(level.getGameTime(), 20)) {
            // Case was just opened (within last second) - require explicit second click
            player.sendSystemMessage(ColorUtils.coloredText("The case was just opened. Click again to enter the pocket dimension.", ColorUtils.SPELL_GOLD));
            DevLogger.logDebug(this, "handleDimensionAccess", "Case just opened, requiring second click");
            DevLogger.logMethodExit(this, "handleDimensionAccess", InteractionResult.SUCCESS);
            return InteractionResult.SUCCESS;
        }

        // Check if player is currently in the pocket dimension
        boolean inPocketDimension = serverLevel.dimension() == data.dimensionKey();
        DevLogger.logParameter(this, "handleDimensionAccess", "inPocketDimension", inPocketDimension);
        
        InteractionResult result;
        if (inPocketDimension) {
            // Return to entry point
            DevLogger.logDebug(this, "handleDimensionAccess", "Returning to entry point");
            result = returnToEntryPoint(player, serverLevel, blockEntity, data);
        } else {
            // Enter pocket dimension - pass the case block position
            DevLogger.logDebug(this, "handleDimensionAccess", "Entering pocket dimension");
            result = enterPocketDimension(player, serverLevel, pos, blockEntity, data);
        }
        
        DevLogger.logMethodExit(this, "handleDimensionAccess", result);
        return result;
    }
    
    /**
     * Teleports player to their pocket dimension.
     * @param casePos The position of the case block (used for exit validation)
     */
    private InteractionResult enterPocketDimension(ServerPlayer player, ServerLevel currentLevel, 
                                                   BlockPos casePos,
                                                   NewtsCaseBlockEntity blockEntity, 
                                                   PocketDimensionData.PocketDimensionComponent data) {
        DevLogger.logMethodEntry(this, "enterPocketDimension", 
            "player=" + (player != null ? player.getName().getString() : "null") +
            ", casePos=" + DevLogger.formatPos(casePos) +
            ", dimensionId=" + data.dimensionId());
        
        // Store case block position as entry position (for exit validation)
        // This ensures we can check if the case is open when exiting
        data = data.withEntry(currentLevel.dimension(), casePos);
        blockEntity.setDimensionData(data);
        
        DevLogger.logStateChange(this, "enterPocketDimension", 
            "Stored entry point, dimension=" + data.dimensionKey());
        
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
        ParticlePool.queueParticle(
            currentLevel,
            ParticleTypes.PORTAL,
            origin,
            30, 0.5, 0.5, 0.5, 0.1
        );
        ParticlePool.queueParticle(
            currentLevel,
            ParticleTypes.END_ROD,
            origin,
            20, 0.3, 0.3, 0.3, 0.05
        );
        
        currentLevel.playSound(null, origin.x, origin.y, origin.z,
            SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
        
        // Teleport to pocket dimension
        DevLogger.logStateChange(this, "enterPocketDimension", 
            "Teleporting player to pocket dimension, spawnPos=" + DevLogger.formatPos(spawnPos));
        player.teleportTo(pocketLevel, spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
            java.util.Set.of(), player.getYRot(), player.getXRot(), false);
        
        // Visual effect at destination
        Vec3 dest = Vec3.atCenterOf(spawnPos);
        ParticlePool.queueParticle(
            pocketLevel,
            ParticleTypes.PORTAL,
            dest,
            30, 0.5, 0.5, 0.5, 0.1
        );
        ParticlePool.queueParticle(
            pocketLevel,
            ParticleTypes.END_ROD,
            dest,
            20, 0.3, 0.3, 0.3, 0.05
        );
        
        pocketLevel.playSound(null, dest.x, dest.y, dest.z,
            SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
        
        player.sendSystemMessage(Component.translatable("message.spells_n_squares.pocket_dimension.entered"));
        DevLogger.logMethodExit(this, "enterPocketDimension", InteractionResult.SUCCESS);
        return InteractionResult.SUCCESS;
    }
    
    /**
     * Teleports player back to their entry point.
     */
    private InteractionResult returnToEntryPoint(ServerPlayer player, ServerLevel pocketLevel, 
                                                 NewtsCaseBlockEntity blockEntity,
                                                 PocketDimensionData.PocketDimensionComponent data) {
        DevLogger.logMethodEntry(this, "returnToEntryPoint", 
            "player=" + (player != null ? player.getName().getString() : "null"));
        
        if (data.entryDimension().isEmpty() || data.entryPosition().isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.spells_n_squares.pocket_dimension.no_entry_point"));
            DevLogger.logWarn(this, "returnToEntryPoint", "No entry point stored");
            DevLogger.logMethodExit(this, "returnToEntryPoint", InteractionResult.FAIL);
            return InteractionResult.FAIL;
        }
        
        ServerLevel targetLevel = pocketLevel.getServer().getLevel(data.entryDimension().get());
        if (targetLevel == null) {
            player.sendSystemMessage(Component.translatable("message.spells_n_squares.pocket_dimension.cannot_return"));
            DevLogger.logError(this, "returnToEntryPoint", "Target level not found", null);
            DevLogger.logMethodExit(this, "returnToEntryPoint", InteractionResult.FAIL);
            return InteractionResult.FAIL;
        }
        
        BlockPos entryPos = data.entryPosition().get();
        DevLogger.logParameter(this, "returnToEntryPoint", "entryPos", DevLogger.formatPos(entryPos));
        
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
            player.sendSystemMessage(ColorUtils.coloredText("Warning: Case block not found. Exiting anyway.", ColorUtils.SPELL_GOLD));
            // Continue with exit - teleport to safe location
        } else {
            // Case exists - check if it's closed
            if (caseState.hasProperty(OPEN) && !caseState.getValue(OPEN)) {
                // Case is closed - warn player but still allow exit to prevent trapping
                player.sendSystemMessage(ColorUtils.coloredText("Warning: The case is closed, but allowing exit to prevent trapping.", ColorUtils.SPELL_GOLD));
                // Continue with exit - don't trap the player
            }
        }
        
        // Visual effect at origin (pocket dimension)
        Vec3 origin = player.position();
        ParticlePool.queueParticle(
            pocketLevel,
            ParticleTypes.PORTAL,
            origin,
            30, 0.5, 0.5, 0.5, 0.1
        );
        ParticlePool.queueParticle(
            pocketLevel,
            ParticleTypes.END_ROD,
            origin,
            20, 0.3, 0.3, 0.3, 0.05
        );
        
        pocketLevel.playSound(null, origin.x, origin.y, origin.z,
            SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
        
        // Teleport back
        DevLogger.logStateChange(this, "returnToEntryPoint", 
            "Teleporting player back to entry point, entryPos=" + DevLogger.formatPos(entryPos));
        player.teleportTo(targetLevel, entryPos.getX() + 0.5, entryPos.getY(), entryPos.getZ() + 0.5,
            java.util.Set.of(), player.getYRot(), player.getXRot(), false);
        
        // Visual effect at destination
        Vec3 dest = Vec3.atCenterOf(entryPos);
        ParticlePool.queueParticle(
            targetLevel,
            ParticleTypes.PORTAL,
            dest,
            30, 0.5, 0.5, 0.5, 0.1
        );
        ParticlePool.queueParticle(
            targetLevel,
            ParticleTypes.END_ROD,
            dest,
            20, 0.3, 0.3, 0.3, 0.05
        );
        
        targetLevel.playSound(null, dest.x, dest.y, dest.z,
            SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
        
        // Clear entry data
        data = data.clearEntry();
        blockEntity.setDimensionData(data);
        DevLogger.logStateChange(this, "returnToEntryPoint", "Cleared entry data");
        
        // Clear player entry tracking
        PocketDimensionManager.clearPlayerEntry(player);
        
        player.sendSystemMessage(Component.translatable("message.spells_n_squares.pocket_dimension.returned"));
        DevLogger.logMethodExit(this, "returnToEntryPoint", InteractionResult.SUCCESS);
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
        DevLogger.logStateChange(this, "setOpen", 
            "open=" + open + ", pos=" + DevLogger.formatPos(pos));
        DevLogger.logMethodEntry(this, "setOpen", 
            "pos=" + DevLogger.formatPos(pos) + ", open=" + open);
        
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
        
        DevLogger.logMethodExit(this, "setOpen");
    }
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        DevLogger.logMethodEntry(this, "newBlockEntity", "pos=" + DevLogger.formatPos(pos));
        BlockEntity result = null;
        if (StorageBlockEntities.NEWTS_CASE_BLOCK_ENTITY != null) {
            result = new NewtsCaseBlockEntity(StorageBlockEntities.NEWTS_CASE_BLOCK_ENTITY.get(), pos, state);
            DevLogger.logStateChange(this, "newBlockEntity", 
                "Created NewtsCaseBlockEntity, pos=" + DevLogger.formatPos(pos));
        } else {
            DevLogger.logWarn(this, "newBlockEntity", "NEWTS_CASE_BLOCK_ENTITY is null");
        }
        DevLogger.logMethodExit(this, "newBlockEntity", result != null ? "NewtsCaseBlockEntity" : "null");
        return result;
    }
    
    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        LOGGER.info("[NewtsCaseBlock] Block placed at {} - Block type: {}", pos, level.getBlockState(pos).getBlock());
        LOGGER.info("[NewtsCaseBlock] Is this block? {}", level.getBlockState(pos).getBlock() == this);
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

