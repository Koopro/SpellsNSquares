package at.koopro.spells_n_squares.features.magic.system;

import net.minecraft.world.entity.player.Player;

/**
 * Manages Patronus forms and protection mechanics for players.
 * Handles Patronus form selection, storage, and dementor defense.
 */
public final class PatronusSystem {
    private PatronusSystem() {
    }
    
    // No static storage - uses PlayerDataComponent for persistence
    
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
        if (player == null || form == null || player.level().isClientSide()) {
            return false;
        }
        
        at.koopro.spells_n_squares.modules.magic.internal.PatronusData current = 
            at.koopro.spells_n_squares.core.data.PlayerDataHelper.getPatronusData(player);
        
        if (current.hasPatronus()) {
            return false; // Already has a Patronus
        }
        
        at.koopro.spells_n_squares.modules.magic.internal.PatronusData updated = new at.koopro.spells_n_squares.modules.magic.internal.PatronusData(
            form.name().toLowerCase(),
            System.currentTimeMillis(),
            discoveryMethod
        );
        
        at.koopro.spells_n_squares.core.data.PlayerDataHelper.setPatronusData(player, updated);
        return true;
    }
    
    /**
     * Gets the Patronus form for a player.
     */
    public static PatronusForm getPatronusForm(Player player) {
        if (player == null) {
            return null;
        }
        at.koopro.spells_n_squares.modules.magic.internal.PatronusData data = 
            at.koopro.spells_n_squares.core.data.PlayerDataHelper.getPatronusData(player);
        if (!data.hasPatronus()) {
            return null;
        }
        
        // Convert string to AnimalForm enum
        try {
            AnimalForm form = AnimalForm.valueOf(data.animalForm().toUpperCase());
            return new PatronusForm(form, data.discoveredDate(), data.discoveryMethod());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * Checks if a player has discovered their Patronus.
     */
    public static boolean hasPatronus(Player player) {
        if (player == null) {
            return false;
        }
        at.koopro.spells_n_squares.modules.magic.internal.PatronusData data = 
            at.koopro.spells_n_squares.core.data.PlayerDataHelper.getPatronusData(player);
        return data.hasPatronus();
    }
    
    /**
     * Gets the animal form for a player's Patronus.
     */
    public static AnimalForm getAnimalForm(Player player) {
        PatronusForm form = getPatronusForm(player);
        return form != null ? form.form() : null;
    }
    
    /**
     * Gets all available Patronus forms.
     */
    public static AnimalForm[] getAllForms() {
        return AnimalForm.values();
    }
}
















