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
 * Evanesco - The Vanishing Spell that makes objects disappear.
 */
public class EvanescoSpell implements Spell {
    
    private static final int COOLDOWN = 40; // 2 seconds
    private static final double RANGE = 8.0;
    
    @Override
    public Identifier getId() {
        return SpellRegistry.spellId("evanesco");
    }
    
    @Override
    public String getName() {
        return "Evanesco";
    }
    
    @Override
    public String getDescription() {
        return "The Vanishing Spell - makes objects disappear";
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
        
        // Find item entities in range
        AABB searchBox = new AABB(playerPos, targetPos).inflate(2.0);
        var items = level.getEntitiesOfClass(ItemEntity.class, searchBox);
        
        boolean vanished = false;
        for (ItemEntity itemEntity : items) {
            // Make the item disappear
            itemEntity.remove(net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
            vanished = true;
            
            // Visual effect
            Vec3 itemPos = itemEntity.position();
            serverLevel.sendParticles(
                ParticleTypes.POOF,
                itemPos.x, itemPos.y, itemPos.z,
                15,
                0.3, 0.3, 0.3,
                0.05
            );
        }
        
        if (vanished) {
            // Audio feedback
            level.playSound(null, targetPos.x, targetPos.y, targetPos.z,
                SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.6f, 1.5f);
            
            // Visual effect at target area
            serverLevel.sendParticles(
                ParticleTypes.POOF,
                targetPos.x, targetPos.y, targetPos.z,
                20,
                1.0, 1.0, 1.0,
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

