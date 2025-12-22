package at.koopro.spells_n_squares.features.economy;

import at.koopro.spells_n_squares.features.economy.CurrencyData.CurrencyDataComponent;
import net.minecraft.world.entity.player.Player;

/**
 * System for managing wizarding currency (Galleons, Sickles, Knuts).
 */
public final class CurrencySystem {
    // Exchange rates: 1 Galleon = 17 Sickles, 1 Sickle = 29 Knuts
    public static final int SICKLES_PER_GALLEON = 17;
    public static final int KNUTS_PER_SICKLE = 29;
    public static final int KNUTS_PER_GALLEON = SICKLES_PER_GALLEON * KNUTS_PER_SICKLE; // 493
    
    private CurrencySystem() {
    }
    
    /**
     * Converts currency to the smallest denomination (Knuts).
     */
    public static int toKnuts(int galleons, int sickles, int knuts) {
        return (galleons * KNUTS_PER_GALLEON) + (sickles * KNUTS_PER_SICKLE) + knuts;
    }
    
    /**
     * Converts Knuts to Galleons, Sickles, and Knuts.
     */
    public static CurrencyAmount fromKnuts(int totalKnuts) {
        int galleons = totalKnuts / KNUTS_PER_GALLEON;
        int remainder = totalKnuts % KNUTS_PER_GALLEON;
        int sickles = remainder / KNUTS_PER_SICKLE;
        int knuts = remainder % KNUTS_PER_SICKLE;
        return new CurrencyAmount(galleons, sickles, knuts);
    }
    
    /**
     * Gets the total currency for a player in Knuts.
     */
    public static int getTotalKnuts(Player player) {
        CurrencyDataComponent data = CurrencyData.getCurrencyData(player);
        return toKnuts(data.galleons(), data.sickles(), data.knuts());
    }
    
    /**
     * Adds currency to a player.
     */
    public static void addCurrency(Player player, int galleons, int sickles, int knuts) {
        CurrencyDataComponent data = CurrencyData.getCurrencyData(player);
        CurrencyDataComponent newData = data.add(galleons, sickles, knuts);
        CurrencyData.setCurrencyData(player, newData);
    }
    
    /**
     * Removes currency from a player.
     * @return True if the player had enough currency
     */
    public static boolean removeCurrency(Player player, int galleons, int sickles, int knuts) {
        CurrencyDataComponent data = CurrencyData.getCurrencyData(player);
        if (data.hasEnough(galleons, sickles, knuts)) {
            CurrencyDataComponent newData = data.remove(galleons, sickles, knuts);
            CurrencyData.setCurrencyData(player, newData);
            return true;
        }
        return false;
    }
    
    /**
     * Represents an amount of currency.
     */
    public record CurrencyAmount(int galleons, int sickles, int knuts) {
        public int toKnuts() {
            return CurrencySystem.toKnuts(galleons, sickles, knuts);
        }
    }
}









