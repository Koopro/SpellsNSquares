package at.koopro.spells_n_squares.features.environment.handler;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles full moon events - werewolf transformations, Mooncalf appearances, etc.
 * Full moon occurs every 8 Minecraft days (192,000 ticks).
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class FullMoonEventHandler {
    
    // Track if full moon is active per level
    private static final Map<ServerLevel, Boolean> fullMoonActive = new HashMap<>();
    
    // Track last full moon time per level
    // @deprecated Reserved for future use when tracking moon history is needed
    @SuppressWarnings("unused")
    private static final Map<ServerLevel, Long> lastFullMoonTime = new HashMap<>();
    
    // Full moon cycle: every 8 days = 192,000 ticks
    // @deprecated Reserved for future use when cycle tracking is needed
    @SuppressWarnings("unused")
    private static final long FULL_MOON_CYCLE = 192000L;
    
    // Full moon duration: 1 day = 24,000 ticks
    // @deprecated Reserved for future use when duration tracking is needed
    @SuppressWarnings("unused")
    private static final long FULL_MOON_DURATION = 24000L;
    
    // Moon phase offset (0 = new moon, 4 = full moon)
    private static final int FULL_MOON_PHASE = 4;
    
    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel().isClientSide() || !(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }
        
        long dayTime = serverLevel.getDayTime();
        long day = dayTime / 24000L;
        
        // Calculate moon phase (0-7, where 4 is full moon)
        int moonPhase = (int) (day % 8L);
        
        boolean isFullMoon = (moonPhase == FULL_MOON_PHASE);
        boolean wasFullMoon = fullMoonActive.getOrDefault(serverLevel, false);
        
        // Check if full moon just started
        if (isFullMoon && !wasFullMoon) {
            onFullMoonStart(serverLevel);
        }
        
        // Check if full moon just ended
        if (!isFullMoon && wasFullMoon) {
            onFullMoonEnd(serverLevel);
        }
        
        fullMoonActive.put(serverLevel, isFullMoon);
        
        // During full moon, spawn Mooncalves and create effects
        boolean isNight = dayTime >= 13000 && dayTime <= 23000;
        if (isFullMoon && isNight) {
            tickFullMoonEffects(serverLevel);
        }
    }
    
    /**
     * Called when full moon starts.
     */
    private static void onFullMoonStart(ServerLevel level) {
        // Notify players
        for (Player player : level.players()) {
            if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.full_moon.started"));
            }
        }
        
        // Play sound near players
        var players = level.players();
        if (!players.isEmpty()) {
            Player firstPlayer = players.get(0);
            level.playSound(null, firstPlayer.getX(), 200, firstPlayer.getZ(),
                SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.AMBIENT, 0.3f, 0.8f);
        }
        
        // Spawn some Mooncalves naturally during full moon
        long dayTime = level.getDayTime() % 24000;
        boolean isNight = dayTime >= 13000 && dayTime <= 23000;
        if (isNight) {
            spawnMooncalves(level);
        }
    }
    
    /**
     * Called when full moon ends.
     */
    private static void onFullMoonEnd(ServerLevel level) {
        // Notify players
        for (Player player : level.players()) {
            if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.full_moon.ended"));
            }
        }
    }
    
    /**
     * Ticks full moon effects during the night.
     */
    private static void tickFullMoonEffects(ServerLevel level) {
        // Spawn moon particles in the sky
        if (level.getGameTime() % 100 == 0) { // Every 5 seconds
            spawnMoonParticles(level);
        }
        
        // Occasionally spawn Mooncalves
        if (level.getRandom().nextInt(200) == 0) { // Rare chance
            spawnMooncalves(level);
        }
    }
    
    /**
     * Spawns moon particles in the sky.
     */
    private static void spawnMoonParticles(ServerLevel level) {
        // Use first player's position or world origin
        double centerX = 0.0;
        double centerZ = 0.0;
        var players = level.players();
        if (!players.isEmpty()) {
            Player firstPlayer = players.get(0);
            centerX = firstPlayer.getX();
            centerZ = firstPlayer.getZ();
        }
        double y = 200.0; // High in the sky
        
        // Spawn particles in a circle around the moon
        for (int i = 0; i < 20; i++) {
            double angle = (i / 20.0) * Math.PI * 2;
            double radius = 30.0 + level.getRandom().nextDouble() * 10.0;
            double x = centerX + Math.cos(angle) * radius;
            double z = centerZ + Math.sin(angle) * radius;
            
            level.sendParticles(ParticleTypes.END_ROD,
                x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
        }
    }
    
    /**
     * Spawns Mooncalves during full moon.
     * TODO: Re-enable when MOONCALF entity is implemented.
     */
    @SuppressWarnings("unused")
    private static void spawnMooncalves(ServerLevel level) {
        // Spawn 1-3 Mooncalves near players
        // int count = 1 + level.getRandom().nextInt(3);
        
        for (Player player : level.players()) {
            if (level.getRandom().nextFloat() < 0.3f) { // 30% chance per player
                // double x = player.getX() + (level.getRandom().nextDouble() - 0.5) * 20.0;
                // double y = player.getY();
                // double z = player.getZ() + (level.getRandom().nextDouble() - 0.5) * 20.0;
                
                // TODO: Re-enable when MOONCALF entity is implemented
                // MooncalfEntity mooncalf = ModEntities.MOONCALF.get().create(level, net.minecraft.world.entity.EntitySpawnReason.NATURAL);
                // if (mooncalf != null) {
                //     mooncalf.setPos(x, y, z);
                //     level.addFreshEntity(mooncalf);
                //     
                //     // Visual effect
                //     level.sendParticles(ParticleTypes.END_ROD,
                //         x, y + 0.5, z, 10, 0.5, 0.5, 0.5, 0.1);
                // }
            }
        }
    }
    
    /**
     * Checks if it's currently a full moon in the given level.
     */
    public static boolean isFullMoon(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }
        return fullMoonActive.getOrDefault(serverLevel, false);
    }
    
    /**
     * Gets the current moon phase (0-7, where 4 is full moon).
     */
    public static int getMoonPhase(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return 0;
        }
        long dayTime = serverLevel.getDayTime();
        long day = dayTime / 24000L;
        return (int) (day % 8L);
    }
}

















