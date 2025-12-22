package at.koopro.spells_n_squares.features.economy;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * System for managing player-to-player trading.
 */
public final class TradingSystem {
    private static final Map<UUID, TradeOffer> ACTIVE_OFFERS = new HashMap<>();
    
    private TradingSystem() {
    }
    
    /**
     * Creates a trade offer from a player.
     */
    public static void createOffer(Player player, ItemStack offering, int priceGalleons, int priceSickles, int priceKnuts) {
        ACTIVE_OFFERS.put(player.getUUID(), new TradeOffer(player.getUUID(), offering, priceGalleons, priceSickles, priceKnuts));
    }
    
    /**
     * Gets a trade offer for a player.
     */
    public static TradeOffer getOffer(Player player) {
        return ACTIVE_OFFERS.get(player.getUUID());
    }
    
    /**
     * Removes a trade offer.
     */
    public static void removeOffer(Player player) {
        ACTIVE_OFFERS.remove(player.getUUID());
    }
    
    /**
     * Executes a trade between two players.
     */
    public static boolean executeTrade(Player seller, Player buyer) {
        TradeOffer offer = getOffer(seller);
        if (offer == null) {
            return false;
        }
        
        // Check if buyer has enough currency
        if (!CurrencySystem.removeCurrency(buyer, offer.priceGalleons(), offer.priceSickles(), offer.priceKnuts())) {
            return false;
        }
        
        // Transfer item
        ItemStack item = offer.offering().copy();
        if (!buyer.getInventory().add(item)) {
            // Refund if inventory is full
            CurrencySystem.addCurrency(buyer, offer.priceGalleons(), offer.priceSickles(), offer.priceKnuts());
            return false;
        }
        
        // Give currency to seller
        CurrencySystem.addCurrency(seller, offer.priceGalleons(), offer.priceSickles(), offer.priceKnuts());
        
        // Remove offer
        removeOffer(seller);
        
        return true;
    }
    
    /**
     * Represents a trade offer.
     */
    public record TradeOffer(
        UUID sellerId,
        ItemStack offering,
        int priceGalleons,
        int priceSickles,
        int priceKnuts
    ) {
    }
}









