package at.koopro.spells_n_squares.features.spell;

import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Accio - The Summoning Charm that pulls items and entities toward the caster.
 */
public class AccioSpell implements Spell {
    
    private static final int COOLDOWN = 40; // 2 seconds
    private static final double RANGE = 32.0; // blocks
    private static final double PULL_FORCE = 0.3;
    
    @Override
    public Identifier getId() {
        return SpellRegistry.spellId("accio");
    }
    
    @Override
    public String getName() {
        return "Accio";
    }
    
    @Override
    public String getDescription() {
        return "The Summoning Charm - pulls items and entities toward you";
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
        
        // Find entities and items in range
        AABB searchBox = new AABB(playerPos, targetPos).inflate(RANGE);
        var entities = level.getEntitiesOfClass(Entity.class, searchBox,
            entity -> entity != player && entity.isAlive() && !entity.isSpectator());
        
        boolean pulled = false;
        for (Entity entity : entities) {
            Vec3 entityPos = entity.position();
            Vec3 pullDir = playerPos.subtract(entityPos).normalize();
            
            // Pull the entity toward the player
            Vec3 currentMotion = entity.getDeltaMovement();
            Vec3 pullMotion = pullDir.scale(PULL_FORCE);
            
            entity.setDeltaMovement(
                currentMotion.x + pullMotion.x,
                currentMotion.y + pullMotion.y + 0.1, // Slight upward pull
                currentMotion.z + pullMotion.z
            );
            entity.hurtMarked = true;
            pulled = true;
            
            // Visual effect trail
            serverLevel.sendParticles(
                ParticleTypes.ENCHANT,
                entityPos.x, entityPos.y + entity.getBbHeight() / 2, entityPos.z,
                3,
                0.1, 0.1, 0.1,
                0.02
            );
        }
        
        if (pulled) {
            // Audio feedback
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.8f, 1.5f);
            
            // Visual effect at target area
            serverLevel.sendParticles(
                ParticleTypes.END_ROD,
                targetPos.x, targetPos.y, targetPos.z,
                15,
                1.0, 1.0, 1.0,
                0.05
            );
            
            return true;
        }
        
        return false;
    }
    
    @Override
    public float getVisualEffectIntensity() {
        return 0.5f;
    }
}




