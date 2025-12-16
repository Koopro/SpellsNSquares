package at.koopro.spells_n_squares.features.wand;

import at.koopro.spells_n_squares.core.config.Config;
import at.koopro.spells_n_squares.core.fx.FXConfigHelper;
import at.koopro.spells_n_squares.core.fx.ParticleEffectRegistry;
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
            int baseCount = 10;
            int particleCount = FXConfigHelper.calculateParticleCountWithLOD(player, pos, baseCount);
            
            // Spawn particles along the cast direction
            for (int i = 0; i < particleCount; i++) {
                double offsetX = look.x * i * 0.2 + (level.getRandom().nextDouble() - 0.5) * 0.1;
                double offsetY = look.y * i * 0.2 + (level.getRandom().nextDouble() - 0.5) * 0.1;
                double offsetZ = look.z * i * 0.2 + (level.getRandom().nextDouble() - 0.5) * 0.1;
                
                Vec3 particlePos = new Vec3(pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ);
                
                if (FXConfigHelper.shouldRenderParticles(player, particlePos)) {
                    serverLevel.sendParticles(
                        particle,
                        particlePos.x, particlePos.y, particlePos.z,
                        1,
                        0.0, 0.0, 0.0,
                        0.0
                    );
                }
            }
            
            // Add core-specific burst effect
            spawnCoreBurst(serverLevel, player, core, pos);
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
            
            int baseCount = 15;
            int particleCount = FXConfigHelper.calculateParticleCountWithLOD(nearestPlayer, position, baseCount);
            
            ParticleOptions particle = getImpactParticle(core);
            serverLevel.sendParticles(
                particle,
                position.x, position.y, position.z,
                particleCount,
                0.3, 0.3, 0.3,
                0.1
            );
            
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
        
        level.sendParticles(
            glowParticle,
            position.x, position.y, position.z,
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
                    serverLevel.sendParticles(
                        particle,
                        x, y, z,
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
                    serverLevel.sendParticles(
                        particle,
                        x, y, z,
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

