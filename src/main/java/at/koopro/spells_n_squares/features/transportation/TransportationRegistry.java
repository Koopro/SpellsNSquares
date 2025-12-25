package at.koopro.spells_n_squares.features.transportation;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for transportation feature items.
 */
public class TransportationRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpellsNSquares.MODID);
    
    public static final DeferredItem<PortkeyItem> PORTKEY = ITEMS.register(
            "portkey", id -> new PortkeyItem(createProperties(id)));
    public static final DeferredItem<FlooPowderItem> FLOO_POWDER = ITEMS.register(
            "floo_powder", id -> new FlooPowderItem(createProperties(id)));
    public static final DeferredItem<BroomstickItem> BROOMSTICK_BASIC = ITEMS.register(
            "broomstick_basic", id -> new BroomstickItem(createProperties(id), BroomstickItem.BroomstickTier.BASIC));
    public static final DeferredItem<BroomstickItem> BROOMSTICK_RACING = ITEMS.register(
            "broomstick_racing", id -> new BroomstickItem(createProperties(id), BroomstickItem.BroomstickTier.RACING));
    public static final DeferredItem<BroomstickItem> BROOMSTICK_FIREBOLT = ITEMS.register(
            "broomstick_firebolt", id -> new BroomstickItem(createProperties(id), BroomstickItem.BroomstickTier.FIREBOLT));
    
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        PortkeyData.DATA_COMPONENTS.register(modEventBus);
        BroomstickData.DATA_COMPONENTS.register(modEventBus);
    }
    
    private static Item.Properties createProperties(Identifier id) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return new Item.Properties().setId(key);
    }
}








