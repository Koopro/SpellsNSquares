package at.koopro.spells_n_squares.features.potions.item;

import at.koopro.spells_n_squares.features.potions.PotionsRegistry;
import at.koopro.spells_n_squares.features.potions.data.PotionData;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Love Potion - makes entities friendly (simplified as speed/regeneration).
 */
public class LovePotionItem extends PotionItem {
    
    public LovePotionItem(Properties properties) {
        super(properties, "love_potion");
    }
    
    public static ItemStack createDefault() {
        ItemStack stack = new ItemStack(
            PotionsRegistry.LOVE_POTION.get(), 1);
        
        List<PotionData.PotionEffect> effects = List.of(
            new PotionData.PotionEffect(
                ResourceKey.create(Registries.MOB_EFFECT, 
                    Identifier.fromNamespaceAndPath("minecraft", "regeneration")),
                600, // 30 seconds
                0
            ),
            new PotionData.PotionEffect(
                ResourceKey.create(Registries.MOB_EFFECT, 
                    Identifier.fromNamespaceAndPath("minecraft", "speed")),
                600,
                0
            )
        );
        
        PotionData.PotionComponent component = new PotionData.PotionComponent("love_potion", effects, 100);
        stack.set(PotionData.POTION_DATA.get(), component);
        return stack;
    }
}

