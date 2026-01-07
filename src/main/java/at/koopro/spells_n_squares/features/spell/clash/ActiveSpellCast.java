package at.koopro.spells_n_squares.features.spell.clash;

import at.koopro.spells_n_squares.features.spell.base.Spell;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

/**
 * Tracks an active spell cast for clash detection.
 */
public record ActiveSpellCast(
    UUID playerId,
    Vec3 wandPosition,
    Vec3 direction,
    Spell spell,
    long castTime,
    int duration
) {
    /**
     * Checks if this cast has expired.
     */
    public boolean isExpired(long currentTime) {
        return (currentTime - castTime) > duration;
    }
}

