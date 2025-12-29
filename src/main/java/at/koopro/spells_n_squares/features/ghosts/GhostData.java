package at.koopro.spells_n_squares.features.ghosts;

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
 * Data component for storing ghost information.
 */
public final class GhostData {
    private GhostData() {
    }
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GhostComponent>> GHOST_DATA =
        DATA_COMPONENTS.register(
            "ghost_data",
            () -> DataComponentType.<GhostComponent>builder()
                .persistent(GhostComponent.CODEC)
                .build()
        );
    
    /**
     * Ghost type.
     */
    public enum GhostType {
        HOUSE_GHOST,
        COMMON_GHOST,
        POLTERGEIST,
        SPIRIT
    }
    
    /**
     * House association for house ghosts.
     */
    public enum HouseAssociation {
        GRYFFINDOR,
        SLYTHERIN,
        HUFFLEPUFF,
        RAVENCLAW,
        NONE
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
     * Component storing ghost information.
     */
    public record GhostComponent(
        UUID ghostId,
        GhostType ghostType,
        HouseAssociation houseAssociation,
        String name,
        String history,
        List<DialogueEntry> dialogueHistory,
        Map<UUID, Integer> playerInteractions,
        boolean canProvideQuests,
        List<String> availableQuests
    ) {
        public static final Codec<GhostComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                UUIDUtil.CODEC.fieldOf("ghostId").forGetter(GhostComponent::ghostId),
                Codec.STRING.xmap(
                    s -> GhostType.valueOf(s),
                    GhostType::name
                ).fieldOf("ghostType").forGetter(GhostComponent::ghostType),
                Codec.STRING.xmap(
                    s -> HouseAssociation.valueOf(s),
                    HouseAssociation::name
                ).optionalFieldOf("houseAssociation", HouseAssociation.NONE).forGetter(GhostComponent::houseAssociation),
                Codec.STRING.fieldOf("name").forGetter(GhostComponent::name),
                Codec.STRING.fieldOf("history").forGetter(GhostComponent::history),
                Codec.list(DialogueEntry.CODEC).optionalFieldOf("dialogueHistory", List.of()).forGetter(GhostComponent::dialogueHistory),
                Codec.unboundedMap(UUIDUtil.CODEC, Codec.INT).optionalFieldOf("playerInteractions", Map.of()).forGetter(GhostComponent::playerInteractions),
                Codec.BOOL.optionalFieldOf("canProvideQuests", false).forGetter(GhostComponent::canProvideQuests),
                Codec.list(Codec.STRING).optionalFieldOf("availableQuests", List.of()).forGetter(GhostComponent::availableQuests)
            ).apply(instance, GhostComponent::new)
        );
        
        public GhostComponent addDialogue(String text, UUID speakerId, long timestamp) {
            List<DialogueEntry> newHistory = new ArrayList<>(dialogueHistory);
            newHistory.add(new DialogueEntry(text, timestamp, speakerId));
            // Keep only last 100 entries
            if (newHistory.size() > 100) {
                newHistory.remove(0);
            }
            return new GhostComponent(ghostId, ghostType, houseAssociation, name, history, newHistory, playerInteractions, canProvideQuests, availableQuests);
        }
        
        public GhostComponent recordInteraction(UUID playerId) {
            Map<UUID, Integer> newInteractions = new HashMap<>(playerInteractions);
            newInteractions.put(playerId, newInteractions.getOrDefault(playerId, 0) + 1);
            return new GhostComponent(ghostId, ghostType, houseAssociation, name, history, dialogueHistory, newInteractions, canProvideQuests, availableQuests);
        }
        
        public int getInteractionCount(UUID playerId) {
            return playerInteractions.getOrDefault(playerId, 0);
        }
    }
}















