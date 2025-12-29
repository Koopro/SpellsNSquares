package at.koopro.spells_n_squares.features.building;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.registry.RegistryHelper;
import at.koopro.spells_n_squares.features.building.block.MagicalLightBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for building feature items and blocks.
 */
public class BuildingRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpellsNSquares.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, SpellsNSquares.MODID);
    
    // Building items
    public static final DeferredItem<WizardTowerItem> WIZARD_TOWER = ITEMS.register(
            "wizard_tower", id -> new WizardTowerItem(RegistryHelper.createItemProperties(id)));
    
    // Building blocks
    public static final DeferredHolder<Block, MagicalLightBlock> MAGICAL_LIGHT_WHITE = RegistryHelper.registerBlockWithItem(
            BLOCKS, ITEMS,
            "magical_light_white",
            id -> new MagicalLightBlock(RegistryHelper.createBlockProperties(id).strength(0.3f), MagicalLightBlock.LightColor.WHITE));
    public static final DeferredHolder<Block, MagicalLightBlock> MAGICAL_LIGHT_BLUE = RegistryHelper.registerBlockWithItem(
            BLOCKS, ITEMS,
            "magical_light_blue",
            id -> new MagicalLightBlock(RegistryHelper.createBlockProperties(id).strength(0.3f), MagicalLightBlock.LightColor.BLUE));
    public static final DeferredHolder<Block, MagicalLightBlock> MAGICAL_LIGHT_GREEN = RegistryHelper.registerBlockWithItem(
            BLOCKS, ITEMS,
            "magical_light_green",
            id -> new MagicalLightBlock(RegistryHelper.createBlockProperties(id).strength(0.3f), MagicalLightBlock.LightColor.GREEN));
    public static final DeferredHolder<Block, MagicalLightBlock> MAGICAL_LIGHT_RED = RegistryHelper.registerBlockWithItem(
            BLOCKS, ITEMS,
            "magical_light_red",
            id -> new MagicalLightBlock(RegistryHelper.createBlockProperties(id).strength(0.3f), MagicalLightBlock.LightColor.RED));
    public static final DeferredHolder<Block, MagicalLightBlock> MAGICAL_LIGHT_PURPLE = RegistryHelper.registerBlockWithItem(
            BLOCKS, ITEMS,
            "magical_light_purple",
            id -> new MagicalLightBlock(RegistryHelper.createBlockProperties(id).strength(0.3f), MagicalLightBlock.LightColor.PURPLE));
    public static final DeferredHolder<Block, MagicalLightBlock> MAGICAL_LIGHT_GOLD = RegistryHelper.registerBlockWithItem(
            BLOCKS, ITEMS,
            "magical_light_gold",
            id -> new MagicalLightBlock(RegistryHelper.createBlockProperties(id).strength(0.3f), MagicalLightBlock.LightColor.GOLD));
    
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
    }
}







