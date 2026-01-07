package at.koopro.spells_n_squares.features.wand.core;

import at.koopro.spells_n_squares.features.wand.registry.WandCore;
import at.koopro.spells_n_squares.features.wand.registry.WandWood;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.UUID;

/**
 * Data component for storing wand core, wood, attunement, and owner information.
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
        boolean attuned,
        String ownerId  // UUID as string, empty string if no owner
    ) {
        public static final Codec<WandDataComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.STRING.fieldOf("core").forGetter(WandDataComponent::coreId),
                Codec.STRING.fieldOf("wood").forGetter(WandDataComponent::woodId),
                Codec.BOOL.fieldOf("attuned").forGetter(WandDataComponent::attuned),
                Codec.STRING.optionalFieldOf("ownerId", "").forGetter(WandDataComponent::ownerId)
            ).apply(instance, WandDataComponent::new)
        );
        
        /**
         * Creates a new component with no owner.
         */
        public WandDataComponent(String coreId, String woodId, boolean attuned) {
            this(coreId, woodId, attuned, "");
        }
        
        public WandCore getCore() {
            return WandCore.fromId(coreId);
        }
        
        public WandWood getWood() {
            return WandWood.fromId(woodId);
        }
        
        /**
         * Checks if the wand has an owner.
         */
        public boolean hasOwner() {
            return ownerId != null && !ownerId.isEmpty();
        }
        
        /**
         * Checks if the given UUID is the owner of this wand.
         */
        public boolean isOwner(UUID uuid) {
            if (!hasOwner()) {
                return false;
            }
            try {
                UUID ownerUuid = UUID.fromString(ownerId);
                return ownerUuid.equals(uuid);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        
        /**
         * Creates a new component with the given owner.
         */
        public WandDataComponent withOwner(UUID uuid) {
            return new WandDataComponent(coreId, woodId, attuned, uuid.toString());
        }
    }
}

