package at.koopro.spells_n_squares.features.contracts;

import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.UUID;

/**
 * Special contract type for Unbreakable Vows.
 * Unbreakable Vows enforce terms magically with severe consequences on violation.
 */
public final class UnbreakableVowContract {
    private UnbreakableVowContract() {
    }
    
    /**
     * Creates an Unbreakable Vow contract.
     * 
     * @param parties List of party UUIDs (typically 2 parties)
     * @param partyNames List of party names
     * @param terms The vow terms
     * @param conditions Optional conditions for the vow
     * @return The created contract component
     */
    public static ContractData.ContractComponent createUnbreakableVow(
            List<UUID> parties,
            List<String> partyNames,
            String terms,
            List<ContractData.ContractCondition> conditions) {
        
        return ContractSystem.createContract(
            "unbreakable_vow",
            parties,
            partyNames,
            terms,
            conditions,
            true // Mark as Unbreakable Vow
        );
    }
    
    /**
     * Creates an Unbreakable Vow between two players.
     * 
     * @param party1 First party
     * @param party2 Second party
     * @param terms The vow terms
     * @return The created contract component
     */
    public static ContractData.ContractComponent createUnbreakableVow(
            ServerPlayer party1,
            ServerPlayer party2,
            String terms) {
        
        return createUnbreakableVow(
            List.of(party1.getUUID(), party2.getUUID()),
            List.of(party1.getName().getString(), party2.getName().getString()),
            terms,
            List.of()
        );
    }
    
    /**
     * Checks if a contract is an Unbreakable Vow.
     */
    public static boolean isUnbreakableVow(ContractData.ContractComponent contract) {
        return contract.isUnbreakableVow() || 
               "unbreakable_vow".equals(contract.contractType());
    }
    
    /**
     * Validates that an Unbreakable Vow has the correct structure.
     * Unbreakable Vows typically require exactly 2 parties.
     */
    public static boolean isValidUnbreakableVow(ContractData.ContractComponent contract) {
        if (!isUnbreakableVow(contract)) {
            return false;
        }
        
        // Unbreakable Vows typically have 2 parties
        return contract.parties().size() >= 2;
    }
}



