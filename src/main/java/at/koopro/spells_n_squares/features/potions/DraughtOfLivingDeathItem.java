package at.koopro.spells_n_squares.features.potions;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Draught of Living Death - powerful sleeping potion that causes deep unconsciousness.
 * Provides long-duration weakness and slowness to simulate deep sleep.
 */
public class DraughtOfLivingDeathItem extends PotionItem {
    
    public DraughtOfLivingDeathItem(Properties properties) {
        super(properties, "draught_of_living_death");
    }
    
    /**
     * Creates a default Draught of Living Death potion with standard effects.
     */
    public static ItemStack createDefault() {
        ItemStack stack = new ItemStack(
            at.koopro.spells_n_squares.features.potions.PotionsRegistry.DRAUGHT_OF_LIVING_DEATH_POTION.get(), 1);
        
        List<PotionData.PotionEffect> effects = List.of(
            new PotionData.PotionEffect(
                ResourceKey.create(net.minecraft.core.registries.Registries.MOB_EFFECT, 
                    net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "weakness")),
                3600, // 3 minutes
                2     // Level 3 - very strong
            ),
            new PotionData.PotionEffect(
                ResourceKey.create(net.minecraft.core.registries.Registries.MOB_EFFECT, 
                    net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "movement_slowdown")),
                3600, // 3 minutes
                3     // Level 4 - extreme slowness
            ),
            new PotionData.PotionEffect(
                ResourceKey.create(net.minecraft.core.registries.Registries.MOB_EFFECT, 
                    net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "mining_fatigue")),
                3600, // 3 minutes
                2     // Level 3
            )
        );
        
        PotionData.PotionComponent component = new PotionData.PotionComponent("draught_of_living_death", effects, 100);
        stack.set(PotionData.POTION_DATA.get(), component);
        return stack;
    }
}

















