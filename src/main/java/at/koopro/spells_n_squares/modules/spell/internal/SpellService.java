package at.koopro.spells_n_squares.modules.spell.internal;

import at.koopro.spells_n_squares.features.spell.SpellManager;
import at.koopro.spells_n_squares.modules.spell.api.ISpellManager;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

/**
 * Implementation of the spell manager service.
 * Wraps the existing SpellManager static methods which now use PlayerDataComponent.
 * Provides input validation and error handling.
 */
public class SpellService implements ISpellManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    @Override
    public boolean isValidSlot(int slot) {
        return SpellManager.isValidSlot(slot);
    }
    
    @Override
    public void setSpellInSlot(Player player, int slot, Identifier spellId) {
        if (player == null) {
            LOGGER.warn("Attempted to set spell in slot {} for null player", slot);
            return;
        }
        if (!isValidSlot(slot)) {
            LOGGER.warn("Attempted to set spell in invalid slot {} for player {}", slot, player.getName().getString());
            return;
        }
        try {
            SpellManager.setSpellInSlot(player, slot, spellId);
        } catch (Exception e) {
            LOGGER.error("Error setting spell {} in slot {} for player {}: {}", 
                spellId, slot, player.getName().getString(), e.getMessage(), e);
        }
    }
    
    @Override
    public Identifier getSpellInSlot(Player player, int slot) {
        if (player == null) {
            LOGGER.warn("Attempted to get spell from slot {} for null player", slot);
            return null;
        }
        if (!isValidSlot(slot)) {
            LOGGER.warn("Attempted to get spell from invalid slot {} for player {}", slot, player.getName().getString());
            return null;
        }
        try {
            return SpellManager.getSpellInSlot(player, slot);
        } catch (Exception e) {
            LOGGER.error("Error getting spell from slot {} for player {}: {}", 
                slot, player.getName().getString(), e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    public boolean castSpellInSlot(Player player, Level level, int slot) {
        if (player == null) {
            LOGGER.warn("Attempted to cast spell from slot {} for null player", slot);
            return false;
        }
        if (level == null) {
            LOGGER.warn("Attempted to cast spell from slot {} for player {} in null level", 
                slot, player.getName().getString());
            return false;
        }
        if (!isValidSlot(slot)) {
            LOGGER.warn("Attempted to cast spell from invalid slot {} for player {}", slot, player.getName().getString());
            return false;
        }
        try {
            return SpellManager.castSpellInSlot(player, level, slot);
        } catch (Exception e) {
            LOGGER.error("Error casting spell from slot {} for player {}: {}", 
                slot, player.getName().getString(), e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public void setCooldown(Player player, Identifier spellId, int ticks) {
        if (player == null) {
            LOGGER.warn("Attempted to set cooldown for null player");
            return;
        }
        if (spellId == null) {
            LOGGER.warn("Attempted to set cooldown for null spell ID for player {}", player.getName().getString());
            return;
        }
        if (ticks < 0) {
            LOGGER.warn("Attempted to set negative cooldown {} ticks for spell {} for player {}", 
                ticks, spellId, player.getName().getString());
            return;
        }
        try {
            SpellManager.setCooldown(player, spellId, ticks);
        } catch (Exception e) {
            LOGGER.error("Error setting cooldown for spell {} for player {}: {}", 
                spellId, player.getName().getString(), e.getMessage(), e);
        }
    }
    
    @Override
    public boolean isOnCooldown(Player player, Identifier spellId) {
        if (player == null) {
            LOGGER.warn("Attempted to check cooldown for null player");
            return false;
        }
        if (spellId == null) {
            return false;
        }
        try {
            return SpellManager.isOnCooldown(player, spellId);
        } catch (Exception e) {
            LOGGER.error("Error checking cooldown for spell {} for player {}: {}", 
                spellId, player.getName().getString(), e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public void tickCooldowns(Player player) {
        if (player == null) {
            LOGGER.warn("Attempted to tick cooldowns for null player");
            return;
        }
        try {
            SpellManager.tickCooldowns(player);
        } catch (Exception e) {
            LOGGER.error("Error ticking cooldowns for player {}: {}", 
                player.getName().getString(), e.getMessage(), e);
        }
    }
    
    @Override
    public void startHoldSpell(Player player, Identifier spellId) {
        if (player == null) {
            LOGGER.warn("Attempted to start hold spell for null player");
            return;
        }
        if (spellId == null) {
            LOGGER.warn("Attempted to start hold spell with null spell ID for player {}", player.getName().getString());
            return;
        }
        try {
            SpellManager.startHoldSpell(player, spellId);
        } catch (Exception e) {
            LOGGER.error("Error starting hold spell {} for player {}: {}", 
                spellId, player.getName().getString(), e.getMessage(), e);
        }
    }
    
    @Override
    public void stopHoldSpell(Player player) {
        if (player == null) {
            LOGGER.warn("Attempted to stop hold spell for null player");
            return;
        }
        try {
            SpellManager.stopHoldSpell(player);
        } catch (Exception e) {
            LOGGER.error("Error stopping hold spell for player {}: {}", 
                player.getName().getString(), e.getMessage(), e);
        }
    }
    
    @Override
    public Identifier getActiveHoldSpell(Player player) {
        if (player == null) {
            LOGGER.warn("Attempted to get active hold spell for null player");
            return null;
        }
        try {
            return SpellManager.getActiveHoldSpell(player);
        } catch (Exception e) {
            LOGGER.error("Error getting active hold spell for player {}: {}", 
                player.getName().getString(), e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    public void tickHoldSpells(Level level) {
        if (level == null) {
            LOGGER.warn("Attempted to tick hold spells for null level");
            return;
        }
        try {
            SpellManager.tickHoldSpells(level);
        } catch (Exception e) {
            LOGGER.error("Error ticking hold spells for level: {}", e.getMessage(), e);
        }
    }
    
    @Override
    public void learnSpell(Player player, Identifier spellId) {
        if (player == null) {
            LOGGER.warn("Attempted to learn spell for null player");
            return;
        }
        if (spellId == null) {
            LOGGER.warn("Attempted to learn null spell ID for player {}", player.getName().getString());
            return;
        }
        try {
            SpellManager.learnSpell(player, spellId);
        } catch (Exception e) {
            LOGGER.error("Error learning spell {} for player {}: {}", 
                spellId, player.getName().getString(), e.getMessage(), e);
        }
    }
    
    @Override
    public void forgetSpell(Player player, Identifier spellId) {
        if (player == null) {
            LOGGER.warn("Attempted to forget spell for null player");
            return;
        }
        if (spellId == null) {
            LOGGER.warn("Attempted to forget null spell ID for player {}", player.getName().getString());
            return;
        }
        try {
            SpellManager.forgetSpell(player, spellId);
        } catch (Exception e) {
            LOGGER.error("Error forgetting spell {} for player {}: {}", 
                spellId, player.getName().getString(), e.getMessage(), e);
        }
    }
    
    @Override
    public boolean hasLearnedSpell(Player player, Identifier spellId) {
        if (player == null) {
            LOGGER.warn("Attempted to check learned spell for null player");
            return false;
        }
        if (spellId == null) {
            return false;
        }
        try {
            return SpellManager.hasLearnedSpell(player, spellId);
        } catch (Exception e) {
            LOGGER.error("Error checking learned spell {} for player {}: {}", 
                spellId, player.getName().getString(), e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public void syncSpellSlotsToClient(Player player) {
        if (player == null) {
            LOGGER.warn("Attempted to sync spell slots for null player");
            return;
        }
        ServerPlayer serverPlayer = at.koopro.spells_n_squares.core.util.PlayerValidationUtils.asServerPlayer(player);
        if (serverPlayer != null) {
            try {
                SpellManager.syncSpellSlotsToClient(serverPlayer);
            } catch (Exception e) {
                LOGGER.error("Error syncing spell slots to client for player {}: {}", 
                    serverPlayer.getName().getString(), e.getMessage(), e);
            }
        }
    }
    
    @Override
    public void syncCooldownsToClient(Player player) {
        if (player == null) {
            LOGGER.warn("Attempted to sync cooldowns for null player");
            return;
        }
        ServerPlayer serverPlayer = at.koopro.spells_n_squares.core.util.PlayerValidationUtils.asServerPlayer(player);
        if (serverPlayer != null) {
            try {
                SpellManager.syncCooldownsToClient(serverPlayer);
            } catch (Exception e) {
                LOGGER.error("Error syncing cooldowns to client for player {}: {}", 
                    serverPlayer.getName().getString(), e.getMessage(), e);
            }
        }
    }
    
    @Override
    public void clearPlayerData(Player player) {
        if (player == null) {
            LOGGER.warn("Attempted to clear player data for null player");
            return;
        }
        try {
            SpellManager.clearPlayerData(player);
        } catch (Exception e) {
            LOGGER.error("Error clearing player data for player {}: {}", 
                player.getName().getString(), e.getMessage(), e);
        }
    }
}

