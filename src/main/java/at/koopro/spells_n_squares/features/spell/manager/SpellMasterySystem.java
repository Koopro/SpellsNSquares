package at.koopro.spells_n_squares.features.spell.manager;

import at.koopro.spells_n_squares.core.data.PlayerDataHelper;
import at.koopro.spells_n_squares.core.util.collection.CollectionFactory;
import at.koopro.spells_n_squares.services.spell.internal.SpellData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

/**
 * Manages spell mastery progression for players.
 * Tracks spell usage and calculates mastery levels that provide bonuses.
 */
public final class SpellMasterySystem {
    private SpellMasterySystem() {
    }
    
    /**
     * Mastery levels for spells.
     */
    public enum MasteryLevel {
        NOVICE(0, 0.0f, 1.0f, "Novice"),
        APPRENTICE(10, 0.1f, 1.1f, "Apprentice"),
        COMPETENT(25, 0.15f, 1.15f, "Competent"),
        EXPERT(50, 0.2f, 1.2f, "Expert"),
        MASTER(100, 0.25f, 1.25f, "Master"),
        GRANDMASTER(250, 0.3f, 1.3f, "Grandmaster");
        
        private final int requiredUses;
        private final float cooldownReduction; // Percentage reduction (0.1 = 10% reduction)
        private final float powerMultiplier; // Power multiplier (1.1 = 10% increase)
        private final String displayName;
        
        MasteryLevel(int requiredUses, float cooldownReduction, float powerMultiplier, String displayName) {
            this.requiredUses = requiredUses;
            this.cooldownReduction = cooldownReduction;
            this.powerMultiplier = powerMultiplier;
            this.displayName = displayName;
        }
        
        public int getRequiredUses() {
            return requiredUses;
        }
        
        public float getCooldownReduction() {
            return cooldownReduction;
        }
        
        public float getPowerMultiplier() {
            return powerMultiplier;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        /**
         * Gets the mastery level for a given number of uses.
         */
        public static MasteryLevel fromUses(int uses) {
            MasteryLevel[] levels = values();
            MasteryLevel current = NOVICE;
            for (int i = levels.length - 1; i >= 0; i--) {
                if (uses >= levels[i].requiredUses) {
                    current = levels[i];
                    break;
                }
            }
            return current;
        }
    }
    
    /**
     * Gets the mastery level for a spell for a player.
     * 
     * @param player The player
     * @param spellId The spell ID
     * @return The mastery level
     */
    public static MasteryLevel getMasteryLevel(Player player, Identifier spellId) {
        if (player == null || spellId == null) {
            return MasteryLevel.NOVICE;
        }
        
        SpellData spellData = PlayerDataHelper.getSpellData(player);
        Map<Identifier, Integer> masteryUses = spellData.masteryUses();
        int uses = masteryUses.getOrDefault(spellId, 0);
        return MasteryLevel.fromUses(uses);
    }
    
    /**
     * Gets the number of times a player has used a spell.
     * 
     * @param player The player
     * @param spellId The spell ID
     * @return The number of uses
     */
    public static int getSpellUses(Player player, Identifier spellId) {
        if (player == null || spellId == null) {
            return 0;
        }
        
        SpellData spellData = PlayerDataHelper.getSpellData(player);
        return spellData.masteryUses().getOrDefault(spellId, 0);
    }
    
    /**
     * Increments the usage count for a spell when it's cast.
     * 
     * @param player The player
     * @param spellId The spell ID
     */
    public static void incrementSpellUse(Player player, Identifier spellId) {
        if (player == null || spellId == null || player.level().isClientSide()) {
            return;
        }
        
        SpellData current = PlayerDataHelper.getSpellData(player);
        Map<Identifier, Integer> masteryUses = CollectionFactory.createMap();
        masteryUses.putAll(current.masteryUses());
        int currentUses = masteryUses.getOrDefault(spellId, 0);
        masteryUses.put(spellId, currentUses + 1);
        
        SpellData updated = current.withMasteryUses(masteryUses);
        PlayerDataHelper.setSpellData(player, updated);
        
        // Check if mastery level increased
        MasteryLevel oldLevel = MasteryLevel.fromUses(currentUses);
        MasteryLevel newLevel = MasteryLevel.fromUses(currentUses + 1);
        
        if (newLevel != oldLevel && player instanceof ServerPlayer serverPlayer) {
            // Notify player of mastery level increase
            at.koopro.spells_n_squares.features.spell.base.Spell spell = at.koopro.spells_n_squares.core.registry.SpellRegistry.get(spellId);
            if (spell != null) {
                net.minecraft.network.chat.Component message = net.minecraft.network.chat.Component.translatable(
                    "message.spells_n_squares.spell_mastery.level_up",
                    spell.getName(),
                    newLevel.getDisplayName()
                );
                serverPlayer.sendSystemMessage(message);
            }
        }
    }
    
    /**
     * Gets the cooldown reduction multiplier for a spell based on mastery.
     * 
     * @param player The player
     * @param spellId The spell ID
     * @return Multiplier (1.0 = no reduction, 0.9 = 10% reduction)
     */
    public static float getCooldownMultiplier(Player player, Identifier spellId) {
        MasteryLevel level = getMasteryLevel(player, spellId);
        return 1.0f - level.getCooldownReduction();
    }
    
    /**
     * Gets the power multiplier for a spell based on mastery.
     * 
     * @param player The player
     * @param spellId The spell ID
     * @return Power multiplier (1.0 = no bonus, 1.1 = 10% increase)
     */
    public static float getPowerMultiplier(Player player, Identifier spellId) {
        MasteryLevel level = getMasteryLevel(player, spellId);
        return level.getPowerMultiplier();
    }
}

