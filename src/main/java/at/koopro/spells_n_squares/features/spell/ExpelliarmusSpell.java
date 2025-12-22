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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Expelliarmus - The Disarming Charm that disarms opponents.
 */
public class ExpelliarmusSpell implements Spell {
    
    private static final int COOLDOWN = 60; // 3 seconds
    private static final double RANGE = 16.0; // blocks
    
    @Override
    public Identifier getId() {
        return SpellRegistry.spellId("expelliarmus");
    }
    
    @Override
    public String getName() {
        return "Expelliarmus";
    }
    
    @Override
    public String getDescription() {
        return "The Disarming Charm - disarms opponents and knocks items from their hands";
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
        
        Vec3 playerPos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        Vec3 targetPos = playerPos.add(lookVec.scale(RANGE));
        
        // Find entities in range
        AABB searchBox = new AABB(playerPos, targetPos).inflate(2.0);
        var entities = level.getEntitiesOfClass(LivingEntity.class, searchBox,
            entity -> entity != player && entity.isAlive() && !entity.isSpectator());
        
        boolean disarmed = false;
        for (LivingEntity target : entities) {
            // Check if target is looking roughly toward the player (within 90 degrees)
            Vec3 toTarget = target.position().subtract(playerPos).normalize();
            Vec3 targetLook = target.getLookAngle();
            double dot = toTarget.dot(targetLook);
            
            if (dot < -0.5) { // Target is facing away from player
                continue;
            }
            
            // Disarm main hand
            ItemStack mainHand = target.getMainHandItem();
            if (!mainHand.isEmpty()) {
                // Drop the item
                net.minecraft.world.entity.item.ItemEntity itemEntity = 
                    target.drop(mainHand, false, false);
                if (itemEntity != null) {
                    // Add velocity to the dropped item
                    Vec3 knockback = lookVec.scale(0.5).add(0, 0.3, 0);
                    itemEntity.setDeltaMovement(knockback);
                }
                target.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                disarmed = true;
            }
            
            // Knockback effect
            Vec3 knockback = lookVec.scale(0.4).add(0, 0.2, 0);
            target.setDeltaMovement(target.getDeltaMovement().add(knockback));
            target.hurtMarked = true;
            
            // Visual effect
            Vec3 entityPos = target.position().add(0, target.getBbHeight() / 2, 0);
            serverLevel.sendParticles(
                ParticleTypes.CRIT,
                entityPos.x, entityPos.y, entityPos.z,
                20,
                0.5, 0.5, 0.5,
                0.1
            );
            serverLevel.sendParticles(
                ParticleTypes.ENCHANT,
                entityPos.x, entityPos.y, entityPos.z,
                15,
                0.3, 0.3, 0.3,
                0.05
            );
        }
        
        if (disarmed) {
            // Audio feedback
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0f, 1.2f);
            
            // Visual effect at cast point
            Vec3 castPos = playerPos.add(lookVec.scale(1.0));
            serverLevel.sendParticles(
                ParticleTypes.ELECTRIC_SPARK,
                castPos.x, castPos.y, castPos.z,
                10,
                0.2, 0.2, 0.2,
                0.05
            );
            
            return true;
        }
        
        return false;
    }
    
    @Override
    public float getVisualEffectIntensity() {
        return 0.6f;
    }
}



