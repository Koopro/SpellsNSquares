package at.koopro.spells_n_squares.features.artifact;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Consumable item that grants immortality for 60 minutes (3 Minecraft days).
 * Once consumed, the player is permanently cursed.
 */
public class ElixirOfLifeItem extends Item {
    
    public ElixirOfLifeItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (level.isClientSide() || !(entity instanceof Player player)) {
            return super.finishUsingItem(stack, level, entity);
        }
        
        // Grant immortality via MobEffect
        ImmortalityHelper.grantImmortality(player);
        
        // Consume the item
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        
        return stack.isEmpty() ? ItemStack.EMPTY : stack;
    }
    
    @Override
    public int getUseDuration(ItemStack stack, net.minecraft.world.entity.LivingEntity entity) {
        return 32; // Standard drink duration
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() == this) {
            player.startUsingItem(hand);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }
}

