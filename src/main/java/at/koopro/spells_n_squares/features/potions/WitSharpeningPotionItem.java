package at.koopro.spells_n_squares.features.potions;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Wit-Sharpening Potion - enhances mental clarity, provides night vision and speed.
 */
public class WitSharpeningPotionItem extends PotionItem {
    
    public WitSharpeningPotionItem(Properties properties) {
        super(properties, "wit_sharpening");
    }
    
    /**
     * Creates a default Wit-Sharpening potion with standard effects.
     */
    public static ItemStack createDefault() {
        ItemStack stack = new ItemStack(
            // TODO: Re-enable when WIT_SHARPENING_POTION is registered in ModItems
            // at.koopro.spells_n_squares.core.registry.ModItems.WIT_SHARPENING_POTION.get()
            net.minecraft.world.item.Items.POTION, 1);
        
        List<PotionData.PotionEffect> effects = List.of(
            new PotionData.PotionEffect(
                ResourceKey.create(net.minecraft.core.registries.Registries.MOB_EFFECT, 
                    net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "night_vision")),
                600, // 30 seconds
                0     // Level 1
            ),
            new PotionData.PotionEffect(
                ResourceKey.create(net.minecraft.core.registries.Registries.MOB_EFFECT, 
                    net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "speed")),
                400, // 20 seconds
                0     // Level 1
            )
        );
        
        PotionData.PotionComponent component = new PotionData.PotionComponent("wit_sharpening", effects, 100);
        stack.set(PotionData.POTION_DATA.get(), component);
        return stack;
    }
}





