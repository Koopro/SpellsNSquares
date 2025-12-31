package at.koopro.spells_n_squares.features.artifacts.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

/**
 * Data component for storing Remembrall forgotten items information.
 */
public final class RemembrallData {
    private RemembrallData() {
    }
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RemembrallComponent>> REMEMBRALL_DATA =
        DATA_COMPONENTS.register(
            "remembrall_data",
            () -> DataComponentType.<RemembrallComponent>builder()
                .persistent(RemembrallComponent.CODEC)
                .build()
        );
    
    /**
     * Component storing forgotten item information.
     */
    public record ForgottenItem(
        Identifier itemId,
        String itemName,
        long forgottenTick
    ) {
        public static final Codec<ForgottenItem> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Identifier.CODEC.fieldOf("itemId").forGetter(ForgottenItem::itemId),
                Codec.STRING.fieldOf("itemName").forGetter(ForgottenItem::itemName),
                Codec.LONG.fieldOf("forgottenTick").forGetter(ForgottenItem::forgottenTick)
            ).apply(instance, ForgottenItem::new)
        );
    }
    
    /**
     * Component storing Remembrall state.
     */
    public record RemembrallComponent(
        List<ForgottenItem> forgottenItems,
        long lastCheckTick
    ) {
        public static final Codec<RemembrallComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.list(ForgottenItem.CODEC).optionalFieldOf("forgottenItems", new ArrayList<>()).forGetter(RemembrallComponent::forgottenItems),
                Codec.LONG.optionalFieldOf("lastCheckTick", 0L).forGetter(RemembrallComponent::lastCheckTick)
            ).apply(instance, RemembrallComponent::new)
        );
        
        public RemembrallComponent() {
            this(new ArrayList<>(), 0L);
        }
        
        public RemembrallComponent withForgottenItem(ForgottenItem item) {
            List<ForgottenItem> newList = new ArrayList<>(forgottenItems);
            newList.add(item);
            return new RemembrallComponent(newList, lastCheckTick);
        }
        
        public RemembrallComponent clearForgottenItems() {
            return new RemembrallComponent(new ArrayList<>(), lastCheckTick);
        }
        
        public boolean hasForgottenItems() {
            return !forgottenItems.isEmpty();
        }
    }
}
