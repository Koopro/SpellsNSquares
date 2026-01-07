package at.koopro.spells_n_squares.features.contracts;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages contract storage and retrieval.
 * Contracts are stored in memory per player.
 */
public final class ContractStorage {
    private static final Map<UUID, List<ContractData>> PLAYER_CONTRACTS = new ConcurrentHashMap<>();
    
    private ContractStorage() {
        // Utility class - prevent instantiation
    }

    /**
     * Adds a contract to storage.
     *
     * @param contract The contract to add
     */
    public static void addContract(ContractData contract) {
        DevLogger.logStateChange(ContractStorage.class, "addContract",
            "Adding contract " + contract.contractId() + " between " + contract.creatorName() + " and " + contract.targetName());
        
        // Add to creator's contracts
        PLAYER_CONTRACTS.computeIfAbsent(contract.creatorId(), k -> new ArrayList<>()).add(contract);
        
        // Add to target's contracts
        PLAYER_CONTRACTS.computeIfAbsent(contract.targetId(), k -> new ArrayList<>()).add(contract);
    }

    /**
     * Gets all contracts for a player.
     *
     * @param playerId The player's UUID
     * @return List of contracts involving this player
     */
    public static List<ContractData> getContractsForPlayer(UUID playerId) {
        return PLAYER_CONTRACTS.getOrDefault(playerId, new ArrayList<>());
    }

    /**
     * Gets pending contracts (not accepted) for a player.
     *
     * @param playerId The player's UUID
     * @return List of pending contracts
     */
    public static List<ContractData> getPendingContractsForPlayer(UUID playerId) {
        return getContractsForPlayer(playerId).stream()
            .filter(contract -> !contract.isAccepted() && contract.targetId().equals(playerId))
            .toList();
    }

    /**
     * Gets a contract by ID.
     *
     * @param contractId The contract ID
     * @return Optional containing the contract, or empty if not found
     */
    public static Optional<ContractData> getContract(UUID contractId) {
        return PLAYER_CONTRACTS.values().stream()
            .flatMap(List::stream)
            .filter(contract -> contract.contractId().equals(contractId))
            .findFirst();
    }

    /**
     * Updates a contract in storage.
     *
     * @param contract The updated contract
     */
    public static void updateContract(ContractData contract) {
        DevLogger.logStateChange(ContractStorage.class, "updateContract",
            "Updating contract " + contract.contractId());
        
        // Remove old contract
        removeContract(contract.contractId());
        
        // Add updated contract
        addContract(contract);
    }

    /**
     * Removes a contract from storage.
     *
     * @param contractId The contract ID to remove
     */
    public static void removeContract(UUID contractId) {
        PLAYER_CONTRACTS.values().forEach(contracts -> 
            contracts.removeIf(contract -> contract.contractId().equals(contractId))
        );
    }

    /**
     * Clears all contracts for a player (e.g., on logout).
     *
     * @param playerId The player's UUID
     */
    public static void clearPlayerContracts(UUID playerId) {
        PLAYER_CONTRACTS.remove(playerId);
        // Also remove from other players' contract lists
        PLAYER_CONTRACTS.values().forEach(contracts ->
            contracts.removeIf(contract -> 
                contract.creatorId().equals(playerId) || contract.targetId().equals(playerId)
            )
        );
    }
}

