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
                // TODO: Integrate with reputation system
                // ReputationSystem.changeReputation(partyId, violatorId, -10);
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
            if (condition.type() == ContractData.ConditionType.LOCATION_REQUIREMENT) {
                // TODO: Check if parties are at required location
            }
            
            // Check item requirements
            if (condition.type() == ContractData.ConditionType.ITEM_REQUIREMENT) {
                // TODO: Check if parties have required items
            }
            
            // Check action requirements
            if (condition.type() == ContractData.ConditionType.ACTION_REQUIREMENT) {
                // TODO: Check if required actions have been performed
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
     */
    private static ServerLevel getServerLevel() {
        // TODO: Get server level from a proper context
        // For now, this is a placeholder
        return null;
    }
}



