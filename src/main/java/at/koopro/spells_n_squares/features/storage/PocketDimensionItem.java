package at.koopro.spells_n_squares.features.storage;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Portable storage item that provides access to a pocket dimension.
 */
public class PocketDimensionItem extends Item {
    
    private static final int DEFAULT_SIZE = 16; // 16x16 blocks
    
    public PocketDimensionItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        
        if (player instanceof ServerPlayer serverPlayer) {
            PocketDimensionData.PocketDimensionComponent data = getPocketDimension(stack);
            
            serverPlayer.sendSystemMessage(Component.translatable(
                "message.spells_n_squares.pocket_dimension.size", data.size(), data.size()
            ));
        }
        
        return InteractionResult.SUCCESS;
    }
    
    public static PocketDimensionData.PocketDimensionComponent getPocketDimension(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof PocketDimensionItem)) {
            return null;
        }
        
        PocketDimensionData.PocketDimensionComponent data = stack.get(PocketDimensionData.POCKET_DIMENSION.get());
        if (data == null) {
            data = PocketDimensionData.PocketDimensionComponent.createDefault(DEFAULT_SIZE);
            stack.set(PocketDimensionData.POCKET_DIMENSION.get(), data);
        }
        return data;
    }
}








