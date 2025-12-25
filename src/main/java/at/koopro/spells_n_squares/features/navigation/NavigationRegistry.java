package at.koopro.spells_n_squares.features.navigation;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for navigation feature items.
 */
public class NavigationRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpellsNSquares.MODID);
    
    public static final DeferredItem<MagicalMapItem> MAGICAL_MAP = ITEMS.register(
            "magical_map", id -> new MagicalMapItem(createProperties(id)));
    public static final DeferredItem<LocationCompassItem> LOCATION_COMPASS = ITEMS.register(
            "location_compass", id -> new LocationCompassItem(createProperties(id)));
    public static final DeferredItem<MagicalJournalItem> MAGICAL_JOURNAL = ITEMS.register(
            "magical_journal", id -> new MagicalJournalItem(createProperties(id)));
    
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
    
    private static Item.Properties createProperties(Identifier id) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return new Item.Properties().setId(key);
    }
}








