package at.koopro.spells_n_squares.features.potions.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Optional;

/**
 * Data component for storing potion properties.
 */
public final class PotionData {
    private PotionData() {
    }
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PotionComponent>> POTION_DATA =
        DATA_COMPONENTS.register(
            "potion_data",
            () -> DataComponentType.<PotionComponent>builder()
                .persistent(PotionComponent.CODEC)
                .build()
        );
    
    /**
     * Component storing potion effect information.
     */
    public record PotionEffect(
        ResourceKey<MobEffect> effectKey,
        int duration,
        int amplifier
    ) {
        public static final Codec<PotionEffect> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                ResourceKey.codec(Registries.MOB_EFFECT).fieldOf("effect").forGetter(PotionEffect::effectKey),
                Codec.INT.fieldOf("duration").forGetter(PotionEffect::duration),
                Codec.INT.fieldOf("amplifier").forGetter(PotionEffect::amplifier)
            ).apply(instance, PotionEffect::new)
        );
        
        public Holder<MobEffect> getEffect() {
            Optional<Holder.Reference<MobEffect>> holderOpt = BuiltInRegistries.MOB_EFFECT.get(effectKey);
            if (holderOpt.isPresent()) {
                return holderOpt.get();
            }
            // Fallback to regeneration - MobEffects.REGENERATION is already a Holder<MobEffect>
            return MobEffects.REGENERATION;
        }
    }
    
    /**
     * Component storing potion properties.
     */
    public record PotionComponent(
        List<PotionEffect> effects,
        int brewingQuality,
        String potionType
    ) {
        public static final Codec<PotionComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.list(PotionEffect.CODEC).fieldOf("effects").forGetter(PotionComponent::effects),
                Codec.INT.optionalFieldOf("quality", 100).forGetter(PotionComponent::brewingQuality),
                Codec.STRING.fieldOf("type").forGetter(PotionComponent::potionType)
            ).apply(instance, PotionComponent::new)
        );
        
        public PotionComponent(String potionType, List<PotionEffect> effects) {
            this(effects, 100, potionType);
        }
        
        public PotionComponent(String potionType, List<PotionEffect> effects, int quality) {
            this(effects, quality, potionType);
        }
    }
}
