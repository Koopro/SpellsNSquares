package at.koopro.spells_n_squares.features.spell.utility;

import at.koopro.spells_n_squares.core.fx.ParticlePool;
import at.koopro.spells_n_squares.core.fx.patterns.SpellFxPatterns;
import at.koopro.spells_n_squares.core.registry.ParticleEffectRegistry;
import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import at.koopro.spells_n_squares.features.spell.base.Spell;
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
                
                // Enhanced visual effect with water mist
                Vec3 entityPos = target.position().add(0, target.getBbHeight() / 2, 0);
                
                ParticleEffectRegistry.ParticleEffectTemplate mistTemplate = 
                    ParticleEffectRegistry.get(Identifier.fromNamespaceAndPath("spells_n_squares", "water_mist"));
                if (mistTemplate != null) {
                    mistTemplate.spawn(serverLevel, entityPos, 1.5);
                }
                
                ParticlePool.queueParticle(
                    serverLevel,
                    ParticleTypes.SPLASH,
                    entityPos,
                    20,
                    0.5, 0.5, 0.5,
                    0.1
                );
                
                // Add droplets effect
                ParticleEffectRegistry.ParticleEffectTemplate dropletsTemplate = 
                    ParticleEffectRegistry.get(Identifier.fromNamespaceAndPath("spells_n_squares", "water_droplets"));
                if (dropletsTemplate != null) {
                    dropletsTemplate.spawn(serverLevel, entityPos, 1.0);
                }
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
            if (stateBelow.canOcclude() && state.isAir() && level.isEmptyBlock(blockPos)) {
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
            
            // Enhanced visual effect along the path using beam pattern
            Vec3 castStart = playerPos.add(lookVec.scale(0.5));
            Vec3 castEnd = playerPos.add(lookVec.scale(RANGE));
            
            SpellFxPatterns.beam()
                .from(castStart)
                .to(castEnd)
                .particle(ParticleTypes.SPLASH)
                .count(20)
                .segmentLength(0.4)
                .jaggedness(0.05)
                .play(serverLevel);
            
            // Add bubbles along the path
            for (int i = 0; i < RANGE; i += 2) {
                Vec3 effectPos = playerPos.add(lookVec.scale(i));
                ParticlePool.queueParticle(
                    serverLevel,
                    ParticleTypes.BUBBLE,
                    effectPos,
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

















