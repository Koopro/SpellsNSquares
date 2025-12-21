package at.koopro.spells_n_squares.features.spell;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Entity Detection spell for finding nearby entities.
 */
public class EntityDetectionSpell implements Spell {
    
    private static final int COOLDOWN_BASE = 60; // 3 seconds
    private static final int DETECTION_RADIUS = 32; // blocks
    
    @Override
    public Identifier getId() {
        return SpellRegistry.spellId("entity_detection");
    }
    
    @Override
    public String getName() {
        return "Entity Detection";
    }
    
    @Override
    public String getDescription() {
        return "Reveals nearby entities";
    }
    
    @Override
    public int getCooldown() {
        return COOLDOWN_BASE;
    }
    
    @Override
    public boolean cast(Player player, Level level) {
        if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }
        
        Vec3 playerPos = player.position();
        AABB searchArea = new AABB(
            playerPos.x - DETECTION_RADIUS, playerPos.y - DETECTION_RADIUS, playerPos.z - DETECTION_RADIUS,
            playerPos.x + DETECTION_RADIUS, playerPos.y + DETECTION_RADIUS, playerPos.z + DETECTION_RADIUS
        );
        
        // Find nearby entities
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, searchArea,
            e -> e != player);
        
        if (entities.isEmpty()) {
            serverPlayer.sendSystemMessage(Component.literal("No entities detected nearby"));
            return false;
        }
        
        // Show detected entities
        serverPlayer.sendSystemMessage(Component.literal("Detected " + entities.size() + " entities:"));
        for (LivingEntity entity : entities) {
            double distance = playerPos.distanceTo(entity.position());
            serverPlayer.sendSystemMessage(Component.literal("  - " + entity.getName().getString() + 
                " (" + String.format("%.1f", distance) + " blocks away)"));
            
            // Visual effect on detected entity
            Vec3 entityPos = entity.position();
            serverLevel.sendParticles(ParticleTypes.END_ROD,
                entityPos.x, entityPos.y + entity.getEyeHeight(), entityPos.z,
                10, 0.2, 0.2, 0.2, 0.05);
        }
        
        return true;
    }
    
    @Override
    public float getVisualEffectIntensity() {
        return 0.5f;
    }
}

