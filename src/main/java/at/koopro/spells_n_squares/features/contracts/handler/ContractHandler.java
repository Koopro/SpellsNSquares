package at.koopro.spells_n_squares.features.contracts.handler;

import at.koopro.spells_n_squares.core.util.TTLCache;
import at.koopro.spells_n_squares.features.contracts.ContractConstants;
import at.koopro.spells_n_squares.features.contracts.ItemIdParser;
import at.koopro.spells_n_squares.features.contracts.LocationParser;
import at.koopro.spells_n_squares.features.contracts.data.ContractData;
import at.koopro.spells_n_squares.features.contracts.system.ContractSystem;
import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

import java.util.List;
import java.util.UUID;

/**
 * Handles contract enforcement and violations.
 * Monitors contract conditions and applies consequences for violations.
 */
public final class ContractHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    // Cache for parsed locations to avoid re-parsing (TTL: 5 minutes)
    private static final long LOCATION_CACHE_TTL = 5 * 60 * 1000; // 5 minutes
    private static final TTLCache<String, LocationParser.ParsedLocation> locationCache = 
        new TTLCache<>(LOCATION_CACHE_TTL);
    
    // Cache for parsed items to avoid re-parsing (TTL: 10 minutes)
    private static final long ITEM_CACHE_TTL = 10 * 60 * 1000; // 10 minutes
    private static final TTLCache<String, ItemIdParser.ParsedItem> itemCache = 
        new TTLCache<>(ITEM_CACHE_TTL);
    
    private ContractHandler() {
    }
    
    /**
     * Called when a contract is violated.
     * 
     * @param level The server level (required for enforcement)
     */
    public static void onContractViolated(UUID contractId, UUID violatorId, 
                                         ContractData.ContractComponent contract,
                                         ServerLevel level) {
        if (level == null) {
            return;
        }
        
        if (contract.isUnbreakableVow()) {
            enforceUnbreakableVow(contractId, violatorId, contract, level);
        } else {
            enforceRegularContract(contractId, violatorId, contract, level);
        }
    }
    
    /**
     * Enforces an Unbreakable Vow violation.
     * Unbreakable Vows have severe consequences (damage/death).
     */
    private static void enforceUnbreakableVow(UUID contractId, UUID violatorId,
                                            ContractData.ContractComponent contract,
                                            ServerLevel level) {
        if (violatorId == null) {
            LOGGER.warn("Cannot enforce Unbreakable Vow: violatorId is null for contract {}", contractId);
            return;
        }
        
        try {
            ServerPlayer violator = level.getServer().getPlayerList().getPlayer(violatorId);
            if (violator == null) {
                LOGGER.warn("Cannot enforce Unbreakable Vow: violator {} not found for contract {}",
                    violatorId, contractId);
                return;
            }
            
            // Unbreakable Vow violation causes severe damage
            violator.sendSystemMessage(net.minecraft.network.chat.Component.translatable(
                "message.spells_n_squares.contract.vow_violated"));
            
            // Apply damage (percentage of max health)
            float damage = violator.getMaxHealth() * ContractConstants.UNBREAKABLE_VOW_DAMAGE_MULTIPLIER;
            violator.hurt(level.damageSources().magic(), damage);
            
            // If health is very low, could cause death
            if (violator.getHealth() <= ContractConstants.VOW_FATAL_HEALTH_THRESHOLD) {
                violator.sendSystemMessage(net.minecraft.network.chat.Component.translatable(
                    "message.spells_n_squares.contract.vow_fatal"));
            }
        } catch (Exception e) {
            LOGGER.error("Error enforcing Unbreakable Vow for contract {} and violator {}: {}",
                contractId, violatorId, e.getMessage(), e);
        }
    }
    
    /**
     * Enforces a regular contract violation.
     * Regular contracts have lighter consequences (reputation loss, etc.).
     */
    private static void enforceRegularContract(UUID contractId, UUID violatorId,
                                              ContractData.ContractComponent contract,
                                              ServerLevel level) {
        if (violatorId == null) {
            LOGGER.warn("Cannot enforce regular contract: violatorId is null for contract {}", contractId);
            return;
        }
        
        try {
            ServerPlayer violator = level.getServer().getPlayerList().getPlayer(violatorId);
            if (violator == null) {
                LOGGER.warn("Cannot enforce regular contract: violator {} not found for contract {}",
                    violatorId, contractId);
                return;
            }
            
            violator.sendSystemMessage(net.minecraft.network.chat.Component.translatable(
                "message.spells_n_squares.contract.violated_warning"));
            
            // Apply reputation penalty to all other parties
            for (UUID partyId : contract.parties()) {
                if (!partyId.equals(violatorId)) {
                    try {
                        // Integrate with reputation system
                        Player partyPlayer = level.getPlayerByUUID(partyId);
                        ServerPlayer serverPartyPlayer = at.koopro.spells_n_squares.core.util.PlayerValidationUtils.asServerPlayer(partyPlayer);
                        if (serverPartyPlayer != null) {
                            at.koopro.spells_n_squares.features.social.ReputationSystem.changePlayerReputation(
                                serverPartyPlayer, violatorId, ContractConstants.REGULAR_CONTRACT_REPUTATION_PENALTY);
                        }
                    } catch (Exception e) {
                        LOGGER.error("Error applying reputation penalty to party {} for contract {}: {}",
                            partyId, contractId, e.getMessage(), e);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error enforcing regular contract {} for violator {}: {}",
                contractId, violatorId, e.getMessage(), e);
        }
    }
    
    /**
     * Checks contract conditions and detects violations.
     */
    public static void checkContractConditions(UUID contractId, ContractData.ContractComponent contract,
                                              ServerLevel level) {
        if (contractId == null || contract == null || level == null) {
            LOGGER.warn("Invalid parameters for checkContractConditions: contractId={}, contract={}, level={}",
                contractId, contract, level);
            return;
        }
        
        try {
            long currentTime = level.getGameTime();
            
            // Check time limit conditions
            for (ContractData.ContractCondition condition : contract.conditions()) {
                if (condition.type() == ContractData.ConditionType.TIME_LIMIT) {
                    if (condition.expiryTime() > 0 && currentTime > condition.expiryTime()) {
                        // Time limit exceeded - find responsible party
                        // For now, mark as violated without specific violator
                        ContractSystem.markContractViolated(contractId, null, level);
                        return;
                    }
                }
                
                // Check location requirements
                if (condition.type() == ContractData.ConditionType.LOCATION_REQUIREMENT && !condition.location().isEmpty()) {
                    // Use cache to avoid re-parsing
                    LocationParser.ParsedLocation parsedLocation = locationCache.getOrCompute(
                        condition.location(),
                        key -> {
                            LocationParser.ParsedLocation parsed = LocationParser.parse(key);
                            return (parsed != null && parsed.isValid()) ? parsed : null;
                        }
                    );
                    
                    if (parsedLocation != null && parsedLocation.isValid()) {
                        checkLocationRequirement(contract, level, parsedLocation);
                    } else {
                        LOGGER.warn("Failed to parse location requirement for contract {}: '{}'",
                            contractId, condition.location());
                    }
                }
                
                // Check item requirements
                if (condition.type() == ContractData.ConditionType.ITEM_REQUIREMENT && !condition.itemId().isEmpty()) {
                    // Use cache to avoid re-parsing
                    ItemIdParser.ParsedItem parsedItem = itemCache.getOrCompute(
                        condition.itemId(),
                        key -> {
                            ItemIdParser.ParsedItem parsed = ItemIdParser.parse(key);
                            return (parsed != null && parsed.isValid()) ? parsed : null;
                        }
                    );
                    
                    if (parsedItem != null && parsedItem.isValid()) {
                        checkItemRequirement(contract, level, parsedItem);
                    } else {
                        LOGGER.warn("Failed to parse item requirement for contract {}: '{}'",
                            contractId, condition.itemId());
                    }
                }
                
                // Check action requirements
                if (condition.type() == ContractData.ConditionType.ACTION_REQUIREMENT && !condition.actionId().isEmpty()) {
                    // Action requirements would need an action tracking system
                    // For now, this is a placeholder that can be extended
                    // Actions could be tracked in a separate system (e.g., ActionTracker)
                    // This would check if the required action (identified by actionId) has been performed
                    // by the contract parties
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error checking contract conditions for contract {}: {}",
                contractId, e.getMessage(), e);
        }
    }
    
    /**
     * Checks if all parties are at the required location.
     */
    private static void checkLocationRequirement(ContractData.ContractComponent contract, ServerLevel level,
                                                 LocationParser.ParsedLocation location) {
        try {
            // Check if all parties are at the required location
            boolean allAtLocation = true;
            for (UUID partyId : contract.parties()) {
                Player partyPlayer = level.getPlayerByUUID(partyId);
                if (partyPlayer == null || !at.koopro.spells_n_squares.core.util.PlayerValidationUtils.isServerPlayer(partyPlayer)) {
                    allAtLocation = false;
                    break;
                }
                
                double dist = partyPlayer.distanceToSqr(location.x(), location.y(), location.z());
                if (dist > location.tolerance() * location.tolerance()) {
                    allAtLocation = false;
                    break;
                }
            }
            
            if (!allAtLocation) {
                // Not all parties at location - could mark as violated or just continue checking
                // For now, we'll just skip this condition check
            }
        } catch (Exception e) {
            LOGGER.error("Error checking location requirement for contract: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Checks if all parties have the required item.
     */
    private static void checkItemRequirement(ContractData.ContractComponent contract, ServerLevel level,
                                            ItemIdParser.ParsedItem parsedItem) {
        try {
            // Check if all parties have the required item
            boolean allHaveItem = true;
            for (UUID partyId : contract.parties()) {
                Player partyPlayer = level.getPlayerByUUID(partyId);
                if (partyPlayer == null || !at.koopro.spells_n_squares.core.util.PlayerValidationUtils.isServerPlayer(partyPlayer)) {
                    allHaveItem = false;
                    break;
                }
                
                // Check inventory for the item
                // Validate inventory size before iterating
                int inventorySize = partyPlayer.getInventory().getContainerSize();
                if (inventorySize <= 0) {
                    allHaveItem = false;
                    break;
                }
                
                boolean hasItem = false;
                for (int i = 0; i < inventorySize; i++) {
                    if (partyPlayer.getInventory().getItem(i).is(parsedItem.item())) {
                        hasItem = true;
                        break;
                    }
                }
                
                if (!hasItem) {
                    allHaveItem = false;
                    break;
                }
            }
            
            if (!allHaveItem) {
                // Not all parties have required item
                // Could mark as violated or continue checking
            }
        } catch (Exception e) {
            LOGGER.error("Error checking item requirement for contract: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Called when a player takes damage.
     * Checks if it's related to a contract violation.
     */
    public static void onPlayerDamage(ServerPlayer player, DamageSource source, float amount) {
        // Check if player has violated any Unbreakable Vows
        List<ContractData.ContractComponent> contracts = ContractSystem.getActivePlayerContracts(player.getUUID());
        for (ContractData.ContractComponent contract : contracts) {
            if (contract.isUnbreakableVow() && contract.isViolated()) {
                // Additional damage from vow violation
                // Check if damage is from magic (vow enforcement)
                if (source.type().msgId().contains("magic")) {
                    // Already handled by enforceUnbreakableVow
                    return;
                }
            }
        }
    }
    
    /**
     * Clears all caches (useful for testing or when world is unloaded).
     */
    public static void clearCaches() {
        locationCache.clear();
        itemCache.clear();
    }
}








