package at.koopro.spells_n_squares.features.portraits.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Data component for storing portrait information.
 */
public final class PortraitData {
    private PortraitData() {
    }
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PortraitComponent>> PORTRAIT_DATA =
        DATA_COMPONENTS.register(
            "portrait_data",
            () -> DataComponentType.<PortraitComponent>builder()
                .persistent(PortraitComponent.CODEC)
                .build()
        );
    
    /**
     * Portrait personality types.
     */
    public enum PersonalityType {
        FRIENDLY,
        GRUMPY,
        WISE,
        MISCHIEVOUS,
        GUARDIAN,
        NEUTRAL
    }
    
    /**
     * Dialogue entry.
     */
    public record DialogueEntry(
        String text,
        long timestamp,
        UUID speakerId
    ) {
        public static final Codec<DialogueEntry> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.STRING.fieldOf("text").forGetter(DialogueEntry::text),
                Codec.LONG.fieldOf("timestamp").forGetter(DialogueEntry::timestamp),
                UUIDUtil.CODEC.fieldOf("speakerId").forGetter(DialogueEntry::speakerId)
            ).apply(instance, DialogueEntry::new)
        );
    }
    
    /**
     * Component storing portrait information.
     */
    public record PortraitComponent(
        UUID portraitId,
        UUID creatorId,
        String creatorName,
        PersonalityType personality,
        String name,
        boolean isAwakened,
        boolean canGuard,
        List<DialogueEntry> dialogueHistory,
        Map<String, String> rememberedFacts
    ) {
        public static final Codec<PortraitComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                UUIDUtil.CODEC.fieldOf("portraitId").forGetter(PortraitComponent::portraitId),
                UUIDUtil.CODEC.fieldOf("creatorId").forGetter(PortraitComponent::creatorId),
                Codec.STRING.fieldOf("creatorName").forGetter(PortraitComponent::creatorName),
                Codec.STRING.xmap(
                    s -> PersonalityType.valueOf(s),
                    PersonalityType::name
                ).fieldOf("personality").forGetter(PortraitComponent::personality),
                Codec.STRING.fieldOf("name").forGetter(PortraitComponent::name),
                Codec.BOOL.optionalFieldOf("isAwakened", false).forGetter(PortraitComponent::isAwakened),
                Codec.BOOL.optionalFieldOf("canGuard", false).forGetter(PortraitComponent::canGuard),
                Codec.list(DialogueEntry.CODEC).optionalFieldOf("dialogueHistory", List.of()).forGetter(PortraitComponent::dialogueHistory),
                Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("rememberedFacts", Map.of()).forGetter(PortraitComponent::rememberedFacts)
            ).apply(instance, PortraitComponent::new)
        );
        
        public PortraitComponent addDialogue(String text, UUID speakerId, long timestamp) {
            List<DialogueEntry> newHistory = new ArrayList<>(dialogueHistory);
            newHistory.add(new DialogueEntry(text, timestamp, speakerId));
            // Keep only last 100 entries
            if (newHistory.size() > 100) {
                newHistory.remove(0);
            }
            return new PortraitComponent(portraitId, creatorId, creatorName, personality, name, isAwakened, canGuard, newHistory, rememberedFacts);
        }
        
        public PortraitComponent awaken() {
            return new PortraitComponent(portraitId, creatorId, creatorName, personality, name, true, canGuard, dialogueHistory, rememberedFacts);
        }
        
        public PortraitComponent rememberFact(String key, String value) {
            Map<String, String> newFacts = new HashMap<>(rememberedFacts);
            newFacts.put(key, value);
            return new PortraitComponent(portraitId, creatorId, creatorName, personality, name, isAwakened, canGuard, dialogueHistory, newFacts);
        }
    }
}















