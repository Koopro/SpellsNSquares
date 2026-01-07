package at.koopro.spells_n_squares.features.spell.manager;

import at.koopro.spells_n_squares.core.data.PlayerDataHelper;
import at.koopro.spells_n_squares.core.util.collection.CollectionFactory;
import at.koopro.spells_n_squares.services.spell.internal.SpellData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Manages spell cooldowns for players.
 * Handles setting, checking, ticking, and querying cooldowns.
 */
public final class SpellCooldownManager {
    // Per-player sync timers for batched cooldown synchronization (UUID -> tick count)
    // Thread safety: Accessed only from main server thread
    private static final Map<UUID, Integer> syncTimers = CollectionFactory.createMap();
    private static final int SYNC_INTERVAL = 20; // Sync every 1 second (20 ticks)
    
    private SpellCooldownManager() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Sets a cooldown for a spell for a player.
     * @param player The player
     * @param spellId The spell ID
     * @param ticks The cooldown duration in ticks
     */
    public static void setCooldown(Player player, Identifier spellId, int ticks) {
        if (spellId == null || ticks <= 0) {
            return;
        }
        
        // Get current spell data
        SpellData current = PlayerDataHelper.getSpellData(player);
        Map<Identifier, Integer> cooldowns = CollectionFactory.createMap();
        cooldowns.putAll(current.cooldowns());
        cooldowns.put(spellId, ticks);
        SpellData updated = current.withCooldowns(cooldowns);
        PlayerDataHelper.setSpellData(player, updated);
    }
    
    /**
     * Checks if a spell is on cooldown for a player.
     * @param player The player
     * @param spellId The spell ID
     * @return true if on cooldown
     */
    public static boolean isOnCooldown(Player player, Identifier spellId) {
        if (spellId == null) {
            return false;
        }
        
        // Get from PlayerDataComponent
        SpellData spellData = PlayerDataHelper.getSpellData(player);
        Integer remaining = spellData.cooldowns().get(spellId);
        return remaining != null && remaining > 0;
    }
    
    /**
     * Gets the remaining cooldown for a spell for a player.
     * @param player The player
     * @param spellId The spell ID
     * @return Remaining cooldown in ticks, or 0 if not on cooldown
     */
    public static int getRemainingCooldown(Player player, Identifier spellId) {
        if (spellId == null) {
            return 0;
        }
        
        // Get from PlayerDataComponent
        SpellData spellData = PlayerDataHelper.getSpellData(player);
        Integer remaining = spellData.cooldowns().get(spellId);
        return remaining != null ? Math.max(0, remaining) : 0;
    }
    
    /**
     * Ticks all cooldowns for a player. Call this every tick.
     * @param player The player
     */
    public static void tickCooldowns(Player player) {
        // Get current spell data
        SpellData current = PlayerDataHelper.getSpellData(player);
        Map<Identifier, Integer> cooldowns = CollectionFactory.createMap();
        cooldowns.putAll(current.cooldowns());
        if (cooldowns.isEmpty()) {
            return;
        }
        
        // Decrement all cooldowns and remove expired ones
        Iterator<Map.Entry<Identifier, Integer>> iterator = cooldowns.entrySet().iterator();
        boolean changed = false;
        while (iterator.hasNext()) {
            Map.Entry<Identifier, Integer> entry = iterator.next();
            int remaining = entry.getValue() - 1;
            if (remaining <= 0) {
                iterator.remove();
                changed = true;
            } else {
                entry.setValue(remaining);
                changed = true;
            }
        }
        
        // Update PlayerDataComponent if changed
        if (changed) {
            SpellData updated = current.withCooldowns(cooldowns);
            PlayerDataHelper.setSpellData(player, updated);
        }
        
        // Batch sync to client (only sync every SYNC_INTERVAL ticks)
        if (changed && player instanceof ServerPlayer serverPlayer) {
            UUID uuid = player.getUUID();
            int timer = syncTimers.getOrDefault(uuid, SYNC_INTERVAL);
            timer++;
            if (timer >= SYNC_INTERVAL) {
                SpellSyncManager.syncCooldownsToClient(serverPlayer);
                syncTimers.put(uuid, 0);
            } else {
                syncTimers.put(uuid, timer);
            }
        }
    }
}




