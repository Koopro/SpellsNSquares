package at.koopro.spells_n_squares.features.potions.item;

import at.koopro.spells_n_squares.features.potions.PotionsRegistry;
import at.koopro.spells_n_squares.features.potions.data.PotionData;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Wolfsbane Potion - provides resistance to negative effects.
 */
public class WolfsbanePotionItem extends PotionItem {
    
    public WolfsbanePotionItem(Properties properties) {
        super(properties, "wolfsbane");
    }
    
    public static ItemStack createDefault() {
        ItemStack stack = new ItemStack(
            PotionsRegistry.WOLFSBANE_POTION.get(), 1);
        
        List<PotionData.PotionEffect> effects = List.of(
            new PotionData.PotionEffect(
                ResourceKey.create(Registries.MOB_EFFECT, 
                    Identifier.fromNamespaceAndPath("minecraft", "resistance")),
                2400, // 2 minutes
                1      // Level 2
            )
        );
        
        PotionData.PotionComponent component = new PotionData.PotionComponent("wolfsbane", effects, 100);
        stack.set(PotionData.POTION_DATA.get(), component);
        return stack;
    }
}

