package at.koopro.spells_n_squares.features.spell;

import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import at.koopro.spells_n_squares.features.creatures.hostile.BoggartEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Riddikulus - The Boggart-Banishing Spell that defeats Boggarts by turning fear into laughter.
 */
public class RiddikulusSpell implements Spell {
    
    private static final int COOLDOWN = 20; // 1 second
    private static final double RANGE = 8.0; // blocks
    private static final int DAMAGE = 5; // Laughter damage to Boggart
    
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
        
        // Find Boggarts in range
        AABB searchBox = new AABB(playerPos, targetPos).inflate(2.0);
        var entities = level.getEntitiesOfClass(Entity.class, searchBox,
            entity -> entity instanceof BoggartEntity && entity.isAlive());
        
        boolean hitBoggart = false;
        for (Entity entity : entities) {
            if (entity instanceof BoggartEntity boggart) {
                // Apply Riddikulus effect
                boggart.applyRiddikulus(DAMAGE);
                hitBoggart = true;
                
                // Visual effects - happy particles
                Vec3 boggartPos = boggart.position();
                for (int i = 0; i < 20; i++) {
                    serverLevel.sendParticles(
                        ParticleTypes.HAPPY_VILLAGER,
                        boggartPos.x, boggartPos.y + boggart.getBbHeight() / 2, boggartPos.z,
                        1,
                        0.5, 0.5, 0.5,
                        0.1
                    );
                }
                
                // Laughter sound effect
                level.playSound(null, boggartPos.x, boggartPos.y, boggartPos.z,
                    SoundEvents.VILLAGER_YES, SoundSource.NEUTRAL, 1.0f, 1.5f);
            }
        }
        
        if (hitBoggart) {
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
        
        return false;
    }
    
    @Override
    public float getVisualEffectIntensity() {
        return 0.6f;
    }
}




