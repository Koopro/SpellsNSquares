package at.koopro.spells_n_squares.features.magic;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.*;

/**
 * Manages Patronus forms and protection mechanics for players.
 * Handles Patronus form selection, storage, and dementor defense.
 */
public final class PatronusSystem {
    private PatronusSystem() {
    }
    
    // Registry of player Patronus forms (UUID -> PatronusForm)
    private static final Map<UUID, PatronusForm> patronusForms = new HashMap<>();
    
    /**
     * Represents a Patronus form.
     */
    public record PatronusForm(
        AnimalForm form,
        long discoveredDate,
        String discoveryMethod // "spell", "item", "event", etc.
    ) {}
    
    /**
     * Available Patronus animal forms.
     */
    public enum AnimalForm {
        STAG("Stag", "A majestic stag, symbol of protection and guidance"),
        OTTER("Otter", "A playful otter, symbol of joy and loyalty"),
        DOG("Dog", "A loyal dog, symbol of friendship and protection"),
        WOLF("Wolf", "A fierce wolf, symbol of strength and pack loyalty"),
        PHOENIX("Phoenix", "A phoenix, symbol of rebirth and hope"),
        SWAN("Swan", "A graceful swan, symbol of beauty and grace"),
        HARE("Hare", "A quick hare, symbol of speed and agility"),
        HORSE("Horse", "A noble horse, symbol of freedom and power"),
        CAT("Cat", "An independent cat, symbol of mystery and grace"),
        FOX("Fox", "A clever fox, symbol of cunning and adaptability"),
        BEAR("Bear", "A powerful bear, symbol of strength and protection"),
        DRAGON("Dragon", "A mighty dragon, symbol of power and majesty");
        
        private final String displayName;
        private final String description;
        
        AnimalForm(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
    }
    
    /**
     * Discovers a Patronus form for a player.
     * Typically happens through a special event or spell.
     */
    public static boolean discoverPatronus(Player player, AnimalForm form, String discoveryMethod) {
        UUID playerId = player.getUUID();
        
        if (patronusForms.containsKey(playerId)) {
            return false; // Already has a Patronus
        }
        
        patronusForms.put(playerId, new PatronusForm(form, System.currentTimeMillis(), discoveryMethod));
        return true;
    }
    
    /**
     * Gets the Patronus form for a player.
     */
    public static PatronusForm getPatronusForm(Player player) {
        return patronusForms.get(player.getUUID());
    }
    
    /**
     * Checks if a player has discovered their Patronus.
     */
    public static boolean hasPatronus(Player player) {
        return patronusForms.containsKey(player.getUUID());
    }
    
    /**
     * Gets the animal form for a player's Patronus.
     */
    public static AnimalForm getAnimalForm(Player player) {
        PatronusForm form = getPatronusForm(player);
        return form != null ? form.form() : null;
    }
    
    /**
     * Clears Patronus data for a player (on disconnect).
     */
    public static void clearPlayerData(Player player) {
        patronusForms.remove(player.getUUID());
    }
    
    /**
     * Gets all available Patronus forms.
     */
    public static AnimalForm[] getAllForms() {
        return AnimalForm.values();
    }
}






