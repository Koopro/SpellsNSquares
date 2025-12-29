package at.koopro.spells_n_squares.init.client;

import at.koopro.spells_n_squares.SpellsNSquares;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Registry for all mod keybinds.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID, value = Dist.CLIENT)
public class ModKeybinds {
    // Spell selector keybinds - default to W, S, A, D
    public static KeyMapping SPELL_SELECTOR_TOP;
    public static KeyMapping SPELL_SELECTOR_BOTTOM;
    public static KeyMapping SPELL_SELECTOR_LEFT;
    public static KeyMapping SPELL_SELECTOR_RIGHT;
    
    // Spell casting keybind - default to right mouse button
    public static KeyMapping SPELL_CAST;
    
    // Spell selection screen keybind - default to R key
    public static KeyMapping SPELL_SELECTION_SCREEN;
    
    // Item/Block debugger keybind - default to F3+Shift+H
    public static KeyMapping DEBUG_ITEM_TOOLTIP;
    
    // Debug tool keybinds
    public static KeyMapping DEBUG_COPY_CLIPBOARD;      // F3+Shift+C
    public static KeyMapping DEBUG_TOGGLE_FILTER;       // F3+Shift+F
    public static KeyMapping DEBUG_EXPORT_FILE;         // F3+Shift+E
    public static KeyMapping DEBUG_PLAYER_INFO;         // F3+Shift+P

    // Cache for Category instances to avoid duplicate registration
    private static final java.util.Map<String, Object> categoryCache = new java.util.HashMap<>();
    
