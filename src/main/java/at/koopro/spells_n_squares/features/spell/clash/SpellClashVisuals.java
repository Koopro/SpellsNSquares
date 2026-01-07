package at.koopro.spells_n_squares.features.spell.clash;

import at.koopro.spells_n_squares.core.fx.patterns.SpellFxPatterns;
import at.koopro.spells_n_squares.features.spell.base.Spell;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates visual effects for spell clashes.
 */
public final class SpellClashVisuals {
    private SpellClashVisuals() {
    }
    
    private static final List<ClashEffect> activeEffects = new ArrayList<>();
    
    /**
     * Creates a clash effect between two wand positions.
     */
    public static void createClashEffect(ServerLevel level, Vec3 wand1Pos, Vec3 wand2Pos, 
                                         Vec3 collisionPoint, Spell spell1, Spell spell2, 
                                         double intensity) {
        if (level == null || wand1Pos == null || wand2Pos == null) {
            return;
        }
        
        // Create clash effect using SpellFxPatterns
        SpellFxPatterns.clash()
            .between(wand1Pos, wand2Pos)
            .duration(40)
            .intensity(intensity)
            .branchChance(0.1)
            .play(level);
        
        // Store effect for ongoing updates
        ClashEffect effect = new ClashEffect(wand1Pos, wand2Pos, 40, intensity, spell1, spell2);
        activeEffects.add(effect);
    }
    
    /**
     * Updates an ongoing clash effect.
     */
    public static void updateClashEffect(ServerLevel level, ClashEffect effect) {
        if (level == null || effect == null || effect.isExpired()) {
            return;
        }
        
        // Update the clash effect each tick (re-spawn particles for animation)
        // This creates the flickering/pulsing effect
        if (level.getGameTime() % 2 == 0) { // Every other tick
            SpellFxPatterns.clash()
                .between(effect.getWand1Pos(), effect.getWand2Pos())
                .duration(2)
                .intensity(effect.getIntensity())
                .branchChance(0.05)
                .play(level);
        }
    }
    
    /**
     * Spawns lightning branch effect.
     */
    public static void spawnLightningBranch(ServerLevel level, Vec3 start, Vec3 end, double branchChance) {
        if (level == null || start == null || end == null) {
            return;
        }
        
        SpellFxPatterns.beam()
            .from(start)
            .to(end)
            .count(5)
            .branchChance(branchChance)
            .jaggedness(0.2)
            .play(level);
    }
}

