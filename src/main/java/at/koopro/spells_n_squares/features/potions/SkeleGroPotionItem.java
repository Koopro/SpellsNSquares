package at.koopro.spells_n_squares.features.potions;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Skele-Gro - bone regrowth potion, provides regeneration and strength.
 */
public class SkeleGroPotionItem extends PotionItem {
    
    public SkeleGroPotionItem(Properties properties) {
        super(properties, "skele_gro");
    }
    
    /**
     * Creates a default Skele-Gro potion with standard effects.
     */
    public static ItemStack createDefault() {
        ItemStack stack = new ItemStack(
            // TODO: Re-enable when SKELE_GRO_POTION is registered in ModItems
            // at.koopro.spells_n_squares.core.registry.ModItems.SKELE_GRO_POTION.get()
            net.minecraft.world.item.Items.POTION, 1);
        
        List<PotionData.PotionEffect> effects = List.of(
            new PotionData.PotionEffect(
                ResourceKey.create(net.minecraft.core.registries.Registries.MOB_EFFECT, 
                    net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "regeneration")),
                800, // 40 seconds
                1     // Level 2
            ),
            new PotionData.PotionEffect(
                ResourceKey.create(net.minecraft.core.registries.Registries.MOB_EFFECT, 
                    net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "strength")),
                400, // 20 seconds
                0     // Level 1
            )
        );
        
        PotionData.PotionComponent component = new PotionData.PotionComponent("skele_gro", effects, 100);
        stack.set(PotionData.POTION_DATA.get(), component);
        return stack;
    }
}








