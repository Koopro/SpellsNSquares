package at.koopro.spells_n_squares.features.mail;

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
 * Data component for storing mailbox inventory (received mail).
 */
public final class MailboxData {
    private MailboxData() {
    }
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MailboxComponent>> MAILBOX_DATA =
        DATA_COMPONENTS.register(
            "mailbox_data",
            () -> DataComponentType.<MailboxComponent>builder()
                .persistent(MailboxComponent.CODEC)
                .build()
        );
    
    /**
     * Component storing mailbox inventory as a list of mail ItemStacks.
     */
    public record MailboxComponent(List<ItemStack> mailItems, int maxSlots) {
        public static final Codec<MailboxComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.list(ItemStack.OPTIONAL_CODEC).fieldOf("mailItems").forGetter(MailboxComponent::mailItems),
                Codec.INT.fieldOf("maxSlots").forGetter(MailboxComponent::maxSlots)
            ).apply(instance, MailboxComponent::new)
        );
        
        public static MailboxComponent createDefault(int maxSlots) {
            List<ItemStack> items = new ArrayList<>();
            for (int i = 0; i < maxSlots; i++) {
                items.add(ItemStack.EMPTY);
            }
            return new MailboxComponent(items, maxSlots);
        }
        
        public ItemStack getMail(int slot) {
            if (slot < 0 || slot >= mailItems.size()) {
                return ItemStack.EMPTY;
            }
            return mailItems.get(slot);
        }
        
        public MailboxComponent setMail(int slot, ItemStack stack) {
            List<ItemStack> newItems = new ArrayList<>(mailItems);
            if (slot >= 0 && slot < newItems.size()) {
                newItems.set(slot, stack);
            }
            return new MailboxComponent(newItems, maxSlots);
        }
        
        public int getMailCount() {
            int count = 0;
            for (ItemStack stack : mailItems) {
                if (!stack.isEmpty()) {
                    count++;
                }
            }
            return count;
        }
        
        public boolean hasSpace() {
            return getMailCount() < maxSlots;
        }
        
        public int getFirstEmptySlot() {
            for (int i = 0; i < mailItems.size(); i++) {
                if (mailItems.get(i).isEmpty()) {
                    return i;
                }
            }
            return -1;
        }
    }
}











