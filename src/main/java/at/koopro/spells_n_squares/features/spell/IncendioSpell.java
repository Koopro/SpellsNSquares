package at.koopro.spells_n_squares.features.spell;

import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Incendio - The Fire-Making Spell that creates fire and damages entities.
 */
public class IncendioSpell implements Spell {
    
    private static final int COOLDOWN = 60; // 3 seconds
    private static final double RANGE = 16.0; // blocks
    private static final float DAMAGE = 4.0f; // 2 hearts
    
    @Override
    public Identifier getId() {
        return SpellRegistry.spellId("incendio");
    }
    
    @Override
    public String getName() {
        return "Incendio";
    }
    
    @Override
    public String getDescription() {
        return "The Fire-Making Spell - creates fire and damages enemies";
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
        
        boolean hit = false;
        
            // Damage and set fire to entities
            for (LivingEntity target : entities) {
                target.hurt(level.damageSources().magic(), DAMAGE);
                target.setRemainingFireTicks(Math.max(target.getRemainingFireTicks(), 100)); // 5 seconds of fire (100 ticks)
            
            hit = true;
            
            // Visual effect
            Vec3 entityPos = target.position().add(0, target.getBbHeight() / 2, 0);
            serverLevel.sendParticles(
                ParticleTypes.FLAME,
                entityPos.x, entityPos.y, entityPos.z,
                30,
                0.5, 0.5, 0.5,
                0.1
            );
            serverLevel.sendParticles(
                ParticleTypes.LAVA,
                entityPos.x, entityPos.y, entityPos.z,
                10,
                0.3, 0.3, 0.3,
                0.05
            );
        }
        
        // Create fire blocks along the path
        int fireBlocks = 0;
        for (int i = 1; i <= (int)RANGE; i += 2) {
            Vec3 checkPos = playerPos.add(lookVec.scale(i));
            BlockPos blockPos = BlockPos.containing(checkPos);
            BlockPos blockPosBelow = blockPos.below();
            
            BlockState stateBelow = level.getBlockState(blockPosBelow);
            BlockState state = level.getBlockState(blockPos);
            
            // Place fire if there's a solid block below and air above
            if (stateBelow.isSolid() && state.isAir() && level.isEmptyBlock(blockPos)) {
                if (level.setBlock(blockPos, Blocks.FIRE.defaultBlockState(), 3)) {
                    fireBlocks++;
                    if (fireBlocks >= 5) break; // Limit to 5 fire blocks
                }
            }
        }
        
        if (hit || fireBlocks > 0) {
            // Audio feedback
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.0f, 1.2f);
            
            // Visual effect along the path
            for (int i = 0; i < RANGE; i += 2) {
                Vec3 effectPos = playerPos.add(lookVec.scale(i));
                serverLevel.sendParticles(
                    ParticleTypes.FLAME,
                    effectPos.x, effectPos.y, effectPos.z,
                    3,
                    0.2, 0.2, 0.2,
                    0.05
                );
            }
            
            return true;
        }
        
        return false;
    }
    
    @Override
    public float getVisualEffectIntensity() {
        return 0.8f;
    }
}



