package at.koopro.spells_n_squares.features.storage.item;

import at.koopro.spells_n_squares.core.data.DataComponentHelper;
import at.koopro.spells_n_squares.features.storage.BagMenuProvider;
import at.koopro.spells_n_squares.features.storage.data.BagInventoryData;
import at.koopro.spells_n_squares.core.data.ItemDataHelper;
import at.koopro.spells_n_squares.features.storage.base.BaseStorageItem;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;

/**
 * Enchanted bag item with expandable inventory.
 */
public class EnchantedBagItem extends BaseStorageItem {
    
    public enum BagTier {
        SMALL(9, "Small"),
        MEDIUM(18, "Medium"),
        LARGE(27, "Large"),
        BOTTOMLESS(54, "Bottomless");
        
        private final int slots;
        private final String name;
        
        BagTier(int slots, String name) {
            this.slots = slots;
            this.name = name;
        }
        
        public int getSlots() {
            return slots;
        }
        
        public String getName() {
            return name;
        }
    }
    
    private final BagTier tier;
    
    public EnchantedBagItem(Properties properties, BagTier tier) {
        super(properties.stacksTo(1));
        this.tier = tier;
    }
    
    public BagTier getTier() {
        return tier;
    }
    
    @Override
    protected MenuProvider createMenuProvider(ItemStack stack) {
        return new BagMenuProvider(stack);
    }
    
    public static BagInventoryData.BagInventoryComponent getBagInventory(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof EnchantedBagItem bagItem)) {
            return null;
        }
        
        return DataComponentHelper.getOrCreateData(
            stack,
            BagInventoryData.BAG_INVENTORY.get(),
            () -> BagInventoryData.BagInventoryComponent.createDefault(bagItem.tier.getSlots())
        );
    }
    
    public static void setBagInventory(ItemStack stack, BagInventoryData.BagInventoryComponent data) {
        if (stack.isEmpty() || !(stack.getItem() instanceof EnchantedBagItem)) {
            return;
        }
        ItemDataHelper.setData(stack, BagInventoryData.BAG_INVENTORY.get(), data);
    }
}




















