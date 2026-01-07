package at.koopro.spells_n_squares.features.spell.transformation;

import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import at.koopro.spells_n_squares.features.spell.base.Spell;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Riddikulus - The Boggart-Banishing Spell that defeats Boggarts by turning fear into laughter.
 */
public class RiddikulusSpell implements Spell {
    
    private static final int COOLDOWN = 20; // 1 second
    private static final double RANGE = 8.0; // blocks
    
    @Override
    public Identifier getId() {
        return SpellRegistry.spellId("riddikulus");
    }
    
    @Override
    public String getName() {
        return "Riddikulus";
    }
    
    @Override
    public String getDescription() {
        return "The Boggart-Banishing Spell - defeats Boggarts by turning fear into something humorous";
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
        
        Vec3 playerPos = player.position();
        Vec3 lookVec = player.getLookAngle();
        Vec3 targetPos = playerPos.add(lookVec.scale(RANGE));
        
        // Boggart entity removed - simplified implementation
        // Visual effect at target area
        serverLevel.sendParticles(
            ParticleTypes.ENCHANT,
            targetPos.x, targetPos.y, targetPos.z,
            15,
            1.0, 1.0, 1.0,
            0.05
        );
        
        // Audio feedback
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.8f, 1.2f);
        
        return true;
    }
    
    @Override
    public float getVisualEffectIntensity() {
        return 0.6f;
    }
}


