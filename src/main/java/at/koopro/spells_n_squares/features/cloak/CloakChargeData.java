package at.koopro.spells_n_squares.features.cloak;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Data component for storing cloak charge information.
 */
public final class CloakChargeData {
    private CloakChargeData() {
    }
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CloakChargeComponent>> CLOAK_CHARGES =
        DATA_COMPONENTS.register(
            "cloak_charges",
            () -> DataComponentType.<CloakChargeComponent>builder()
                .persistent(CloakChargeComponent.CODEC)
                .build()
        );
    
    /**
     * Component data for cloak charges.
     */
    public record CloakChargeComponent(
        int currentCharges,
        int maxCharges,
        int lastRechargeTick
    ) {
        public static final Codec<CloakChargeComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.INT.fieldOf("current").forGetter(CloakChargeComponent::currentCharges),
                Codec.INT.fieldOf("max").forGetter(CloakChargeComponent::maxCharges),
                Codec.INT.fieldOf("lastRecharge").forGetter(CloakChargeComponent::lastRechargeTick)
            ).apply(instance, CloakChargeComponent::new)
        );
        
        public static CloakChargeComponent createDefault(int maxCharges) {
            return new CloakChargeComponent(maxCharges, maxCharges, 0);
        }
    }
}

