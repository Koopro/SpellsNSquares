package at.koopro.spells_n_squares.features.artifacts.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.UUIDUtil;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;
import java.util.UUID;

/**
 * Data component for storing Elder Wand ownership and mastery information.
 */
public final class ElderWandData {
    private ElderWandData() {
    }
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ElderWandComponent>> ELDER_WAND_DATA =
        DATA_COMPONENTS.register(
            "elder_wand_data",
            () -> DataComponentType.<ElderWandComponent>builder()
                .persistent(ElderWandComponent.CODEC)
                .build()
        );
    
    /**
     * Component storing Elder Wand ownership and mastery.
     */
    public record ElderWandComponent(
        Optional<UUID> ownerId,
        String ownerName,
        int masteryLevel,
        long lastTransferTick
    ) {
        public static final Codec<ElderWandComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                UUIDUtil.CODEC.optionalFieldOf("ownerId").forGetter(ElderWandComponent::ownerId),
                Codec.STRING.optionalFieldOf("ownerName", "").forGetter(ElderWandComponent::ownerName),
                Codec.INT.optionalFieldOf("masteryLevel", 0).forGetter(ElderWandComponent::masteryLevel),
                Codec.LONG.optionalFieldOf("lastTransferTick", 0L).forGetter(ElderWandComponent::lastTransferTick)
            ).apply(instance, ElderWandComponent::new)
        );
        
        public ElderWandComponent() {
            this(Optional.empty(), "", 0, 0L);
        }
        
        public ElderWandComponent withOwner(UUID ownerId, String ownerName) {
            return new ElderWandComponent(Optional.of(ownerId), ownerName, 0, System.currentTimeMillis());
        }
        
        public ElderWandComponent increaseMastery() {
            return new ElderWandComponent(ownerId, ownerName, Math.min(100, masteryLevel + 1), lastTransferTick);
        }
        
        /**
         * Gets the cooldown reduction multiplier (50% reduction = 0.5x cooldown).
         */
        public float getCooldownReduction() {
            return 0.5f; // 50% cooldown reduction
        }
        
        /**
         * Gets the spell power multiplier.
         */
        public float getPowerMultiplier() {
            return 1.5f + (masteryLevel * 0.01f); // Base 1.5x, increases with mastery
        }
    }
}
