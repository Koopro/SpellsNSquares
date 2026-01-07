package at.koopro.spells_n_squares.features.spell.client;

import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

/**
 * Handles persistence of favorite spells to client-side storage.
 */
public final class FavoritesPersistence {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String FAVORITES_FILE_NAME = "spell_favorites.dat";
    private static final String FAVORITES_KEY = "favorites";
    
    private FavoritesPersistence() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Gets the path to the favorites file.
     */
    private static Path getFavoritesFilePath() {
        // Use Minecraft's game directory
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc == null || mc.gameDirectory == null) {
            return null;
        }
        return mc.gameDirectory.toPath()
            .resolve("config")
            .resolve(FAVORITES_FILE_NAME);
    }
    
    /**
     * Saves favorite spells to disk.
     */
    public static void saveFavorites(Set<Identifier> favorites) {
        try {
            Path filePath = getFavoritesFilePath();
            if (filePath == null) {
                LOGGER.warn("Cannot save favorites: Minecraft instance not available");
                return;
            }
            
            // Create config directory if it doesn't exist
            Path configDir = filePath.getParent();
            if (configDir != null && !Files.exists(configDir)) {
                Files.createDirectories(configDir);
            }
            
            // Create NBT tag with favorites
            CompoundTag tag = new CompoundTag();
            ListTag favoritesList = new ListTag();
            for (Identifier spellId : favorites) {
                if (spellId != null) {
                    favoritesList.add(StringTag.valueOf(spellId.toString()));
                }
            }
            tag.put(FAVORITES_KEY, favoritesList);
            
            // Write to file
            net.minecraft.nbt.NbtIo.writeCompressed(tag, filePath);
            LOGGER.debug("Saved {} favorite spells to {}", favorites.size(), filePath);
        } catch (IOException e) {
            LOGGER.error("Failed to save favorite spells", e);
        } catch (Exception e) {
            LOGGER.error("Unexpected error saving favorite spells", e);
        }
    }
    
    /**
     * Loads favorite spells from disk.
     */
    public static Set<Identifier> loadFavorites() {
        try {
            Path filePath = getFavoritesFilePath();
            if (filePath == null || !Files.exists(filePath)) {
                return at.koopro.spells_n_squares.core.util.collection.CollectionFactory.createSet();
            }
            
            // Read from file
            CompoundTag tag = net.minecraft.nbt.NbtIo.readCompressed(filePath, net.minecraft.nbt.NbtAccounter.unlimitedHeap());
            if (tag == null || !tag.contains(FAVORITES_KEY)) {
                return at.koopro.spells_n_squares.core.util.collection.CollectionFactory.createSet();
            }
            
            // Parse favorites list
            java.util.Optional<ListTag> favoritesListOpt = tag.getList(FAVORITES_KEY);
            if (favoritesListOpt.isEmpty()) {
                return at.koopro.spells_n_squares.core.util.collection.CollectionFactory.createSet();
            }
            
            ListTag favoritesList = favoritesListOpt.get();
            Set<Identifier> favorites = at.koopro.spells_n_squares.core.util.collection.CollectionFactory.createSet();
            
            for (int i = 0; i < favoritesList.size(); i++) {
                Tag itemTag = favoritesList.get(i);
                if (itemTag instanceof StringTag stringTag) {
                    // StringTag - convert to string (StringTag.toString() returns the value)
                    String spellIdString = stringTag.toString();
                    // Remove quotes if present
                    if (spellIdString.startsWith("\"") && spellIdString.endsWith("\"")) {
                        spellIdString = spellIdString.substring(1, spellIdString.length() - 1);
                    }
                    try {
                        Identifier spellId = Identifier.parse(spellIdString);
                        favorites.add(spellId);
                    } catch (Exception e) {
                        LOGGER.warn("Invalid spell ID in favorites file: {}", spellIdString);
                    }
                }
            }
            
            LOGGER.debug("Loaded {} favorite spells from {}", favorites.size(), filePath);
            return favorites;
        } catch (IOException e) {
            LOGGER.warn("Failed to load favorite spells (file may not exist): {}", e.getMessage());
            return at.koopro.spells_n_squares.core.util.collection.CollectionFactory.createSet();
        } catch (Exception e) {
            LOGGER.error("Unexpected error loading favorite spells", e);
            return at.koopro.spells_n_squares.core.util.collection.CollectionFactory.createSet();
        }
    }
}

