package at.koopro.spells_n_squares.features.combat.system;

import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * System for managing spell combos (chaining spells for bonus effects).
 */
public final class SpellComboSystem {
    private static final Map<List<Identifier>, SpellCombo> COMBOS = new HashMap<>();
    
    private SpellComboSystem() {
    }
    
    /**
     * Registers a spell combo.
     */
    public static void register(SpellCombo combo) {
        COMBOS.put(combo.getSpellSequence(), combo);
    }
    
    /**
     * Checks if a sequence of spells matches a combo.
     */
    public static SpellCombo checkCombo(List<Identifier> spellSequence) {
        return COMBOS.get(spellSequence);
    }
    
    /**
     * Represents a spell combo.
     */
    public static class SpellCombo {
        private final List<Identifier> spellSequence;
        private final String name;
        private final String description;
        private final float damageMultiplier;
        private final float cooldownReduction;
        
        public SpellCombo(List<Identifier> spellSequence, String name, String description, 
                         float damageMultiplier, float cooldownReduction) {
            this.spellSequence = new ArrayList<>(spellSequence);
            this.name = name;
            this.description = description;
            this.damageMultiplier = damageMultiplier;
            this.cooldownReduction = cooldownReduction;
        }
        
        public List<Identifier> getSpellSequence() {
            return new ArrayList<>(spellSequence);
        }
        
        public String getName() {
            return name;
        }
        
        public String getDescription() {
            return description;
        }
        
        public float getDamageMultiplier() {
            return damageMultiplier;
        }
        
        public float getCooldownReduction() {
            return cooldownReduction;
        }
    }
}

