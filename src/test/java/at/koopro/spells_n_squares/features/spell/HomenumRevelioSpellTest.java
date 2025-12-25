package at.koopro.spells_n_squares.features.spell;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HomenumRevelioSpellTest {

    @Test
    void basicMetadataIsStable() {
        HomenumRevelioSpell spell = new HomenumRevelioSpell();
        assertEquals("Homenum Revelio", spell.getName());
        assertTrue(spell.getDescription().toLowerCase().contains("reveal"));
        assertTrue(spell.getCooldown() >= 40);
    }

    @Test
    void visualIntensityModerateForUtilitySpell() {
        HomenumRevelioSpell spell = new HomenumRevelioSpell();
        float intensity = spell.getVisualEffectIntensity();
        assertTrue(intensity > 0.0f);
        assertTrue(intensity <= 0.7f);
    }
}

