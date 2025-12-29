package at.koopro.spells_n_squares.features.automation;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.registry.RegistryHelper;
import at.koopro.spells_n_squares.features.automation.block.ItemCollectorBlock;
import at.koopro.spells_n_squares.features.automation.block.MagicalComposterBlock;
import at.koopro.spells_n_squares.features.automation.block.MagicalFarmBlock;
import at.koopro.spells_n_squares.features.automation.block.MagicalFurnaceBlock;
import at.koopro.spells_n_squares.features.automation.block.ResourceGeneratorBlock;
import at.koopro.spells_n_squares.features.automation.block.SelfStirringCauldronBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
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
            "enchanted_workbench", id -> new EnchantedWorkbenchItem(RegistryHelper.createItemProperties(id)));
    public static final DeferredItem<AutoHarvestTool> AUTO_HARVEST_HOE = ITEMS.register(
            "auto_harvest_hoe", id -> new AutoHarvestTool(RegistryHelper.createItemProperties(id)));
    
    // Automation blocks
    public static final DeferredHolder<Block, SelfStirringCauldronBlock> SELF_STIRRING_CAULDRON = RegistryHelper.registerBlockWithItem(
            BLOCKS, ITEMS,
            "self_stirring_cauldron",
            id -> new SelfStirringCauldronBlock(RegistryHelper.createBlockProperties(id).strength(2.0f)));
    public static final DeferredHolder<Block, MagicalFurnaceBlock> MAGICAL_FURNACE = RegistryHelper.registerBlockWithItem(
            BLOCKS, ITEMS,
            "magical_furnace",
            id -> new MagicalFurnaceBlock(RegistryHelper.createBlockProperties(id).strength(3.5f)));
    public static final DeferredHolder<Block, MagicalFarmBlock> MAGICAL_FARM = RegistryHelper.registerBlockWithItem(
            BLOCKS, ITEMS,
            "magical_farm",
            id -> new MagicalFarmBlock(RegistryHelper.createBlockProperties(id).strength(2.0f)));
    public static final DeferredHolder<Block, ItemCollectorBlock> ITEM_COLLECTOR = RegistryHelper.registerBlockWithItem(
            BLOCKS, ITEMS,
            "item_collector",
            id -> new ItemCollectorBlock(RegistryHelper.createBlockProperties(id).strength(2.5f)));
    public static final DeferredHolder<Block, MagicalComposterBlock> MAGICAL_COMPOSTER = RegistryHelper.registerBlockWithItem(
            BLOCKS, ITEMS,
            "magical_composter",
            id -> new MagicalComposterBlock(RegistryHelper.createBlockProperties(id).strength(2.0f)));
    public static final DeferredHolder<Block, ResourceGeneratorBlock> RESOURCE_GENERATOR = RegistryHelper.registerBlockWithItem(
            BLOCKS, ITEMS,
            "resource_generator",
            id -> new ResourceGeneratorBlock(RegistryHelper.createBlockProperties(id).strength(3.0f)));
    
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
    }
}







