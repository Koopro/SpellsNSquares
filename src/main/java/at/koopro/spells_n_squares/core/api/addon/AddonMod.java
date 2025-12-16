package at.koopro.spells_n_squares.core.api.addon;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a class as an addon for Spells_n_Squares.
 * Classes annotated with this must implement IAddon.
 * 
 * Example:
 * <pre>
 * {@code @AddonMod(
 *     modId = "my_addon",
 *     name = "My Addon",
 *     version = "1.0.0",
 *     dependencies = {"spells_n_squares"},
 *     minApiVersion = "1.0.0"
 * )}
 * public class MyAddon implements IAddon {
 *     // ...
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AddonMod {
    /**
     * The unique identifier for this addon (typically the mod ID).
     * @return The addon ID
     */
    String modId();
    
    /**
     * The display name of this addon.
     * @return The addon name
     */
    String name();
    
    /**
     * The version of this addon.
     * @return The addon version
     */
    String version();
    
    /**
     * Dependencies required by this addon.
     * Format: "modid" or "modid@version" for version-specific dependencies.
     * @return Array of dependency strings
     */
    String[] dependencies() default {};
    
    /**
     * Minimum required API version of Spells_n_Squares.
     * Defaults to "1.0.0".
     * @return The minimum API version
     */
    String minApiVersion() default "1.0.0";
}









