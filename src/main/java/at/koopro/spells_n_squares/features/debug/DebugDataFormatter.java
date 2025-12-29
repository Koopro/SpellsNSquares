package at.koopro.spells_n_squares.features.debug;

import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.List;

/**
 * Formats debug data with structured output, color coding, and proper indentation.
 * Handles NBT tags, data components, enchantments, and attributes.
 */
public final class DebugDataFormatter {
    private static final int MAX_DEPTH = 10;
    private static final int MAX_STRING_LENGTH = 200;
    private static final int MAX_LINES_PER_TAG = 50;
    
    private DebugDataFormatter() {
        // Utility class
    }
    
    /**
     * Formats NBT data recursively with color coding and indentation.
     * @param tag The NBT tag to format
     * @param indent Current indentation level
     * @param colorize Whether to add color codes
     * @param output List to add formatted components to
     * @param lineCount Counter for limiting output size
     * @return Updated line count
     */
    public static int formatNBT(Tag tag, int indent, boolean colorize, List<Component> output, int[] lineCount) {
        if (indent > MAX_DEPTH || lineCount[0] >= MAX_LINES_PER_TAG) {
            if (lineCount[0] >= MAX_LINES_PER_TAG) {
                output.add(Component.literal(getIndent(indent) + (colorize ? "§8" : "") + "... (truncated)"));
            }
            return lineCount[0];
        }
        
        String indentStr = getIndent(indent);
        
        if (tag instanceof CompoundTag compound) {
            output.add(Component.literal(indentStr + (colorize ? "§9" : "") + "{"));
            lineCount[0]++;
            
            for (String key : compound.keySet()) {
                if (lineCount[0] >= MAX_LINES_PER_TAG) {
                    output.add(Component.literal(getIndent(indent + 1) + (colorize ? "§8" : "") + "... (truncated)"));
                    break;
                }
                
                Tag value = compound.get(key);
                String keyColor = colorize ? "§e" : "";
                String valuePrefix = indentStr + "  " + keyColor + key + (colorize ? "§7" : "") + ": ";
                
                if (value instanceof CompoundTag || value instanceof ListTag) {
                    output.add(Component.literal(valuePrefix));
                    lineCount[0]++;
                    formatNBT(value, indent + 1, colorize, output, lineCount);
                } else {
                    String valueStr = formatNBTValue(value, colorize);
                    output.add(Component.literal(valuePrefix + valueStr));
                    lineCount[0]++;
                }
            }
            
            output.add(Component.literal(indentStr + (colorize ? "§9" : "") + "}"));
            lineCount[0]++;
            
        } else if (tag instanceof ListTag list) {
            output.add(Component.literal(indentStr + (colorize ? "§a" : "") + "["));
            lineCount[0]++;
            
            for (int i = 0; i < list.size() && lineCount[0] < MAX_LINES_PER_TAG; i++) {
                Tag element = list.get(i);
                String indexPrefix = indentStr + "  " + (colorize ? "§7" : "") + "[" + i + "]: ";
                
                if (element instanceof CompoundTag || element instanceof ListTag) {
                    output.add(Component.literal(indexPrefix));
                    lineCount[0]++;
                    formatNBT(element, indent + 1, colorize, output, lineCount);
                } else {
                    String valueStr = formatNBTValue(element, colorize);
                    output.add(Component.literal(indexPrefix + valueStr));
                    lineCount[0]++;
                }
            }
            
            if (list.size() > MAX_LINES_PER_TAG) {
                output.add(Component.literal(getIndent(indent + 1) + (colorize ? "§8" : "") + "... (" + (list.size() - MAX_LINES_PER_TAG) + " more)"));
                lineCount[0]++;
            }
            
            output.add(Component.literal(indentStr + (colorize ? "§a" : "") + "]"));
            lineCount[0]++;
            
        } else {
            // Primitive type
            String valueStr = formatNBTValue(tag, colorize);
            output.add(Component.literal(indentStr + valueStr));
            lineCount[0]++;
        }
        
        return lineCount[0];
    }
    
