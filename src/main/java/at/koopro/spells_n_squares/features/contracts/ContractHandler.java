package at.koopro.spells_n_squares.features.contracts;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.function.Predicate;

/**
 * Handles contract validation, completion checking, and system integration.
 * Provides methods to check contract requirements including reputation,
 * location, items, and actions.
 */
public final class ContractHandler {
    
    private ContractHandler() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Contract requirement types for validation.
     */
    public enum RequirementType {
        REPUTATION,
        LOCATION,
        ITEM,
        ACTION
    }
    
    /**
     * Represents a contract requirement.
     */
    public record ContractRequirement(
        RequirementType type,
        String requirementId,
        Object requirementData
    ) {}
    
    /**
     * Represents location requirement data.
     */
    public record LocationRequirement(
        ResourceKey<Level> dimension,
        BlockPos position,
        double radius
    ) {}
    
    /**
     * Represents item requirement data.
     */
    public record ItemRequirement(
        Item item,
        int count,
        Predicate<ItemStack> customCheck
    ) {}
    
    /**
     * Represents action requirement data.
     */
    public record ActionRequirement(
        String actionId,
        Map<String, Object> actionData
    ) {}
    
    /**
     * Validates if a contract can be created between two players.
     * 
     * @param creator The player creating the contract
     * @param target The target player
     * @param requirements List of requirements for the contract
     * @return Validation result with success status and error message if failed
     */
    public static ValidationResult validateContractCreation(
            ServerPlayer creator, 
            ServerPlayer target, 
            List<ContractRequirement> requirements) {
        
        if (creator == null || target == null) {
            return ValidationResult.failure("Creator or target player is null");
        }
        
        if (creator.getUUID().equals(target.getUUID())) {
            return ValidationResult.failure("Cannot create contract with yourself");
        }
        
        // Check reputation requirements
        for (ContractRequirement req : requirements) {
            if (req.type() == RequirementType.REPUTATION) {
                ValidationResult repResult = checkReputationRequirement(creator, target, req);
                if (!repResult.success) {
                    return repResult;
                }
            }
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Checks if contract completion requirements are met.
     * 
     * @param contract The contract to check
     * @param player The player to check requirements for
     * @param requirements List of requirements
     * @param level The server level
     * @return Validation result
     */
    public static ValidationResult checkContractCompletion(
            ContractData contract,
            ServerPlayer player,
            List<ContractRequirement> requirements,
            ServerLevel level) {
        
        if (contract == null || player == null || level == null) {
            return ValidationResult.failure("Invalid contract, player, or level");
        }
        
        if (!contract.isAccepted()) {
            return ValidationResult.failure("Contract must be accepted before completion");
        }
        
        if (contract.isCompleted()) {
            return ValidationResult.failure("Contract is already completed");
        }
        
        // Check if player is involved in contract
        if (!contract.creatorId().equals(player.getUUID()) && 
            !contract.targetId().equals(player.getUUID())) {
            return ValidationResult.failure("Player is not involved in this contract");
        }
        
            // Check all requirements
        for (ContractRequirement req : requirements) {
            ValidationResult result = switch (req.type()) {
                case REPUTATION -> {
                    Player creatorPlayer = level.getPlayerByUUID(contract.creatorId());
                    Player targetPlayer = level.getPlayerByUUID(contract.targetId());
                    if (creatorPlayer instanceof ServerPlayer creator && 
                        targetPlayer instanceof ServerPlayer target) {
                        yield checkReputationRequirement(creator, target, req);
                    }
                    yield ValidationResult.failure("Creator or target player not found");
                }
                case LOCATION -> checkLocationRequirement(player, req, level);
                case ITEM -> checkItemRequirement(player, req);
                case ACTION -> checkActionRequirement(player, contract, req);
            };
            
            if (!result.success) {
                return result;
            }
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Checks reputation requirement.
     * Note: This is a placeholder that will integrate with ReputationSystem when available.
     */
    private static ValidationResult checkReputationRequirement(
            ServerPlayer creator, 
            ServerPlayer target, 
            ContractRequirement requirement) {
        
        // TODO: Integrate with ReputationSystem when available
        // For now, always pass reputation checks
        DevLogger.logMethodEntry(ContractHandler.class, "checkReputationRequirement",
            "creator=" + (creator != null ? creator.getName().getString() : "null") +
            ", target=" + (target != null ? target.getName().getString() : "null"));
        
        // Placeholder: Check if reputation system exists and validate
        // This will be enhanced when ReputationSystem is implemented
        return ValidationResult.success();
    }
    
    /**
     * Checks location requirement.
     */
    private static ValidationResult checkLocationRequirement(
            ServerPlayer player,
            ContractRequirement requirement,
            ServerLevel level) {
        
        if (requirement.requirementData() instanceof LocationRequirement locReq) {
            // Check dimension
            if (!level.dimension().equals(locReq.dimension())) {
                return ValidationResult.failure(
                    "Player must be in dimension: " + locReq.dimension());
            }
            
            // Check position within radius
            BlockPos playerPos = player.blockPosition();
            double distance = Math.sqrt(
                Math.pow(playerPos.getX() - locReq.position().getX(), 2) +
                Math.pow(playerPos.getY() - locReq.position().getY(), 2) +
                Math.pow(playerPos.getZ() - locReq.position().getZ(), 2)
            );
            
            if (distance > locReq.radius()) {
                return ValidationResult.failure(
                    "Player must be within " + locReq.radius() + " blocks of required location");
            }
            
            return ValidationResult.success();
        }
        
        return ValidationResult.failure("Invalid location requirement data");
    }
    
    /**
     * Checks item requirement.
     */
    private static ValidationResult checkItemRequirement(
            ServerPlayer player,
            ContractRequirement requirement) {
        
        if (requirement.requirementData() instanceof ItemRequirement itemReq) {
            int foundCount = 0;
            
            // Check inventory
            Inventory inventory = player.getInventory();
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack stack = inventory.getItem(i);
                if (!stack.isEmpty()) {
                    if (stack.getItem().equals(itemReq.item())) {
                        foundCount += stack.getCount();
                    }
                    
                    // Check custom predicate if provided
                    if (itemReq.customCheck() != null && itemReq.customCheck().test(stack)) {
                        foundCount += stack.getCount();
                    }
                }
            }
            
            if (foundCount < itemReq.count()) {
                var itemKey = BuiltInRegistries.ITEM.getResourceKey(itemReq.item());
                String itemName = itemKey.map(key -> key.toString())
                    .orElse("unknown");
                return ValidationResult.failure(
                    "Player must have at least " + itemReq.count() + 
                    " of item: " + itemName);
            }
            
            return ValidationResult.success();
        }
        
        return ValidationResult.failure("Invalid item requirement data");
    }
    
    /**
     * Checks action requirement.
     * Note: This is a placeholder that will track actions when action system is implemented.
     */
    private static ValidationResult checkActionRequirement(
            ServerPlayer player,
            ContractData contract,
            ContractRequirement requirement) {
        
        if (requirement.requirementData() instanceof ActionRequirement actionReq) {
            // TODO: Integrate with action tracking system when available
            // For now, this is a placeholder that always passes
            DevLogger.logMethodEntry(ContractHandler.class, "checkActionRequirement",
                "player=" + player.getName().getString() +
                ", actionId=" + actionReq.actionId());
            
            // Placeholder: Check if action has been performed
            // This will be enhanced when action tracking system is implemented
            return ValidationResult.success();
        }
        
        return ValidationResult.failure("Invalid action requirement data");
    }
    
    /**
     * Gets the server level from a player.
     * 
     * @param player The player
     * @return The server level, or null if not available
     */
    public static ServerLevel getServerLevel(Player player) {
        if (player == null) {
            return null;
        }
        
        if (player.level() instanceof ServerLevel serverLevel) {
            return serverLevel;
        }
        
        return null;
    }
    
    /**
     * Validation result for contract operations.
     */
    public static final class ValidationResult {
        public final boolean success;
        public final String errorMessage;
        
        private ValidationResult(boolean success, String errorMessage) {
            this.success = success;
            this.errorMessage = errorMessage;
        }
        
        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }
        
        public static ValidationResult failure(String errorMessage) {
            return new ValidationResult(false, errorMessage);
        }
    }
}

