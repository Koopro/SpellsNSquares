package at.koopro.spells_n_squares.features.wand;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.registry.RegistryHelper;
import at.koopro.spells_n_squares.features.wand.block.WandLatheBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for wand feature items and blocks.
 */
public class WandRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpellsNSquares.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, SpellsNSquares.MODID);
    
    public static final DeferredItem<WandItem> DEMO_WAND = ITEMS.register(
            "demo_wand", id -> new WandItem(createProperties(id)));
    
    public static final DeferredHolder<Block, WandLatheBlock> WAND_LATHE = RegistryHelper.registerBlockWithItem(
            BLOCKS, ITEMS,
            "wand_lathe",
            id -> new WandLatheBlock(RegistryHelper.createBlockProperties(id).strength(2.5f)));
    
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        WandData.DATA_COMPONENTS.register(modEventBus);
    }
    
    private static Item.Properties createProperties(Identifier id) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return new Item.Properties().setId(key);
    }
}













