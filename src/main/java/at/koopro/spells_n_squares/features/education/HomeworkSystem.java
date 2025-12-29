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
    
    private static final String PERSISTENT_DATA_KEY = "spells_n_squares:homework_data";
    
    /**
     * Gets homework data for a player from their persistent data component.
     */
    public static HomeworkData getHomeworkData(Player player) {
        if (player.level().isClientSide()) {
            // On client, return default (data syncs from server)
            return new HomeworkData();
        }
        
        var persistentData = player.getPersistentData();
        var tagOpt = persistentData.getCompound(PERSISTENT_DATA_KEY);
        
        if (tagOpt.isEmpty()) {
            return new HomeworkData();
        }
        
        var tag = tagOpt.get();
        if (tag.isEmpty()) {
            return new HomeworkData();
        }
        
        try {
            return HomeworkData.CODEC.parse(
                net.minecraft.nbt.NbtOps.INSTANCE,
                tag
            ).result().orElse(new HomeworkData());
        } catch (Exception e) {
            com.mojang.logging.LogUtils.getLogger().warn(
                "Failed to load homework data for player {}, using default", player.getName().getString(), e);
            return new HomeworkData();
        }
    }
    
    /**
     * Sets homework data for a player in their persistent data component.
     */
    private static void setHomeworkData(Player player, HomeworkData data) {
        if (player.level().isClientSide()) {
            return; // Only set on server
        }
        
        try {
            var result = HomeworkData.CODEC.encodeStart(
                net.minecraft.nbt.NbtOps.INSTANCE,
                data
            );
            
            result.result().ifPresent(tag -> {
                player.getPersistentData().put(PERSISTENT_DATA_KEY, tag);
            });
        } catch (Exception e) {
            com.mojang.logging.LogUtils.getLogger().warn(
                "Failed to save homework data for player {}", player.getName().getString(), e);
        }
    }
    
    /**
     * Assigns homework to a player.
     */
    public static void assignHomework(Player player, Identifier classId, String description, int requiredProgress) {
        if (!player.level().isClientSide()) {
            HomeworkData current = getHomeworkData(player);
            HomeworkAssignment assignment = new HomeworkAssignment(classId, description, 0, requiredProgress, false);
            HomeworkData updated = current.withAssignment(classId, assignment);
            setHomeworkData(player, updated);
        }
    }
}

