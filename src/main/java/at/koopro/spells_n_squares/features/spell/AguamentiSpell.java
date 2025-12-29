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
 * Aguamenti - The Water-Making Spell that creates water and extinguishes fire.
 */
public class AguamentiSpell implements Spell {
    
    private static final int COOLDOWN = 40; // 2 seconds
    private static final double RANGE = 12.0; // blocks
    
    @Override
    public Identifier getId() {
        return SpellRegistry.spellId("aguamenti");
    }
    
    @Override
    public String getName() {
        return "Aguamenti";
    }
    
    @Override
    public String getDescription() {
        return "The Water-Making Spell - creates water and extinguishes fire";
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
        
        // Extinguish fire on entities
        AABB searchBox = new AABB(playerPos, targetPos).inflate(2.0);
        var entities = level.getEntitiesOfClass(LivingEntity.class, searchBox,
            entity -> entity != player && entity.isAlive() && !entity.isSpectator());
        
        boolean extinguished = false;
        
        for (LivingEntity target : entities) {
            if (target.isOnFire()) {
                target.clearFire();
                extinguished = true;
                
                // Visual effect
                Vec3 entityPos = target.position().add(0, target.getBbHeight() / 2, 0);
                serverLevel.sendParticles(
                    ParticleTypes.SPLASH,
                    entityPos.x, entityPos.y, entityPos.z,
                    20,
                    0.5, 0.5, 0.5,
                    0.1
                );
            }
        }
        
        // Create water blocks along the path
        int waterBlocks = 0;
        for (int i = 2; i <= (int)RANGE; i += 2) {
            Vec3 checkPos = playerPos.add(lookVec.scale(i));
            BlockPos blockPos = BlockPos.containing(checkPos);
            BlockPos blockPosBelow = blockPos.below();
            
            BlockState stateBelow = level.getBlockState(blockPosBelow);
            BlockState state = level.getBlockState(blockPos);
            
            // Place water if there's a solid block below and air above
            if (stateBelow.isSolid() && state.isAir() && level.isEmptyBlock(blockPos)) {
                if (level.setBlock(blockPos, Blocks.WATER.defaultBlockState(), 3)) {
                    waterBlocks++;
                    if (waterBlocks >= 3) break; // Limit to 3 water blocks
                }
            }
            
            // Extinguish fire blocks
            if (state.is(Blocks.FIRE)) {
                level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
                extinguished = true;
            }
        }
        
        if (extinguished || waterBlocks > 0) {
            // Audio feedback
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BUCKET_EMPTY, SoundSource.PLAYERS, 0.8f, 1.0f);
            
            // Visual effect along the path
            for (int i = 0; i < RANGE; i += 2) {
                Vec3 effectPos = playerPos.add(lookVec.scale(i));
                serverLevel.sendParticles(
                    ParticleTypes.SPLASH,
                    effectPos.x, effectPos.y, effectPos.z,
                    5,
                    0.2, 0.2, 0.2,
                    0.05
                );
                serverLevel.sendParticles(
                    ParticleTypes.BUBBLE,
                    effectPos.x, effectPos.y, effectPos.z,
                    3,
                    0.1, 0.1, 0.1,
                    0.02
                );
            }
            
            return true;
        }
        
        return false;
    }
    
    @Override
    public float getVisualEffectIntensity() {
        return 0.5f;
    }
}












