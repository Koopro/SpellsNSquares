package at.koopro.spells_n_squares.features.potions.item;

import at.koopro.spells_n_squares.features.potions.PotionsRegistry;
import at.koopro.spells_n_squares.features.potions.data.PotionData;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
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
            PotionsRegistry.BABBLING_BEVERAGE_POTION.get(), 1);
        
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














