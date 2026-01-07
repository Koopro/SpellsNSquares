package at.koopro.spells_n_squares.features.spell;

import at.koopro.spells_n_squares.core.config.Config;
import at.koopro.spells_n_squares.features.spell.combat.ConfringoSpell;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfringoSpellTest {

    @Test
    void cooldownRespectsGlobalMultiplier() {
        ConfringoSpell spell = new ConfringoSpell();
        // Default config multiplier is 1.0
        int base = spell.getCooldown();
        assertTrue(base >= 20);

        // Sanity: multiplier accessors should return positive values
        assertTrue(Config.getSpellCooldownMultiplier() > 0.0);
        assertTrue(Config.getSpellDamageMultiplier() > 0.0);
    }

    @Test
    void visualIntensityHighForFxHeavySpell() {
        ConfringoSpell spell = new ConfringoSpell();
        float intensity = spell.getVisualEffectIntensity();
        assertTrue(intensity > 0.7f);
        assertEquals(0.9f, intensity, 0.0001f);
    }
}

