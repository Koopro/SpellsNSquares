package at.koopro.spells_n_squares.features.portraits.item;

import at.koopro.spells_n_squares.features.portraits.data.PortraitData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

/**
 * Item to create magical portraits.
 * Right-clicking a wall with this item creates a portrait.
 */
public class PortraitFrameItem extends Item {
    
    public PortraitFrameItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();
        
        if (level.isClientSide() || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.SUCCESS;
        }
        
        BlockState state = level.getBlockState(pos);
        
        // Check if there's already a portrait block at this position
        if (state.getBlock() instanceof at.koopro.spells_n_squares.features.portraits.block.MagicalPortraitBlock) {
            serverPlayer.sendSystemMessage(Component.translatable(
                "message.spells_n_squares.portrait.already_exists"));
            return InteractionResult.FAIL;
        }
        
        // Check adjacent blocks for existing portraits (portraits are placed on walls)
        // For now, just create portrait data
        
        // Create portrait from player's appearance
        PortraitData.PortraitComponent portrait = createPortrait(serverPlayer, stack);
        
        // Place portrait block (would need to check adjacent wall)
        // For now, just notify player
        serverPlayer.sendSystemMessage(Component.translatable(
            "message.spells_n_squares.portrait.created", portrait.name()));
        
        // Consume item
        stack.shrink(1);
        
        return InteractionResult.SUCCESS;
    }
    
    /**
     * Creates a portrait component from a player.
     */
    private PortraitData.PortraitComponent createPortrait(ServerPlayer player, ItemStack frameStack) {
        UUID portraitId = UUID.randomUUID();
        UUID creatorId = player.getUUID();
        String creatorName = player.getName().getString();
        
        // Default personality (could be configurable)
        PortraitData.PersonalityType personality = PortraitData.PersonalityType.NEUTRAL;
        String name = creatorName + "'s Portrait";
        
        return new PortraitData.PortraitComponent(
            portraitId,
            creatorId,
            creatorName,
            personality,
            name,
            false, // Not awakened by default
            false, // Can't guard by default
            java.util.List.of(),
            java.util.Map.of()
        );
    }
}
















