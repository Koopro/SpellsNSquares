package at.koopro.spells_n_squares.features.potions.item;

import at.koopro.spells_n_squares.features.potions.PotionsRegistry;
import at.koopro.spells_n_squares.features.potions.data.PotionData;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Strength Potion - provides strength effect.
 */
public class StrengthPotionItem extends PotionItem {
    
    public StrengthPotionItem(Properties properties) {
        super(properties, "strength");
    }
    
    public static ItemStack createDefault() {
        ItemStack stack = new ItemStack(
            PotionsRegistry.STRENGTH_POTION.get(), 1);
        
        List<PotionData.PotionEffect> effects = List.of(
            new PotionData.PotionEffect(
                ResourceKey.create(Registries.MOB_EFFECT, 
                    Identifier.fromNamespaceAndPath("minecraft", "strength")),
                600, // 30 seconds
                0     // Level 1
            )
        );
        
        PotionData.PotionComponent component = new PotionData.PotionComponent("strength", effects, 100);
        stack.set(PotionData.POTION_DATA.get(), component);
        return stack;
    }
}

