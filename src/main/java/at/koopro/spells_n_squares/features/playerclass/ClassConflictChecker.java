package at.koopro.spells_n_squares.features.playerclass;

import java.util.*;

/**
 * Handles class conflict checking and stacking rules.
 * Determines if classes can be added together and identifies conflicts.
 */
public final class ClassConflictChecker {
    private ClassConflictChecker() {
    }
    
    /**
     * Types of conflicts between classes.
     */
    public enum ConflictType {
        /**
         * Classes are mutually exclusive and cannot be held simultaneously.
         */
        MUTUALLY_EXCLUSIVE,
        
        /**
         * Classes have conflicting abilities but can technically coexist.
         * May cause gameplay issues or restrictions.
         */
        CONFLICTING_ABILITIES,
        
        /**
         * Classes are compatible and can stack freely.
         */
        COMPATIBLE,
        
        /**
         * Same class - cannot add duplicate.
         */
        DUPLICATE
    }
    
    // Mutually exclusive class pairs
    private static final Map<PlayerClass, Set<PlayerClass>> mutuallyExclusive = new HashMap<>();
    
    // Conflicting ability pairs (can coexist but may have restrictions)
    private static final Map<PlayerClass, Set<PlayerClass>> conflictingAbilities = new HashMap<>();
    
    static {
        initializeConflictRules();
    }
    
    /**
     * Initializes conflict rules for all classes.
     */
    private static void initializeConflictRules() {
        // Mutually exclusive organizations
        addMutuallyExclusive(PlayerClass.DEATH_EATER, PlayerClass.ORDER_MEMBER);
        
        // Auror cannot be a Death Eater (law enforcement vs criminal)
        addMutuallyExclusive(PlayerClass.AUROR, PlayerClass.DEATH_EATER);
        
        // Alignment conflicts
        addConflictingAbilities(PlayerClass.GOOD, PlayerClass.EVIL);
        addConflictingAbilities(PlayerClass.GOOD, PlayerClass.DARK_WIZARD);
        addConflictingAbilities(PlayerClass.EVIL, PlayerClass.DARK_WIZARD);
        
        // Auror conflicts with Dark Wizard (law enforcement vs illegal magic)
        addConflictingAbilities(PlayerClass.AUROR, PlayerClass.DARK_WIZARD);
        
        // Order Member conflicts with Dark Wizard
        addConflictingAbilities(PlayerClass.ORDER_MEMBER, PlayerClass.DARK_WIZARD);
    }
    
    /**
     * Adds a mutually exclusive relationship between two classes.
     */
    private static void addMutuallyExclusive(PlayerClass class1, PlayerClass class2) {
        mutuallyExclusive.computeIfAbsent(class1, k -> new HashSet<>()).add(class2);
        mutuallyExclusive.computeIfAbsent(class2, k -> new HashSet<>()).add(class1);
    }
    
    /**
     * Adds a conflicting abilities relationship between two classes.
     */
    private static void addConflictingAbilities(PlayerClass class1, PlayerClass class2) {
        conflictingAbilities.computeIfAbsent(class1, k -> new HashSet<>()).add(class2);
        conflictingAbilities.computeIfAbsent(class2, k -> new HashSet<>()).add(class1);
    }
    
