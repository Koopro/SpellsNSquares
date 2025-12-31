package at.koopro.spells_n_squares.features.debug;

import com.mojang.logging.LogUtils;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Manages debug tool configuration including section filtering.
 * Persists settings to a config file.
 */
public final class DebugConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Path CONFIG_FILE = FMLPaths.CONFIGDIR.get().resolve("spells_n_squares_debug.properties");
    
    // Section names
    public static final String SECTION_ITEM_PROPERTIES = "item_properties";
    public static final String SECTION_DATA_COMPONENTS = "data_components";
    public static final String SECTION_NBT_DATA = "nbt_data";
    public static final String SECTION_BLOCK_STATES = "block_states";
    public static final String SECTION_BLOCK_ENTITY = "block_entity";
    public static final String SECTION_ENCHANTMENTS = "enchantments";
    public static final String SECTION_ATTRIBUTES = "attributes";
    public static final String SECTION_RECIPES = "recipes";
    public static final String SECTION_REGISTRY_INFO = "registry_info";
    public static final String SECTION_PERFORMANCE = "performance";
    
    private static final Map<String, Boolean> sectionVisibility = new HashMap<>();
    private static boolean initialized = false;
    
    static {
        // Default: all sections visible
        sectionVisibility.put(SECTION_ITEM_PROPERTIES, true);
        sectionVisibility.put(SECTION_DATA_COMPONENTS, true);
        sectionVisibility.put(SECTION_NBT_DATA, true);
        sectionVisibility.put(SECTION_BLOCK_STATES, true);
        sectionVisibility.put(SECTION_BLOCK_ENTITY, true);
        sectionVisibility.put(SECTION_ENCHANTMENTS, true);
        sectionVisibility.put(SECTION_ATTRIBUTES, true);
        sectionVisibility.put(SECTION_RECIPES, true);
        sectionVisibility.put(SECTION_REGISTRY_INFO, true);
        sectionVisibility.put(SECTION_PERFORMANCE, false); // Performance metrics hidden by default
    }
    
    private DebugConfig() {
        // Utility class
    }
    
    /**
     * Initializes the config, loading from file if it exists.
     */
    public static void initialize() {
        if (initialized) {
            return;
        }
        
        loadConfig();
        initialized = true;
    }
    
    /**
     * Checks if a section should be displayed.
     */
    public static boolean shouldShowSection(String sectionName) {
        if (!initialized) {
            initialize();
        }
        return sectionVisibility.getOrDefault(sectionName, true);
    }
    
    /**
     * Toggles a section's visibility.
     */
    public static void toggleSection(String sectionName) {
        if (!initialized) {
            initialize();
        }
        
        boolean current = sectionVisibility.getOrDefault(sectionName, true);
        sectionVisibility.put(sectionName, !current);
        saveConfig();
    }
    
    /**
     * Sets a section's visibility.
     */
    public static void setSectionVisible(String sectionName, boolean visible) {
        if (!initialized) {
            initialize();
        }
        
        sectionVisibility.put(sectionName, visible);
        saveConfig();
    }
    
    /**
     * Gets all section visibility settings.
     */
    public static Map<String, Boolean> getAllSections() {
        if (!initialized) {
            initialize();
        }
        return new HashMap<>(sectionVisibility);
    }
    
    /**
     * Loads configuration from file.
     */
    private static void loadConfig() {
        if (!Files.exists(CONFIG_FILE)) {
            return;
        }
        
        Properties props = new Properties();
        try {
            props.load(Files.newInputStream(CONFIG_FILE));
            
            for (String key : sectionVisibility.keySet()) {
                String value = props.getProperty("section." + key);
                if (value != null) {
                    sectionVisibility.put(key, Boolean.parseBoolean(value));
                }
            }
            
            LOGGER.debug("Loaded debug config from {}", CONFIG_FILE);
        } catch (IOException e) {
            LOGGER.warn("Failed to load debug config: {}", e.getMessage());
        }
    }
    
    /**
     * Saves configuration to file.
     */
    private static void saveConfig() {
        Properties props = new Properties();
        
        for (Map.Entry<String, Boolean> entry : sectionVisibility.entrySet()) {
            props.setProperty("section." + entry.getKey(), String.valueOf(entry.getValue()));
        }
        
        try {
            Files.createDirectories(CONFIG_FILE.getParent());
            props.store(Files.newOutputStream(CONFIG_FILE), "Spells N Squares Debug Tool Configuration");
            LOGGER.debug("Saved debug config to {}", CONFIG_FILE);
        } catch (IOException e) {
            LOGGER.warn("Failed to save debug config: {}", e.getMessage());
        }
    }
    
    /**
     * Resets all sections to default visibility.
     */
    public static void resetToDefaults() {
        sectionVisibility.clear();
        sectionVisibility.put(SECTION_ITEM_PROPERTIES, true);
        sectionVisibility.put(SECTION_DATA_COMPONENTS, true);
        sectionVisibility.put(SECTION_NBT_DATA, true);
        sectionVisibility.put(SECTION_BLOCK_STATES, true);
        sectionVisibility.put(SECTION_BLOCK_ENTITY, true);
        sectionVisibility.put(SECTION_ENCHANTMENTS, true);
        sectionVisibility.put(SECTION_ATTRIBUTES, true);
        sectionVisibility.put(SECTION_RECIPES, true);
        sectionVisibility.put(SECTION_REGISTRY_INFO, true);
        sectionVisibility.put(SECTION_PERFORMANCE, false);
        saveConfig();
    }
}





