package at.koopro.spells_n_squares.features.potions.item;

import at.koopro.spells_n_squares.features.potions.PotionsRegistry;
import at.koopro.spells_n_squares.features.potions.data.PotionData;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Fire Protection Potion - A potion that provides fire resistance.
 */
public class FireProtectionPotionItem extends PotionItem {
    
    public FireProtectionPotionItem(Properties properties) {
        super(properties, "fire_protection");
    }
    
    /**
     * Creates a default Fire Protection Potion with standard effects.
     */
    public static ItemStack createDefault() {
        ItemStack stack = new ItemStack(
            PotionsRegistry.FIRE_PROTECTION_POTION.get(), 1);
        
        List<PotionData.PotionEffect> effects = List.of(
            new PotionData.PotionEffect(
                ResourceKey.create(Registries.MOB_EFFECT, 
                    net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "fire_resistance")),
                600, // 30 seconds
                0     // Level 1
            )
        );
        
        PotionData.PotionComponent component = new PotionData.PotionComponent("fire_protection", effects, 100);
        stack.set(PotionData.POTION_DATA.get(), component);
        return stack;
    }
}















