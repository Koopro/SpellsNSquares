package at.koopro.spells_n_squares.features.storage.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

/**
 * Data component for storing bag inventory contents.
 */
public final class BagInventoryData {
    private BagInventoryData() {
    }
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BagInventoryComponent>> BAG_INVENTORY =
        DATA_COMPONENTS.register("bag_inventory", () -> DataComponentType.<BagInventoryComponent>builder()
            .persistent(BagInventoryComponent.CODEC)
            .build());
    
    /**
     * Component storing bag inventory as a list of ItemStacks.
     */
    public record BagInventoryComponent(List<ItemStack> items, int maxSlots) {
        public static final Codec<BagInventoryComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.list(ItemStack.OPTIONAL_CODEC).fieldOf("items").forGetter(BagInventoryComponent::items),
                Codec.INT.fieldOf("maxSlots").forGetter(BagInventoryComponent::maxSlots)
            ).apply(instance, BagInventoryComponent::new)
        );
        
        public static BagInventoryComponent createDefault(int maxSlots) {
            List<ItemStack> items = new ArrayList<>();
            for (int i = 0; i < maxSlots; i++) {
                items.add(ItemStack.EMPTY);
            }
            return new BagInventoryComponent(items, maxSlots);
        }
        
        public ItemStack getItem(int slot) {
            if (slot < 0 || slot >= items.size()) {
                return ItemStack.EMPTY;
            }
            return items.get(slot);
        }
        
        public BagInventoryComponent setItem(int slot, ItemStack stack) {
            List<ItemStack> newItems = new ArrayList<>(items);
            if (slot >= 0 && slot < newItems.size()) {
                newItems.set(slot, stack);
            }
            return new BagInventoryComponent(newItems, maxSlots);
        }
        
        public int getItemCount() {
            int count = 0;
            for (ItemStack stack : items) {
                if (!stack.isEmpty()) {
                    count++;
                }
            }
            return count;
        }
    }
}
