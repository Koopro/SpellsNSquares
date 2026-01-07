package at.koopro.spells_n_squares.core.base.block;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Component for GeckoLib block functionality.
 * Provides block entity creation and ticker management.
 * Uses composition instead of inheritance for flexibility.
 */
public class GeckoLibBlockComponent {
    private final Block block;
    private BlockEntityCallback blockEntityCallback;
    private TickerCallback tickerCallback;
    
    /**
     * Callback for creating block entities.
     */
    @FunctionalInterface
    public interface BlockEntityCallback {
        @Nullable
        BlockEntity newBlockEntity(BlockPos pos, BlockState state);
    }
    
    /**
     * Callback for getting tickers.
     */
    public interface TickerCallback {
        @Nullable
        <T extends BlockEntity> BlockEntityTicker<T> getClientTicker(Level level, BlockState state, BlockEntityType<T> type);
        @Nullable
        <T extends BlockEntity> BlockEntityTicker<T> getServerTicker(Level level, BlockState state, BlockEntityType<T> type);
    }
    
    public GeckoLibBlockComponent(Block block) {
        this.block = block;
    }
    
    /**
     * Sets the callback for creating block entities.
     */
    public void setBlockEntityCallback(BlockEntityCallback callback) {
        this.blockEntityCallback = callback;
    }
    
    /**
     * Sets the callback for getting tickers.
     */
    public void setTickerCallback(TickerCallback callback) {
        this.tickerCallback = callback;
    }
    
    /**
     * Creates a new block entity instance for this block.
     * Calls the block entity callback if set.
     */
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (blockEntityCallback != null) {
            return blockEntityCallback.newBlockEntity(pos, state);
        }
        return null;
    }
    
    /**
     * Gets the ticker for this block entity.
     * Calls the ticker callback if set.
     */
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,
                                                                   BlockState state,
                                                                   BlockEntityType<T> type) {
        DevLogger.logMethodEntry(block, "getTicker", 
            "pos=" + (level != null ? "level" : "null") + 
            ", clientSide=" + (level != null ? level.isClientSide() : "null"));
        
        BlockEntityTicker<T> result = null;
        if (tickerCallback != null) {
            if (level.isClientSide()) {
                result = tickerCallback.getClientTicker(level, state, type);
            } else {
                result = tickerCallback.getServerTicker(level, state, type);
            }
        }
        
        DevLogger.logMethodExit(block, "getTicker", result != null ? "ticker" : "null");
        return result;
    }
    
    /**
     * Creates a ticker that validates the block entity type.
     * 
     * @param <T> The block entity type
     * @param expectedType The expected block entity type
     * @param ticker The ticker function
     * @return A ticker that validates the type
     */
    public <T extends BlockEntity> BlockEntityTicker<T> createTicker(BlockEntityType<? extends T> expectedType,
                                                                    BlockEntityTicker<? super T> ticker) {
        return (level, pos, state, blockEntity) -> {
            if (blockEntity.getType() == expectedType) {
                ticker.tick(level, pos, state, (T) blockEntity);
            }
        };
    }
}

