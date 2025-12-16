package at.koopro.spells_n_squares.features.spell;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.features.spell.entity.ShieldOrbEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/**
 * Handles projectile blocking for Protego shields.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class ShieldProjectileHandler {
    
    /**
     * Intercepts incoming damage and blocks projectiles if they hit a shield.
     * This is the first event in the damage pipeline and is cancellable.
     */
    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        Entity target = event.getEntity();
        if (!(target instanceof Player player)) {
            return;
        }
        
        // Check if player has an active shield
        ShieldOrbEntity shield = getPlayerShield(player);
        if (shield != null) {
            // Check if damage source is from a projectile
            Entity source = event.getSource().getEntity();
            if (source instanceof Projectile && shield.shouldBlockProjectile(source)) {
                // Block the damage
                event.setCanceled(true);
                
                // Visual and audio feedback when blocking
                handleProjectileBlock(shield, source);
            }
        }
    }
    
    /**
     * Gets the active shield for a player, if one exists.
     */
    private static ShieldOrbEntity getPlayerShield(Player player) {
        for (Entity entity : player.level().getEntitiesOfClass(ShieldOrbEntity.class, 
                player.getBoundingBox().inflate(5.0))) {
            ShieldOrbEntity shield = (ShieldOrbEntity) entity;
            if (shield.getOwner() == player) {
                return shield;
            }
        }
        return null;
    }
    
    /**
     * Handles visual and audio effects when a projectile is blocked.
     */
    private static void handleProjectileBlock(ShieldOrbEntity shield, Entity projectile) {
        if (shield.level() instanceof ServerLevel serverLevel) {
            Vec3 hitPos = projectile.position();
            
            // Visual effect - burst of particles
            serverLevel.sendParticles(
                ParticleTypes.ELECTRIC_SPARK,
                hitPos.x, hitPos.y, hitPos.z,
                30, 0.4, 0.4, 0.4, 0.15
            );
            
            // Additional magical particles
            serverLevel.sendParticles(
                ParticleTypes.ENCHANT,
                hitPos.x, hitPos.y, hitPos.z,
                15, 0.3, 0.3, 0.3, 0.1
            );
            
            // Sound effect
            shield.level().playSound(null, hitPos.x, hitPos.y, hitPos.z,
                SoundEvents.ENCHANTMENT_TABLE_USE,
                SoundSource.PLAYERS,
                0.8f, 1.5f);
        }
        
        // Remove the projectile
        projectile.discard();
    }
}
