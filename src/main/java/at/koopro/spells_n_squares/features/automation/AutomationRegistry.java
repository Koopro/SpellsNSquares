package at.koopro.spells_n_squares.features.automation;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.features.automation.block.ItemCollectorBlock;
import at.koopro.spells_n_squares.features.automation.block.MagicalComposterBlock;
import at.koopro.spells_n_squares.features.automation.block.MagicalFarmBlock;
import at.koopro.spells_n_squares.features.automation.block.MagicalFurnaceBlock;
import at.koopro.spells_n_squares.features.automation.block.ResourceGeneratorBlock;
import at.koopro.spells_n_squares.features.automation.block.SelfStirringCauldronBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for automation feature items and blocks.
 */
public class AutomationRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpellsNSquares.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, SpellsNSquares.MODID);
    
    // Automation items
    public static final DeferredItem<EnchantedWorkbenchItem> ENCHANTED_WORKBENCH = ITEMS.register(
            "enchanted_workbench", id -> new EnchantedWorkbenchItem(createItemProperties(id)));
    public static final DeferredItem<AutoHarvestTool> AUTO_HARVEST_HOE = ITEMS.register(
            "auto_harvest_hoe", id -> new AutoHarvestTool(createItemProperties(id)));
    
    // Automation blocks
    public static final DeferredHolder<Block, SelfStirringCauldronBlock> SELF_STIRRING_CAULDRON = registerBlockWithItem(
            "self_stirring_cauldron",
            id -> new SelfStirringCauldronBlock(createBlockProperties(id).strength(2.0f)));
    public static final DeferredHolder<Block, MagicalFurnaceBlock> MAGICAL_FURNACE = registerBlockWithItem(
            "magical_furnace",
            id -> new MagicalFurnaceBlock(createBlockProperties(id).strength(3.5f)));
    public static final DeferredHolder<Block, MagicalFarmBlock> MAGICAL_FARM = registerBlockWithItem(
            "magical_farm",
            id -> new MagicalFarmBlock(createBlockProperties(id).strength(2.0f)));
    public static final DeferredHolder<Block, ItemCollectorBlock> ITEM_COLLECTOR = registerBlockWithItem(
            "item_collector",
            id -> new ItemCollectorBlock(createBlockProperties(id).strength(2.5f)));
    public static final DeferredHolder<Block, MagicalComposterBlock> MAGICAL_COMPOSTER = registerBlockWithItem(
            "magical_composter",
            id -> new MagicalComposterBlock(createBlockProperties(id).strength(2.0f)));
    public static final DeferredHolder<Block, ResourceGeneratorBlock> RESOURCE_GENERATOR = registerBlockWithItem(
            "resource_generator",
            id -> new ResourceGeneratorBlock(createBlockProperties(id).strength(3.0f)));
    
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
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



