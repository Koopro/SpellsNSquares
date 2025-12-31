package at.koopro.spells_n_squares.features.organizations.base;

import java.util.UUID;

/**
 * Interface for organization member records.
 * All organization member records should implement this interface.
 */
public interface OrganizationMember {
    /**
     * Gets the player's UUID.
     * @return The player UUID
     */
    UUID getPlayerId();
}


