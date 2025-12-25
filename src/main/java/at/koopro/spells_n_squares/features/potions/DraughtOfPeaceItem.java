package at.koopro.spells_n_squares.features.potions;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Draught of Peace - A calming potion that soothes anxiety and agitation.
 */
public class DraughtOfPeaceItem extends PotionItem {
    
    public DraughtOfPeaceItem(Properties properties) {
        super(properties, "draught_of_peace");
    }
    
    /**
     * Creates a default Draught of Peace with standard effects.
     */
    public static ItemStack createDefault() {
        ItemStack stack = new ItemStack(
            at.koopro.spells_n_squares.features.potions.PotionsRegistry.DRAUGHT_OF_PEACE_POTION.get(), 1);
        
        List<PotionData.PotionEffect> effects = List.of(
            new PotionData.PotionEffect(
                ResourceKey.create(Registries.MOB_EFFECT, 
                    net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "regeneration")),
                300, // 15 seconds
                0     // Level 1
            )
        );
        
        PotionData.PotionComponent component = new PotionData.PotionComponent("draught_of_peace", effects, 100);
        stack.set(PotionData.POTION_DATA.get(), component);
        return stack;
    }
}









