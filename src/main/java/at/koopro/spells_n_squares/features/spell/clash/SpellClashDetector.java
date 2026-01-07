package at.koopro.spells_n_squares.features.spell.clash;

import at.koopro.spells_n_squares.core.config.Config;
import at.koopro.spells_n_squares.features.spell.base.Spell;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;

/**
 * Detects spell clashes (projectile collisions and duels).
 */
public final class SpellClashDetector {
    private SpellClashDetector() {
    }
    
    // Track active spell casts per player
    private static final Map<UUID, ActiveSpellCast> activeCasts = new HashMap<>();
    
    // Track ongoing clash effects
    private static final List<ClashEffect> activeClashes = new ArrayList<>();
    
    // Maximum time to track a cast (1 second in ticks)
    private static final int MAX_CAST_TRACK_TIME = 20;
    
    /**
     * Tracks an active spell cast for clash detection.
     */
    public static void trackSpellCast(Player player, Vec3 wandPos, Vec3 direction, Spell spell) {
        if (player == null || wandPos == null || direction == null || spell == null) {
            return;
        }
        
        if (!Config.enableSpellClashEffects()) {
            return;
        }
        
        activeCasts.put(player.getUUID(), new ActiveSpellCast(
            player.getUUID(),
            wandPos,
            direction,
            spell,
            player.level().getGameTime(),
            MAX_CAST_TRACK_TIME
        ));
    }
    
    /**
     * Checks for projectile-to-projectile collisions.
     * Called each server tick.
     */
    public static void checkProjectileCollisions(ServerLevel level) {
        if (!Config.enableSpellClashEffects()) {
            return;
        }
        
        if (level == null) {
            return;
        }
        
        // Get all projectiles
        List<Projectile> projectiles = level.getEntitiesOfClass(Projectile.class, 
            new AABB(-1000, -1000, -1000, 1000, 1000, 1000));
        
        // Check for collisions (simple distance check for now)
        for (int i = 0; i < projectiles.size(); i++) {
            Projectile p1 = projectiles.get(i);
            if (p1.isRemoved() || !isSpellProjectile(p1)) {
                continue;
            }
            
            for (int j = i + 1; j < projectiles.size(); j++) {
                Projectile p2 = projectiles.get(j);
                if (p2.isRemoved() || !isSpellProjectile(p2)) {
                    continue;
                }
                
                // Check if projectiles are close enough to collide
                double distance = p1.position().distanceTo(p2.position());
                if (distance < 1.0) {
                    // Collision detected
                    Player player1 = getProjectileCaster(p1);
                    Player player2 = getProjectileCaster(p2);
                    
                    if (player1 != null && player2 != null && player1 != player2) {
                        Vec3 collisionPoint = p1.position().add(p2.position()).scale(0.5);
                        Vec3 wand1Pos = getWandPosition(player1);
                        Vec3 wand2Pos = getWandPosition(player2);
                        
                        if (wand1Pos != null && wand2Pos != null) {
                            SpellClashVisuals.createClashEffect(level, wand1Pos, wand2Pos, 
                                collisionPoint, null, null, 1.0);
                        }
                        
                        // Remove projectiles
                        p1.discard();
                        p2.discard();
                    }
                }
            }
        }
    }
    
    /**
     * Checks for duel clashes (two players casting at each other).
     * Called each server tick.
     */
    public static void checkDuelClashes(ServerLevel level) {
        if (!Config.enableSpellClashEffects()) {
            return;
        }
        
        if (level == null) {
            return;
        }
        
        double clashRange = Config.getSpellClashRange();
        List<ActiveSpellCast> casts = new ArrayList<>(activeCasts.values());
        
        for (int i = 0; i < casts.size(); i++) {
            ActiveSpellCast cast1 = casts.get(i);
            Player player1 = level.getPlayerByUUID(cast1.playerId());
            if (player1 == null || !player1.isAlive()) {
                continue;
            }
            
            Vec3 wand1Pos = cast1.wandPosition();
            
            for (int j = i + 1; j < casts.size(); j++) {
                ActiveSpellCast cast2 = casts.get(j);
                Player player2 = level.getPlayerByUUID(cast2.playerId());
                if (player2 == null || !player2.isAlive() || player1 == player2) {
                    continue;
                }
                
                Vec3 wand2Pos = cast2.wandPosition();
                
                // Check distance
                double distance = wand1Pos.distanceTo(wand2Pos);
                if (distance > clashRange) {
                    continue;
                }
                
                // Check if players are looking at each other
                Vec3 dir1 = cast1.direction();
                Vec3 dir2 = cast2.direction();
                Vec3 toPlayer2 = wand2Pos.subtract(wand1Pos).normalize();
                Vec3 toPlayer1 = wand1Pos.subtract(wand2Pos).normalize();
                
                double dot1 = dir1.dot(toPlayer2);
                double dot2 = dir2.dot(toPlayer1);
                
                // Both players must be looking at each other (angle check)
                if (dot1 > 0.7 && dot2 > 0.7) {
                    // Duel clash detected
                    Vec3 collisionPoint = wand1Pos.add(wand2Pos).scale(0.5);
                    
                    SpellClashVisuals.createClashEffect(level, wand1Pos, wand2Pos, 
                        collisionPoint, cast1.spell(), cast2.spell(), 1.0);
                }
            }
        }
    }
    
    /**
     * Updates ongoing clash effects.
     */
    public static void updateClashEffects(ServerLevel level) {
        activeClashes.removeIf(clash -> {
            clash.tick();
            if (clash.isExpired()) {
                return true;
            }
            SpellClashVisuals.updateClashEffect(level, clash);
            return false;
        });
    }
    
    /**
     * Cleans up expired spell cast tracking.
     */
    public static void cleanupExpiredCasts(ServerLevel level) {
        if (level == null) {
            return;
        }
        
        long currentTime = level.getGameTime();
        activeCasts.entrySet().removeIf(entry -> entry.getValue().isExpired(currentTime));
    }
    
    /**
     * Checks if a projectile is a spell projectile.
     */
    private static boolean isSpellProjectile(Projectile projectile) {
        // For now, check if it's a fireball (can be extended with tags/data components)
        return projectile.getType() == net.minecraft.world.entity.EntityType.SMALL_FIREBALL;
    }
    
    /**
     * Gets the caster of a projectile.
     */
    private static Player getProjectileCaster(Projectile projectile) {
        Entity owner = projectile.getOwner();
        if (owner instanceof Player player) {
            return player;
        }
        return null;
    }
    
    /**
     * Gets the wand position for a player.
     */
    private static Vec3 getWandPosition(Player player) {
        if (player == null) {
            return null;
        }
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        return eyePos.add(lookVec.scale(0.5));
    }
}

