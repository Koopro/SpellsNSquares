package at.koopro.spells_n_squares.services.wand.internal;

import at.koopro.spells_n_squares.core.registry.ModTags;
import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import at.koopro.spells_n_squares.features.wand.system.WandAttunementHandler;
import at.koopro.spells_n_squares.features.wand.core.WandDataHelper;
import at.koopro.spells_n_squares.services.wand.api.IWandService;
import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

/**
 * Implementation of the wand service.
 * Wraps existing wand functionality in a service interface.
 * Provides input validation and error handling.
 */
public class WandService implements IWandService {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    @Override
    public boolean isWand(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        try {
            return !stack.isEmpty() && stack.is(ModTags.WANDS);
        } catch (Exception e) {
            LOGGER.error("Error checking if item stack is a wand: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean isAttuned(ItemStack wand) {
        if (wand == null || wand.isEmpty()) {
            return false;
        }
        if (!isWand(wand)) {
            return false;
        }
        try {
            return WandDataHelper.isAttuned(wand);
        } catch (Exception e) {
            LOGGER.error("Error checking if wand is attuned: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean attuneWand(Player player, ItemStack wand, Level level) {
        DevLogger.logMethodEntry(this, "attuneWand", 
            "player=" + (player != null ? player.getName().getString() : "null"));
        
        if (player == null) {
            LOGGER.warn("Attempted to attune wand for null player");
            DevLogger.logWarn(this, "attuneWand", "Player is null");
            DevLogger.logMethodExit(this, "attuneWand", false);
            return false;
        }
        if (wand == null || wand.isEmpty()) {
            LOGGER.warn("Attempted to attune null or empty wand for player {}", player.getName().getString());
            DevLogger.logWarn(this, "attuneWand", "Wand is null or empty");
            DevLogger.logMethodExit(this, "attuneWand", false);
            return false;
        }
        if (level == null) {
            LOGGER.warn("Attempted to attune wand for player {} in null level", player.getName().getString());
            DevLogger.logWarn(this, "attuneWand", "Level is null");
            DevLogger.logMethodExit(this, "attuneWand", false);
            return false;
        }
        if (!isWand(wand)) {
            LOGGER.warn("Attempted to attune non-wand item for player {}", player.getName().getString());
            DevLogger.logWarn(this, "attuneWand", "Item is not a wand");
            DevLogger.logMethodExit(this, "attuneWand", false);
            return false;
        }
        try {
            // Directly set attuned status
            WandDataHelper.setAttuned(wand, true);
            DevLogger.logStateChange(this, "attuneWand", 
                "Wand attuned for " + player.getName().getString());
            DevLogger.logMethodExit(this, "attuneWand", true);
            return true;
        } catch (Exception e) {
            LOGGER.error("Error attuning wand for player {}: {}", 
                player.getName().getString(), e.getMessage(), e);
            DevLogger.logError(this, "attuneWand", "Error attuning wand", e);
            DevLogger.logMethodExit(this, "attuneWand", false);
            return false;
        }
    }
    
    @Override
    public boolean startAttunement(Player player, Level level) {
        DevLogger.logMethodEntry(this, "startAttunement", 
            "player=" + (player != null ? player.getName().getString() : "null"));
        
        if (player == null) {
            LOGGER.warn("Attempted to start attunement for null player");
            DevLogger.logWarn(this, "startAttunement", "Player is null");
            DevLogger.logMethodExit(this, "startAttunement", false);
            return false;
        }
        if (level == null) {
            LOGGER.warn("Attempted to start attunement for player {} in null level", player.getName().getString());
            DevLogger.logWarn(this, "startAttunement", "Level is null");
            DevLogger.logMethodExit(this, "startAttunement", false);
            return false;
        }
        try {
            boolean result = WandAttunementHandler.startAttunement(player, level);
            DevLogger.logStateChange(this, "startAttunement", 
                "Attunement started: " + result);
            DevLogger.logMethodExit(this, "startAttunement", result);
            return result;
        } catch (Exception e) {
            LOGGER.error("Error starting attunement for player {}: {}", 
                player.getName().getString(), e.getMessage(), e);
            DevLogger.logError(this, "startAttunement", "Error starting attunement", e);
            DevLogger.logMethodExit(this, "startAttunement", false);
            return false;
        }
    }
    
    @Override
    public boolean isAttuning(Player player) {
        if (player == null) {
            LOGGER.warn("Attempted to check attunement status for null player");
            return false;
        }
        try {
            return WandAttunementHandler.isAttuning(player);
        } catch (Exception e) {
            LOGGER.error("Error checking attunement status for player {}: {}", 
                player.getName().getString(), e.getMessage(), e);
            return false;
        }
    }
}

