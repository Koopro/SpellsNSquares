package at.koopro.spells_n_squares.features.wand;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Data component for storing wand core, wood, and attunement information.
 */
public final class WandData {
    private WandData() {
    }
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WandDataComponent>> WAND_DATA =
        DATA_COMPONENTS.register(
            "wand_data",
            () -> DataComponentType.<WandDataComponent>builder()
                .persistent(WandDataComponent.CODEC)
                .build()
        );
    
    /**
     * Component data for wand properties.
     */
    public record WandDataComponent(
        String coreId,
        String woodId,
        boolean attuned
    ) {
        public static final Codec<WandDataComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.STRING.fieldOf("core").forGetter(WandDataComponent::coreId),
                Codec.STRING.fieldOf("wood").forGetter(WandDataComponent::woodId),
                Codec.BOOL.fieldOf("attuned").forGetter(WandDataComponent::attuned)
            ).apply(instance, WandDataComponent::new)
        );
        
        public WandCore getCore() {
            return WandCore.fromId(coreId);
        }
        
        public WandWood getWood() {
            return WandWood.fromId(woodId);
        }
    }
}

