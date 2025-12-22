package at.koopro.spells_n_squares.features.potions;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Felix Felicis - Liquid Luck potion.
 */
public class FelixFelicisItem extends PotionItem {
    
    public FelixFelicisItem(Properties properties) {
        super(properties, "felix_felicis");
    }
    
    public static ItemStack createDefault() {
        ItemStack stack = new ItemStack(
            at.koopro.spells_n_squares.features.potions.PotionsRegistry.FELIX_FELICIS.get(), 1);
        
        // Luck effect (if available) or use regeneration as substitute
        List<PotionData.PotionEffect> effects = List.of(
            new PotionData.PotionEffect(
                ResourceKey.create(Registries.MOB_EFFECT, 
                    Identifier.fromNamespaceAndPath("minecraft", "luck")),
                3600, // 3 minutes
                0
            )
        );
        
        PotionData.PotionComponent component = new PotionData.PotionComponent("felix_felicis", effects, 100);
        stack.set(PotionData.POTION_DATA.get(), component);
        return stack;
    }
}
