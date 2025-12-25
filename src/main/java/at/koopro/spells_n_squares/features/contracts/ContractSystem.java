package at.koopro.spells_n_squares.features.contracts;

import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.*;

/**
 * Core contract management system.
 * Handles contract creation, storage, and retrieval.
 */
public final class ContractSystem {
    private ContractSystem() {
    }
    
    // Active contracts: contractId -> ContractComponent
    private static final Map<UUID, ContractData.ContractComponent> activeContracts = new HashMap<>();
    
    // Player contracts: playerId -> list of contractIds
    private static final Map<UUID, Set<UUID>> playerContracts = new HashMap<>();
    
    /**
     * Creates a new contract.
     * 
     * @param contractType The type of contract
     * @param parties List of party UUIDs
     * @param partyNames List of party names (must match parties order)
     * @param terms The contract terms/description
     * @param conditions List of contract conditions
     * @param isUnbreakableVow Whether this is an Unbreakable Vow
     * @return The created contract component
     */
    public static ContractData.ContractComponent createContract(
            String contractType,
            List<UUID> parties,
            List<String> partyNames,
            String terms,
            List<ContractData.ContractCondition> conditions,
            boolean isUnbreakableVow) {
        
        if (parties.size() != partyNames.size()) {
            throw new IllegalArgumentException("Parties and party names must have the same size");
        }
        
        UUID contractId = UUID.randomUUID();
        long creationTime = System.currentTimeMillis();
        
        ContractData.ContractComponent contract = new ContractData.ContractComponent(
            contractId,
            contractType,
            parties,
            partyNames,
            terms,
            conditions != null ? conditions : List.of(),
            creationTime,
            isUnbreakableVow,
            false,
            null
        );
        
        // Store contract
        activeContracts.put(contractId, contract);
        
        // Index by players
        for (UUID partyId : parties) {
            playerContracts.computeIfAbsent(partyId, k -> new HashSet<>()).add(contractId);
        }
        
        return contract;
    }
    
    /**
     * Gets a contract by ID.
     */
    public static ContractData.ContractComponent getContract(UUID contractId) {
        return activeContracts.get(contractId);
    }
    
    /**
     * Gets all contracts for a player.
     */
    public static List<ContractData.ContractComponent> getPlayerContracts(UUID playerId) {
        Set<UUID> contractIds = playerContracts.get(playerId);
        if (contractIds == null || contractIds.isEmpty()) {
            return List.of();
        }
        
        List<ContractData.ContractComponent> contracts = new ArrayList<>();
        for (UUID contractId : contractIds) {
            ContractData.ContractComponent contract = activeContracts.get(contractId);
            if (contract != null) {
                contracts.add(contract);
            }
        }
        return contracts;
    }
    
    /**
     * Gets active (non-violated) contracts for a player.
     */
    public static List<ContractData.ContractComponent> getActivePlayerContracts(UUID playerId) {
        return getPlayerContracts(playerId).stream()
            .filter(c -> !c.isViolated())
            .toList();
    }
    
    /**
     * Marks a contract as violated.
     */
    public static void markContractViolated(UUID contractId, UUID violatorId) {
        ContractData.ContractComponent contract = activeContracts.get(contractId);
        if (contract != null && !contract.isViolated()) {
            ContractData.ContractComponent violated = contract.markAsViolated(violatorId);
            activeContracts.put(contractId, violated);
            
            // Notify contract handler
            ContractHandler.onContractViolated(contractId, violatorId, violated);
        }
    }
    
    /**
     * Removes a contract (e.g., when fulfilled or cancelled).
     */
    public static void removeContract(UUID contractId) {
        ContractData.ContractComponent contract = activeContracts.remove(contractId);
        if (contract != null) {
            // Remove from player indexes
            for (UUID partyId : contract.parties()) {
                Set<UUID> contracts = playerContracts.get(partyId);
                if (contracts != null) {
                    contracts.remove(contractId);
                    if (contracts.isEmpty()) {
                        playerContracts.remove(partyId);
                    }
                }
            }
        }
    }
    
    /**
     * Checks if a contract is expired.
     */
    public static boolean isContractExpired(UUID contractId, long currentTime) {
        ContractData.ContractComponent contract = activeContracts.get(contractId);
        return contract != null && contract.isExpired(currentTime);
    }
    
    /**
     * Creates a contract item stack with the contract data.
     * TODO: Re-enable when CONTRACT item is implemented
     */
    public static ItemStack createContractItem(ContractData.ContractComponent contract) {
        // TODO: Re-enable when CONTRACT item is implemented
        return ItemStack.EMPTY; // Temporary placeholder
    }
    
    /**
     * Gets contract data from an item stack.
     */
    public static ContractData.ContractComponent getContractData(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ContractItem)) {
            return null;
        }
        return stack.get(ContractData.CONTRACT_DATA.get());
    }
    
    /**
     * Processes contract conditions and checks for violations.
     * Should be called periodically (e.g., on server tick).
     */
    public static void processContracts(ServerLevel level) {
        long currentTime = level.getGameTime();
        List<UUID> toRemove = new ArrayList<>();
        
        for (Map.Entry<UUID, ContractData.ContractComponent> entry : activeContracts.entrySet()) {
            UUID contractId = entry.getKey();
            ContractData.ContractComponent contract = entry.getValue();
            
            // Check for expired contracts
            if (contract.isExpired(currentTime)) {
                // Expired contracts are considered violated if they have time limits
                boolean hasTimeLimit = contract.conditions().stream()
                    .anyMatch(c -> c.type() == ContractData.ConditionType.TIME_LIMIT);
                if (hasTimeLimit) {
                    markContractViolated(contractId, null); // No specific violator for expiration
                } else {
                    toRemove.add(contractId);
                }
            }
            
            // Check other conditions
            ContractHandler.checkContractConditions(contractId, contract, level);
        }
        
        // Remove fulfilled/expired contracts
        for (UUID contractId : toRemove) {
            removeContract(contractId);
        }
    }
}











