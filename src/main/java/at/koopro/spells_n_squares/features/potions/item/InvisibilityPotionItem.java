package at.koopro.spells_n_squares.features.potions.item;

import at.koopro.spells_n_squares.features.potions.PotionsRegistry;
import at.koopro.spells_n_squares.features.potions.data.PotionData;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Invisibility Potion - provides invisibility effect.
 */
public class InvisibilityPotionItem extends PotionItem {
    
    public InvisibilityPotionItem(Properties properties) {
        super(properties, "invisibility");
    }
    
    public static ItemStack createDefault() {
        ItemStack stack = new ItemStack(
            PotionsRegistry.INVISIBILITY_POTION.get(), 1);
        
        List<PotionData.PotionEffect> effects = List.of(
            new PotionData.PotionEffect(
                ResourceKey.create(Registries.MOB_EFFECT, 
                    Identifier.fromNamespaceAndPath("minecraft", "invisibility")),
                1200, // 60 seconds
                0      // Level 1
            )
        );
        
        PotionData.PotionComponent component = new PotionData.PotionComponent("invisibility", effects, 100);
        stack.set(PotionData.POTION_DATA.get(), component);
        return stack;
    }
}

