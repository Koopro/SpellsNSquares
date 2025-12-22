package at.koopro.spells_n_squares.core.registry.addon;

import at.koopro.spells_n_squares.features.playerclass.ClassCategory;
import at.koopro.spells_n_squares.features.playerclass.ClassConflictChecker;
import at.koopro.spells_n_squares.features.playerclass.PlayerClass;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for addons to register custom player classes.
 * Note: Since PlayerClass is currently an enum, addons cannot directly add new enum values.
 * This registry provides a way to register class metadata and conflict rules for future use.
 * 
 * For now, addons should use existing PlayerClass enum values and register their
 * custom behavior through conflict rules and ability systems.
 */
public final class AddonPlayerClassRegistry {
    private final String addonId;
    
    // Store class definitions for future use (when PlayerClass becomes registry-based)
    private static final Map<String, ClassDefinition> classDefinitions = new HashMap<>();
    
    // Store conflict rules registered by addons
    private static final Map<String, ConflictRule> conflictRules = new HashMap<>();
    
    public AddonPlayerClassRegistry(String addonId) {
        this.addonId = addonId;
    }
    
    /**
     * Represents a class definition for addon-registered classes.
     * This is prepared for future when PlayerClass becomes registry-based.
     */
    public static class ClassDefinition {
        private final String name;
        private final String displayName;
        private final String description;
        private final ClassCategory category;
        private final Map<String, Object> properties;
        
        public ClassDefinition(String name, String displayName, String description, ClassCategory category, Map<String, Object> properties) {
            this.name = name;
            this.displayName = displayName;
            this.description = description;
            this.category = category;
            this.properties = properties != null ? new HashMap<>(properties) : new HashMap<>();
        }
        
        public String getName() { return name; }
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
        public ClassCategory getCategory() { return category; }
        public Map<String, Object> getProperties() { return new HashMap<>(properties); }
    }
    
    /**
     * Represents a conflict rule between classes.
     */
    public static class ConflictRule {
        private final PlayerClass class1;
        private final PlayerClass class2;
        private final ClassConflictChecker.ConflictType conflictType;
        
        public ConflictRule(PlayerClass class1, PlayerClass class2, ClassConflictChecker.ConflictType conflictType) {
            this.class1 = class1;
            this.class2 = class2;
            this.conflictType = conflictType;
        }
        
        public PlayerClass getClass1() { return class1; }
        public PlayerClass getClass2() { return class2; }
        public ClassConflictChecker.ConflictType getConflictType() { return conflictType; }
    }
    
    /**
     * Registers a class definition for future use.
     * Currently, PlayerClass is an enum, so this stores metadata for when the system
     * is refactored to support dynamic classes.
     * 
     * @param name The class name (will be namespaced with addon ID)
     * @param displayName The display name
     * @param description The description
     * @param category The class category
     * @param properties Additional properties
     */
    public void registerPlayerClass(String name, String displayName, String description, ClassCategory category, Map<String, Object> properties) {
        String fullName = addonId + ":" + name;
        ClassDefinition definition = new ClassDefinition(fullName, displayName, description, category, properties);
        classDefinitions.put(fullName, definition);
    }
    
    /**
     * Registers a class definition using a builder.
     */
    public ClassDefinitionBuilder createClassBuilder() {
        return new ClassDefinitionBuilder(addonId);
    }
    
    /**
     * Builder for class definitions.
     */
    public static class ClassDefinitionBuilder {
        private final String addonId;
        private String name;
        private String displayName;
        private String description;
        private ClassCategory category;
        private Map<String, Object> properties = new HashMap<>();
        
        public ClassDefinitionBuilder(String addonId) {
            this.addonId = addonId;
        }
        
        public ClassDefinitionBuilder name(String name) {
            this.name = name;
            return this;
        }
        
        public ClassDefinitionBuilder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }
        
        public ClassDefinitionBuilder description(String description) {
            this.description = description;
            return this;
        }
        
        public ClassDefinitionBuilder category(ClassCategory category) {
            this.category = category;
            return this;
        }
        
        public ClassDefinitionBuilder property(String key, Object value) {
            this.properties.put(key, value);
            return this;
        }
        
        public void register(AddonPlayerClassRegistry registry) {
            if (name == null || displayName == null || category == null) {
                throw new IllegalArgumentException("Name, displayName, and category are required");
            }
            registry.registerPlayerClass(name, displayName, description != null ? description : "", category, properties);
        }
    }
    
    /**
     * Adds a conflict rule between two classes.
     * @param class1 First class
     * @param class2 Second class
     * @param conflictType Type of conflict
     */
    public void addConflictRule(PlayerClass class1, PlayerClass class2, ClassConflictChecker.ConflictType conflictType) {
        String ruleKey = addonId + ":" + class1.name() + ":" + class2.name();
        ConflictRule rule = new ConflictRule(class1, class2, conflictType);
        conflictRules.put(ruleKey, rule);
        
        // Apply the rule immediately
        if (conflictType == ClassConflictChecker.ConflictType.MUTUALLY_EXCLUSIVE) {
            ClassConflictChecker.registerMutuallyExclusive(class1, class2);
        } else if (conflictType == ClassConflictChecker.ConflictType.CONFLICTING_ABILITIES) {
            ClassConflictChecker.registerConflictingAbilities(class1, class2);
        }
    }
    
    /**
     * Adds a stacking rule between two classes.
     * Currently, stacking is determined by category, but this allows addons to override.
     * @param class1 First class
     * @param class2 Second class
     * @param canStack Whether the classes can stack
     */
    public void addStackingRule(PlayerClass class1, PlayerClass class2, boolean canStack) {
        if (!canStack) {
            // If cannot stack, register as mutually exclusive
            addConflictRule(class1, class2, ClassConflictChecker.ConflictType.MUTUALLY_EXCLUSIVE);
        }
        // If can stack, no action needed (default behavior)
    }
    
    /**
     * Gets the addon ID.
     * @return The addon ID
     */
    public String getAddonId() {
        return addonId;
    }
    
    /**
     * Gets all registered class definitions for this addon.
     * @return Map of class name to definition
     */
    public Map<String, ClassDefinition> getClassDefinitions() {
        Map<String, ClassDefinition> result = new HashMap<>();
        for (Map.Entry<String, ClassDefinition> entry : classDefinitions.entrySet()) {
            if (entry.getKey().startsWith(addonId + ":")) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }
}






