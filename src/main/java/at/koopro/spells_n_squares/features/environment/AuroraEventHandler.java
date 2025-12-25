package at.koopro.spells_n_squares.features.environment;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
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
     * Manually triggers an aurora event (for testing or special events).
     */
    public static void triggerAurora(ServerLevel level) {
        spawnAurora(level);
    }
}












