package at.koopro.spells_n_squares.features.spell;

import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Periculum - A spell that shoots red sparks into the air as a signal.
 */
public class PericulumSpell implements Spell {
    
    private static final int COOLDOWN = 10; // 0.5 seconds
    
    @Override
    public Identifier getId() {
        return SpellRegistry.spellId("periculum");
    }
    
    @Override
    public String getName() {
        return "Periculum";
    }
    
    @Override
    public String getDescription() {
        return "Shoots red sparks into the air as a signal";
    }
    
    @Override
    public int getCooldown() {
        return COOLDOWN;
    }
    
    @Override
    public boolean cast(Player player, Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }
        
        Vec3 pos = player.position().add(0, player.getEyeHeight(), 0);
        Vec3 direction = player.getLookAngle();
        
        // Shoot red sparks upward
        for (int i = 0; i < 5; i++) {
            Vec3 sparkPos = pos.add(direction.scale(i * 0.5));
            sparkPos = sparkPos.add(0, i * 0.5, 0);
            
            serverLevel.sendParticles(
                ParticleTypes.FLAME,
                sparkPos.x, sparkPos.y, sparkPos.z,
                10,
                0.1, 0.1, 0.1,
                0.05
            );
        }
        
        // Audio feedback
        level.playSound(null, pos.x, pos.y, pos.z,
            SoundEvents.FIRE_AMBIENT, SoundSource.PLAYERS, 0.5f, 1.5f);
        
        return true;
    }
    
    @Override
    public float getVisualEffectIntensity() {
        return 0.5f;
    }
}













