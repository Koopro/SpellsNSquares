package at.koopro.spells_n_squares.features.communication;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;

/**
 * Two-way mirror item for communication between players.
 */
public class TwoWayMirrorItem extends Item {
    
    public TwoWayMirrorItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.FAIL;
        }
        
        MirrorData.MirrorDataComponent data = getMirrorData(stack);
        
        if (data.isPaired()) {
            sendMessage(serverPlayer, stack, data);
        } else {
            serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.mirror.pair_instruction"));
        }
        
        return InteractionResult.SUCCESS;
    }
    
    public static void pairMirrors(ItemStack mirror1, ItemStack mirror2, Player player) {
        if (mirror1.isEmpty() || mirror2.isEmpty() || 
            !(mirror1.getItem() instanceof TwoWayMirrorItem) ||
            !(mirror2.getItem() instanceof TwoWayMirrorItem)) {
            return;
        }
        
        UUID mirror1Id = UUID.randomUUID();
        UUID mirror2Id = UUID.randomUUID();
        
        MirrorData.MirrorDataComponent data1 = getMirrorData(mirror1);
        MirrorData.MirrorDataComponent data2 = getMirrorData(mirror2);
        
        mirror1.set(MirrorData.MIRROR_DATA.get(), 
            new MirrorData.MirrorDataComponent(java.util.Optional.of(mirror2Id), data1.mirrorName()));
        mirror2.set(MirrorData.MIRROR_DATA.get(),
            new MirrorData.MirrorDataComponent(java.util.Optional.of(mirror1Id), data2.mirrorName()));
        
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.mirror.paired"));
        }
    }
    
    private void sendMessage(ServerPlayer sender, ItemStack mirror, MirrorData.MirrorDataComponent data) {
        sender.sendSystemMessage(Component.translatable("message.spells_n_squares.mirror.message_sent"));
    }
    
    public static MirrorData.MirrorDataComponent getMirrorData(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof TwoWayMirrorItem)) {
            return null;
        }
        
        MirrorData.MirrorDataComponent data = stack.get(MirrorData.MIRROR_DATA.get());
        if (data == null) {
            data = MirrorData.MirrorDataComponent.createDefault("Unnamed Mirror");
            stack.set(MirrorData.MIRROR_DATA.get(), data);
        }
        return data;
    }
}








