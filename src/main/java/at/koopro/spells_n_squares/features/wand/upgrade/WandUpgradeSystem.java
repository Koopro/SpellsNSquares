package at.koopro.spells_n_squares.features.wand.upgrade;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import at.koopro.spells_n_squares.features.wand.core.WandData;
import at.koopro.spells_n_squares.features.wand.core.WandDataHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages wand upgrades and enhancements.
 * Allows players to upgrade wands with materials to improve their properties.
 */
public final class WandUpgradeSystem {
    
    private static final Map<UUID, Map<Identifier, WandUpgradeData>> PLAYER_WAND_UPGRADES = new ConcurrentHashMap<>();
    
    private WandUpgradeSystem() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Represents wand upgrade data.
     */
    public record WandUpgradeData(
        int upgradeLevel,
        float powerBonus,
        float efficiencyBonus,
        float durabilityBonus,
        Set<String> upgradeTypes
    ) {
        public static WandUpgradeData DEFAULT = new WandUpgradeData(0, 0.0f, 0.0f, 0.0f, new HashSet<>());
        
        public WandUpgradeData {
            upgradeLevel = Math.max(0, Math.min(10, upgradeLevel));
            powerBonus = Math.max(0.0f, Math.min(2.0f, powerBonus));
            efficiencyBonus = Math.max(0.0f, Math.min(1.0f, efficiencyBonus));
            durabilityBonus = Math.max(0.0f, Math.min(2.0f, durabilityBonus));
            
            if (upgradeTypes == null) {
                upgradeTypes = new HashSet<>();
            }
        }
    }
    
    /**
     * Gets upgrade data for a wand.
     * 
     * @param wand The wand item stack
     * @return Upgrade data, or default if not upgraded
     */
    public static WandUpgradeData getUpgradeData(ItemStack wand) {
        if (wand == null || wand.isEmpty()) {
            return WandUpgradeData.DEFAULT;
        }
        
        WandData.WandDataComponent wandData = WandDataHelper.getWandData(wand);
        if (wandData == null || !wandData.hasOwner()) {
            return WandUpgradeData.DEFAULT;
        }
        
        try {
            UUID ownerId = UUID.fromString(wandData.ownerId());
            Map<Identifier, WandUpgradeData> upgrades = PLAYER_WAND_UPGRADES.get(ownerId);
            if (upgrades == null) {
                return WandUpgradeData.DEFAULT;
            }
            
            // Use wand's unique identifier (core + wood combination)
            Identifier wandId = Identifier.fromNamespaceAndPath("spells_n_squares", 
                wandData.coreId() + "_" + wandData.woodId());
            
            return upgrades.getOrDefault(wandId, WandUpgradeData.DEFAULT);
        } catch (IllegalArgumentException e) {
            return WandUpgradeData.DEFAULT;
        }
    }
    
    /**
     * Applies an upgrade to a wand.
     * 
     * @param wand The wand item stack
     * @param upgradeType The type of upgrade
     * @param bonusValue The bonus value to apply
     * @return true if upgrade was applied
     */
    public static boolean applyUpgrade(ItemStack wand, String upgradeType, float bonusValue) {
        if (wand == null || wand.isEmpty() || upgradeType == null) {
            return false;
        }
        
        WandData.WandDataComponent wandData = WandDataHelper.getWandData(wand);
        if (wandData == null || !wandData.hasOwner()) {
            return false;
        }
        
        try {
            UUID ownerId = UUID.fromString(wandData.ownerId());
            Identifier wandId = Identifier.fromNamespaceAndPath("spells_n_squares",
                wandData.coreId() + "_" + wandData.woodId());
            
            Map<Identifier, WandUpgradeData> upgrades = PLAYER_WAND_UPGRADES.computeIfAbsent(ownerId, k -> new ConcurrentHashMap<>());
            WandUpgradeData current = upgrades.getOrDefault(wandId, WandUpgradeData.DEFAULT);
            
            WandUpgradeData updated;
            Set<String> newTypes = new HashSet<>(current.upgradeTypes());
            newTypes.add(upgradeType);
            
            switch (upgradeType.toLowerCase()) {
                case "power":
                    updated = new WandUpgradeData(
                        current.upgradeLevel() + 1,
                        current.powerBonus() + bonusValue,
                        current.efficiencyBonus(),
                        current.durabilityBonus(),
                        newTypes
                    );
                    break;
                case "efficiency":
                    updated = new WandUpgradeData(
                        current.upgradeLevel() + 1,
                        current.powerBonus(),
                        current.efficiencyBonus() + bonusValue,
                        current.durabilityBonus(),
                        newTypes
                    );
                    break;
                case "durability":
                    updated = new WandUpgradeData(
                        current.upgradeLevel() + 1,
                        current.powerBonus(),
                        current.efficiencyBonus(),
                        current.durabilityBonus() + bonusValue,
                        newTypes
                    );
                    break;
                default:
                    return false;
            }
            
            upgrades.put(wandId, updated);
            
            DevLogger.logStateChange(WandUpgradeSystem.class, "applyUpgrade",
                "Wand: " + wandId + ", Upgrade: " + upgradeType + ", Level: " + updated.upgradeLevel());
            
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * Gets the power multiplier for a wand based on upgrades.
     * 
     * @param wand The wand item stack
     * @return Power multiplier (1.0 = no bonus)
     */
    public static float getPowerMultiplier(ItemStack wand) {
        WandUpgradeData upgrade = getUpgradeData(wand);
        return 1.0f + upgrade.powerBonus();
    }
    
    /**
     * Gets the efficiency multiplier for a wand based on upgrades.
     * 
     * @param wand The wand item stack
     * @return Efficiency multiplier (1.0 = no bonus, higher = more efficient)
     */
    public static float getEfficiencyMultiplier(ItemStack wand) {
        WandUpgradeData upgrade = getUpgradeData(wand);
        return 1.0f + upgrade.efficiencyBonus();
    }
}

