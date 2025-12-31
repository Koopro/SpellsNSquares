package at.koopro.spells_n_squares.features.potions.item;

import at.koopro.spells_n_squares.features.potions.PotionsRegistry;
import at.koopro.spells_n_squares.features.potions.data.PotionData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Polyjuice Potion - transformation potion that allows the user to take on another's appearance.
 * In this implementation, it provides invisibility and other effects to simulate transformation.
 */
public class PolyjuicePotionItem extends PotionItem {
    
    public PolyjuicePotionItem(Properties properties) {
        super(properties, "polyjuice");
    }
    
    /**
     * Creates a default Polyjuice potion with standard effects.
     */
    public static ItemStack createDefault() {
        ItemStack stack = new ItemStack(
            PotionsRegistry.POLYJUICE_POTION.get(), 1);
        
        List<PotionData.PotionEffect> effects = List.of(
            new PotionData.PotionEffect(
                ResourceKey.create(net.minecraft.core.registries.Registries.MOB_EFFECT, 
                    net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "invisibility")),
                1200, // 60 seconds
                0     // Level 1
            ),
            new PotionData.PotionEffect(
                ResourceKey.create(net.minecraft.core.registries.Registries.MOB_EFFECT, 
                    net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "night_vision")),
                1200, // 60 seconds
                0     // Level 1
            )
        );
        
        PotionData.PotionComponent component = new PotionData.PotionComponent("polyjuice", effects, 100);
        stack.set(PotionData.POTION_DATA.get(), component);
        return stack;
    }
}


















