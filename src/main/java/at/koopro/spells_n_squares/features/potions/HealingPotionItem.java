package at.koopro.spells_n_squares.features.potions;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Healing Potion - provides regeneration effect.
 */
public class HealingPotionItem extends PotionItem {
    
    public HealingPotionItem(Properties properties) {
        super(properties, "healing");
    }
    
    /**
     * Creates a default healing potion with standard effects.
     */
    public static ItemStack createDefault() {
        ItemStack stack = new ItemStack(
            // TODO: Re-enable when HEALING_POTION is registered in ModItems
            // at.koopro.spells_n_squares.core.registry.ModItems.HEALING_POTION.get(), 1);
            net.minecraft.world.item.Items.POTION, 1); // Placeholder
        
        List<PotionData.PotionEffect> effects = List.of(
            new PotionData.PotionEffect(
                ResourceKey.create(net.minecraft.core.registries.Registries.MOB_EFFECT, 
                    net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "regeneration")),
                400, // 20 seconds
                0     // Level 1
            )
        );
        
        PotionData.PotionComponent component = new PotionData.PotionComponent("healing", effects, 100);
        stack.set(PotionData.POTION_DATA.get(), component);
        return stack;
    }
}

