package at.koopro.spells_n_squares.features.wand;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for wand feature items.
 */
public class WandRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpellsNSquares.MODID);
    
    public static final DeferredItem<WandItem> DEMO_WAND = ITEMS.register(
            "demo_wand", id -> new WandItem(createProperties(id)));
    
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        WandData.DATA_COMPONENTS.register(modEventBus);
    }
    
    private static Item.Properties createProperties(Identifier id) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return new Item.Properties().setId(key);
    }
}








