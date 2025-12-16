package at.koopro.spells_n_squares.features.education;

import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * System for managing classes and learning in the wizarding world.
 */
public final class ClassSystem {
    private static final Map<Identifier, ClassType> CLASSES = new HashMap<>();
    
    private ClassSystem() {
    }
    
    /**
     * Registers a class type.
     */
    public static void register(Identifier id, ClassType classType) {
        if (CLASSES.containsKey(id)) {
            throw new IllegalArgumentException("Class already registered: " + id);
        }
        CLASSES.put(id, classType);
    }
    
    /**
     * Gets a class type by ID.
     */
    public static ClassType get(Identifier id) {
        return CLASSES.get(id);
    }
    
    /**
     * Gets all registered class IDs.
     */
    public static Set<Identifier> getAllIds() {
        return CLASSES.keySet();
    }
    
    /**
     * Represents a type of class that can be taken.
     */
    public static class ClassType {
        private final Identifier id;
        private final String name;
        private final String description;
        private final int duration; // Duration in ticks
        
        public ClassType(Identifier id, String name, String description, int duration) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.duration = duration;
        }
        
        public Identifier getId() {
            return id;
        }
        
        public String getName() {
            return name;
        }
        
        public String getDescription() {
            return description;
        }
        
        public int getDuration() {
            return duration;
        }
    }
}

