package at.koopro.spells_n_squares.features.potions.item;

import at.koopro.spells_n_squares.features.potions.PotionsRegistry;
import at.koopro.spells_n_squares.features.potions.data.PotionData;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Veritaserum - truth serum that applies weakness and confusion.
 */
public class VeritaserumItem extends PotionItem {
    
    public VeritaserumItem(Properties properties) {
        super(properties, "veritaserum");
    }
    
    public static ItemStack createDefault() {
        ItemStack stack = new ItemStack(
            at.koopro.spells_n_squares.features.potions.PotionsRegistry.VERITASERUM.get(), 1);
        
        List<PotionData.PotionEffect> effects = List.of(
            new PotionData.PotionEffect(
                ResourceKey.create(Registries.MOB_EFFECT, 
                    Identifier.fromNamespaceAndPath("minecraft", "weakness")),
                1200, // 60 seconds
                1      // Level 2
            ),
            new PotionData.PotionEffect(
                ResourceKey.create(Registries.MOB_EFFECT, 
                    Identifier.fromNamespaceAndPath("minecraft", "nausea")),
                1200,
                1
            )
        );
        
        PotionData.PotionComponent component = new PotionData.PotionComponent("veritaserum", effects, 100);
        stack.set(PotionData.POTION_DATA.get(), component);
        return stack;
    }
}
