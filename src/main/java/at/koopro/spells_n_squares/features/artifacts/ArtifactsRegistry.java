package at.koopro.spells_n_squares.features.artifacts;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for artifacts feature items.
 */
public class ArtifactsRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpellsNSquares.MODID);
    
    public static final DeferredItem<TimeTurnerItem> TIME_TURNER = ITEMS.register(
            "time_turner", id -> new TimeTurnerItem(createTimeTurnerProperties(id)));
    
    public static final DeferredItem<SneakoscopeItem> SNEAKOSCOPE = ITEMS.register(
            "sneakoscope", id -> new SneakoscopeItem(createProperties(id)));
    
    public static final DeferredItem<ElderWandItem> ELDER_WAND = ITEMS.register(
            "elder_wand", id -> new ElderWandItem(createProperties(id)));
    
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        TimeTurnerItem.DATA_COMPONENTS.register(modEventBus);
        MirrorOfErisedData.DATA_COMPONENTS.register(modEventBus);
    }
    
    private static Item.Properties createProperties(Identifier id) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return new Item.Properties().setId(key);
    }
    
    private static Item.Properties createTimeTurnerProperties(Identifier id) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return new Item.Properties()
            .setId(key)
            .durability(1000)
            .rarity(Rarity.EPIC);
    }
}











