package at.koopro.spells_n_squares.features.contracts;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.UUID;

/**
 * Handles contract enforcement and violations.
 * Monitors contract conditions and applies consequences for violations.
 */
public final class ContractHandler {
    private ContractHandler() {
    }
    
    /**
     * Called when a contract is violated.
     */
    public static void onContractViolated(UUID contractId, UUID violatorId, 
                                         ContractData.ContractComponent contract) {
        if (contract.isUnbreakableVow()) {
            enforceUnbreakableVow(contractId, violatorId, contract);
        } else {
            enforceRegularContract(contractId, violatorId, contract);
        }
    }
    
    /**
     * Enforces an Unbreakable Vow violation.
     * Unbreakable Vows have severe consequences (damage/death).
     */
    private static void enforceUnbreakableVow(UUID contractId, UUID violatorId,
                                            ContractData.ContractComponent contract) {
        if (violatorId == null) {
            return;
        }
        
        ServerLevel level = getServerLevel();
        if (level == null) {
            return;
        }
        
        ServerPlayer violator = level.getServer().getPlayerList().getPlayer(violatorId);
        if (violator == null) {
            return;
        }
        
        // Unbreakable Vow violation causes severe damage
        violator.sendSystemMessage(net.minecraft.network.chat.Component.translatable(
            "message.spells_n_squares.contract.vow_violated"));
        
        // Apply damage (50% of max health)
        float damage = violator.getMaxHealth() * 0.5f;
        violator.hurt(level.damageSources().magic(), damage);
        
        // If health is very low, could cause death
        if (violator.getHealth() <= 1.0f) {
            violator.sendSystemMessage(net.minecraft.network.chat.Component.translatable(
                "message.spells_n_squares.contract.vow_fatal"));
        }
    }
    
    /**
     * Enforces a regular contract violation.
     * Regular contracts have lighter consequences (reputation loss, etc.).
     */
    private static void enforceRegularContract(UUID contractId, UUID violatorId,
                                              ContractData.ContractComponent contract) {
        if (violatorId == null) {
            return;
        }
        
        ServerLevel level = getServerLevel();
        if (level == null) {
            return;
        }
        
        ServerPlayer violator = level.getServer().getPlayerList().getPlayer(violatorId);
        if (violator == null) {
            return;
        }
        
        violator.sendSystemMessage(net.minecraft.network.chat.Component.translatable(
            "message.spells_n_squares.contract.violated_warning"));
        
        // Apply reputation penalty to all other parties
        for (UUID partyId : contract.parties()) {
            if (!partyId.equals(violatorId)) {
                // Integrate with reputation system
                Player partyPlayer = level.getPlayerByUUID(partyId);
                if (partyPlayer instanceof ServerPlayer serverPartyPlayer) {
                    at.koopro.spells_n_squares.features.social.ReputationSystem.changePlayerReputation(
                        serverPartyPlayer, violatorId, -10);
                }
            }
        }
    }
    
    /**
     * Checks contract conditions and detects violations.
     */
    public static void checkContractConditions(UUID contractId, ContractData.ContractComponent contract,
                                              ServerLevel level) {
        long currentTime = level.getGameTime();
        
        // Check time limit conditions
        for (ContractData.ContractCondition condition : contract.conditions()) {
            if (condition.type() == ContractData.ConditionType.TIME_LIMIT) {
                if (condition.expiryTime() > 0 && currentTime > condition.expiryTime()) {
                    // Time limit exceeded - find responsible party
                    // For now, mark as violated without specific violator
                    ContractSystem.markContractViolated(contractId, null);
                    return;
                }
            }
            
            // Check location requirements
            if (condition.type() == ContractData.ConditionType.LOCATION_REQUIREMENT && !condition.location().isEmpty()) {
                // Parse location string (format: "x,y,z" or "x,y,z,dimension")
                String[] parts = condition.location().split(",");
                if (parts.length >= 3) {
                    try {
                        double reqX = Double.parseDouble(parts[0]);
                        double reqY = Double.parseDouble(parts[1]);
                        double reqZ = Double.parseDouble(parts[2]);
                        double tolerance = parts.length > 3 ? Double.parseDouble(parts[3]) : 5.0; // Default 5 block tolerance
                        
                        // Check if all parties are at the required location
                        boolean allAtLocation = true;
                        for (UUID partyId : contract.parties()) {
                            Player partyPlayer = level.getPlayerByUUID(partyId);
                            if (partyPlayer == null || !(partyPlayer instanceof ServerPlayer)) {
                                allAtLocation = false;
                                break;
                            }
                            
                            double dist = partyPlayer.distanceToSqr(reqX, reqY, reqZ);
                            if (dist > tolerance * tolerance) {
                                allAtLocation = false;
                                break;
                            }
                        }
                        
                        if (!allAtLocation) {
                            // Not all parties at location - could mark as violated or just continue checking
                            // For now, we'll just skip this condition check
                        }
                    } catch (NumberFormatException e) {
                        // Invalid location format, skip
                    }
                }
            }
            
            // Check item requirements
            if (condition.type() == ContractData.ConditionType.ITEM_REQUIREMENT && !condition.itemId().isEmpty()) {
                // Parse item ID and check if parties have the item
                try {
                    net.minecraft.resources.Identifier itemId = net.minecraft.resources.Identifier.parse(condition.itemId());
                    java.util.Optional<net.minecraft.world.item.Item> itemOptional = 
                        net.minecraft.core.registries.BuiltInRegistries.ITEM.getOptional(itemId);
                    
                    if (itemOptional.isPresent()) {
                        net.minecraft.world.item.Item requiredItem = itemOptional.get();
                        // Check if all parties have the required item
                        boolean allHaveItem = true;
                        for (UUID partyId : contract.parties()) {
                            Player partyPlayer = level.getPlayerByUUID(partyId);
                            if (partyPlayer == null || !(partyPlayer instanceof ServerPlayer)) {
                                allHaveItem = false;
                                break;
                            }
                            
                            // Check inventory for the item
                            boolean hasItem = false;
                            for (int i = 0; i < partyPlayer.getInventory().getContainerSize(); i++) {
                                if (partyPlayer.getInventory().getItem(i).is(requiredItem)) {
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
                    }
                } catch (Exception e) {
                    // Invalid item ID format, skip
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
     * Gets the server level (helper method).
     * This should be called with a proper ServerLevel context.
     */
    private static ServerLevel getServerLevel() {
        // This method should not be used - always pass ServerLevel as parameter
        // Keeping for backward compatibility but should be removed
        return null;
    }
}











