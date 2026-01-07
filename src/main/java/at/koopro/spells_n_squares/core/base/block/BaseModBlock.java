package at.koopro.spells_n_squares.core.base.block;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * Base class for mod blocks with common functionality.
 * Provides interaction helpers, placement helpers, and standardized patterns.
 * Uses composition with ModBlockComponent for flexibility.
 */
public abstract class BaseModBlock extends Block {
    protected final ModBlockComponent modBlockComponent;
    
    public BaseModBlock(Properties properties) {
        super(properties);
        this.modBlockComponent = new ModBlockComponent(this);
        
        // Set up callbacks
        modBlockComponent.setStateCallback(new ModBlockComponent.StateCallback() {
            @Override
            public BlockState createDefaultState(BlockState baseState) {
                // This will be overridden by subclasses if needed
                return baseState;
            }
            
            @Override
            public void addStateProperties(StateDefinition.Builder<Block, BlockState> builder) {
                BaseModBlock.this.addStateProperties(builder);
            }
        });
        modBlockComponent.setPlacementCallback((state, context) -> customizePlacementState(state, context));
        modBlockComponent.setInteractionCallback(new ModBlockComponent.InteractionCallback() {
            @Override
            public InteractionResult onServerInteract(BlockState state, Level level, BlockPos pos,
                                                     ServerPlayer player, InteractionHand hand,
                                                     BlockHitResult hit) {
                return BaseModBlock.this.onServerInteract(state, level, pos, player, hand, hit);
            }
            
            @Override
            public boolean canPlayerInteract(Player player, BlockPos pos) {
                return BaseModBlock.this.canPlayerInteract(player, pos);
            }
        });
        
        // Register default state after state definition is initialized
        // Note: stateDefinition is initialized by Block constructor, so we can access it here
        registerDefaultState(createDefaultState());
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        // Call addStateProperties directly since modBlockComponent may not be initialized yet
        // (createBlockStateDefinition is called during super() constructor)
        if (modBlockComponent != null) {
            modBlockComponent.addStateProperties(builder);
        } else {
            // If component isn't initialized yet, call the method directly
            // This will be overridden by subclasses to add their properties
            addStateProperties(builder);
        }
    }
    
    /**
     * Creates the default block state.
     * Subclasses should override to set up their default state properties.
     * This is called after the state definition is created.
     * 
     * @return The default block state
     */
    protected BlockState createDefaultState() {
        return modBlockComponent.createDefaultState(this.stateDefinition.any());
    }
    
    /**
     * Adds custom state properties to the block state definition.
     * Subclasses should override to add their specific properties.
     * 
     * @param builder The state definition builder
     */
    protected void addStateProperties(StateDefinition.Builder<Block, BlockState> builder) {
        // Override in subclasses to add properties
    }
    
    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        DevLogger.logMethodEntry(this, "getStateForPlacement", 
            "pos=" + DevLogger.formatPos(context.getClickedPos()) + 
            ", player=" + (context.getPlayer() != null ? context.getPlayer().getName().getString() : "null"));
        
        BlockState state = super.getStateForPlacement(context);
        if (state == null) {
            state = defaultBlockState();
        }
        BlockState result = modBlockComponent.customizePlacementState(state, context);
        
        DevLogger.logMethodExit(this, "getStateForPlacement", result);
        return result;
    }
    
    /**
     * Customizes the block state during placement.
     * Subclasses should override to set up placement-specific state.
     * 
     * @param state The base state
     * @param context The placement context
     * @return The customized state
     */
    protected BlockState customizePlacementState(BlockState state, BlockPlaceContext context) {
        DevLogger.logMethodEntry(this, "customizePlacementState", 
            "pos=" + DevLogger.formatPos(context.getClickedPos()));
        BlockState result = state;
        DevLogger.logMethodExit(this, "customizePlacementState", result);
        return result;
    }
    
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        DevLogger.logBlockInteraction(this, "useWithoutItem", player, pos, state);
        DevLogger.logMethodEntry(this, "useWithoutItem", 
            "pos=" + DevLogger.formatPos(pos) + 
            ", player=" + (player != null ? player.getName().getString() : "null") +
            ", clientSide=" + level.isClientSide());
        
        // Client-side: return SUCCESS to allow server-side processing
        if (level.isClientSide()) {
            DevLogger.logMethodExit(this, "useWithoutItem", InteractionResult.SUCCESS);
            return InteractionResult.SUCCESS;
        }
        
        // Server-side: validate player and delegate
        if (!(player instanceof ServerPlayer serverPlayer)) {
            DevLogger.logMethodExit(this, "useWithoutItem", InteractionResult.FAIL);
            return InteractionResult.FAIL;
        }
        
        InteractionResult result = modBlockComponent.onServerInteract(state, level, pos, serverPlayer, InteractionHand.MAIN_HAND, hitResult);
        DevLogger.logMethodExit(this, "useWithoutItem", result);
        return result;
    }
    
    /**
     * Handles server-side block interaction.
     * Subclasses should override to implement their specific interaction logic.
     * 
     * @param state The block state
     * @param level The level (guaranteed to be server-side)
     * @param pos The block position
     * @param player The server player (guaranteed to be non-null)
     * @param hand The interaction hand
     * @param hit The hit result
     * @return The interaction result
     */
    protected InteractionResult onServerInteract(BlockState state, Level level, BlockPos pos,
                                                 ServerPlayer player, InteractionHand hand,
                                                 BlockHitResult hit) {
        DevLogger.logMethodEntry(this, "onServerInteract", 
            "pos=" + DevLogger.formatPos(pos) + 
            ", player=" + (player != null ? player.getName().getString() : "null") +
            ", hand=" + hand);
        InteractionResult result = InteractionResult.PASS;
        DevLogger.logMethodExit(this, "onServerInteract", result);
        return result;
    }
    
    /**
     * Checks if a player can interact with this block.
     * 
     * @param player The player
     * @param pos The block position
     * @return True if the player can interact
     */
    protected boolean canPlayerInteract(Player player, BlockPos pos) {
        return modBlockComponent.canPlayerInteract(player, pos);
    }
    
    /**
     * Creates standard block properties with common defaults.
     * 
     * @return A properties builder with common defaults
     */
    protected static Properties createProperties() {
        return BlockBehaviour.Properties.of();
    }
    
    /**
     * Creates standard block properties with strength.
     * 
     * @param strength The block strength
     * @return A properties builder with strength
     */
    protected static Properties createProperties(float strength) {
        return BlockBehaviour.Properties.of().strength(strength);
    }
    
    /**
     * Creates standard block properties with strength and resistance.
     * 
     * @param strength The block strength
     * @param resistance The explosion resistance
     * @return A properties builder with strength and resistance
     */
    protected static Properties createProperties(float strength, float resistance) {
        return BlockBehaviour.Properties.of().strength(strength, resistance);
    }
}

