package at.koopro.spells_n_squares.features.economy;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.features.economy.block.AutomatedShopBlock;
import at.koopro.spells_n_squares.features.economy.block.TradingPostBlock;
import at.koopro.spells_n_squares.features.economy.block.VaultBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for economy feature blocks.
 */
public class EconomyRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpellsNSquares.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, SpellsNSquares.MODID);
    
    public static final DeferredHolder<Block, TradingPostBlock> TRADING_POST = registerBlockWithItem(
            "trading_post",
            id -> new TradingPostBlock(createProperties(id).strength(2.5f)));
    public static final DeferredHolder<Block, AutomatedShopBlock> AUTOMATED_SHOP = registerBlockWithItem(
            "automated_shop",
            id -> new AutomatedShopBlock(createProperties(id).strength(2.5f)));
    public static final DeferredHolder<Block, VaultBlock> VAULT = registerBlockWithItem(
            "vault",
            id -> new VaultBlock(createProperties(id).strength(3.5f)));
    
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
    }
    
    private static BlockBehaviour.Properties createProperties(Identifier id) {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, id);
        return BlockBehaviour.Properties.of().setId(key);
    }
    
    private static Item.Properties createItemProperties(Identifier id) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return new Item.Properties().setId(key);
    }
    
    /**
     * Registers a block and its corresponding BlockItem.
     * @param name The block name
     * @param blockSupplier The supplier that creates the block
     * @return The DeferredHolder for the registered block
     */
    private static <T extends Block> DeferredHolder<Block, T> registerBlockWithItem(
            String name,
            java.util.function.Function<Identifier, T> blockSupplier) {
        DeferredHolder<Block, T> block = BLOCKS.register(name, blockSupplier);
        
        // Register corresponding BlockItem
        ITEMS.register(name, id -> new BlockItem(
            block.value(),
            createItemProperties(id)
        ));
        
        return block;
    }
}



