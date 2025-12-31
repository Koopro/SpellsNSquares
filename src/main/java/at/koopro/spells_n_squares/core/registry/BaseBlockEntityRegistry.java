package at.koopro.spells_n_squares.core.registry;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;
import java.util.function.Supplier;

/**
 * Base class for BlockEntity registries.
 * Provides common patterns for registering BlockEntity types with consistent structure.
 * 
 * <p>Subclasses should:
 * <ul>
 *   <li>Declare their DeferredRegister as a static final field using {@link #createRegistry()}</li>
 *   <li>Declare their BlockEntityType holders as static fields</li>
 *   <li>Implement initialization methods that call {@link #registerBlockEntityType}</li>
 * </ul>
 */
public abstract class BaseBlockEntityRegistry {
    /**
     * The mod ID, shared across all BlockEntity registries.
     */
    protected static final String MODID = SpellsNSquares.MODID;
    
    /**
     * Creates a DeferredRegister for BlockEntity types with the mod's ID.
     * This is the standard way to create a BlockEntity registry.
     * 
     * @return A new DeferredRegister for BlockEntity types
     */
    public static DeferredRegister<BlockEntityType<?>> createRegistry() {
        return DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    }
    
    /**
     * Functional interface for creating BlockEntity instances.
     * 
     * @param <T> The BlockEntity type
     */
    @FunctionalInterface
    public interface BlockEntityFactory<T extends BlockEntity> {
        T create(BlockEntityType<T> type, BlockPos pos, BlockState state);
    }
    
    /**
     * Registers a BlockEntityType with the given name, factory, and blocks.
     * This is the standard pattern for registering BlockEntity types.
     * 
     * <p>This method handles the common pattern of creating a BlockEntityType
     * with a factory function and a set of valid blocks.
     * 
     * @param <T> The BlockEntity type
     * @param registry The DeferredRegister to register with
     * @param name The name of the BlockEntity type (e.g., "mailbox_block_entity")
     * @param factory A function that creates a BlockEntity instance given the type, position, and state
     * @param blocks The set of blocks that this BlockEntity type is valid for
     * @return A DeferredHolder for the registered BlockEntity type
     */
    @SuppressWarnings("unchecked")
    public static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> registerBlockEntityType(
            DeferredRegister<BlockEntityType<?>> registry,
            String name,
            BlockEntityFactory<T> factory,
            Set<Block> blocks) {
        
        final BlockEntityType<T>[] typeRef = new BlockEntityType[1];
        
        return registry.register(name, () -> {
            typeRef[0] = new BlockEntityType<>(
                (pos, state) -> factory.create(typeRef[0], pos, state),
                blocks
            );
            return typeRef[0];
        });
    }
    
    /**
     * Registers a BlockEntityType with a single block.
     * Convenience method for the common case of one block per BlockEntity type.
     * 
     * @param <T> The BlockEntity type
     * @param registry The DeferredRegister to register with
     * @param name The name of the BlockEntity type
     * @param factory A function that creates a BlockEntity instance
     * @param block The block that this BlockEntity type is valid for
     * @return A DeferredHolder for the registered BlockEntity type
     */
    public static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> registerBlockEntityType(
            DeferredRegister<BlockEntityType<?>> registry,
            String name,
            BlockEntityFactory<T> factory,
            Block block) {
        return registerBlockEntityType(registry, name, factory, Set.of(block));
    }
    
    /**
     * Registers a BlockEntityType with a block from a DeferredHolder.
     * Convenience method for when the block comes from a DeferredHolder.
     * 
     * @param <T> The BlockEntity type
     * @param registry The DeferredRegister to register with
     * @param name The name of the BlockEntity type
     * @param factory A function that creates a BlockEntity instance
     * @param blockHolder A supplier that provides the block (typically a DeferredHolder)
     * @return A DeferredHolder for the registered BlockEntity type
     */
    public static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> registerBlockEntityType(
            DeferredRegister<BlockEntityType<?>> registry,
            String name,
            BlockEntityFactory<T> factory,
            Supplier<Block> blockHolder) {
        return registerBlockEntityType(registry, name, factory, blockHolder.get());
    }
}

