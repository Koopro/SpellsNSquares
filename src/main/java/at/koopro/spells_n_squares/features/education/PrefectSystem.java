package at.koopro.spells_n_squares.features.education;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.*;

/**
 * Manages the prefect system.
 * Handles prefect selection, powers, and badge distribution.
 */
public final class PrefectSystem {
    private PrefectSystem() {
    }
    
    // Registry of prefects (UUID -> PrefectData)
    private static final Map<UUID, PrefectData> prefects = new HashMap<>();
    
    /**
     * Represents prefect data.
     */
    public record PrefectData(
        UUID playerId,
        String house,
        long appointmentDate,
        boolean isHeadBoyOrGirl,
        Set<String> powers
    ) {}
    
    /**
     * Prefect powers.
     */
    public enum PrefectPower {
        DEDUCT_POINTS("Deduct House Points", "Can deduct points from other houses"),
        AWARD_POINTS("Award House Points", "Can award points to own house"),
        DETENTION("Assign Detention", "Can assign detention to students"),
        CURFEW_ENFORCEMENT("Enforce Curfew", "Can enforce curfew rules"),
        QUIET_HOURS("Enforce Quiet Hours", "Can enforce quiet hours in common rooms");
        
        private final String displayName;
        private final String description;
        
        PrefectPower(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
    }
    
    /**
     * Appoints a player as a prefect.
     */
    public static boolean appointPrefect(ServerPlayer player, String house) {
        UUID playerId = player.getUUID();
        
        if (prefects.containsKey(playerId)) {
            return false; // Already a prefect
        }
        
        Set<String> powers = new HashSet<>();
        for (PrefectPower power : PrefectPower.values()) {
            powers.add(power.name());
        }
        
        prefects.put(playerId, new PrefectData(
            playerId,
            house,
            System.currentTimeMillis(),
            false,
            powers
        ));
        
        return true;
    }
    
    /**
     * Appoints a player as Head Boy or Head Girl.
     */
    public static boolean appointHeadStudent(ServerPlayer player, String house) {
        UUID playerId = player.getUUID();
        
        PrefectData existing = prefects.get(playerId);
        if (existing == null) {
            // Must be a prefect first
            appointPrefect(player, house);
            existing = prefects.get(playerId);
        }
        
        prefects.put(playerId, new PrefectData(
            existing.playerId(),
            existing.house(),
            existing.appointmentDate(),
            true,
            existing.powers()
        ));
        
        return true;
    }
    
    /**
     * Checks if a player is a prefect.
     */
    public static boolean isPrefect(ServerPlayer player) {
        return prefects.containsKey(player.getUUID());
    }
    
    /**
     * Checks if a player is Head Boy or Head Girl.
     */
    public static boolean isHeadStudent(ServerPlayer player) {
        PrefectData data = prefects.get(player.getUUID());
        return data != null && data.isHeadBoyOrGirl();
    }
    
    /**
     * Gets prefect data for a player.
     */
    public static PrefectData getPrefectData(ServerPlayer player) {
        return prefects.get(player.getUUID());
    }
    
    /**
     * Checks if a player has a specific prefect power.
     */
    public static boolean hasPower(ServerPlayer player, PrefectPower power) {
        PrefectData data = prefects.get(player.getUUID());
        return data != null && data.powers().contains(power.name());
    }
    
    /**
     * Removes prefect status from a player.
     */
    public static boolean removePrefect(ServerPlayer player) {
        return prefects.remove(player.getUUID()) != null;
    }
    
    /**
     * Creates a prefect badge item.
     */
    public static ItemStack createPrefectBadge(ServerPlayer player) {
        PrefectData data = getPrefectData(player);
        if (data == null) {
            return ItemStack.EMPTY;
        }
        
        // TODO: Create badge item with prefect data
        // For now, return empty stack
        return ItemStack.EMPTY;
    }
}