    /**
     * Checks if a class can be added to a player's existing classes.
     * @param newClass The class to add
     * @param existingClasses The player's current classes
     * @return true if the class can be added
     */
    public static boolean canAddClass(PlayerClass newClass, Set<PlayerClass> existingClasses) {
        if (newClass == null || newClass == PlayerClass.NONE) {
            return false;
        }
        
        // Check for duplicate
        if (existingClasses.contains(newClass)) {
            return false;
        }
        
        // Check for mutually exclusive classes
        Set<PlayerClass> exclusive = mutuallyExclusive.get(newClass);
        if (exclusive != null) {
            for (PlayerClass existing : existingClasses) {
                if (exclusive.contains(existing)) {
                    return false;
                }
            }
        }
        
        // Check category-based rules
        if (!canStackByCategory(newClass, existingClasses)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Gets all classes that conflict with the new class.
     * @param newClass The class to check
     * @param existingClasses The player's current classes
     * @return List of conflicting classes
     */
    public static List<PlayerClass> getConflictingClasses(PlayerClass newClass, Set<PlayerClass> existingClasses) {
        List<PlayerClass> conflicts = new ArrayList<>();
        
        if (newClass == null || newClass == PlayerClass.NONE) {
            return conflicts;
        }
        
        // Check for duplicate
        if (existingClasses.contains(newClass)) {
            conflicts.add(newClass);
            return conflicts;
        }
        
        // Check for mutually exclusive classes
        Set<PlayerClass> exclusive = mutuallyExclusive.get(newClass);
        if (exclusive != null) {
            for (PlayerClass existing : existingClasses) {
                if (exclusive.contains(existing)) {
                    conflicts.add(existing);
                }
            }
        }
        
        // Check category-based conflicts
        ClassCategory newCategory = newClass.getCategory();
        for (PlayerClass existing : existingClasses) {
            if (newCategory == ClassCategory.BLOOD_STATUS && existing.getCategory() == ClassCategory.BLOOD_STATUS) {
                conflicts.add(existing);
            } else if (newCategory == ClassCategory.ORGANIZATION && existing.getCategory() == ClassCategory.ORGANIZATION) {
                conflicts.add(existing);
            }
        }
        
        return conflicts;
    }
    
    /**
     * Gets the conflict type between two classes.
     * @param class1 First class
     * @param class2 Second class
     * @return The conflict type
     */
    public static ConflictType getConflictType(PlayerClass class1, PlayerClass class2) {
        if (class1 == class2) {
            return ConflictType.DUPLICATE;
        }
        
        // Check mutually exclusive
        Set<PlayerClass> exclusive = mutuallyExclusive.get(class1);
        if (exclusive != null && exclusive.contains(class2)) {
            return ConflictType.MUTUALLY_EXCLUSIVE;
        }
        
        // Check conflicting abilities
        Set<PlayerClass> conflicting = conflictingAbilities.get(class1);
        if (conflicting != null && conflicting.contains(class2)) {
            return ConflictType.CONFLICTING_ABILITIES;
        }
        
        // Check category-based rules
        ClassCategory cat1 = class1.getCategory();
        ClassCategory cat2 = class2.getCategory();
        
        if (cat1 == ClassCategory.BLOOD_STATUS && cat2 == ClassCategory.BLOOD_STATUS) {
            return ConflictType.MUTUALLY_EXCLUSIVE;
        }
        
        if (cat1 == ClassCategory.ORGANIZATION && cat2 == ClassCategory.ORGANIZATION) {
            return ConflictType.MUTUALLY_EXCLUSIVE;
        }
        
        return ConflictType.COMPATIBLE;
    }
    
    /**
     * Checks if classes can stack based on their categories.
     */
    private static boolean canStackByCategory(PlayerClass newClass, Set<PlayerClass> existingClasses) {
        ClassCategory newCategory = newClass.getCategory();
        
        // Blood status: only one allowed
        if (newCategory == ClassCategory.BLOOD_STATUS) {
            for (PlayerClass existing : existingClasses) {
                if (existing.getCategory() == ClassCategory.BLOOD_STATUS) {
                    return false;
                }
            }
        }
        
        // Organization: only one allowed (will be enforced when organizations are added)
        if (newCategory == ClassCategory.ORGANIZATION) {
            for (PlayerClass existing : existingClasses) {
                if (existing.getCategory() == ClassCategory.ORGANIZATION) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * Registers a mutually exclusive relationship between two classes.
     * Can be called by addons to define custom conflict rules.
     * @param class1 First class
     * @param class2 Second class
     */
    public static void registerMutuallyExclusive(PlayerClass class1, PlayerClass class2) {
        addMutuallyExclusive(class1, class2);
    }
    
    /**
     * Registers a conflicting abilities relationship between two classes.
     * Can be called by addons to define custom conflict rules.
     * @param class1 First class
     * @param class2 Second class
     */
    public static void registerConflictingAbilities(PlayerClass class1, PlayerClass class2) {
        addConflictingAbilities(class1, class2);
    }
}
















