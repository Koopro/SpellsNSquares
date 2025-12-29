package at.koopro.spells_n_squares.features.potions;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Pepperup Potion - cure for common cold, provides health and removes negative effects.
 */
public class PepperupPotionItem extends PotionItem {
    
    public PepperupPotionItem(Properties properties) {
        super(properties, "pepperup");
    }
    
    /**
     * Creates a default pepperup potion with standard effects.
     */
    public static ItemStack createDefault() {
        ItemStack stack = new ItemStack(
            // TODO: Re-enable when PEPPERUP_POTION is registered in ModItems
            // at.koopro.spells_n_squares.core.registry.ModItems.PEPPERUP_POTION.get()
            net.minecraft.world.item.Items.POTION, 1);
        
        List<PotionData.PotionEffect> effects = List.of(
            new PotionData.PotionEffect(
                ResourceKey.create(net.minecraft.core.registries.Registries.MOB_EFFECT, 
                    net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "regeneration")),
                200, // 10 seconds
                0     // Level 1
            ),
            new PotionData.PotionEffect(
                ResourceKey.create(net.minecraft.core.registries.Registries.MOB_EFFECT, 
                    net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "fire_resistance")),
                600, // 30 seconds
                0     // Level 1
            )
        );
        
        PotionData.PotionComponent component = new PotionData.PotionComponent("pepperup", effects, 100);
        stack.set(PotionData.POTION_DATA.get(), component);
        return stack;
    }
}

















