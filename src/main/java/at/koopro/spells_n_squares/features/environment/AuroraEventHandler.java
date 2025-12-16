package at.koopro.spells_n_squares.features.environment;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles Aurora events - magical light shows in the sky.
 * Aurora events occur randomly at night and create beautiful particle effects.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class AuroraEventHandler {
    
    // Track active aurora events per level
    private static final Map<ServerLevel, AuroraEvent> activeAuroras = new HashMap<>();
    
    // Chance of aurora spawning per night tick (1 in 10000 = rare but possible)
    private static final int AURORA_SPAWN_CHANCE = 10000;
    
    // Aurora duration in ticks (5 minutes)
    private static final int AURORA_DURATION = 6000;
    
    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel().isClientSide() || !(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }
        
        // Update existing auroras
        AuroraEvent aurora = activeAuroras.get(serverLevel);
        if (aurora != null) {
            if (aurora.tick(serverLevel)) {
                // Aurora still active
                return;
            } else {
                // Aurora ended
                activeAuroras.remove(serverLevel);
                return;
            }
        }
        
        // Try to spawn new aurora at night
        long dayTime = serverLevel.getDayTime() % 24000;
        boolean isNight = dayTime >= 13000 && dayTime <= 23000;
        if (isNight && serverLevel.getRandom().nextInt(AURORA_SPAWN_CHANCE) == 0) {
            spawnAurora(serverLevel);
        }
    }
    
    /**
     * Spawns an aurora event in the level.
     */
    public static void spawnAurora(ServerLevel level) {
        if (activeAuroras.containsKey(level)) {
            return; // Already has an active aurora
        }
        
        AuroraEvent aurora = new AuroraEvent(level.getRandom());
        activeAuroras.put(level, aurora);
        
        // Notify players
        for (Player player : level.players()) {
            if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(net.minecraft.network.chat.Component.translatable("message.spells_n_squares.aurora.started"));
            }
        }
        
        // Play sound at world origin (spawn is typically at 0,0 or near players)
        if (!level.players().isEmpty()) {
            Player firstPlayer = level.players().get(0);
            level.playSound(null, firstPlayer.getX(), 200, firstPlayer.getZ(),
                SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.AMBIENT, 0.5f, 0.8f);
        }
    }
    
    /**
     * Represents an active aurora event.
     */
    private static class AuroraEvent {
        private int remainingTicks;
        private final net.minecraft.util.RandomSource random;
        
        public AuroraEvent(net.minecraft.util.RandomSource random) {
            this.remainingTicks = AURORA_DURATION;
            this.random = random;
        }
        
        /**
         * Ticks the aurora event and spawns particles.
         * @return true if aurora is still active, false if it ended
         */
        public boolean tick(ServerLevel level) {
            if (remainingTicks <= 0) {
                return false;
            }
            
            remainingTicks--;
            
            // Spawn aurora particles every 5 ticks
            if (remainingTicks % 5 == 0) {
                spawnAuroraParticles(level);
            }
            
            return true;
        }
        
        /**
         * Spawns aurora particles in the sky.
         */
        private void spawnAuroraParticles(ServerLevel level) {
            // Spawn particles in a wide arc across the sky
            int particleCount = 50;
            // Use first player's position or world origin
            double centerX = 0.0;
            double centerZ = 0.0;
            if (!level.players().isEmpty()) {
                Player firstPlayer = level.players().get(0);
                centerX = firstPlayer.getX();
                centerZ = firstPlayer.getZ();
            }
            double y = 200.0; // High in the sky
            
            for (int i = 0; i < particleCount; i++) {
                double angle = (i / (double) particleCount) * Math.PI * 2;
                double radius = 50.0 + random.nextDouble() * 30.0;
                double x = centerX + Math.cos(angle) * radius;
                double z = centerZ + Math.sin(angle) * radius;
                double offsetY = Math.sin(angle * 2) * 10.0; // Wave pattern
                
                // Use end rod particles with custom colors (Minecraft doesn't support colored particles directly,
                // but we can use different particle types for variety)
                net.minecraft.core.particles.ParticleOptions particle;
                int particleType = random.nextInt(4);
                switch (particleType) {
                    case 0 -> particle = ParticleTypes.END_ROD;
                    case 1 -> particle = ParticleTypes.ELECTRIC_SPARK;
                    case 2 -> particle = ParticleTypes.WAX_ON;
                    default -> particle = ParticleTypes.GLOW;
                }
                
                level.sendParticles(particle, x, y + offsetY, z, 1, 0.0, 0.0, 0.0, 0.0);
            }
        }
    }
    
    /**
     * Manually triggers an aurora event (for testing or special events).
     */
    public static void triggerAurora(ServerLevel level) {
        spawnAurora(level);
    }
}




