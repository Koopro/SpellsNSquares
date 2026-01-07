package at.koopro.spells_n_squares.features.wand.visual;

import at.koopro.spells_n_squares.core.config.Config;
import at.koopro.spells_n_squares.core.fx.FXConfigHelper;
import at.koopro.spells_n_squares.core.fx.ParticlePool;
import at.koopro.spells_n_squares.core.fx.patterns.SpellFxPatterns;
import at.koopro.spells_n_squares.core.registry.ParticleEffectRegistry;
import at.koopro.spells_n_squares.features.wand.core.WandDataHelper;
import at.koopro.spells_n_squares.features.wand.registry.WandCore;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Handles visual effects for wands based on their core type.
 */
public final class WandVisualEffects {
    private WandVisualEffects() {
    }
    
    /**
     * Spawns particle trail effects when casting a spell.
     */
    public static void spawnCastTrail(Level level, Player player, ItemStack wand) {
        if (level.isClientSide() || !Config.areWandParticlesEnabled()) {
            return; // Only spawn on server and if enabled
        }
        
        WandCore core = WandDataHelper.getCore(wand);
        if (core == null) {
            return;
        }
        
        ParticleOptions particle = getTrailParticle(core);
        Vec3 pos = player.position().add(0, player.getEyeHeight(), 0);
        Vec3 look = player.getLookAngle();
        
        if (level instanceof ServerLevel serverLevel) {
            // Use beam pattern for enhanced trail effect
            Vec3 trailStart = pos.add(look.scale(0.3));
            Vec3 trailEnd = pos.add(look.scale(8.0));
            
            SpellFxPatterns.beam()
                .from(trailStart)
                .to(trailEnd)
                .particle(particle)
                .count(15)
                .segmentLength(0.3)
                .jaggedness(0.05)
                .play(serverLevel);
            
            // Add core-specific burst effect
            spawnCoreBurst(serverLevel, player, core, pos);
            
            // Use wand trail templates
            Identifier trailCoreId = Identifier.fromNamespaceAndPath("spells_n_squares", "wand_cast_trail_core");
            Identifier trailSparksId = Identifier.fromNamespaceAndPath("spells_n_squares", "wand_cast_trail_sparks");
            
            ParticleEffectRegistry.ParticleEffectTemplate trailCore = ParticleEffectRegistry.get(trailCoreId);
            ParticleEffectRegistry.ParticleEffectTemplate trailSparks = ParticleEffectRegistry.get(trailSparksId);
            
            if (trailCore != null) {
                trailCore.spawn(serverLevel, trailStart, 1.0);
            }
            if (trailSparks != null) {
                trailSparks.spawn(serverLevel, trailStart, 0.8);
            }
        }
    }
    
    /**
     * Spawns a core-specific burst effect on spell cast.
     */
    private static void spawnCoreBurst(ServerLevel level, Player player, WandCore core, Vec3 position) {
        Identifier effectId = switch (core) {
            case PHOENIX_FEATHER -> Identifier.fromNamespaceAndPath("spells_n_squares", "golden_burst");
            case DRAGON_HEARTSTRING -> Identifier.fromNamespaceAndPath("spells_n_squares", "fire_burst");
            case UNICORN_HAIR -> Identifier.fromNamespaceAndPath("spells_n_squares", "silver_mist");
        };
        
        ParticleEffectRegistry.ParticleEffectTemplate template = ParticleEffectRegistry.get(effectId);
        if (template != null) {
            double multiplier = FXConfigHelper.getEffectiveParticleMultiplier();
            template.spawn(level, position, multiplier);
        }
    }
    
    /**
     * Spawns impact particles when a spell hits.
     */
    public static void spawnImpactEffect(Level level, Vec3 position, ItemStack wand) {
        if (level.isClientSide() || !Config.areWandParticlesEnabled()) {
            return;
        }
        
        WandCore core = WandDataHelper.getCore(wand);
        if (core == null) {
            return;
        }
        
        if (level instanceof ServerLevel serverLevel) {
            // Check distance before spawning
            Player nearestPlayer = level.getNearestPlayer(position.x, position.y, position.z, 
                Config.getMaxParticleDistance(), false);
            if (nearestPlayer == null) {
                return; // Too far from any player
            }
            
            // Use burst pattern for impact effect
            SpellFxPatterns.burst()
                .center(position)
                .particle(getImpactParticle(core))
                .count(15)
                .radius(0.3)
                .speed(0.1)
                .play(serverLevel);
            
            // Add multi-layer effect for high quality
            if (FXConfigHelper.useHighQualityEffects()) {
                spawnImpactLayers(serverLevel, position, core, nearestPlayer);
            }
        }
    }
    
