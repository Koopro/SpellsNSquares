package at.koopro.spells_n_squares.features.automation.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

/**
 * Enchanted workbench item for portable crafting.
 */
public class EnchantedWorkbenchItem extends Item {
    
    public EnchantedWorkbenchItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.workbench.description"));
        }
        
        return InteractionResult.SUCCESS;
    }
}




















