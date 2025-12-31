package at.koopro.spells_n_squares.features.creatures.data;

import at.koopro.spells_n_squares.core.registry.CreatureRegistry;
import at.koopro.spells_n_squares.features.creatures.CreatureType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;
import java.util.UUID;

/**
 * Data component for storing creature information (taming, stats, etc.).
 */
public final class CreatureData {
    private CreatureData() {
    }
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CreatureDataComponent>> CREATURE_DATA =
        DATA_COMPONENTS.register(
            "creature_data",
            () -> DataComponentType.<CreatureDataComponent>builder()
                .persistent(CreatureDataComponent.CODEC)
                .build()
        );
    
    /**
     * Data component for creature properties.
     */
    public record CreatureDataComponent(
        Identifier creatureTypeId,
        Optional<UUID> ownerId,
        int loyalty,
        int health,
        int maxHealth,
        boolean isTamed
    ) {
        public static final Codec<CreatureDataComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Identifier.CODEC.fieldOf("creatureType").forGetter(CreatureDataComponent::creatureTypeId),
                UUIDUtil.CODEC.optionalFieldOf("owner").forGetter(CreatureDataComponent::ownerId),
                Codec.INT.fieldOf("loyalty").forGetter(CreatureDataComponent::loyalty),
                Codec.INT.fieldOf("health").forGetter(CreatureDataComponent::health),
                Codec.INT.fieldOf("maxHealth").forGetter(CreatureDataComponent::maxHealth),
                Codec.BOOL.fieldOf("isTamed").forGetter(CreatureDataComponent::isTamed)
            ).apply(instance, CreatureDataComponent::new)
        );
        
        public CreatureDataComponent(Identifier creatureTypeId) {
            this(creatureTypeId, Optional.empty(), 0, 20, 20, false);
        }
        
        public CreatureType getCreatureType() {
            return CreatureRegistry.get(creatureTypeId);
        }
    }
}

