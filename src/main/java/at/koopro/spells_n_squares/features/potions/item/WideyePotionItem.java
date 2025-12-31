package at.koopro.spells_n_squares.features.potions.item;

import at.koopro.spells_n_squares.features.potions.PotionsRegistry;
import at.koopro.spells_n_squares.features.potions.data.PotionData;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Wideye Potion - An awakener potion that prevents sleep and increases alertness.
 */
public class WideyePotionItem extends PotionItem {
    
    public WideyePotionItem(Properties properties) {
        super(properties, "wideye");
    }
    
    /**
     * Creates a default Wideye Potion with standard effects.
     */
    public static ItemStack createDefault() {
        ItemStack stack = new ItemStack(
            PotionsRegistry.WIDEYE_POTION.get(), 1);
        
        List<PotionData.PotionEffect> effects = List.of(
            new PotionData.PotionEffect(
                ResourceKey.create(Registries.MOB_EFFECT, 
                    net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "night_vision")),
                600, // 30 seconds
                0     // Level 1
            )
        );
        
        PotionData.PotionComponent component = new PotionData.PotionComponent("wideye", effects, 100);
        stack.set(PotionData.POTION_DATA.get(), component);
        return stack;
    }
}















