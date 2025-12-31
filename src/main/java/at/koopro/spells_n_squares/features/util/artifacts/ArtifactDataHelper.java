package at.koopro.spells_n_squares.features.util.artifacts;

import at.koopro.spells_n_squares.core.data.ItemDataHelper;
import at.koopro.spells_n_squares.features.artifacts.data.*;
import at.koopro.spells_n_squares.features.artifacts.item.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Type-safe helper for artifact data component access.
 * Provides convenient methods for getting and setting artifact-specific data.
 */
public final class ArtifactDataHelper {
    private ArtifactDataHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Gets Elder Wand data from an item stack.
     * 
     * @param stack The item stack
     * @return The Elder Wand component, or a new default component if not present
     */
    public static ElderWandData.ElderWandComponent getElderWandData(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return new ElderWandData.ElderWandComponent();
        }
        // Check if item is an ElderWandItem using class comparison to avoid instanceof issues
        Item item = stack.getItem();
        if (!ElderWandItem.class.isAssignableFrom(item.getClass())) {
            return new ElderWandData.ElderWandComponent();
        }
        
        return ItemDataHelper.getData(stack, ElderWandData.ELDER_WAND_DATA.get())
            .orElseGet(() -> {
                ElderWandData.ElderWandComponent defaultData = new ElderWandData.ElderWandComponent();
                ItemDataHelper.setData(stack, ElderWandData.ELDER_WAND_DATA.get(), defaultData);
                return defaultData;
            });
    }
    
    /**
     * Gets Sorting Hat data from an item stack.
     * 
     * @param stack The item stack
     * @return The Sorting Hat component, or a new default component if not present
     */
    public static SortingHatData.SortingHatComponent getSortingHatData(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof SortingHatItem)) {
            return new SortingHatData.SortingHatComponent();
        }
        
        return ItemDataHelper.getData(stack, SortingHatData.SORTING_HAT_DATA.get())
            .orElseGet(() -> {
                SortingHatData.SortingHatComponent defaultData = new SortingHatData.SortingHatComponent();
                ItemDataHelper.setData(stack, SortingHatData.SORTING_HAT_DATA.get(), defaultData);
                return defaultData;
            });
    }
    
    /**
     * Gets Pensieve data from an item stack.
     * 
     * @param stack The item stack
     * @return The Pensieve component, or a new default component if not present
     */
    public static PensieveData.PensieveComponent getPensieveData(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof PensieveItem)) {
            return new PensieveData.PensieveComponent();
        }
        
        return ItemDataHelper.getData(stack, PensieveData.PENSIEVE_DATA.get())
            .orElseGet(() -> {
                PensieveData.PensieveComponent defaultData = new PensieveData.PensieveComponent();
                ItemDataHelper.setData(stack, PensieveData.PENSIEVE_DATA.get(), defaultData);
                return defaultData;
            });
    }
    
    /**
     * Gets Deluminator data from an item stack.
     * 
     * @param stack The item stack
     * @return The Deluminator component, or a new default component if not present
     */
    public static DeluminatorData.DeluminatorComponent getDeluminatorData(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof DeluminatorItem)) {
            return new DeluminatorData.DeluminatorComponent();
        }
        
        return ItemDataHelper.getData(stack, DeluminatorData.DELUMINATOR_DATA.get())
            .orElseGet(() -> {
                DeluminatorData.DeluminatorComponent defaultData = new DeluminatorData.DeluminatorComponent();
                ItemDataHelper.setData(stack, DeluminatorData.DELUMINATOR_DATA.get(), defaultData);
                return defaultData;
            });
    }
    
    /**
     * Gets Goblet of Fire data from an item stack.
     * 
     * @param stack The item stack
     * @return The Goblet of Fire component, or a new default component if not present
     */
    public static GobletOfFireData.GobletOfFireComponent getGobletOfFireData(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof GobletOfFireItem)) {
            return new GobletOfFireData.GobletOfFireComponent();
        }
        
        return ItemDataHelper.getData(stack, GobletOfFireData.GOBLET_OF_FIRE_DATA.get())
            .orElseGet(() -> {
                GobletOfFireData.GobletOfFireComponent defaultData = new GobletOfFireData.GobletOfFireComponent();
                ItemDataHelper.setData(stack, GobletOfFireData.GOBLET_OF_FIRE_DATA.get(), defaultData);
                return defaultData;
            });
    }
    
    /**
     * Gets Mirror of Erised data from an item stack.
     * 
     * @param stack The item stack
     * @return The Mirror of Erised component, or a new default component if not present
     */
    public static MirrorOfErisedData.MirrorOfErisedComponent getMirrorData(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof MirrorOfErisedItem)) {
            return new MirrorOfErisedData.MirrorOfErisedComponent();
        }
        
        return ItemDataHelper.getData(stack, MirrorOfErisedData.MIRROR_OF_ERISED_DATA.get())
            .orElseGet(() -> {
                MirrorOfErisedData.MirrorOfErisedComponent defaultData = new MirrorOfErisedData.MirrorOfErisedComponent();
                ItemDataHelper.setData(stack, MirrorOfErisedData.MIRROR_OF_ERISED_DATA.get(), defaultData);
                return defaultData;
            });
    }
    
    /**
     * Gets Marauder's Map data from an item stack.
     * 
     * @param stack The item stack
     * @return The Marauder's Map component, or a new default component if not present
     */
    public static MaraudersMapData.MaraudersMapComponent getMaraudersMapData(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof MaraudersMapItem)) {
            return new MaraudersMapData.MaraudersMapComponent();
        }
        
        return ItemDataHelper.getData(stack, MaraudersMapData.MARAUDERS_MAP_DATA.get())
            .orElseGet(() -> {
                MaraudersMapData.MaraudersMapComponent defaultData = new MaraudersMapData.MaraudersMapComponent();
                ItemDataHelper.setData(stack, MaraudersMapData.MARAUDERS_MAP_DATA.get(), defaultData);
                return defaultData;
            });
    }
    
    /**
     * Gets Resurrection Stone data from an item stack.
     * 
     * @param stack The item stack
     * @return The Resurrection Stone component, or a new default component if not present
     */
    public static ResurrectionStoneData.ResurrectionStoneComponent getResurrectionStoneData(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof ResurrectionStoneItem)) {
            return new ResurrectionStoneData.ResurrectionStoneComponent();
        }
        
        return ItemDataHelper.getData(stack, ResurrectionStoneData.RESURRECTION_STONE_DATA.get())
            .orElseGet(() -> {
                ResurrectionStoneData.ResurrectionStoneComponent defaultData = new ResurrectionStoneData.ResurrectionStoneComponent();
                ItemDataHelper.setData(stack, ResurrectionStoneData.RESURRECTION_STONE_DATA.get(), defaultData);
                return defaultData;
            });
    }
}

