package at.koopro.spells_n_squares.features.mail.item;

import at.koopro.spells_n_squares.core.util.PlayerValidationUtils;
import at.koopro.spells_n_squares.features.mail.data.MailData;
import at.koopro.spells_n_squares.features.mail.system.OwlPostSystem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

/**
 * Mail item that can be written and sent via Owl Post.
 */
public class MailItem extends Item {
    
    public MailItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (level.isClientSide()) {
            MailData.MailComponent mailData = OwlPostSystem.getMailData(stack);
            if (mailData == null) {
                // Empty mail - open writing interface
                net.minecraft.client.Minecraft.getInstance().setScreen(
                    new at.koopro.spells_n_squares.features.mail.client.MailWritingScreen(stack));
            } else {
                // Mail with content - read it (could open a read screen too)
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.SUCCESS;
        }
        
        ServerPlayer serverPlayer = PlayerValidationUtils.asServerPlayer(player);
        if (serverPlayer == null) {
            return InteractionResult.FAIL;
        }
        
        MailData.MailComponent mailData = OwlPostSystem.getMailData(stack);
        
        if (mailData == null) {
            // Empty mail - writing interface is handled client-side
            return InteractionResult.SUCCESS;
        } else {
            // Mail with content - read it
            readMail(serverPlayer, mailData);
        }
        
        return InteractionResult.SUCCESS;
    }
    
    /**
     * Reads mail content.
     */
    private void readMail(ServerPlayer player, MailData.MailComponent mailData) {
        if (!mailData.read()) {
            // Mark as read
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof MailItem) {
                stack.set(MailData.MAIL_DATA.get(), mailData.markAsRead());
            }
        }
        
        // Display mail content
        player.sendSystemMessage(Component.translatable("message.spells_n_squares.mail.from", mailData.senderName()));
        player.sendSystemMessage(Component.translatable("message.spells_n_squares.mail.subject", mailData.subject()));
        player.sendSystemMessage(Component.translatable("message.spells_n_squares.mail.message", mailData.message()));
        
        if (mailData.hasAttachments()) {
            player.sendSystemMessage(Component.translatable("message.spells_n_squares.mail.has_attachments", mailData.attachments().size()));
        }
    }
    
    /**
     * Writes mail content to the item stack.
     * Called by command or GUI.
     */
    public static void writeMail(ItemStack stack, UUID senderId, String senderName, UUID recipientId,
                                String recipientName, String subject, String message, List<ItemStack> attachments) {
        MailData.MailComponent mailData = new MailData.MailComponent(
            senderId,
            senderName,
            recipientId,
            recipientName,
            subject,
            message,
            System.currentTimeMillis(),
            false,
            attachments != null ? attachments : List.of()
        );
        
        stack.set(MailData.MAIL_DATA.get(), mailData);
    }
    
    /**
     * Sends mail via Owl Post.
     */
    public static void sendMail(ServerPlayer sender, ItemStack mailStack, UUID recipientId, ServerLevel level) {
        MailData.MailComponent mailData = OwlPostSystem.getMailData(mailStack);
        if (mailData == null) {
            sender.sendSystemMessage(Component.translatable("message.spells_n_squares.mail.not_written"));
            return;
        }
        
        // Find recipient
        ServerPlayer recipient = level.getServer().getPlayerList().getPlayer(recipientId);
        Vec3 recipientPos = recipient != null ? recipient.position() : sender.position();
        
        // Schedule delivery (with owl spawning for visual effect)
        OwlPostSystem.scheduleDelivery(mailStack.copy(), sender.getUUID(), recipientId, 
                                     sender.position(), recipientPos, true);
        
        // Remove mail from sender's inventory
        mailStack.shrink(1);
        
        // Visual and sound effects
        Vec3 pos = sender.position();
        level.sendParticles(ParticleTypes.ENCHANT,
            pos.x, pos.y + 1.5, pos.z,
            20, 0.5, 0.5, 0.5, 0.1);
        
        level.playSound(null, pos.x, pos.y, pos.z,
            SoundEvents.VILLAGER_WORK_LIBRARIAN, SoundSource.PLAYERS, 0.8f, 1.2f);
        
        sender.sendSystemMessage(Component.translatable("message.spells_n_squares.mail.sent"));
    }
    
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag flag) {
        MailData.MailComponent mailData = OwlPostSystem.getMailData(stack);
        if (mailData != null) {
            tooltip.add(Component.translatable("message.spells_n_squares.mail.from", mailData.senderName()));
            tooltip.add(Component.translatable("message.spells_n_squares.mail.subject", mailData.subject()));
            if (mailData.read()) {
                tooltip.add(Component.translatable("message.spells_n_squares.mail.read"));
            }
        } else {
            tooltip.add(Component.translatable("message.spells_n_squares.mail.empty"));
        }
    }
}
