    /**
     * Formats a primitive NBT value with color coding.
     */
    private static String formatNBTValue(Tag tag, boolean colorize) {
        if (tag == null) {
            return colorize ? "§8null" : "null";
        }
        
        String value = tag.toString();
        String color = "";
        
        if (colorize) {
            if (tag instanceof StringTag) {
                color = "§e"; // Yellow for strings
            } else if (tag instanceof NumericTag) {
                color = "§b"; // Cyan for numbers
            } else if (tag instanceof ByteTag) {
                color = "§3"; // Dark aqua for bytes
            } else if (tag instanceof IntTag) {
                color = "§b"; // Cyan for integers
            } else if (tag instanceof LongTag) {
                color = "§b"; // Cyan for longs
            } else if (tag instanceof FloatTag || tag instanceof DoubleTag) {
                color = "§d"; // Light purple for floats/doubles
            } else if (tag instanceof ByteArrayTag || tag instanceof IntArrayTag || tag instanceof LongArrayTag) {
                color = "§5"; // Dark purple for arrays
            } else {
                color = "§7"; // Gray for other types
            }
        }
        
        // Truncate long strings
        if (value.length() > MAX_STRING_LENGTH) {
            value = value.substring(0, MAX_STRING_LENGTH - 3) + "...";
        }
        
        return color + value + (colorize ? "§r" : "");
    }
    
    /**
     * Formats a data component with type information.
     */
    public static Component formatDataComponent(TypedDataComponent<?> component) {
        String typeName = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(component.type()).toString();
        Object value = component.value();
        
        String valueStr;
        if (value == null) {
            valueStr = "§8null";
        } else {
            valueStr = value.toString();
            if (valueStr.length() > 150) {
                valueStr = valueStr.substring(0, 147) + "...";
            }
        }
        
        return Component.literal("§7" + typeName + ": §f" + valueStr);
    }
    
    /**
     * Formats enchantments with level and description.
     */
    @SuppressWarnings("deprecation")
    public static void formatEnchantments(ItemStack stack, List<Component> output) {
        ItemEnchantments enchantments = stack.getEnchantments();
        if (enchantments == null || enchantments.isEmpty()) {
            return;
        }
        
        output.add(Component.literal("§7--- Enchantments ---"));
        
        for (var entry : enchantments.entrySet()) {
            var enchantmentHolder = entry.getKey();
            int level = entry.getIntValue();
            
            String enchantName = "unknown";
            if (enchantmentHolder.isBound() && enchantmentHolder.value() != null) {
                try {
                    enchantName = enchantmentHolder.unwrapKey().<String>map(key -> key.toString()).orElse("unknown");
                } catch (Exception e) {
                    enchantName = "unknown";
                }
            }
            String levelStr = level > 1 ? " §7(Level " + level + ")" : "";
            
            output.add(Component.literal("§7  §f" + enchantName + levelStr));
        }
        
        output.add(Component.literal(""));
    }
    
    /**
     * Formats item attributes and modifiers.
     */
    public static void formatAttributes(ItemStack stack, List<Component> output) {
        var attributes = stack.getAttributeModifiers();
        if (attributes == null || attributes.modifiers().isEmpty()) {
            return;
        }
        
        output.add(Component.literal("§7--- Attributes ---"));
        
        for (var entry : attributes.modifiers()) {
            var attribute = entry.attribute();
            var modifier = entry.modifier();
            
            String attrName = attribute.isBound() ? 
                BuiltInRegistries.ATTRIBUTE.getKey(attribute.value()).toString() : 
                "unknown";
            double amount = modifier.amount();
            String operation = modifier.operation().name();
            
            output.add(Component.literal("§7  §f" + attrName + ": §b" + amount + " §7(" + operation + ")"));
        }
        
        output.add(Component.literal(""));
    }
    
    /**
     * Gets indentation string for the given level.
     */
    private static String getIndent(int level) {
        return "  ".repeat(Math.max(0, level));
    }
    
    /**
     * Formats NBT for tooltip display (simplified version).
     */
    public static void formatNBTForTooltip(List<Component> tooltip, CompoundTag tag, int indent) {
        int[] lineCount = {0};
        formatNBT(tag, indent, true, tooltip, lineCount);
    }
}

