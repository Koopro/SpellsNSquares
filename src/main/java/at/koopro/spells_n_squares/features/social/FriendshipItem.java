package at.koopro.spells_n_squares.features.social;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Item to initiate friendships (e.g., "Friendship Bracelet").
 * Right-clicking another player with this item sends a friend request.
 */
public class FriendshipItem extends Item {
    
    public FriendshipItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, 
                                                  net.minecraft.world.entity.LivingEntity target, 
                                                  InteractionHand hand) {
        if (player.level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.FAIL;
        }
        
        // Only works on other players
        if (!(target instanceof ServerPlayer targetPlayer)) {
            return InteractionResult.PASS;
        }
        
        // Can't friend yourself
        if (serverPlayer.getUUID().equals(targetPlayer.getUUID())) {
            serverPlayer.sendSystemMessage(Component.translatable(
                "message.spells_n_squares.social.cannot_friend_self"));
            return InteractionResult.FAIL;
        }
        
        // Send friend request
        boolean success = FriendshipSystem.sendFriendRequest(serverPlayer, targetPlayer);
        
        if (success) {
            serverPlayer.sendSystemMessage(Component.translatable(
                "message.spells_n_squares.social.friend_request_sent", targetPlayer.getName()));
            
            // Consume item (optional - could make it reusable)
            // stack.shrink(1);
        } else {
            serverPlayer.sendSystemMessage(Component.translatable(
                "message.spells_n_squares.social.friend_request_failed"));
        }
        
        return InteractionResult.SUCCESS;
    }
    
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.spells_n_squares.friendship_item.description"));
        tooltip.add(Component.translatable("tooltip.spells_n_squares.friendship_item.usage"));
    }
}