    /**
     * Spawns additional impact layers for high-quality effects.
     */
    private static void spawnImpactLayers(ServerLevel level, Vec3 position, WandCore core, Player player) {
        int layerCount = FXConfigHelper.calculateParticleCountWithLOD(player, position, 5);
        
        ParticleOptions glowParticle = switch (core) {
            case PHOENIX_FEATHER -> ParticleTypes.END_ROD;
            case DRAGON_HEARTSTRING -> ParticleTypes.FLAME;
            case UNICORN_HAIR -> ParticleTypes.ELECTRIC_SPARK;
        };
        
        ParticlePool.queueParticle(
            level,
            glowParticle,
            position,
            layerCount,
            0.2, 0.2, 0.2,
            0.05
        );
    }
    
    /**
     * Spawns charged/ready glow particles around the wand.
     */
    public static void spawnChargedGlow(Level level, Player player, ItemStack wand) {
        if (level.isClientSide() || !Config.areWandParticlesEnabled()) {
            return;
        }
        
        WandCore core = WandDataHelper.getCore(wand);
        if (core == null) {
            return;
        }
        
        // Only show glow if wand is attuned
        if (!WandDataHelper.isAttuned(wand)) {
            return;
        }
        
        ParticleOptions particle = getGlowParticle(core);
        Vec3 pos = player.position().add(0, player.getEyeHeight() - 0.2, 0);
        Vec3 look = player.getLookAngle();
        Vec3 right = look.cross(new Vec3(0, 1, 0)).normalize();
        
        if (level instanceof ServerLevel serverLevel) {
            int baseCount = 5;
            int particleCount = FXConfigHelper.calculateParticleCount(baseCount);
            
            // Spawn particles around the wand tip
            for (int i = 0; i < particleCount; i++) {
                double angle = (i / (double) particleCount) * Math.PI * 2;
                double radius = 0.2;
                double x = pos.x + look.x * 0.5 + right.x * radius * Math.cos(angle);
                double y = pos.y + look.y * 0.5;
                double z = pos.z + look.z * 0.5 + right.z * radius * Math.cos(angle);
                
                Vec3 particlePos = new Vec3(x, y, z);
                if (FXConfigHelper.shouldRenderParticles(player, particlePos)) {
                    ParticlePool.queueParticle(
                        serverLevel,
                        particle,
                        particlePos,
                        1,
                        0.0, 0.0, 0.0,
                        0.0
                    );
                }
            }
        }
    }
    
    /**
     * Spawns aura particles around a wand (ambient effect).
     */
    public static void spawnWandAura(Level level, Player player, ItemStack wand) {
        if (level.isClientSide() || !Config.areEnvironmentalEffectsEnabled()) {
            return;
        }
        
        WandCore core = WandDataHelper.getCore(wand);
        if (core == null) {
            return;
        }
        
        if (level instanceof ServerLevel serverLevel) {
            Vec3 pos = player.position().add(0, player.getEyeHeight() - 0.1, 0);
            ParticleOptions particle = getGlowParticle(core);
            
            // Spawn subtle aura particles
            int auraCount = FXConfigHelper.calculateParticleCount(2);
            for (int i = 0; i < auraCount; i++) {
                double angle = level.getRandom().nextDouble() * Math.PI * 2;
                double radius = 0.3 + level.getRandom().nextDouble() * 0.2;
                double x = pos.x + Math.cos(angle) * radius;
                double y = pos.y + (level.getRandom().nextDouble() - 0.5) * 0.2;
                double z = pos.z + Math.sin(angle) * radius;
                
                Vec3 particlePos = new Vec3(x, y, z);
                if (FXConfigHelper.shouldRenderParticles(player, particlePos)) {
                    ParticlePool.queueParticle(
                        serverLevel,
                        particle,
                        particlePos,
                        1,
                        0.0, 0.0, 0.0,
                        0.0
                    );
                }
            }
        }
    }
    
    /**
     * Gets the trail particle type for a wand core.
     */
    private static ParticleOptions getTrailParticle(WandCore core) {
        return switch (core) {
            case PHOENIX_FEATHER -> ParticleTypes.END_ROD; // Golden sparks
            case DRAGON_HEARTSTRING -> ParticleTypes.FLAME; // Red flames
            case UNICORN_HAIR -> ParticleTypes.ELECTRIC_SPARK; // Silver mist/sparks
        };
    }
    
    /**
     * Gets the impact particle type for a wand core.
     */
    private static ParticleOptions getImpactParticle(WandCore core) {
        return switch (core) {
            case PHOENIX_FEATHER -> ParticleTypes.TOTEM_OF_UNDYING; // Golden burst
            case DRAGON_HEARTSTRING -> ParticleTypes.EXPLOSION; // Red explosion
            case UNICORN_HAIR -> ParticleTypes.ENCHANT; // Silver enchant
        };
    }
    
    /**
     * Gets the glow particle type for a wand core.
     */
    private static ParticleOptions getGlowParticle(WandCore core) {
        return switch (core) {
            case PHOENIX_FEATHER -> ParticleTypes.END_ROD;
            case DRAGON_HEARTSTRING -> ParticleTypes.FLAME;
            case UNICORN_HAIR -> ParticleTypes.ELECTRIC_SPARK;
        };
    }
}

