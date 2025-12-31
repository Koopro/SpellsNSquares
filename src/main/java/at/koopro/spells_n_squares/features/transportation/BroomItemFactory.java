package at.koopro.spells_n_squares.features.transportation;

import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Factory for creating ItemStacks from BroomstickTier.
 * Replaces switch statement with Map-based dispatch for better maintainability.
 */
public final class BroomItemFactory {
    private static final Map<BroomstickItem.BroomstickTier, Supplier<ItemStack>> tierToItemMap = new HashMap<>();
    private static boolean initialized = false;
    
    private BroomItemFactory() {
    }
    
    /**
     * Initializes the factory with tier-to-item mappings.
     */
    private static void initialize() {
        if (initialized) {
            return;
        }
        
        tierToItemMap.put(BroomstickItem.BroomstickTier.DEMO, () -> new ItemStack(TransportationRegistry.DEMO_BROOM.get()));
        tierToItemMap.put(BroomstickItem.BroomstickTier.BASIC, () -> new ItemStack(TransportationRegistry.BROOMSTICK_BASIC.get()));
        tierToItemMap.put(BroomstickItem.BroomstickTier.BLUEBOTTLE, () -> new ItemStack(TransportationRegistry.BROOMSTICK_BLUEBOTTLE.get()));
        tierToItemMap.put(BroomstickItem.BroomstickTier.SHOOTING_STAR, () -> new ItemStack(TransportationRegistry.BROOMSTICK_SHOOTING_STAR.get()));
        tierToItemMap.put(BroomstickItem.BroomstickTier.CLEANSWEEP_5, () -> new ItemStack(TransportationRegistry.BROOMSTICK_CLEANSWEEP_5.get()));
        tierToItemMap.put(BroomstickItem.BroomstickTier.CLEANSWEEP_7, () -> new ItemStack(TransportationRegistry.BROOMSTICK_CLEANSWEEP_7.get()));
        tierToItemMap.put(BroomstickItem.BroomstickTier.COMET_140, () -> new ItemStack(TransportationRegistry.BROOMSTICK_COMET_140.get()));
        tierToItemMap.put(BroomstickItem.BroomstickTier.COMET_260, () -> new ItemStack(TransportationRegistry.BROOMSTICK_COMET_260.get()));
        tierToItemMap.put(BroomstickItem.BroomstickTier.NIMBUS_2000, () -> new ItemStack(TransportationRegistry.BROOMSTICK_NIMBUS_2000.get()));
        tierToItemMap.put(BroomstickItem.BroomstickTier.NIMBUS_2001, () -> new ItemStack(TransportationRegistry.BROOMSTICK_NIMBUS_2001.get()));
        tierToItemMap.put(BroomstickItem.BroomstickTier.SILVER_ARROW, () -> new ItemStack(TransportationRegistry.BROOMSTICK_SILVER_ARROW.get()));
        tierToItemMap.put(BroomstickItem.BroomstickTier.RACING, () -> new ItemStack(TransportationRegistry.BROOMSTICK_RACING.get()));
        tierToItemMap.put(BroomstickItem.BroomstickTier.FIREBOLT, () -> new ItemStack(TransportationRegistry.BROOMSTICK_FIREBOLT.get()));
        tierToItemMap.put(BroomstickItem.BroomstickTier.FIREBOLT_SUPREME, () -> new ItemStack(TransportationRegistry.BROOMSTICK_FIREBOLT_SUPREME.get()));
        
        initialized = true;
    }
    
    /**
     * Creates an ItemStack for the given broom tier.
     * @param tier The broom tier
     * @return The ItemStack, or ItemStack.EMPTY if tier is null or not found
     */
    public static ItemStack createItem(BroomstickItem.BroomstickTier tier) {
        if (tier == null) {
            return ItemStack.EMPTY;
        }
        
        if (!initialized) {
            initialize();
        }
        
        Supplier<ItemStack> supplier = tierToItemMap.get(tier);
        if (supplier == null) {
            return ItemStack.EMPTY;
        }
        
        return supplier.get();
    }
}


