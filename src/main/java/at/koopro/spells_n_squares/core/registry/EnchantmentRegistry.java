package at.koopro.spells_n_squares.core.registry;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import at.koopro.spells_n_squares.features.enchantments.Enchantment;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Registry for all enchantments and charms in the mod.
 */
public final class EnchantmentRegistry {
    private static final Map<Identifier, Enchantment> ENCHANTMENTS = new HashMap<>();
    
    private EnchantmentRegistry() {
    }
    
    /**
     * Registers an enchantment with the registry.
     * @param id The unique identifier for the enchantment
     * @param enchantment The enchantment to register
     */
    public static void register(Identifier id, Enchantment enchantment) {
        if (ENCHANTMENTS.containsKey(id)) {
            throw new IllegalArgumentException("Enchantment already registered: " + id);
        }
        ENCHANTMENTS.put(id, enchantment);
    }
    
    /**
     * Registers an enchantment with the registry using the mod's namespace.
     * @param path The path for the enchantment (will be namespaced with mod ID)
     * @param enchantment The enchantment to register
     */
    public static void register(String path, Enchantment enchantment) {
        register(ModIdentifierHelper.modId(path), enchantment);
    }
    
    /**
     * Gets an enchantment by its ID.
     * @param id The enchantment ID
     * @return The enchantment, or null if not found
     */
    public static Enchantment get(Identifier id) {
        return ENCHANTMENTS.get(id);
    }
    
    /**
     * Gets all registered enchantments.
     * @return A set of all enchantment IDs
     */
    public static Set<Identifier> getAllIds() {
        return ENCHANTMENTS.keySet();
    }
    
    /**
     * Gets all registered enchantments.
     * @return A map of all enchantments
     */
    public static Map<Identifier, Enchantment> getAll() {
        return new HashMap<>(ENCHANTMENTS);
    }
    
    /**
     * Checks if an enchantment is registered.
     * @param id The enchantment ID
     * @return True if registered, false otherwise
     */
    public static boolean isRegistered(Identifier id) {
        return ENCHANTMENTS.containsKey(id);
    }
}



