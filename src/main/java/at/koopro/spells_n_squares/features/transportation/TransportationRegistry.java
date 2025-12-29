package at.koopro.spells_n_squares.features.transportation;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for transportation feature items and entities.
 */
public class TransportationRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpellsNSquares.MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, SpellsNSquares.MODID);
    
    public static final DeferredItem<PortkeyItem> PORTKEY = ITEMS.register(
            "portkey", id -> new PortkeyItem(createProperties(id)));
    public static final DeferredItem<FlooPowderItem> FLOO_POWDER = ITEMS.register(
            "floo_powder", id -> new FlooPowderItem(createProperties(id)));
    // Demo broom
    public static final DeferredItem<BroomstickItem> DEMO_BROOM = ITEMS.register(
            "demo_broom", id -> new BroomstickItem(createProperties(id), BroomstickItem.BroomstickTier.DEMO));
    // Basic brooms
    public static final DeferredItem<BroomstickItem> BROOMSTICK_BLUEBOTTLE = ITEMS.register(
            "broomstick_bluebottle", id -> new BroomstickItem(createProperties(id), BroomstickItem.BroomstickTier.BLUEBOTTLE));
    public static final DeferredItem<BroomstickItem> BROOMSTICK_SHOOTING_STAR = ITEMS.register(
            "broomstick_shooting_star", id -> new BroomstickItem(createProperties(id), BroomstickItem.BroomstickTier.SHOOTING_STAR));
    public static final DeferredItem<BroomstickItem> BROOMSTICK_BASIC = ITEMS.register(
            "broomstick_basic", id -> new BroomstickItem(createProperties(id), BroomstickItem.BroomstickTier.BASIC));
    
    // Cleansweep series
    public static final DeferredItem<BroomstickItem> BROOMSTICK_CLEANSWEEP_5 = ITEMS.register(
            "broomstick_cleansweep_5", id -> new BroomstickItem(createProperties(id), BroomstickItem.BroomstickTier.CLEANSWEEP_5));
    public static final DeferredItem<BroomstickItem> BROOMSTICK_CLEANSWEEP_7 = ITEMS.register(
            "broomstick_cleansweep_7", id -> new BroomstickItem(createProperties(id), BroomstickItem.BroomstickTier.CLEANSWEEP_7));
    
    // Comet series
    public static final DeferredItem<BroomstickItem> BROOMSTICK_COMET_140 = ITEMS.register(
            "broomstick_comet_140", id -> new BroomstickItem(createProperties(id), BroomstickItem.BroomstickTier.COMET_140));
    public static final DeferredItem<BroomstickItem> BROOMSTICK_COMET_260 = ITEMS.register(
            "broomstick_comet_260", id -> new BroomstickItem(createProperties(id), BroomstickItem.BroomstickTier.COMET_260));
    
    // Nimbus series
    public static final DeferredItem<BroomstickItem> BROOMSTICK_NIMBUS_2000 = ITEMS.register(
            "broomstick_nimbus_2000", id -> new BroomstickItem(createProperties(id), BroomstickItem.BroomstickTier.NIMBUS_2000));
    public static final DeferredItem<BroomstickItem> BROOMSTICK_NIMBUS_2001 = ITEMS.register(
            "broomstick_nimbus_2001", id -> new BroomstickItem(createProperties(id), BroomstickItem.BroomstickTier.NIMBUS_2001));
    
    // Premium brooms
    public static final DeferredItem<BroomstickItem> BROOMSTICK_SILVER_ARROW = ITEMS.register(
            "broomstick_silver_arrow", id -> new BroomstickItem(createProperties(id), BroomstickItem.BroomstickTier.SILVER_ARROW));
    public static final DeferredItem<BroomstickItem> BROOMSTICK_RACING = ITEMS.register(
            "broomstick_racing", id -> new BroomstickItem(createProperties(id), BroomstickItem.BroomstickTier.RACING));
    public static final DeferredItem<BroomstickItem> BROOMSTICK_FIREBOLT = ITEMS.register(
            "broomstick_firebolt", id -> new BroomstickItem(createProperties(id), BroomstickItem.BroomstickTier.FIREBOLT));
    public static final DeferredItem<BroomstickItem> BROOMSTICK_FIREBOLT_SUPREME = ITEMS.register(
            "broomstick_firebolt_supreme", id -> new BroomstickItem(createProperties(id), BroomstickItem.BroomstickTier.FIREBOLT_SUPREME));
    
    // Entities
    public static final DeferredHolder<EntityType<?>, EntityType<BroomEntity>> BROOM_ENTITY = ENTITIES.register(
        "broom",
        () -> {
            ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE,
                ModIdentifierHelper.modId("broom"));
            return EntityType.Builder.<BroomEntity>of(BroomEntity::new, MobCategory.MISC)
                .sized(0.5f, 0.2f) // Smaller hitbox: 0.5 blocks wide, 0.2 blocks tall
                .clientTrackingRange(64)
                .updateInterval(1)
                .build(key);
        }
    );
    
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        ENTITIES.register(modEventBus);
        PortkeyData.DATA_COMPONENTS.register(modEventBus);
        BroomstickData.DATA_COMPONENTS.register(modEventBus);
    }
    
    private static Item.Properties createProperties(Identifier id) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return new Item.Properties().setId(key);
    }
}












