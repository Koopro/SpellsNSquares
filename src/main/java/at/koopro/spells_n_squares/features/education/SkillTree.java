package at.koopro.spells_n_squares.features.education;

import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Skill tree system for learning new spells and abilities through classes.
 */
public final class SkillTree {
    private static final Map<Identifier, SkillNode> SKILLS = new HashMap<>();
    private static final Map<Identifier, Set<Identifier>> PREREQUISITES = new HashMap<>();
    
    private SkillTree() {
    }
    
    /**
     * Registers a skill node.
     */
    public static void register(Identifier id, SkillNode skill) {
        if (SKILLS.containsKey(id)) {
            throw new IllegalArgumentException("Skill already registered: " + id);
        }
        SKILLS.put(id, skill);
    }
    
    /**
     * Sets prerequisites for a skill.
     */
    public static void setPrerequisites(Identifier skillId, Set<Identifier> prerequisites) {
        PREREQUISITES.put(skillId, new HashSet<>(prerequisites));
    }
    
    /**
     * Gets a skill by ID.
     */
    public static SkillNode get(Identifier id) {
        return SKILLS.get(id);
    }
    
    /**
     * Checks if a player can learn a skill (has prerequisites).
     */
    public static boolean canLearn(Identifier skillId, Set<Identifier> learnedSkills) {
        Set<Identifier> prerequisites = PREREQUISITES.get(skillId);
        if (prerequisites == null || prerequisites.isEmpty()) {
            return true; // No prerequisites
        }
        return learnedSkills.containsAll(prerequisites);
    }
    
    /**
     * Represents a skill node in the skill tree.
     */
    public static class SkillNode {
        private final Identifier id;
        private final String name;
        private final String description;
        private final Identifier classId; // Which class teaches this skill
        
        public SkillNode(Identifier id, String name, String description, Identifier classId) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.classId = classId;
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
        
        public Identifier getClassId() {
            return classId;
        }
    }
}

