package at.koopro.spells_n_squares.features.storage;

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
 * Data component for storing trunk inventory contents.
 */
public final class TrunkInventoryData {
    private TrunkInventoryData() {
    }
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TrunkInventoryComponent>> TRUNK_INVENTORY =
        DATA_COMPONENTS.register("trunk_inventory", () -> DataComponentType.<TrunkInventoryComponent>builder()
            .persistent(TrunkInventoryComponent.CODEC)
            .build());
    
    /**
     * Component storing trunk inventory with multiple compartments.
     */
    public record TrunkInventoryComponent(
        List<List<ItemStack>> compartments,
        int maxCompartments,
        int slotsPerCompartment,
        String password
    ) {
        public static final Codec<TrunkInventoryComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.list(Codec.list(ItemStack.OPTIONAL_CODEC)).fieldOf("compartments").forGetter(TrunkInventoryComponent::compartments),
                Codec.INT.fieldOf("maxCompartments").forGetter(TrunkInventoryComponent::maxCompartments),
                Codec.INT.fieldOf("slotsPerCompartment").forGetter(TrunkInventoryComponent::slotsPerCompartment),
                Codec.STRING.optionalFieldOf("password", "").forGetter(TrunkInventoryComponent::password)
            ).apply(instance, TrunkInventoryComponent::new)
        );
        
        public static TrunkInventoryComponent createDefault(int maxCompartments, int slotsPerCompartment) {
            List<List<ItemStack>> compartments = new ArrayList<>();
            for (int i = 0; i < maxCompartments; i++) {
                List<ItemStack> compartment = new ArrayList<>();
                for (int j = 0; j < slotsPerCompartment; j++) {
                    compartment.add(ItemStack.EMPTY);
                }
                compartments.add(compartment);
            }
            return new TrunkInventoryComponent(compartments, maxCompartments, slotsPerCompartment, "");
        }
        
        public boolean isLocked() {
            return password != null && !password.isEmpty();
        }
        
        public boolean checkPassword(String input) {
            return password != null && password.equals(input);
        }
    }
}

