package at.koopro.spells_n_squares.features.potions;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Murtlap Essence - A healing potion that soothes cuts and wounds.
 */
public class MurtlapEssenceItem extends PotionItem {
    
    public MurtlapEssenceItem(Properties properties) {
        super(properties, "murtlap_essence");
    }
    
    /**
     * Creates a default Murtlap Essence with standard effects.
     */
    public static ItemStack createDefault() {
        ItemStack stack = new ItemStack(
            at.koopro.spells_n_squares.core.registry.ModItems.MURTLAP_ESSENCE_POTION.get(), 1);
        
        List<PotionData.PotionEffect> effects = List.of(
            new PotionData.PotionEffect(
                ResourceKey.create(Registries.MOB_EFFECT, 
                    net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "regeneration")),
                200, // 10 seconds
                0     // Level 1
            )
        );
        
        PotionData.PotionComponent component = new PotionData.PotionComponent("murtlap_essence", effects, 100);
        stack.set(PotionData.POTION_DATA.get(), component);
        return stack;
    }
}