    private static final String KEY_CATEGORY = "key.categories.spells_n_squares";
    
    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        // Create keybinds using reflection to work around Category type issue
        SPELL_SELECTOR_TOP = createKeyMapping(
            "key.spells_n_squares.spell_selector_top",
            InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_W),
            KEY_CATEGORY
        );
        
        SPELL_SELECTOR_BOTTOM = createKeyMapping(
            "key.spells_n_squares.spell_selector_bottom",
            InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_S),
            KEY_CATEGORY
        );
        
        SPELL_SELECTOR_LEFT = createKeyMapping(
            "key.spells_n_squares.spell_selector_left",
            InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_A),
            KEY_CATEGORY
        );
        
        SPELL_SELECTOR_RIGHT = createKeyMapping(
            "key.spells_n_squares.spell_selector_right",
            InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_D),
            KEY_CATEGORY
        );
        
        SPELL_CAST = createKeyMapping(
            "key.spells_n_squares.spell_cast",
            InputConstants.Type.MOUSE.getOrCreate(GLFW.GLFW_MOUSE_BUTTON_RIGHT),
            KEY_CATEGORY
        );
        
        SPELL_SELECTION_SCREEN = createKeyMapping(
            "key.spells_n_squares.spell_selection_screen",
            InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_R),
            KEY_CATEGORY
        );
        
        DEBUG_ITEM_TOOLTIP = createKeyMapping(
            "key.spells_n_squares.debug_item_tooltip",
            InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_H),
            KEY_CATEGORY
        );
        
        DEBUG_COPY_CLIPBOARD = createKeyMapping(
            "key.spells_n_squares.debug_copy_clipboard",
            InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_C),
            KEY_CATEGORY
        );
        
        DEBUG_TOGGLE_FILTER = createKeyMapping(
            "key.spells_n_squares.debug_toggle_filter",
            InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_F),
            KEY_CATEGORY
        );
        
        DEBUG_EXPORT_FILE = createKeyMapping(
            "key.spells_n_squares.debug_export_file",
            InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_E),
            KEY_CATEGORY
        );
        
        DEBUG_PLAYER_INFO = createKeyMapping(
            "key.spells_n_squares.debug_player_info",
            InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_P),
            KEY_CATEGORY
        );
        
        // Set conflict context and register all keybinds
        KeyMapping[] keybinds = {
            SPELL_SELECTOR_TOP,
            SPELL_SELECTOR_BOTTOM,
            SPELL_SELECTOR_LEFT,
            SPELL_SELECTOR_RIGHT,
            SPELL_CAST,
            SPELL_SELECTION_SCREEN,
            DEBUG_ITEM_TOOLTIP,
            DEBUG_COPY_CLIPBOARD,
            DEBUG_TOGGLE_FILTER,
            DEBUG_EXPORT_FILE,
            DEBUG_PLAYER_INFO
        };
        
        for (KeyMapping keybind : keybinds) {
            keybind.setKeyConflictContext(KeyConflictContext.UNIVERSAL);
            event.register(keybind);
        }
    }
    
    /**
     * Helper method to create a KeyMapping using reflection to work around Category type.
     * Uses the NeoForge constructor with KeyModifier which might handle Category differently.
     */
    @SuppressWarnings("unchecked")
    private static KeyMapping createKeyMapping(String name, InputConstants.Key key, String category) {
        try {
            Class<?> categoryClass = Class.forName("net.minecraft.client.KeyMapping$Category");
            Object categoryObj = createCategoryFromString(category, categoryClass);
            
            // Try NeoForge constructor with KeyModifier: (String, IKeyConflictContext, KeyModifier, Key, Category)
            try {
                Constructor<KeyMapping> constructor = (Constructor<KeyMapping>) KeyMapping.class.getDeclaredConstructor(
                    String.class,
                    net.neoforged.neoforge.client.settings.IKeyConflictContext.class,
                    KeyModifier.class,
                    InputConstants.Key.class,
                    categoryClass
                );
                constructor.setAccessible(true);
                return constructor.newInstance(name, KeyConflictContext.UNIVERSAL, KeyModifier.NONE, key, categoryObj);
            } catch (NoSuchMethodException e) {
                // Try without KeyModifier
                Constructor<KeyMapping> constructor = (Constructor<KeyMapping>) KeyMapping.class.getDeclaredConstructor(
                    String.class,
                    net.neoforged.neoforge.client.settings.IKeyConflictContext.class,
                    InputConstants.Key.class,
                    categoryClass
                );
                constructor.setAccessible(true);
                return constructor.newInstance(name, KeyConflictContext.UNIVERSAL, key, categoryObj);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create KeyMapping: " + name + ". Category type not found or incompatible.", e);
        }
    }
    
    /**
     * Attempts to create a Category object from a string.
     * Category might be a record or a class with specific constructors.
     * Categories are cached to avoid duplicate registration errors.
     */
    private static Object createCategoryFromString(String categoryStr, Class<?> categoryClass) throws Exception {
        // Check cache first - Category instances must be reused to avoid duplicate registration
        if (categoryCache.containsKey(categoryStr)) {
            return categoryCache.get(categoryStr);
        }
        
        Object categoryObj;
        
        // First, try constructor with String parameter (most common case)
        Constructor<?>[] constructors = categoryClass.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            Class<?>[] paramTypes = constructor.getParameterTypes();
            if (paramTypes.length == 1 && paramTypes[0] == String.class) {
                constructor.setAccessible(true);
                categoryObj = constructor.newInstance(categoryStr);
                categoryCache.put(categoryStr, categoryObj);
                return categoryObj;
            }
        }
        
        // Try static factory methods
        for (Method method : categoryClass.getDeclaredMethods()) {
            if (Modifier.isStatic(method.getModifiers()) && 
                method.getReturnType() == categoryClass &&
                method.getParameterCount() == 1 &&
                method.getParameterTypes()[0] == String.class) {
                method.setAccessible(true);
                categoryObj = method.invoke(null, categoryStr);
                categoryCache.put(categoryStr, categoryObj);
                return categoryObj;
            }
        }
        
        // Try public static methods
        for (Method method : categoryClass.getMethods()) {
            if (Modifier.isStatic(method.getModifiers()) && 
                method.getReturnType() == categoryClass &&
                method.getParameterCount() == 1) {
                try {
                    method.setAccessible(true);
                    categoryObj = method.invoke(null, categoryStr);
                    categoryCache.put(categoryStr, categoryObj);
                    return categoryObj;
                } catch (Exception ignored) {
                }
            }
        }
        
        // If it's a record, try to find the canonical constructor
        // Check if it has a single String field (common for records)
        Field[] fields = categoryClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType() == String.class && !Modifier.isStatic(field.getModifiers())) {
                // Try to create using constructor with this field
                try {
                    Constructor<?> recordConstructor = categoryClass.getDeclaredConstructor(String.class);
                    recordConstructor.setAccessible(true);
                    categoryObj = recordConstructor.newInstance(categoryStr);
                    categoryCache.put(categoryStr, categoryObj);
                    return categoryObj;
                } catch (Exception ignored) {
                }
            }
        }
        
        throw new RuntimeException("Cannot create Category from string: " + categoryStr + 
            ". Tried all constructors and factory methods. Available constructors: " + constructors.length);
    }
}

