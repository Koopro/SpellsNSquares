package at.koopro.spells_n_squares.features.education;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * System for managing homework assignments.
 */
public final class HomeworkSystem {
    private HomeworkSystem() {
    }
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<HomeworkData>> HOMEWORK_DATA =
        DATA_COMPONENTS.register(
            "homework_data",
            () -> DataComponentType.<HomeworkData>builder()
                .persistent(HomeworkData.CODEC)
                .build()
        );
    
    /**
     * Data component for storing homework assignments.
     */
    public record HomeworkData(Map<Identifier, HomeworkAssignment> assignments) {
        public static final Codec<HomeworkData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.unboundedMap(Identifier.CODEC, HomeworkAssignment.CODEC).fieldOf("assignments").forGetter(HomeworkData::assignments)
            ).apply(instance, HomeworkData::new)
        );
        
        public HomeworkData() {
            this(new HashMap<>());
        }
        
        public List<HomeworkAssignment> getActiveAssignments() {
            return new ArrayList<>(assignments.values());
        }
        
        public HomeworkData withAssignment(Identifier classId, HomeworkAssignment assignment) {
            Map<Identifier, HomeworkAssignment> newAssignments = new HashMap<>(assignments);
            newAssignments.put(classId, assignment);
            return new HomeworkData(newAssignments);
        }
        
        public HomeworkData removeAssignment(Identifier classId) {
            Map<Identifier, HomeworkAssignment> newAssignments = new HashMap<>(assignments);
            newAssignments.remove(classId);
            return new HomeworkData(newAssignments);
        }
    }
    
    /**
     * Represents a homework assignment.
     */
    public record HomeworkAssignment(
        Identifier classId,
        String description,
        int progress,
        int requiredProgress,
        boolean completed
    ) {
        public static final Codec<HomeworkAssignment> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Identifier.CODEC.fieldOf("classId").forGetter(HomeworkAssignment::classId),
                Codec.STRING.fieldOf("description").forGetter(HomeworkAssignment::description),
                Codec.INT.fieldOf("progress").forGetter(HomeworkAssignment::progress),
                Codec.INT.fieldOf("requiredProgress").forGetter(HomeworkAssignment::requiredProgress),
                Codec.BOOL.fieldOf("completed").forGetter(HomeworkAssignment::completed)
            ).apply(instance, HomeworkAssignment::new)
        );
        
        public HomeworkAssignment withProgress(int newProgress) {
            boolean newCompleted = newProgress >= requiredProgress;
            return new HomeworkAssignment(classId, description, Math.min(newProgress, requiredProgress), requiredProgress, newCompleted);
        }
    }
    
    /**
     * Gets homework data for a player.
     */
    public static HomeworkData getHomeworkData(Player player) {
        // TODO: Retrieve from player data component
        // - Use player.getData(HOMEWORK_DATA) to get the component
        // - Return default HomeworkData if not present
        return new HomeworkData();
    }
    
    /**
     * Assigns homework to a player.
     */
    public static void assignHomework(Player player, Identifier classId, String description, int requiredProgress) {
        // TODO: Add homework assignment to player data component
        // - Get current data: HomeworkData current = getHomeworkData(player)
        // - Create assignment: HomeworkAssignment assignment = new HomeworkAssignment(classId, description, 0, requiredProgress, false)
        // - Update data: HomeworkData updated = current.withAssignment(classId, assignment)
        // - Save: player.setData(HOMEWORK_DATA, updated)
    }
}

