package at.koopro.spells_n_squares.features.storage;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.features.storage.block.AutoSortChestBlock;
import at.koopro.spells_n_squares.features.storage.block.MagicalTrunkBlock;
import at.koopro.spells_n_squares.features.storage.block.NewtsCaseBlock;
import at.koopro.spells_n_squares.features.storage.block.NewtsCaseBlockItem;
import at.koopro.spells_n_squares.features.storage.block.StorageBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for storage feature items and blocks.
 */
public class StorageRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpellsNSquares.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, SpellsNSquares.MODID);
    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATORS = 
        DeferredRegister.create(Registries.CHUNK_GENERATOR, SpellsNSquares.MODID);
    
    // Enchanted bags
    public static final DeferredItem<EnchantedBagItem> ENCHANTED_BAG_SMALL = ITEMS.register(
            "enchanted_bag_small", id -> new EnchantedBagItem(createItemProperties(id), EnchantedBagItem.BagTier.SMALL));
    public static final DeferredItem<EnchantedBagItem> ENCHANTED_BAG_MEDIUM = ITEMS.register(
            "enchanted_bag_medium", id -> new EnchantedBagItem(createItemProperties(id), EnchantedBagItem.BagTier.MEDIUM));
    public static final DeferredItem<EnchantedBagItem> ENCHANTED_BAG_LARGE = ITEMS.register(
            "enchanted_bag_large", id -> new EnchantedBagItem(createItemProperties(id), EnchantedBagItem.BagTier.LARGE));
    public static final DeferredItem<EnchantedBagItem> ENCHANTED_BAG_BOTTOMLESS = ITEMS.register(
            "enchanted_bag_bottomless", id -> new EnchantedBagItem(createItemProperties(id), EnchantedBagItem.BagTier.BOTTOMLESS));
    
    // Storage items
    public static final DeferredItem<PocketDimensionItem> POCKET_DIMENSION = ITEMS.register(
            "pocket_dimension", id -> new PocketDimensionItem(createItemProperties(id)));
    
    // Storage blocks
    public static final DeferredHolder<Block, MagicalTrunkBlock> MAGICAL_TRUNK = registerBlockWithItem(
            "magical_trunk", 
            id -> new MagicalTrunkBlock(createBlockProperties(id).strength(2.5f)));
    
    public static final DeferredHolder<Block, AutoSortChestBlock> AUTO_SORT_CHEST = registerBlockWithItem(
            "auto_sort_chest",
            id -> new AutoSortChestBlock(createBlockProperties(id).strength(2.5f)));
    
    public static final DeferredHolder<Block, NewtsCaseBlock> NEWTS_CASE = BLOCKS.register(
            "newts_case",
            id -> new NewtsCaseBlock(createBlockProperties(id).strength(2.5f)));
    
    public static final DeferredItem<NewtsCaseBlockItem> NEWTS_CASE_ITEM = ITEMS.register(
            "newts_case",
            id -> new NewtsCaseBlockItem(NEWTS_CASE.value(), createItemProperties(id)));
    
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        BagInventoryData.DATA_COMPONENTS.register(modEventBus);
        TrunkInventoryData.DATA_COMPONENTS.register(modEventBus);
        PocketDimensionData.DATA_COMPONENTS.register(modEventBus);
        
        // Register pocket dimension chunk generator
        CHUNK_GENERATORS.register("pocket_chunk_generator", () -> PocketChunkGenerator.CODEC);
        CHUNK_GENERATORS.register(modEventBus);
        
        // Register BlockEntity types (must be after blocks are registered)
        StorageBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        StorageBlockEntities.initializeNewtsCaseBlockEntity();
    }
    
    private static Item.Properties createItemProperties(Identifier id) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return new Item.Properties().setId(key);
    }
    
    private static BlockBehaviour.Properties createBlockProperties(Identifier id) {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, id);
        return BlockBehaviour.Properties.of().setId(key);
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



