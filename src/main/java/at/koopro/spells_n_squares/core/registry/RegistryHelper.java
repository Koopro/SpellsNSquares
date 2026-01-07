package at.koopro.spells_n_squares.core.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

/**
 * Utility class for common registry operations.
 * Provides centralized methods for creating item/block properties and registering blocks with items.
 */
public final class RegistryHelper {
    private RegistryHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Creates Item.Properties with the appropriate ResourceKey for the given identifier.
     * 
     * @param id The identifier for the item
     * @return Item.Properties configured with the resource key
     */
    public static Item.Properties createItemProperties(Identifier id) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return new Item.Properties().setId(key);
    }
    
    /**
     * Creates BlockBehaviour.Properties with the appropriate ResourceKey for the given identifier.
     * 
     * @param id The identifier for the block
     * @return BlockBehaviour.Properties configured with the resource key
     */
    public static BlockBehaviour.Properties createBlockProperties(Identifier id) {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, id);
        return BlockBehaviour.Properties.of().setId(key);
    }
    
    /**
     * Registers a block and its corresponding BlockItem.
     * 
     * @param blocks The DeferredRegister for blocks
     * @param items The DeferredRegister for items
     * @param name The block name
     * @param blockSupplier The supplier that creates the block
     * @param <T> The type of block
     * @return The DeferredHolder for the registered block
     */
    public static <T extends Block> DeferredHolder<Block, T> registerBlockWithItem(
            DeferredRegister<Block> blocks,
            DeferredRegister.Items items,
            String name,
            Function<Identifier, T> blockSupplier) {
        DeferredHolder<Block, T> block = blocks.register(name, blockSupplier);
        
        // Register corresponding BlockItem
        items.register(name, id -> new BlockItem(
            block.value(),
            createItemProperties(id)
        ));
        
        return block;
    }
}













