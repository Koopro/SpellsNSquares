package at.koopro.spells_n_squares.features.potions;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Babbling Beverage - A potion that causes uncontrollable babbling.
 */
public class BabblingBeverageItem extends PotionItem {
    
    public BabblingBeverageItem(Properties properties) {
        super(properties, "babbling_beverage");
    }
    
    /**
     * Creates a default Babbling Beverage with standard effects.
     */
    public static ItemStack createDefault() {
        ItemStack stack = new ItemStack(
            // TODO: Re-enable when BABBLING_BEVERAGE_POTION is registered in ModItems
            // at.koopro.spells_n_squares.core.registry.ModItems.BABBLING_BEVERAGE_POTION.get(), 1);
            net.minecraft.world.item.Items.POTION, 1); // Placeholder
        
        List<PotionData.PotionEffect> effects = List.of(
            new PotionData.PotionEffect(
                ResourceKey.create(Registries.MOB_EFFECT, 
                    net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "confusion")),
                200, // 10 seconds
                1     // Level 2
            )
        );
        
        PotionData.PotionComponent component = new PotionData.PotionComponent("babbling_beverage", effects, 100);
        stack.set(PotionData.POTION_DATA.get(), component);
        return stack;
    }
}




