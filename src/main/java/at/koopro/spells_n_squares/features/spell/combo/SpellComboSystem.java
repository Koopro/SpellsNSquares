package at.koopro.spells_n_squares.features.spell.combo;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages spell combo system for chaining spells for bonus effects.
 * Tracks recent spell casts and detects combo patterns.
 */
public final class SpellComboSystem {
    
    private static final Map<UUID, List<ComboEntry>> PLAYER_COMBOS = new ConcurrentHashMap<>();
    private static final int MAX_COMBO_HISTORY = 5;
    private static final long COMBO_TIMEOUT_TICKS = 100; // 5 seconds at 20 TPS
    
    private SpellComboSystem() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Represents a spell cast entry for combo tracking.
     */
    public record ComboEntry(Identifier spellId, long timestamp) {}
    
    /**
     * Represents a detected combo with bonus information.
     */
    public record ComboResult(ComboType comboType, float powerMultiplier, float cooldownReduction, Component message) {}
    
    /**
     * Defines combo types and their effects.
     */
    public enum ComboType {
        NONE(1.0f, 0.0f, null),
        DOUBLE_CAST(1.15f, 0.1f, Component.translatable("combo.spells_n_squares.double_cast")),
        TRIPLE_CAST(1.25f, 0.15f, Component.translatable("combo.spells_n_squares.triple_cast")),
        ELEMENTAL_CHAIN(1.3f, 0.2f, Component.translatable("combo.spells_n_squares.elemental_chain")),
        SPELL_MASTERY(1.4f, 0.25f, Component.translatable("combo.spells_n_squares.spell_mastery"));
        
        private final float powerMultiplier;
        private final float cooldownReduction;
        private final Component message;
        
        ComboType(float powerMultiplier, float cooldownReduction, Component message) {
            this.powerMultiplier = powerMultiplier;
            this.cooldownReduction = cooldownReduction;
            this.message = message;
        }
        
        public float getPowerMultiplier() {
            return powerMultiplier;
        }
        
        public float getCooldownReduction() {
            return cooldownReduction;
        }
        
        public Component getMessage() {
            return message;
        }
    }
    
    /**
     * Records a spell cast for combo tracking.
     * 
     * @param player The player casting the spell
     * @param spellId The spell ID
     * @param currentTick The current game tick
     */
    public static void recordSpellCast(Player player, Identifier spellId, long currentTick) {
        if (player == null || spellId == null) {
            return;
        }
        
        UUID playerId = player.getUUID();
        List<ComboEntry> comboHistory = PLAYER_COMBOS.computeIfAbsent(playerId, k -> new ArrayList<>());
        
        // Add new entry
        comboHistory.add(new ComboEntry(spellId, currentTick));
        
        // Remove old entries (beyond max history or timeout)
        comboHistory.removeIf(entry -> 
            currentTick - entry.timestamp() > COMBO_TIMEOUT_TICKS ||
            comboHistory.size() > MAX_COMBO_HISTORY
        );
        
        // Keep only recent entries
        while (comboHistory.size() > MAX_COMBO_HISTORY) {
            comboHistory.remove(0);
        }
        
        DevLogger.logStateChange(SpellComboSystem.class, "recordSpellCast",
            "Player: " + player.getName().getString() + ", Spell: " + spellId + ", History size: " + comboHistory.size());
    }
    
    /**
     * Detects and returns combo information for the current spell cast.
     * 
     * @param player The player
     * @param currentTick The current game tick
     * @return Combo result with bonus information
     */
    public static ComboResult detectCombo(Player player, long currentTick) {
        if (player == null) {
            return new ComboResult(ComboType.NONE, 1.0f, 0.0f, null);
        }
        
        UUID playerId = player.getUUID();
        List<ComboEntry> comboHistory = PLAYER_COMBOS.get(playerId);
        
        if (comboHistory == null || comboHistory.size() < 2) {
            return new ComboResult(ComboType.NONE, 1.0f, 0.0f, null);
        }
        
        // Remove expired entries
        comboHistory.removeIf(entry -> currentTick - entry.timestamp() > COMBO_TIMEOUT_TICKS);
        
        if (comboHistory.size() < 2) {
            return new ComboResult(ComboType.NONE, 1.0f, 0.0f, null);
        }
        
        // Check for double/triple cast (same spell)
        Identifier lastSpell = comboHistory.get(comboHistory.size() - 1).spellId();
        int sameSpellCount = 1;
        for (int i = comboHistory.size() - 2; i >= 0; i--) {
            if (comboHistory.get(i).spellId().equals(lastSpell)) {
                sameSpellCount++;
            } else {
                break;
            }
        }
        
        if (sameSpellCount >= 3) {
            return new ComboResult(ComboType.TRIPLE_CAST, 
                ComboType.TRIPLE_CAST.getPowerMultiplier(),
                ComboType.TRIPLE_CAST.getCooldownReduction(),
                ComboType.TRIPLE_CAST.getMessage());
        } else if (sameSpellCount >= 2) {
            return new ComboResult(ComboType.DOUBLE_CAST,
                ComboType.DOUBLE_CAST.getPowerMultiplier(),
                ComboType.DOUBLE_CAST.getCooldownReduction(),
                ComboType.DOUBLE_CAST.getMessage());
        }
        
        // Check for elemental chain (different spells in sequence)
        Set<Identifier> uniqueSpells = new HashSet<>();
        for (ComboEntry entry : comboHistory) {
            uniqueSpells.add(entry.spellId());
        }
        
        if (uniqueSpells.size() >= 3 && comboHistory.size() >= 3) {
            return new ComboResult(ComboType.ELEMENTAL_CHAIN,
                ComboType.ELEMENTAL_CHAIN.getPowerMultiplier(),
                ComboType.ELEMENTAL_CHAIN.getCooldownReduction(),
                ComboType.ELEMENTAL_CHAIN.getMessage());
        }
        
        return new ComboResult(ComboType.NONE, 1.0f, 0.0f, null);
    }
    
    /**
     * Applies combo bonuses to a spell cast.
     * 
     * @param player The player
     * @param spellId The spell being cast
     * @param currentTick The current game tick
     * @return Modified power multiplier and cooldown reduction
     */
    public static ComboResult applyCombo(Player player, Identifier spellId, long currentTick) {
        ComboResult combo = detectCombo(player, currentTick);
        
        if (combo.comboType() != ComboType.NONE && player instanceof ServerPlayer serverPlayer) {
            // Notify player of combo
            if (combo.message() != null) {
                serverPlayer.sendSystemMessage(combo.message());
            }
        }
        
        return combo;
    }
    
    /**
     * Clears combo history for a player.
     * 
     * @param player The player
     */
    public static void clearComboHistory(Player player) {
        if (player != null) {
            PLAYER_COMBOS.remove(player.getUUID());
        }
    }
    
    /**
     * Gets the current combo history for a player.
     * 
     * @param player The player
     * @return List of recent spell casts
     */
    public static List<ComboEntry> getComboHistory(Player player) {
        if (player == null) {
            return Collections.emptyList();
        }
        
        List<ComboEntry> history = PLAYER_COMBOS.get(player.getUUID());
        return history != null ? new ArrayList<>(history) : Collections.emptyList();
    }
}

