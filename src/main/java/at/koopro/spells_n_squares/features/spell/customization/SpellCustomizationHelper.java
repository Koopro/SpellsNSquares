package at.koopro.spells_n_squares.features.spell.customization;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Helper class for spell customization.
 * Allows players to modify spell parameters like power, range, duration, etc.
 */
public final class SpellCustomizationHelper {
    
    private static final Map<UUID, Map<Identifier, SpellCustomization>> PLAYER_CUSTOMIZATIONS = new ConcurrentHashMap<>();
    
    private SpellCustomizationHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Represents spell customization data.
     */
    public record SpellCustomization(
        float powerMultiplier,
        float rangeMultiplier,
        float durationMultiplier,
        float cooldownMultiplier,
        Map<String, String> customProperties
    ) {
        public static SpellCustomization DEFAULT = new SpellCustomization(1.0f, 1.0f, 1.0f, 1.0f, new HashMap<>());
        
        public SpellCustomization {
            // Clamp multipliers to reasonable ranges
            powerMultiplier = Math.max(0.5f, Math.min(2.0f, powerMultiplier));
            rangeMultiplier = Math.max(0.5f, Math.min(2.0f, rangeMultiplier));
            durationMultiplier = Math.max(0.5f, Math.min(2.0f, durationMultiplier));
            cooldownMultiplier = Math.max(0.5f, Math.min(2.0f, cooldownMultiplier));
            
            if (customProperties == null) {
                customProperties = new HashMap<>();
            }
        }
    }
    
    /**
     * Gets customization for a spell for a player.
     * 
     * @param player The player
     * @param spellId The spell ID
     * @return Customization data, or default if not customized
     */
    public static SpellCustomization getCustomization(Player player, Identifier spellId) {
        if (player == null || spellId == null) {
            return SpellCustomization.DEFAULT;
        }
        
        Map<Identifier, SpellCustomization> customizations = PLAYER_CUSTOMIZATIONS.get(player.getUUID());
        if (customizations == null) {
            return SpellCustomization.DEFAULT;
        }
        
        return customizations.getOrDefault(spellId, SpellCustomization.DEFAULT);
    }
    
    /**
     * Sets customization for a spell for a player.
     * 
     * @param player The player
     * @param spellId The spell ID
     * @param customization The customization data
     */
    public static void setCustomization(Player player, Identifier spellId, SpellCustomization customization) {
        if (player == null || spellId == null || customization == null) {
            return;
        }
        
        UUID playerId = player.getUUID();
        Map<Identifier, SpellCustomization> customizations = PLAYER_CUSTOMIZATIONS.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>());
        customizations.put(spellId, customization);
        
        DevLogger.logStateChange(SpellCustomizationHelper.class, "setCustomization",
            "Player: " + player.getName().getString() + ", Spell: " + spellId);
    }
    
    /**
     * Updates a specific multiplier for a spell.
     * 
     * @param player The player
     * @param spellId The spell ID
     * @param multiplierType The type of multiplier to update
     * @param value The new value
     */
    public static void updateMultiplier(Player player, Identifier spellId, String multiplierType, float value) {
        if (player == null || spellId == null || multiplierType == null) {
            return;
        }
        
        SpellCustomization current = getCustomization(player, spellId);
        SpellCustomization updated;
        
        switch (multiplierType.toLowerCase()) {
            case "power":
                updated = new SpellCustomization(value, current.rangeMultiplier(), 
                    current.durationMultiplier(), current.cooldownMultiplier(), current.customProperties());
                break;
            case "range":
                updated = new SpellCustomization(current.powerMultiplier(), value,
                    current.durationMultiplier(), current.cooldownMultiplier(), current.customProperties());
                break;
            case "duration":
                updated = new SpellCustomization(current.powerMultiplier(), current.rangeMultiplier(),
                    value, current.cooldownMultiplier(), current.customProperties());
                break;
            case "cooldown":
                updated = new SpellCustomization(current.powerMultiplier(), current.rangeMultiplier(),
                    current.durationMultiplier(), value, current.customProperties());
                break;
            default:
                return;
        }
        
        setCustomization(player, spellId, updated);
    }
    
    /**
     * Resets customization for a spell to default.
     * 
     * @param player The player
     * @param spellId The spell ID
     */
    public static void resetCustomization(Player player, Identifier spellId) {
        if (player == null || spellId == null) {
            return;
        }
        
        UUID playerId = player.getUUID();
        Map<Identifier, SpellCustomization> customizations = PLAYER_CUSTOMIZATIONS.get(playerId);
        if (customizations != null) {
            customizations.remove(spellId);
        }
    }
    
    /**
     * Gets all customized spells for a player.
     * 
     * @param player The player
     * @return Set of spell IDs that have customizations
     */
    public static Set<Identifier> getCustomizedSpells(Player player) {
        if (player == null) {
            return Collections.emptySet();
        }
        
        Map<Identifier, SpellCustomization> customizations = PLAYER_CUSTOMIZATIONS.get(player.getUUID());
        return customizations != null ? new HashSet<>(customizations.keySet()) : Collections.emptySet();
    }
}

