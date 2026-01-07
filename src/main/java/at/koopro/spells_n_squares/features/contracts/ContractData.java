package at.koopro.spells_n_squares.features.contracts;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.util.ExtraCodecs;

import java.util.UUID;

/**
 * Data class representing a magical contract between players.
 * 
 * <p>A contract is a binding agreement between two players with the following properties:
 * <ul>
 *   <li>Unique contract ID for identification</li>
 *   <li>Creator and target player information</li>
 *   <li>Contract text describing the agreement</li>
 *   <li>Creation timestamp</li>
 *   <li>Acceptance and completion status</li>
 * </ul>
 * 
 * <p>Contracts are stored using the {@link ContractStorage} class and can be serialized
 * using the provided {@link #CODEC}.
 * 
 * <p>Example usage:
 * <pre>{@code
 * ContractData contract = ContractData.create(
 *     creatorUUID, "Alice",
 *     targetUUID, "Bob",
 *     "I agree to trade 10 diamonds for 1 emerald"
 * );
 * ContractStorage.addContract(contract);
 * }</pre>
 * 
 * @param contractId Unique identifier for this contract
 * @param creatorId UUID of the player who created the contract
 * @param creatorName Display name of the creator
 * @param targetId UUID of the player who is the target/recipient
 * @param targetName Display name of the target
 * @param contractText The text content of the contract
 * @param creationTime Timestamp when the contract was created (milliseconds since epoch)
 * @param isAccepted Whether the target has accepted the contract
 * @param isCompleted Whether the contract has been completed
 * @since 1.0.0
 */
public record ContractData(
    UUID contractId,
    UUID creatorId,
    String creatorName,
    UUID targetId,
    String targetName,
    String contractText,
    long creationTime,
    boolean isAccepted,
    boolean isCompleted
) {
    public static final Codec<ContractData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            UUIDUtil.CODEC.fieldOf("contractId").forGetter(ContractData::contractId),
            UUIDUtil.CODEC.fieldOf("creatorId").forGetter(ContractData::creatorId),
            ExtraCodecs.NON_EMPTY_STRING.fieldOf("creatorName").forGetter(ContractData::creatorName),
            UUIDUtil.CODEC.fieldOf("targetId").forGetter(ContractData::targetId),
            ExtraCodecs.NON_EMPTY_STRING.fieldOf("targetName").forGetter(ContractData::targetName),
            ExtraCodecs.NON_EMPTY_STRING.fieldOf("contractText").forGetter(ContractData::contractText),
            Codec.LONG.fieldOf("creationTime").forGetter(ContractData::creationTime),
            Codec.BOOL.fieldOf("isAccepted").forGetter(ContractData::isAccepted),
            Codec.BOOL.fieldOf("isCompleted").forGetter(ContractData::isCompleted)
        ).apply(instance, ContractData::new)
    );

    /**
     * Creates a new contract with the given parameters.
     * 
     * <p>This factory method creates a new contract with:
     * <ul>
     *   <li>A randomly generated contract ID</li>
     *   <li>Current system time as creation timestamp</li>
     *   <li>Initial status: not accepted, not completed</li>
     * </ul>
     *
     * @param creatorId UUID of the player creating the contract (must not be null)
     * @param creatorName Display name of the creator (must not be null or empty)
     * @param targetId UUID of the target player (must not be null)
     * @param targetName Display name of the target (must not be null or empty)
     * @param contractText The contract text content (must not be null or empty)
     * @return A new ContractData instance with generated ID and current timestamp
     * @throws NullPointerException if any UUID parameter is null
     * @throws IllegalArgumentException if any string parameter is null or empty
     */
    public static ContractData create(UUID creatorId, String creatorName, UUID targetId, String targetName, String contractText) {
        return new ContractData(
            UUID.randomUUID(),
            creatorId,
            creatorName,
            targetId,
            targetName,
            contractText,
            System.currentTimeMillis(),
            false,
            false
        );
    }
}

