package at.koopro.spells_n_squares.features.potions.item;

import at.koopro.spells_n_squares.core.config.Config;
import at.koopro.spells_n_squares.features.potions.data.PotionData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Base class for potion items that apply effects when consumed.
 */
public class PotionItem extends Item {
    
    private final String potionType;
    
    public PotionItem(Properties properties, String potionType) {
        super(properties);
        this.potionType = potionType;
    }
    
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        
        // Get potion data from stack
        PotionData.PotionComponent component = getPotionData(stack);
        if (component != null) {
            // Apply all effects from the potion
            for (PotionData.PotionEffect effect : component.effects()) {
                // Adjust duration based on quality and global config
                float qualityMultiplier = component.brewingQuality() / 100.0f;
                double configMultiplier = Config.getPotionDurationMultiplier();
                int adjustedDuration = (int) (effect.duration() * qualityMultiplier * configMultiplier);

                if (adjustedDuration > 0) {
                    entity.addEffect(new MobEffectInstance(
                        effect.getEffect(),
                        adjustedDuration,
                        effect.amplifier(),
                        false,
                        true,
                        true
                    ));
                }
            }
        }
        
        return result;
    }
    
    /**
     * Gets the potion data component from an item stack.
     */
    public static PotionData.PotionComponent getPotionData(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof PotionItem)) {
            return null;
        }
        
        return stack.get(PotionData.POTION_DATA.get());
    }
    
    public String getPotionType() {
        return potionType;
    }
}

