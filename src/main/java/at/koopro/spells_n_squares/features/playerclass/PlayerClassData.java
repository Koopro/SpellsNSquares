package at.koopro.spells_n_squares.features.playerclass;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.*;
import java.util.UUID;

/**
 * Data component for storing player class data persistently.
 * Supports multiple classes per player with metadata.
 */
public final class PlayerClassData {
    private PlayerClassData() {
    }
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PlayerClassComponent>> PLAYER_CLASS_DATA =
        DATA_COMPONENTS.register(
            "player_class_data",
            () -> DataComponentType.<PlayerClassComponent>builder()
                .persistent(PlayerClassComponent.CODEC)
                .build()
        );
    
    /**
     * Metadata for a specific class instance.
     */
    public record ClassMetadata(
        long acquiredTimestamp,
        String acquiredBy, // Spell, item, command, etc.
        Map<String, Object> customData // Class-specific data
    ) {
        public static final Codec<ClassMetadata> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.LONG.fieldOf("acquiredTimestamp").forGetter(ClassMetadata::acquiredTimestamp),
                Codec.STRING.optionalFieldOf("acquiredBy", "").forGetter(ClassMetadata::acquiredBy),
                Codec.unboundedMap(Codec.STRING, Codec.STRING)
                    .xmap(
                        map -> {
                            Map<String, Object> result = new HashMap<>();
                            for (Map.Entry<String, String> entry : map.entrySet()) {
                                result.put(entry.getKey(), entry.getValue());
                            }
                            return result;
                        },
                        map -> {
                            Map<String, String> result = new HashMap<>();
                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                result.put(entry.getKey(), String.valueOf(entry.getValue()));
                            }
                            return result;
                        }
                    )
                    .optionalFieldOf("customData", Map.of())
                    .forGetter(ClassMetadata::customData)
            ).apply(instance, ClassMetadata::new)
        );
        
        public static ClassMetadata createDefault(String acquiredBy) {
            return new ClassMetadata(System.currentTimeMillis(), acquiredBy, new HashMap<>());
        }
    }
    
    /**
     * Component storing player class information.
     */
    public record PlayerClassComponent(
        Set<String> classNames, // Store as strings for serialization
        String primaryClassName,
        Map<String, ClassMetadata> classMetadata
    ) {
        public static final Codec<PlayerClassComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.list(Codec.STRING)
                    .xmap(l -> new HashSet<>(l), s -> new ArrayList<>(s))
                    .fieldOf("classNames")
                    .forGetter(c -> new HashSet<>(c.classNames)),
                Codec.STRING.optionalFieldOf("primaryClassName", "").forGetter(PlayerClassComponent::primaryClassName),
                Codec.unboundedMap(Codec.STRING, ClassMetadata.CODEC)
                    .optionalFieldOf("classMetadata", Map.of())
                    .forGetter(PlayerClassComponent::classMetadata)
            ).apply(instance, PlayerClassComponent::new)
        );
        
        public static PlayerClassComponent createDefault() {
            return new PlayerClassComponent(new HashSet<>(), "", new HashMap<>());
        }
        
        /**
         * Gets the set of PlayerClass enums from class names.
         */
        public Set<PlayerClass> getClasses() {
            Set<PlayerClass> classes = new HashSet<>();
            for (String name : classNames) {
                PlayerClass clazz = PlayerClass.fromName(name);
                if (clazz != PlayerClass.NONE) {
                    classes.add(clazz);
                }
            }
            return classes;
        }
        
        /**
         * Gets the primary class.
         */
        public PlayerClass getPrimaryClass() {
            if (primaryClassName == null || primaryClassName.isEmpty()) {
                Set<PlayerClass> classes = getClasses();
                if (classes.isEmpty()) {
                    return PlayerClass.NONE;
                }
                return getPrimaryClassFromSet(classes);
            }
            return PlayerClass.fromName(primaryClassName);
        }
        
        /**
         * Gets the primary class from a set of classes.
         */
        private static PlayerClass getPrimaryClassFromSet(Set<PlayerClass> classes) {
            if (classes == null || classes.isEmpty()) {
                return PlayerClass.NONE;
            }
            
            // Priority order: BASE > ROLE > TRANSFORMATION > ALIGNMENT > ORGANIZATION > BLOOD_STATUS
            ClassCategory[] priority = {
                ClassCategory.BASE,
                ClassCategory.ROLE,
                ClassCategory.TRANSFORMATION,
                ClassCategory.ALIGNMENT,
                ClassCategory.ORGANIZATION,
                ClassCategory.BLOOD_STATUS
            };
            
            for (ClassCategory category : priority) {
                for (PlayerClass clazz : classes) {
                    if (clazz.getCategory() == category) {
                        return clazz;
                    }
                }
            }
            
            // Fallback: return first class
            return classes.iterator().next();
        }
        
        /**
         * Adds a class to the component.
         */
        public PlayerClassComponent addClass(PlayerClass clazz, ClassMetadata metadata) {
            Set<String> newNames = new HashSet<>(classNames);
            newNames.add(clazz.name());
            
            Map<String, ClassMetadata> newMetadata = new HashMap<>(classMetadata);
            newMetadata.put(clazz.name(), metadata);
            
            Set<PlayerClass> updatedClasses = getClasses();
            updatedClasses.add(clazz);
            String newPrimary = getPrimaryClassFromSet(updatedClasses).name();
            
            return new PlayerClassComponent(newNames, newPrimary, newMetadata);
        }
        
        /**
         * Removes a class from the component.
         */
        public PlayerClassComponent removeClass(PlayerClass clazz) {
            Set<String> newNames = new HashSet<>(classNames);
            newNames.remove(clazz.name());
            
            Map<String, ClassMetadata> newMetadata = new HashMap<>(classMetadata);
            newMetadata.remove(clazz.name());
            
            Set<PlayerClass> remaining = getClasses();
            remaining.remove(clazz);
            String newPrimary = remaining.isEmpty() ? "" : getPrimaryClassFromSet(remaining).name();
            
            return new PlayerClassComponent(newNames, newPrimary, newMetadata);
        }
    }
    
    // Static storage for player class data (UUID -> PlayerClassComponent)
    private static final Map<UUID, PlayerClassComponent> playerClassData = new HashMap<>();
    
    /**
     * Gets player class data from a player's data component.
     * TODO: Migrate to actual data component when player data components are fully implemented
     */
    public static PlayerClassComponent getPlayerClassData(Player player) {
        return playerClassData.computeIfAbsent(player.getUUID(), uuid -> PlayerClassComponent.createDefault());
    }
    
    /**
     * Sets player class data in a player's data component.
     * TODO: Migrate to actual data component when player data components are fully implemented
     */
    public static void setPlayerClassData(Player player, PlayerClassComponent data) {
        playerClassData.put(player.getUUID(), data);
    }
    
    /**
     * Gets all classes for a player from data component.
     */
    public static Set<PlayerClass> getClasses(Player player) {
        return getPlayerClassData(player).getClasses();
    }
    
    /**
     * Gets the primary class for a player from data component.
     */
    public static PlayerClass getPrimaryClass(Player player) {
        return getPlayerClassData(player).getPrimaryClass();
    }
    
    /**
     * Adds a class to a player's data component.
     */
    public static void addClass(Player player, PlayerClass clazz, String acquiredBy) {
        PlayerClassComponent current = getPlayerClassData(player);
        ClassMetadata metadata = ClassMetadata.createDefault(acquiredBy);
        PlayerClassComponent updated = current.addClass(clazz, metadata);
        setPlayerClassData(player, updated);
    }
    
    /**
     * Removes a class from a player's data component.
     */
    public static void removeClass(Player player, PlayerClass clazz) {
        PlayerClassComponent current = getPlayerClassData(player);
        PlayerClassComponent updated = current.removeClass(clazz);
        setPlayerClassData(player, updated);
    }
}



