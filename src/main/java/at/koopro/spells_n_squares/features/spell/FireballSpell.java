package at.koopro.spells_n_squares.features.spell;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Fireball spell that shoots a fireball forward.
 */
public class FireballSpell implements Spell {
    
    private static final int COOLDOWN = 40; // 2 seconds
    private static final double FIREBALL_SPEED = 0.5;
    
    @Override
    public Identifier getId() {
        return at.koopro.spells_n_squares.core.registry.SpellRegistry.spellId("fireball");
    }
    
    @Override
    public String getName() {
        return "Fireball";
    }
    
    @Override
    public String getDescription() {
        return "Shoots a fireball forward";
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
        Vec3 spawnPos = player.getEyePosition().add(lookVec.scale(1.5));
        
        // Create the fireball entity
        Entity fireball = EntityType.SMALL_FIREBALL.create(serverLevel, EntitySpawnReason.TRIGGERED);
        if (fireball != null) {
            fireball.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
            fireball.setDeltaMovement(
                lookVec.x * FIREBALL_SPEED, 
                lookVec.y * FIREBALL_SPEED, 
                lookVec.z * FIREBALL_SPEED
            );
            serverLevel.addFreshEntity(fireball);
            
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0f, 1.0f);
            
            return true;
        }
        
        return false;
    }
    
    @Override
    public float getVisualEffectIntensity() {
        return 0.7f;
    }
}
