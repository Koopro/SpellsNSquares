package at.koopro.spells_n_squares.features.contracts;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Data component for storing contract information.
 */
public final class ContractData {
    private ContractData() {
    }
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ContractComponent>> CONTRACT_DATA =
        DATA_COMPONENTS.register(
            "contract_data",
            () -> DataComponentType.<ContractComponent>builder()
                .persistent(ContractComponent.CODEC)
                .build()
        );
    
    /**
     * Contract condition types.
     */
    public enum ConditionType {
        TIME_LIMIT,
        LOCATION_REQUIREMENT,
        ITEM_REQUIREMENT,
        ACTION_REQUIREMENT
    }
    
    /**
     * Contract condition.
     */
    public record ContractCondition(
        ConditionType type,
        String description,
        long expiryTime,
        String location,
        String itemId,
        String actionId
    ) {
        public static final Codec<ContractCondition> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.STRING.xmap(
                    s -> ConditionType.valueOf(s),
                    ConditionType::name
                ).fieldOf("type").forGetter(ContractCondition::type),
                Codec.STRING.fieldOf("description").forGetter(ContractCondition::description),
                Codec.LONG.optionalFieldOf("expiryTime", 0L).forGetter(ContractCondition::expiryTime),
                Codec.STRING.optionalFieldOf("location", "").forGetter(ContractCondition::location),
                Codec.STRING.optionalFieldOf("itemId", "").forGetter(ContractCondition::itemId),
                Codec.STRING.optionalFieldOf("actionId", "").forGetter(ContractCondition::actionId)
            ).apply(instance, ContractCondition::new)
        );
    }
    
    /**
     * Component storing contract information.
     */
    public record ContractComponent(
        UUID contractId,
        String contractType,
        List<UUID> parties,
        List<String> partyNames,
        String terms,
        List<ContractCondition> conditions,
        long creationTime,
        boolean isUnbreakableVow,
        boolean isViolated,
        UUID violatorId
    ) {
        public static final Codec<ContractComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                UUIDUtil.CODEC.fieldOf("contractId").forGetter(ContractComponent::contractId),
                Codec.STRING.fieldOf("contractType").forGetter(ContractComponent::contractType),
                Codec.list(UUIDUtil.CODEC).fieldOf("parties").forGetter(ContractComponent::parties),
                Codec.list(Codec.STRING).fieldOf("partyNames").forGetter(ContractComponent::partyNames),
                Codec.STRING.fieldOf("terms").forGetter(ContractComponent::terms),
                Codec.list(ContractCondition.CODEC).optionalFieldOf("conditions", List.of()).forGetter(ContractComponent::conditions),
                Codec.LONG.fieldOf("creationTime").forGetter(ContractComponent::creationTime),
                Codec.BOOL.optionalFieldOf("isUnbreakableVow", false).forGetter(ContractComponent::isUnbreakableVow),
                Codec.BOOL.optionalFieldOf("isViolated", false).forGetter(ContractComponent::isViolated),
                UUIDUtil.CODEC.optionalFieldOf("violatorId").forGetter(c -> Optional.ofNullable(c.violatorId()))
            ).apply(instance, (contractId, contractType, parties, partyNames, terms, conditions, creationTime, isUnbreakableVow, isViolated, violatorIdOpt) -> 
                new ContractComponent(contractId, contractType, parties, partyNames, terms, conditions, creationTime, isUnbreakableVow, isViolated, violatorIdOpt.orElse(null)))
        );
        
        public ContractComponent markAsViolated(UUID violatorId) {
            return new ContractComponent(contractId, contractType, parties, partyNames, terms, conditions, creationTime, isUnbreakableVow, true, violatorId);
        }
        
        public boolean isExpired(long currentTime) {
            return conditions.stream()
                .anyMatch(c -> c.type() == ConditionType.TIME_LIMIT && c.expiryTime() > 0 && currentTime > c.expiryTime());
        }
        
        public boolean involvesParty(UUID playerId) {
            return parties.contains(playerId);
        }
    }
}















