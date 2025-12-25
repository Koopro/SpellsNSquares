package at.koopro.spells_n_squares.features.spell;

import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Locomotor - A spell that moves objects through the air.
 */
public class LocomotorSpell implements Spell {
    
    private static final int COOLDOWN = 30; // 1.5 seconds
    private static final double RANGE = 12.0;
    private static final double MOVE_SPEED = 0.3;
    
    @Override
    public Identifier getId() {
        return SpellRegistry.spellId("locomotor");
    }
    
    @Override
    public String getName() {
        return "Locomotor";
    }
    
    @Override
    public String getDescription() {
        return "A spell that moves objects through the air toward you";
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
        
        Vec3 playerPos = player.position().add(0, player.getEyeHeight(), 0);
        
        // Find item entities in range
        AABB searchBox = new AABB(playerPos, playerPos).inflate(RANGE);
        var items = level.getEntitiesOfClass(ItemEntity.class, searchBox,
            item -> item.isAlive() && !item.hasPickUpDelay());
        
        boolean moved = false;
        for (ItemEntity itemEntity : items) {
            Vec3 itemPos = itemEntity.position();
            Vec3 direction = playerPos.subtract(itemPos).normalize();
            
            // Move item toward player
            Vec3 velocity = direction.scale(MOVE_SPEED);
            itemEntity.setDeltaMovement(velocity);
            itemEntity.hurtMarked = true;
            moved = true;
            
            // Visual effect
            serverLevel.sendParticles(
                ParticleTypes.ENCHANT,
                itemPos.x, itemPos.y, itemPos.z,
                5,
                0.2, 0.2, 0.2,
                0.02
            );
        }
        
        if (moved) {
            // Audio feedback
            level.playSound(null, playerPos.x, playerPos.y, playerPos.z,
                SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.5f, 1.2f);
            
            return true;
        }
        
        return false;
    }
    
    @Override
    public float getVisualEffectIntensity() {
        return 0.3f;
    }
}









