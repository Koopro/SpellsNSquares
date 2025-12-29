package at.koopro.spells_n_squares.features.debug;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Handles exporting debug data to clipboard and files.
 */
public final class DebugDataExporter {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    private DebugDataExporter() {
        // Utility class
    }
    
    /**
     * Copies item debug data to clipboard.
     */
    public static boolean copyToClipboard(ItemStack stack) {
        try {
            String data = formatForExport(stack, true);
            StringSelection selection = new StringSelection(data);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);
            return true;
        } catch (Exception e) {
            LOGGER.warn("Failed to copy to clipboard: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Exports item debug data to a file.
     * @param stack The item stack to export
     * @param format The format ("json", "nbt", or "txt")
     * @return The path to the exported file, or null if failed
     */
    public static Path exportToFile(ItemStack stack, String format) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String itemName = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().replace(':', '_');
            String fileName = String.format("debug_export_%s_%s.%s", itemName, timestamp, format);
            
            Path exportDir = Paths.get("debug_exports");
            Files.createDirectories(exportDir);
            Path filePath = exportDir.resolve(fileName);
            
            String content;
            switch (format.toLowerCase()) {
                case "nbt":
                    content = formatNBTExport(stack);
                    break;
                case "json":
                    content = formatJSONExport(stack);
                    break;
                case "txt":
                default:
                    content = formatForExport(stack, true);
                    break;
            }
            
            Files.writeString(filePath, content);
            LOGGER.info("Exported debug data to {}", filePath);
            return filePath;
        } catch (IOException e) {
            LOGGER.warn("Failed to export to file: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Formats item data for export (plain text format).
     */
    public static String formatForExport(ItemStack stack, boolean includeNBT) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("=== Item Debug Export ===\n");
        sb.append("Timestamp: ").append(LocalDateTime.now()).append("\n\n");
        
        // Basic info
        sb.append("Item ID: ").append(BuiltInRegistries.ITEM.getKey(stack.getItem())).append("\n");
        sb.append("Count: ").append(stack.getCount()).append("\n");
        
        if (stack.isDamaged()) {
            sb.append("Damage: ").append(stack.getDamageValue()).append(" / ").append(stack.getMaxDamage()).append("\n");
        }
        
        // Data components
        var components = stack.getComponents();
        if (!components.isEmpty()) {
            sb.append("\n--- Data Components ---\n");
            for (var component : components) {
                String typeName = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(component.type()).toString();
                sb.append(typeName).append(": ").append(component.value()).append("\n");
            }
        }
        
        // Enchantments
        @SuppressWarnings("deprecation")
        var enchantments = stack.getEnchantments();
        if (enchantments != null && !enchantments.isEmpty()) {
            sb.append("\n--- Enchantments ---\n");
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
                sb.append(enchantName).append(": Level ").append(level).append("\n");
            }
        }
        
        // Attributes
        var attributes = stack.getAttributeModifiers();
        if (attributes != null && !attributes.modifiers().isEmpty()) {
            sb.append("\n--- Attributes ---\n");
            for (var entry : attributes.modifiers()) {
                var attrHolder = entry.attribute();
                String attrName = attrHolder.isBound() ? 
                    BuiltInRegistries.ATTRIBUTE.getKey(attrHolder.value()).toString() : 
                    "unknown";
                double amount = entry.modifier().amount();
                String operation = entry.modifier().operation().name();
                sb.append(attrName).append(": ").append(amount).append(" (").append(operation).append(")\n");
            }
        }
        
        // NBT
        if (includeNBT) {
            // Note: ItemStack.save() doesn't exist in this version
            // Using toString() as fallback for now
            CompoundTag nbt = null;
            try {
                // Try to get NBT from components
                var stackComponents = stack.getComponents();
                if (!stackComponents.isEmpty()) {
                    nbt = new CompoundTag();
                    // Simplified - just indicate NBT exists
                    nbt.putString("item", BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
                }
            } catch (Exception e) {
                nbt = null;
            }
            if (nbt != null && !nbt.isEmpty()) {
                sb.append("\n--- NBT Data ---\n");
                sb.append(nbt.toString()).append("\n");
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Formats item data as NBT string.
     */
    private static String formatNBTExport(ItemStack stack) {
        // Note: ItemStack.save() doesn't exist in this version - use fallback
        CompoundTag nbt = new CompoundTag();
        nbt.putString("item", BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
        nbt.putInt("count", stack.getCount());
        return nbt != null ? nbt.toString() : "{}";
    }
    
    /**
     * Formats item data as JSON (simplified, using NBT JSON representation).
     */
    private static String formatJSONExport(ItemStack stack) {
        // Note: ItemStack.save() doesn't exist in this version - use fallback
        CompoundTag nbt = new CompoundTag();
        nbt.putString("item", BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
        nbt.putInt("count", stack.getCount());
        if (nbt == null || nbt.isEmpty()) {
            return "{}";
        }
        
        // Convert NBT to JSON-like format
        // This is a simplified version - for full JSON, would need proper NBT to JSON conversion
        return nbt.toString();
    }
}

