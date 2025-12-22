package at.koopro.spells_n_squares.features.building;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.features.building.block.MagicalLightBlock;
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
 * Registry for building feature items and blocks.
 */
public class BuildingRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpellsNSquares.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, SpellsNSquares.MODID);
    
    // Building items
    public static final DeferredItem<WizardTowerItem> WIZARD_TOWER = ITEMS.register(
            "wizard_tower", id -> new WizardTowerItem(createItemProperties(id)));
    
    // Building blocks
    public static final DeferredHolder<Block, MagicalLightBlock> MAGICAL_LIGHT_WHITE = registerBlockWithItem(
            "magical_light_white",
            id -> new MagicalLightBlock(createBlockProperties(id).strength(0.3f), MagicalLightBlock.LightColor.WHITE));
    public static final DeferredHolder<Block, MagicalLightBlock> MAGICAL_LIGHT_BLUE = registerBlockWithItem(
            "magical_light_blue",
            id -> new MagicalLightBlock(createBlockProperties(id).strength(0.3f), MagicalLightBlock.LightColor.BLUE));
    public static final DeferredHolder<Block, MagicalLightBlock> MAGICAL_LIGHT_GREEN = registerBlockWithItem(
            "magical_light_green",
            id -> new MagicalLightBlock(createBlockProperties(id).strength(0.3f), MagicalLightBlock.LightColor.GREEN));
    public static final DeferredHolder<Block, MagicalLightBlock> MAGICAL_LIGHT_RED = registerBlockWithItem(
            "magical_light_red",
            id -> new MagicalLightBlock(createBlockProperties(id).strength(0.3f), MagicalLightBlock.LightColor.RED));
    public static final DeferredHolder<Block, MagicalLightBlock> MAGICAL_LIGHT_PURPLE = registerBlockWithItem(
            "magical_light_purple",
            id -> new MagicalLightBlock(createBlockProperties(id).strength(0.3f), MagicalLightBlock.LightColor.PURPLE));
    public static final DeferredHolder<Block, MagicalLightBlock> MAGICAL_LIGHT_GOLD = registerBlockWithItem(
            "magical_light_gold",
            id -> new MagicalLightBlock(createBlockProperties(id).strength(0.3f), MagicalLightBlock.LightColor.GOLD));
    
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



