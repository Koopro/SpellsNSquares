package at.koopro.spells_n_squares.features.storage;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Enchanted bag item with expandable inventory.
 */
public class EnchantedBagItem extends Item {
    
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
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        
        if (player instanceof ServerPlayer serverPlayer) {
            openBagInventory(serverPlayer, stack);
        }
        
        return InteractionResult.SUCCESS;
    }
    
    private void openBagInventory(ServerPlayer player, ItemStack bagStack) {
        BagInventoryData.BagInventoryComponent inventory = getBagInventory(bagStack);
        if (inventory != null) {
            int itemCount = inventory.getItemCount();
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                String.format("Bag contains %d/%d items", itemCount, inventory.maxSlots())
            ));
        }
    }
    
    public static BagInventoryData.BagInventoryComponent getBagInventory(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof EnchantedBagItem bagItem)) {
            return null;
        }
        
        BagInventoryData.BagInventoryComponent data = stack.get(BagInventoryData.BAG_INVENTORY.get());
        if (data == null) {
            data = BagInventoryData.BagInventoryComponent.createDefault(bagItem.tier.getSlots());
            stack.set(BagInventoryData.BAG_INVENTORY.get(), data);
        }
        return data;
    }
    
    public static void setBagInventory(ItemStack stack, BagInventoryData.BagInventoryComponent data) {
        if (stack.isEmpty() || !(stack.getItem() instanceof EnchantedBagItem)) {
            return;
        }
        stack.set(BagInventoryData.BAG_INVENTORY.get(), data);
    }
}
