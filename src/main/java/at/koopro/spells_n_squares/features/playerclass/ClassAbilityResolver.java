package at.koopro.spells_n_squares.features.playerclass;

import net.minecraft.world.entity.player.Player;

import java.util.*;

/**
 * Resolves and combines abilities from multiple player classes.
 * Handles ability priority, stacking, and conflicts.
 */
public final class ClassAbilityResolver {
    private ClassAbilityResolver() {
    }
    
    /**
     * Represents an ability that can be granted by a class.
     */
    public record Ability(
        String id,
        String name,
        String description,
        AbilityType type,
        int priority, // Higher priority abilities override lower priority ones
        Map<String, Object> properties // Ability-specific properties
    ) {
        /**
         * Types of abilities.
         */
        public enum AbilityType {
            /**
             * Passive ability that always applies.
             */
            PASSIVE,
            
            /**
             * Active ability that can be used.
             */
            ACTIVE,
            
            /**
             * Permission/access ability.
             */
            PERMISSION,
            
            /**
             * Stat modifier ability.
             */
            STAT_MODIFIER
        }
    }
    
    // Registry of abilities by class
    private static final Map<PlayerClass, Set<Ability>> classAbilities = new HashMap<>();
    
    // Ability priority map (ability ID -> priority)
    private static final Map<String, Integer> abilityPriorities = new HashMap<>();
    
    static {
        initializeDefaultAbilities();
    }
    
    /**
     * Initializes default abilities for wizarding world classes.
     */
    private static void initializeDefaultAbilities() {
        // Wizard abilities
        registerClassAbility(PlayerClass.WIZARD, new Ability(
            "wizard_magic",
            "Wizard Magic",
            "Access to wizard spells and magic",
            Ability.AbilityType.PERMISSION,
            10,
            Map.of("spellAccess", true)
        ));
        
        // Student abilities
        registerClassAbility(PlayerClass.STUDENT, new Ability(
            "student_learning",
            "Student Learning",
            "Enhanced experience gain from educational activities",
            Ability.AbilityType.PASSIVE,
            10,
            Map.of("xpBonus", 1.2)
        ));
        registerClassAbility(PlayerClass.STUDENT, new Ability(
            "student_hogwarts",
            "Hogwarts Access",
            "Access to Hogwarts facilities and classes",
            Ability.AbilityType.PERMISSION,
            15,
            Map.of("hogwartsAccess", true)
        ));
        
        // Auror abilities
        registerClassAbility(PlayerClass.AUROR, new Ability(
            "auror_combat",
            "Auror Combat Training",
            "Enhanced combat abilities and dark wizard detection",
            Ability.AbilityType.PASSIVE,
            20,
            Map.of("combatBonus", 1.5, "darkWizardDetection", true)
        ));
        registerClassAbility(PlayerClass.AUROR, new Ability(
            "auror_authority",
            "Auror Authority",
            "Authority to arrest dark wizards and access Ministry resources",
            Ability.AbilityType.PERMISSION,
            25,
            Map.of("arrestAuthority", true, "ministryAccess", true)
        ));
        
        // Death Eater abilities
        registerClassAbility(PlayerClass.DEATH_EATER, new Ability(
            "death_eater_dark_magic",
            "Dark Magic Mastery",
            "Access to forbidden dark magic spells",
            Ability.AbilityType.PERMISSION,
            25,
            Map.of("darkMagicAccess", true, "unforgivableCurses", true)
        ));
        registerClassAbility(PlayerClass.DEATH_EATER, new Ability(
            "death_eater_fear",
            "Inspiring Fear",
            "Creatures and NPCs are more likely to flee",
            Ability.AbilityType.PASSIVE,
            15,
            Map.of("fearAura", true)
        ));
        
        // Order Member abilities
        registerClassAbility(PlayerClass.ORDER_MEMBER, new Ability(
            "order_protection",
            "Order Protection",
            "Enhanced defensive abilities and protection magic",
            Ability.AbilityType.PASSIVE,
            20,
            Map.of("defenseBonus", 1.3, "protectionMagic", true)
        ));
        registerClassAbility(PlayerClass.ORDER_MEMBER, new Ability(
            "order_network",
            "Order Network",
            "Access to Order safe houses and resources",
            Ability.AbilityType.PERMISSION,
            15,
            Map.of("safeHouseAccess", true)
        ));
        
        // Teacher abilities
        registerClassAbility(PlayerClass.TEACHER, new Ability(
            "teacher_knowledge",
            "Teaching Knowledge",
            "Can teach spells to other players",
            Ability.AbilityType.ACTIVE,
            20,
            Map.of("canTeach", true)
        ));
        
        // Healer abilities
        registerClassAbility(PlayerClass.HEALER, new Ability(
            "healer_medicine",
            "Healing Mastery",
            "Enhanced healing abilities and potion brewing",
            Ability.AbilityType.PASSIVE,
            20,
            Map.of("healingBonus", 2.0, "potionMastery", true)
        ));
        
        // Quidditch Player abilities
        registerClassAbility(PlayerClass.QUIDDITCH_PLAYER, new Ability(
            "quidditch_flying",
            "Quidditch Flying",
            "Enhanced broomstick control and flying speed",
            Ability.AbilityType.PASSIVE,
            15,
            Map.of("flyingSpeed", 1.3, "broomControl", true)
        ));
        
        // Werewolf abilities
        registerClassAbility(PlayerClass.WEREWOLF, new Ability(
            "werewolf_transform",
            "Werewolf Transformation",
            "Transform into a werewolf during full moon",
            Ability.AbilityType.ACTIVE,
            20,
            Map.of("moonPhase", "full")
        ));
        registerClassAbility(PlayerClass.WEREWOLF, new Ability(
            "werewolf_strength",
            "Werewolf Strength",
            "Increased strength and combat ability",
            Ability.AbilityType.STAT_MODIFIER,
            15,
            Map.of("strengthBonus", 2.0)
        ));
        
        // Vampire abilities
        registerClassAbility(PlayerClass.VAMPIRE, new Ability(
            "vampire_bloodlust",
            "Bloodlust",
            "Requires blood to survive",
            Ability.AbilityType.PASSIVE,
            20,
            Map.of("bloodRequired", true)
        ));
        registerClassAbility(PlayerClass.VAMPIRE, new Ability(
            "vampire_night_vision",
            "Night Vision",
            "Enhanced vision in darkness",
            Ability.AbilityType.PASSIVE,
            10,
            Map.of("nightVision", true)
        ));
        
        // Dark Wizard abilities
        registerClassAbility(PlayerClass.DARK_WIZARD, new Ability(
            "dark_wizard_curses",
            "Dark Curses",
            "Access to dark curses and forbidden magic",
            Ability.AbilityType.PERMISSION,
            20,
            Map.of("darkCurses", true)
        ));
    }
    
