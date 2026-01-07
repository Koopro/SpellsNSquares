package at.koopro.spells_n_squares.core.base.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Base class for GeckoLib blocks with block entities.
 * Provides block entity creation and ticker management.
 * Extends BaseModBlock to inherit all block functionality.
 * Uses composition with GeckoLibBlockComponent for flexibility.
 */
public abstract class BaseGeoBlock extends BaseModBlock implements EntityBlock {
    protected final GeckoLibBlockComponent geckoLibBlockComponent;
    
    public BaseGeoBlock(Properties properties) {
        super(properties);
        this.geckoLibBlockComponent = new GeckoLibBlockComponent(this);
        
        // Set up callbacks
        geckoLibBlockComponent.setBlockEntityCallback((pos, state) -> newBlockEntity(pos, state));
        geckoLibBlockComponent.setTickerCallback(new GeckoLibBlockComponent.TickerCallback() {
            @Override
            @Nullable
            public <T extends BlockEntity> BlockEntityTicker<T> getClientTicker(Level level, BlockState state, BlockEntityType<T> type) {
                return BaseGeoBlock.this.getClientTicker(level, state, type);
            }
            
            @Override
            @Nullable
            public <T extends BlockEntity> BlockEntityTicker<T> getServerTicker(Level level, BlockState state, BlockEntityType<T> type) {
                return BaseGeoBlock.this.getServerTicker(level, state, type);
            }
        });
    }
    
    /**
     * Creates a new block entity instance for this block.
     * Subclasses must implement to provide their specific block entity type.
     * 
     * @param pos The block position
     * @param state The block state
     * @return The block entity instance, or null if no block entity
     */
    @Nullable
    @Override
    public abstract BlockEntity newBlockEntity(BlockPos pos, BlockState state);
    
    /**
     * Gets the block entity type for this block.
     * Subclasses should override to return their specific type.
     * 
     * @return The block entity type, or null if not available
     */
    @Nullable
    protected BlockEntityType<?> getBlockEntityType() {
        return null;
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(net.minecraft.world.level.Level level,
                                                                   BlockState state,
                                                                   BlockEntityType<T> type) {
        return geckoLibBlockComponent.getTicker(level, state, type);
    }
    
    /**
     * Gets the client-side ticker for this block entity.
     * Subclasses should override if they need client-side ticking.
     * 
     * @param <T> The block entity type
     * @param level The level
     * @param state The block state
     * @param type The block entity type
     * @return The ticker, or null if no client-side ticking needed
     */
    @Nullable
    protected <T extends BlockEntity> BlockEntityTicker<T> getClientTicker(net.minecraft.world.level.Level level,
                                                                             BlockState state,
                                                                             BlockEntityType<T> type) {
        return null;
    }
    
    /**
     * Gets the server-side ticker for this block entity.
     * Subclasses should override if they need server-side ticking.
     * 
     * @param <T> The block entity type
     * @param level The level
     * @param state The block state
     * @param type The block entity type
     * @return The ticker, or null if no server-side ticking needed
     */
    @Nullable
    protected <T extends BlockEntity> BlockEntityTicker<T> getServerTicker(net.minecraft.world.level.Level level,
                                                                            BlockState state,
                                                                            BlockEntityType<T> type) {
        return null;
    }
    
    /**
     * Creates a ticker that validates the block entity type.
     * 
     * @param <T> The block entity type
     * @param expectedType The expected block entity type
     * @param ticker The ticker function
     * @return A ticker that validates the type
     */
    protected <T extends BlockEntity> BlockEntityTicker<T> createTicker(BlockEntityType<? extends T> expectedType,
                                                                        BlockEntityTicker<? super T> ticker) {
        return geckoLibBlockComponent.createTicker(expectedType, ticker);
    }
}

