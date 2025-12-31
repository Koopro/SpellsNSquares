package at.koopro.spells_n_squares.features.artifacts.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Data component for storing Goblet of Fire tournament information.
 */
public final class GobletOfFireData {
    private GobletOfFireData() {
    }
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GobletOfFireComponent>> GOBLET_OF_FIRE_DATA =
        DATA_COMPONENTS.register(
            "goblet_of_fire_data",
            () -> DataComponentType.<GobletOfFireComponent>builder()
                .persistent(GobletOfFireComponent.CODEC)
                .build()
        );
    
    /**
     * Component storing tournament participant information.
     */
    public record Participant(
        UUID playerId,
        String playerName,
        long entryTick
    ) {
        public static final Codec<Participant> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                UUIDUtil.CODEC.fieldOf("playerId").forGetter(Participant::playerId),
                Codec.STRING.fieldOf("playerName").forGetter(Participant::playerName),
                Codec.LONG.fieldOf("entryTick").forGetter(Participant::entryTick)
            ).apply(instance, Participant::new)
        );
    }
    
    /**
     * Component storing Goblet of Fire state.
     */
    public record GobletOfFireComponent(
        List<Participant> participants,
        List<UUID> selectedChampions,
        boolean tournamentActive,
        long lastSelectionTick
    ) {
        public static final Codec<GobletOfFireComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.list(Participant.CODEC).optionalFieldOf("participants", new ArrayList<>()).forGetter(GobletOfFireComponent::participants),
                Codec.list(UUIDUtil.CODEC).optionalFieldOf("champions", new ArrayList<>()).forGetter(GobletOfFireComponent::selectedChampions),
                Codec.BOOL.optionalFieldOf("active", false).forGetter(GobletOfFireComponent::tournamentActive),
                Codec.LONG.optionalFieldOf("lastSelection", 0L).forGetter(GobletOfFireComponent::lastSelectionTick)
            ).apply(instance, GobletOfFireComponent::new)
        );
        
        public GobletOfFireComponent() {
            this(new ArrayList<>(), new ArrayList<>(), false, 0L);
        }
        
        public GobletOfFireComponent withParticipant(Participant participant) {
            List<Participant> newList = new ArrayList<>(participants);
            newList.add(participant);
            return new GobletOfFireComponent(newList, selectedChampions, tournamentActive, lastSelectionTick);
        }
        
        public GobletOfFireComponent selectChampions(List<UUID> champions) {
            return new GobletOfFireComponent(participants, new ArrayList<>(champions), true, lastSelectionTick);
        }
        
        public boolean hasParticipant(UUID playerId) {
            return participants.stream().anyMatch(p -> p.playerId().equals(playerId));
        }
    }
}
