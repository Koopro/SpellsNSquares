package at.koopro.spells_n_squares.features.potions;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
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
            // TODO: Re-enable when WIDEYE_POTION is registered in ModItems
            // at.koopro.spells_n_squares.core.registry.ModItems.WIDEYE_POTION.get()
            net.minecraft.world.item.Items.POTION, 1);
        
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














