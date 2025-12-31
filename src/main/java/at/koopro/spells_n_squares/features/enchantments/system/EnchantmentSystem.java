package at.koopro.spells_n_squares.features.enchantments.system;

import at.koopro.spells_n_squares.features.enchantments.Enchantment;

import at.koopro.spells_n_squares.core.registry.EnchantmentRegistry;
import at.koopro.spells_n_squares.features.wand.WandDataHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;

/**
 * System for managing enchantments on items and wands.
 */
public final class EnchantmentSystem {
    private EnchantmentSystem() {
    }
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<EnchantmentData>> ENCHANTMENT_DATA =
        DATA_COMPONENTS.register(
            "enchantment_data",
            () -> DataComponentType.<EnchantmentData>builder()
                .persistent(EnchantmentData.CODEC)
                .build()
        );
    
    /**
     * Data component for storing enchantments on items.
     */
    public record EnchantmentData(Map<Identifier, Integer> enchantments) {
        public static final Codec<EnchantmentData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.unboundedMap(Identifier.CODEC, Codec.INT).fieldOf("enchantments").forGetter(EnchantmentData::enchantments)
            ).apply(instance, EnchantmentData::new)
        );
        
        public EnchantmentData() {
            this(new HashMap<>());
        }
        
        public boolean hasEnchantment(Identifier id) {
            return enchantments.containsKey(id);
        }
        
        public int getLevel(Identifier id) {
            return enchantments.getOrDefault(id, 0);
        }
        
        public EnchantmentData withEnchantment(Identifier id, int level) {
            Map<Identifier, Integer> newEnchantments = new HashMap<>(enchantments);
            if (level <= 0) {
                newEnchantments.remove(id);
            } else {
                Enchantment enchantment = EnchantmentRegistry.get(id);
                if (enchantment != null) {
                    int maxLevel = enchantment.getMaxLevel();
                    newEnchantments.put(id, Math.min(level, maxLevel));
                } else {
                    newEnchantments.put(id, level);
                }
            }
            return new EnchantmentData(newEnchantments);
        }
        
        public EnchantmentData removeEnchantment(Identifier id) {
            return withEnchantment(id, 0);
        }
    }
    
    /**
     * Gets the enchantment data from an item stack.
     * @param stack The item stack
     * @return The enchantment data, or empty data if none exists
     */
    public static EnchantmentData getEnchantmentData(ItemStack stack) {
        if (stack.has(ENCHANTMENT_DATA)) {
            return stack.get(ENCHANTMENT_DATA);
        }
        return new EnchantmentData();
    }
    
    /**
     * Sets enchantment data on an item stack.
     * @param stack The item stack
     * @param data The enchantment data
     */
    public static void setEnchantmentData(ItemStack stack, EnchantmentData data) {
        stack.set(ENCHANTMENT_DATA, data);
    }
    
    /**
     * Adds an enchantment to an item stack.
     * @param stack The item stack
     * @param enchantmentId The enchantment ID
     * @param level The enchantment level
     * @return True if the enchantment was successfully added
     */
    public static boolean addEnchantment(ItemStack stack, Identifier enchantmentId, int level) {
        Enchantment enchantment = EnchantmentRegistry.get(enchantmentId);
        if (enchantment == null) {
            return false;
        }
        
        // Check if enchantment can be applied to this item
        if (!canApplyTo(stack, enchantment)) {
            return false;
        }
        
        EnchantmentData data = getEnchantmentData(stack);
        EnchantmentData newData = data.withEnchantment(enchantmentId, level);
        setEnchantmentData(stack, newData);
        return true;
    }
    
    /**
     * Removes an enchantment from an item stack.
     * @param stack The item stack
     * @param enchantmentId The enchantment ID
     */
    public static void removeEnchantment(ItemStack stack, Identifier enchantmentId) {
        EnchantmentData data = getEnchantmentData(stack);
        EnchantmentData newData = data.removeEnchantment(enchantmentId);
        setEnchantmentData(stack, newData);
    }
    
    /**
     * Checks if an enchantment can be applied to an item stack.
     * @param stack The item stack
     * @param enchantment The enchantment
     * @return True if the enchantment can be applied
     */
    public static boolean canApplyTo(ItemStack stack, Enchantment enchantment) {
        // Check if it's a wand enchantment and the item is a wand
        if (enchantment.getType() == Enchantment.EnchantmentType.WAND) {
            return WandDataHelper.hasWandData(stack);
        }
        
        // For now, allow universal and item enchantments on any item
        // More specific checks can be added later
        return enchantment.getType() == Enchantment.EnchantmentType.UNIVERSAL 
            || enchantment.getType() == Enchantment.EnchantmentType.ITEM;
    }
    
    /**
     * Gets the level of an enchantment on an item stack.
     * @param stack The item stack
     * @param enchantmentId The enchantment ID
     * @return The level, or 0 if not present
     */
    public static int getEnchantmentLevel(ItemStack stack, Identifier enchantmentId) {
        EnchantmentData data = getEnchantmentData(stack);
        return data.getLevel(enchantmentId);
    }
    
    /**
     * Checks if an item stack has an enchantment.
     * @param stack The item stack
     * @param enchantmentId The enchantment ID
     * @return True if the enchantment is present
     */
    public static boolean hasEnchantment(ItemStack stack, Identifier enchantmentId) {
        EnchantmentData data = getEnchantmentData(stack);
        return data.hasEnchantment(enchantmentId);
    }
}

