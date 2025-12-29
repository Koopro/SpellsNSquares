package at.koopro.spells_n_squares.features.potions;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Swelling Solution - A potion that causes swelling.
 */
public class SwellingSolutionItem extends PotionItem {
    
    public SwellingSolutionItem(Properties properties) {
        super(properties, "swelling_solution");
    }
    
    /**
     * Creates a default Swelling Solution with standard effects.
     */
    public static ItemStack createDefault() {
        ItemStack stack = new ItemStack(
            at.koopro.spells_n_squares.features.potions.PotionsRegistry.SWELLING_SOLUTION_POTION.get(), 1);
        
        List<PotionData.PotionEffect> effects = List.of(
            new PotionData.PotionEffect(
                ResourceKey.create(Registries.MOB_EFFECT, 
                    net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "slowness")),
                200, // 10 seconds
                1     // Level 2
            )
        );
        
        PotionData.PotionComponent component = new PotionData.PotionComponent("swelling_solution", effects, 100);
        stack.set(PotionData.POTION_DATA.get(), component);
        return stack;
    }
}













