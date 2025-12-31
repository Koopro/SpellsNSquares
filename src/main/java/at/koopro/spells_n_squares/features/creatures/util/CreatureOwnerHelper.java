package at.koopro.spells_n_squares.features.creatures.util;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Optional;
import java.util.UUID;

/**
 * Utility class for handling owner UUID serialization and deserialization.
 * Provides consistent owner data handling across all tamable creatures.
 */
public final class CreatureOwnerHelper {
    private static final String OWNER_KEY = "Owner";
    
    private CreatureOwnerHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Saves the owner UUID to the output data.
     * @param output The output data to write to
     * @param ownerId The owner UUID, wrapped in Optional
     */
    public static void saveOwner(ValueOutput output, Optional<UUID> ownerId) {
        if (ownerId.isPresent()) {
            output.putString(OWNER_KEY, ownerId.get().toString());
        }
    }
    
    /**
     * Loads the owner UUID from the input data.
     * @param input The input data to read from
     * @return Optional containing the owner UUID, or empty if not present or invalid
     */
    public static Optional<UUID> loadOwner(ValueInput input) {
        String ownerStr = input.getStringOr(OWNER_KEY, null);
        if (ownerStr != null) {
            try {
                return Optional.of(UUID.fromString(ownerStr));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}
















