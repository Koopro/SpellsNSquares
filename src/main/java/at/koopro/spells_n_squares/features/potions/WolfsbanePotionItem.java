package at.koopro.spells_n_squares.features.potions;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffects;
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
            // TODO: Re-enable when WOLFSBANE_POTION is registered in ModItems
            // at.koopro.spells_n_squares.core.registry.ModItems.WOLFSBANE_POTION.get(), 1);
            net.minecraft.world.item.Items.POTION, 1); // Placeholder
        
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

