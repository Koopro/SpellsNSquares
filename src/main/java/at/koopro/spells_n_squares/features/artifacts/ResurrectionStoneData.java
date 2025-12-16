package at.koopro.spells_n_squares.features.artifacts;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.UUIDUtil;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Data component for storing Resurrection Stone summoned entities information.
 */
public final class ResurrectionStoneData {
    private ResurrectionStoneData() {
    }
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResurrectionStoneComponent>> RESURRECTION_STONE_DATA =
        DATA_COMPONENTS.register(
            "resurrection_stone_data",
            () -> DataComponentType.<ResurrectionStoneComponent>builder()
                .persistent(ResurrectionStoneComponent.CODEC)
                .build()
        );
    
    /**
     * Component storing summoned shade information.
     */
    public record ShadeInfo(
        UUID entityId,
        String entityName,
        long summonTick
    ) {
        public static final Codec<ShadeInfo> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                UUIDUtil.CODEC.fieldOf("entityId").forGetter(ShadeInfo::entityId),
                Codec.STRING.fieldOf("entityName").forGetter(ShadeInfo::entityName),
                Codec.LONG.fieldOf("summonTick").forGetter(ShadeInfo::summonTick)
            ).apply(instance, ShadeInfo::new)
        );
    }
    
    /**
     * Component storing Resurrection Stone state.
     */
    public record ResurrectionStoneComponent(
        List<ShadeInfo> summonedShades,
        long lastUseTick
    ) {
        public static final Codec<ResurrectionStoneComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.list(ShadeInfo.CODEC).fieldOf("shades").forGetter(ResurrectionStoneComponent::summonedShades),
                Codec.LONG.fieldOf("lastUse").forGetter(ResurrectionStoneComponent::lastUseTick)
            ).apply(instance, ResurrectionStoneComponent::new)
        );
        
        public ResurrectionStoneComponent() {
            this(new ArrayList<>(), 0L);
        }
        
        public ResurrectionStoneComponent addShade(UUID entityId, String entityName, long currentTick) {
            List<ShadeInfo> newShades = new ArrayList<>(summonedShades);
            newShades.add(new ShadeInfo(entityId, entityName, currentTick));
            return new ResurrectionStoneComponent(newShades, currentTick);
        }
        
        public ResurrectionStoneComponent removeShade(UUID entityId) {
            List<ShadeInfo> newShades = new ArrayList<>(summonedShades);
            newShades.removeIf(shade -> shade.entityId().equals(entityId));
            return new ResurrectionStoneComponent(newShades, lastUseTick);
        }
        
        public ResurrectionStoneComponent withLastUse(long tick) {
            return new ResurrectionStoneComponent(summonedShades, tick);
        }
    }
}
