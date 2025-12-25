package at.koopro.spells_n_squares.features.spell;

import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Liberacorpus - Counter-spell to Levicorpus that releases hanging victims.
 */
public class LiberacorpusSpell implements Spell {
    
    private static final int COOLDOWN = 40; // 2 seconds
    private static final double RANGE = 10.0;
    
    @Override
    public Identifier getId() {
        return SpellRegistry.spellId("liberacorpus");
    }
    
    @Override
    public String getName() {
        return "Liberacorpus";
    }
    
    @Override
    public String getDescription() {
        return "Counter-spell to Levicorpus that releases hanging victims";
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
        
        Vec3 lookVec = player.getLookAngle();
        Vec3 playerPos = player.position();
        Vec3 targetPos = playerPos.add(lookVec.scale(RANGE));
        
        // Find entities in range
        AABB searchBox = new AABB(playerPos, targetPos).inflate(2.0);
        var entities = level.getEntitiesOfClass(Entity.class, searchBox,
            entity -> entity instanceof LivingEntity living && 
                     entity != player && 
                     entity.isAlive() && 
                     !entity.isSpectator());
        
        boolean released = false;
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity living) {
                // Remove any hanging effects (simplified - would check for hanging state)
                // Reset movement to normal
                living.setDeltaMovement(0, 0, 0);
                living.hurtMarked = true;
                released = true;
                
                // Visual effect
                Vec3 entityPos = entity.position();
                serverLevel.sendParticles(
                    ParticleTypes.ENCHANT,
                    entityPos.x, entityPos.y + entity.getBbHeight(), entityPos.z,
                    15,
                    0.3, 0.3, 0.3,
                    0.05
                );
            }
        }
        
        if (released) {
            // Audio feedback
            level.playSound(null, targetPos.x, targetPos.y, targetPos.z,
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.6f, 1.5f);
            
            // Visual effect at target area
            serverLevel.sendParticles(
                ParticleTypes.ENCHANT,
                targetPos.x, targetPos.y + 1.0, targetPos.z,
                20,
                0.5, 0.5, 0.5,
                0.1
            );
            
            return true;
        }
        
        return false;
    }
    
    @Override
    public float getVisualEffectIntensity() {
        return 0.4f;
    }
}









