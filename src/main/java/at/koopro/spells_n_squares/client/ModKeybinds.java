package at.koopro.spells_n_squares.client;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

import at.koopro.spells_n_squares.SpellsNSquares;

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
        
        // Set conflict context and register all keybinds
        KeyMapping[] keybinds = {
            SPELL_SELECTOR_TOP,
            SPELL_SELECTOR_BOTTOM,
            SPELL_SELECTOR_LEFT,
            SPELL_SELECTOR_RIGHT,
            SPELL_CAST,
            SPELL_SELECTION_SCREEN
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
