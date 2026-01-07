package at.koopro.spells_n_squares.core.base.item;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Base class for mod items with common functionality.
 * Provides tooltip helpers, NBT data helpers, and standardized patterns.
 */
public abstract class BaseModItem extends Item {
    
    public BaseModItem(Properties properties) {
        super(properties);
    }
    
    // Note: Subclasses should override appendHoverText directly if they need custom tooltips
    // The method signature may vary by Minecraft version, so we don't provide a base implementation here
    
    /**
     * Legacy method for adding custom tooltips (for convenience).
     * Subclasses can override this instead of addCustomTooltips if they prefer.
     * 
     * @param stack The item stack
     * @param level The level (may be null)
     * @param tooltips The tooltip list to add to
     * @param flag The tooltip flag
     */
    @Deprecated
    protected void addCustomTooltips(ItemStack stack, @Nullable Level level, List<Component> tooltips, TooltipFlag flag) {
        // Override in subclasses
    }
    
    
    /**
     * Gets a data component value from the item stack.
     * 
     * @param <T> The component type
     * @param stack The item stack
     * @param componentType The component type
     * @return The component value, or null if not present
     */
    protected <T> @Nullable T getDataComponent(ItemStack stack, net.minecraft.core.component.DataComponentType<T> componentType) {
        DevLogger.logMethodEntry(this, "getDataComponent", 
            "componentType=" + (componentType != null ? componentType.toString() : "null"));
        T result = stack.get(componentType);
        DevLogger.logReturnValue(this, "getDataComponent", result);
        return result;
    }
    
    /**
     * Sets a data component value on the item stack.
     * 
     * @param <T> The component type
     * @param stack The item stack
     * @param componentType The component type
     * @param value The value to set
     */
    protected <T> void setDataComponent(ItemStack stack, net.minecraft.core.component.DataComponentType<T> componentType, T value) {
        DevLogger.logMethodEntry(this, "setDataComponent", 
            "componentType=" + (componentType != null ? componentType.toString() : "null") + 
            ", value=" + value);
        stack.set(componentType, value);
        DevLogger.logStateChange(this, "setDataComponent", "component set");
        DevLogger.logMethodExit(this, "setDataComponent");
    }
    
    /**
     * Removes a data component from the item stack.
     * 
     * @param <T> The component type
     * @param stack The item stack
     * @param componentType The component type
     */
    protected <T> void removeDataComponent(ItemStack stack, net.minecraft.core.component.DataComponentType<T> componentType) {
        DevLogger.logMethodEntry(this, "removeDataComponent", 
            "componentType=" + (componentType != null ? componentType.toString() : "null"));
        stack.remove(componentType);
        DevLogger.logStateChange(this, "removeDataComponent", "component removed");
        DevLogger.logMethodExit(this, "removeDataComponent");
    }
    
    /**
     * Checks if a data component is present on the item stack.
     * 
     * @param <T> The component type
     * @param stack The item stack
     * @param componentType The component type
     * @return True if the component is present
     */
    protected <T> boolean hasDataComponent(ItemStack stack, net.minecraft.core.component.DataComponentType<T> componentType) {
        DevLogger.logMethodEntry(this, "hasDataComponent", 
            "componentType=" + (componentType != null ? componentType.toString() : "null"));
        boolean result = stack.has(componentType);
        DevLogger.logReturnValue(this, "hasDataComponent", result);
        return result;
    }
    
    /**
     * Creates standard item properties with common defaults.
     * 
     * @return A properties builder with common defaults
     */
    protected static Properties createProperties() {
        return new Properties();
    }
    
    /**
     * Creates standard item properties with durability.
     * 
     * @param durability The durability value
     * @return A properties builder with durability
     */
    protected static Properties createProperties(int durability) {
        return new Properties().durability(durability);
    }
    
    /**
     * Creates standard item properties with stack size.
     * 
     * @param stackSize The maximum stack size
     * @return A properties builder with stack size
     */
    protected static Properties createProperties(int stackSize, boolean fireResistant) {
        Properties props = new Properties().stacksTo(stackSize);
        if (fireResistant) {
            props.fireResistant();
        }
        return props;
    }
}


