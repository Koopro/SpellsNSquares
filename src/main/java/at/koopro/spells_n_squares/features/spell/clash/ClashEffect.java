package at.koopro.spells_n_squares.features.spell.clash;

import at.koopro.spells_n_squares.features.spell.base.Spell;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * Tracks an ongoing clash effect.
 */
public class ClashEffect {
    private final Vec3 wand1Pos;
    private final Vec3 wand2Pos;
    private final int duration;
    private int age;
    private final double intensity;
    private final Spell spell1;
    private final Spell spell2;
    private final List<Vec3> branchPoints;
    
    public ClashEffect(Vec3 wand1Pos, Vec3 wand2Pos, int duration, double intensity, 
                       Spell spell1, Spell spell2) {
        this.wand1Pos = wand1Pos;
        this.wand2Pos = wand2Pos;
        this.duration = duration;
        this.age = 0;
        this.intensity = intensity;
        this.spell1 = spell1;
        this.spell2 = spell2;
        this.branchPoints = new ArrayList<>();
    }
    
    public Vec3 getWand1Pos() {
        return wand1Pos;
    }
    
    public Vec3 getWand2Pos() {
        return wand2Pos;
    }
    
    public int getDuration() {
        return duration;
    }
    
    public int getAge() {
        return age;
    }
    
    public void tick() {
        age++;
    }
    
    public boolean isExpired() {
        return age >= duration;
    }
    
    public double getIntensity() {
        return intensity;
    }
    
    public Spell getSpell1() {
        return spell1;
    }
    
    public Spell getSpell2() {
        return spell2;
    }
    
    public List<Vec3> getBranchPoints() {
        return branchPoints;
    }
}

