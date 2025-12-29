package at.koopro.spells_n_squares.features.potions;

import at.koopro.spells_n_squares.core.config.Config;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Simple tests around potion-related config defaults.
 */
class PotionConfigTest {

    @Test
    void potionDurationMultiplierHasSensibleDefault() {
        double mult = Config.getPotionDurationMultiplier();
        assertTrue(mult > 0.0);
        assertEquals(1.0, mult, 0.0001);
    }
}