    /**
     * Registers an ability for a class.
     * Can be called by addons to add custom abilities.
     */
    public static void registerClassAbility(PlayerClass playerClass, Ability ability) {
        classAbilities.computeIfAbsent(playerClass, k -> new HashSet<>()).add(ability);
        abilityPriorities.put(ability.id(), ability.priority());
    }
    
    /**
     * Gets all active abilities for a player based on their classes.
     * Combines abilities from all classes, resolving conflicts by priority.
     * @param player The player
     * @return Set of active abilities
     */
    public static Set<Ability> getActiveAbilities(Player player) {
        Set<PlayerClass> classes = PlayerClassManager.getPlayerClasses(player);
        Map<String, Ability> abilityMap = new HashMap<>();
        
        // Collect all abilities from all classes
        for (PlayerClass clazz : classes) {
            Set<Ability> abilities = classAbilities.getOrDefault(clazz, Collections.emptySet());
            for (Ability ability : abilities) {
                // If ability already exists, keep the one with higher priority
                Ability existing = abilityMap.get(ability.id());
                if (existing == null || ability.priority() > existing.priority()) {
                    abilityMap.put(ability.id(), ability);
                }
            }
        }
        
        return new HashSet<>(abilityMap.values());
    }
    
    /**
     * Gets a specific ability for a player.
     * @param player The player
     * @param abilityId The ability ID
     * @return The ability, or null if player doesn't have it
     */
    public static Ability getAbility(Player player, String abilityId) {
        Set<Ability> abilities = getActiveAbilities(player);
        for (Ability ability : abilities) {
            if (ability.id().equals(abilityId)) {
                return ability;
            }
        }
        return null;
    }
    
    /**
     * Checks if a player has a specific ability.
     * @param player The player
     * @param abilityId The ability ID
     * @return true if the player has the ability
     */
    public static boolean hasAbility(Player player, String abilityId) {
        return getAbility(player, abilityId) != null;
    }
    
    /**
     * Gets abilities by type for a player.
     * @param player The player
     * @param type The ability type
     * @return Set of abilities of the specified type
     */
    public static Set<Ability> getAbilitiesByType(Player player, Ability.AbilityType type) {
        Set<Ability> allAbilities = getActiveAbilities(player);
        Set<Ability> filtered = new HashSet<>();
        for (Ability ability : allAbilities) {
            if (ability.type() == type) {
                filtered.add(ability);
            }
        }
        return filtered;
    }
    
    /**
     * Gets all abilities for a specific class.
     * @param playerClass The class
     * @return Set of abilities for that class
     */
    public static Set<Ability> getClassAbilities(PlayerClass playerClass) {
        return new HashSet<>(classAbilities.getOrDefault(playerClass, Collections.emptySet()));
    }
}















