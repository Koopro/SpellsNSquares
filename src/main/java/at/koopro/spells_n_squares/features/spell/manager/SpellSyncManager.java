package at.koopro.spells_n_squares.features.spell.manager;

import at.koopro.spells_n_squares.core.data.PlayerDataHelper;
import at.koopro.spells_n_squares.core.util.collection.CollectionFactory;
import at.koopro.spells_n_squares.core.util.player.PlayerDataSyncUtils;
import at.koopro.spells_n_squares.features.spell.network.SpellCooldownSyncPayload;
import at.koopro.spells_n_squares.features.spell.network.SpellSlotsSyncPayload;
import at.koopro.spells_n_squares.services.spell.internal.SpellData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Map;

/**
 * Manages syncing spell data from server to client.
 * Handles spell slots and cooldown synchronization.
 */
public final class SpellSyncManager {
    private SpellSyncManager() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Syncs spell slots to the client for a server player.
     * @param serverPlayer The server player
     */
    public static void syncSpellSlotsToClient(ServerPlayer serverPlayer) {
        // Get from PlayerDataComponent
        SpellData spellData = PlayerDataHelper.getSpellData(serverPlayer);
        Identifier[] slots = spellData.slots().toArray();
        
        // Convert array to list for payload
        List<Identifier> slotList = CollectionFactory.createList(SpellManager.MAX_SLOTS);
        for (int i = 0; i < SpellManager.MAX_SLOTS; i++) {
            slotList.add(slots[i]);
        }
        
        SpellSlotsSyncPayload payload = new SpellSlotsSyncPayload(slotList);
        PlayerDataSyncUtils.syncToClient(serverPlayer, payload, "spell slots");
    }
    
    /**
     * Syncs cooldowns to the client for a server player.
     * @param serverPlayer The server player
     */
    public static void syncCooldownsToClient(ServerPlayer serverPlayer) {
        // Get from PlayerDataComponent
        SpellData spellData = PlayerDataHelper.getSpellData(serverPlayer);
        Map<Identifier, Integer> cooldowns = spellData.cooldowns();
        
        // Create a copy to avoid concurrent modification issues
        Map<Identifier, Integer> cooldownsCopy = CollectionFactory.createMap();
        cooldownsCopy.putAll(cooldowns);
        SpellCooldownSyncPayload payload = new SpellCooldownSyncPayload(cooldownsCopy);
        PlayerDataSyncUtils.syncToClient(serverPlayer, payload, "spell cooldowns");
    }
}




