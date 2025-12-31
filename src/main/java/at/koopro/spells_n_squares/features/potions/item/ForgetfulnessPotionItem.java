package at.koopro.spells_n_squares.features.potions.item;

import at.koopro.spells_n_squares.features.potions.PotionsRegistry;
import at.koopro.spells_n_squares.features.potions.data.PotionData;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Forgetfulness Potion - A potion that causes memory loss.
 */
public class ForgetfulnessPotionItem extends PotionItem {
    
    public ForgetfulnessPotionItem(Properties properties) {
        super(properties, "forgetfulness");
    }
    
    /**
     * Creates a default Forgetfulness Potion with standard effects.
     */
    public static ItemStack createDefault() {
        ItemStack stack = new ItemStack(
            PotionsRegistry.FORGETFULNESS_POTION.get(), 1);
        
        List<PotionData.PotionEffect> effects = List.of(
            new PotionData.PotionEffect(
                ResourceKey.create(Registries.MOB_EFFECT, 
                    net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "weakness")),
                300, // 15 seconds
                0     // Level 1
            )
        );
        
        PotionData.PotionComponent component = new PotionData.PotionComponent("forgetfulness", effects, 100);
        stack.set(PotionData.POTION_DATA.get(), component);
        return stack;
    }
}















