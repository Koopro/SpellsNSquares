package at.koopro.spells_n_squares.features.organizations.base;

import java.util.Set;
import java.util.UUID;

/**
 * Interface for organization mission/session records.
 * All organization mission/session records should implement this interface.
 */
public interface OrganizationMission {
    /**
     * Gets the mission/session ID.
     * @return The mission/session ID
     */
    String getMissionId();
    
    /**
     * Gets the set of assigned member UUIDs.
     * @return The assigned member UUIDs
     */
    Set<UUID> getAssignedMembers();
}


