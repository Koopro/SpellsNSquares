package at.koopro.spells_n_squares.features.mail;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Data component for storing mail content and metadata.
 */
public final class MailData {
    private MailData() {
    }
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MailComponent>> MAIL_DATA =
        DATA_COMPONENTS.register(
            "mail_data",
            () -> DataComponentType.<MailComponent>builder()
                .persistent(MailComponent.CODEC)
                .build()
        );
    
    /**
     * Component storing mail information.
     */
    public record MailComponent(
        UUID senderId,
        String senderName,
        UUID recipientId,
        String recipientName,
        String subject,
        String message,
        long timestamp,
        boolean read,
        List<ItemStack> attachments
    ) {
        public static final Codec<MailComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                UUIDUtil.CODEC.fieldOf("senderId").forGetter(MailComponent::senderId),
                Codec.STRING.fieldOf("senderName").forGetter(MailComponent::senderName),
                UUIDUtil.CODEC.fieldOf("recipientId").forGetter(MailComponent::recipientId),
                Codec.STRING.fieldOf("recipientName").forGetter(MailComponent::recipientName),
                Codec.STRING.fieldOf("subject").forGetter(MailComponent::subject),
                Codec.STRING.fieldOf("message").forGetter(MailComponent::message),
                Codec.LONG.fieldOf("timestamp").forGetter(MailComponent::timestamp),
                Codec.BOOL.optionalFieldOf("read", false).forGetter(MailComponent::read),
                Codec.list(ItemStack.OPTIONAL_CODEC).optionalFieldOf("attachments", List.of()).forGetter(MailComponent::attachments)
            ).apply(instance, MailComponent::new)
        );
        
        public MailComponent markAsRead() {
            return new MailComponent(senderId, senderName, recipientId, recipientName, subject, message, timestamp, true, attachments);
        }
        
        public boolean hasAttachments() {
            return attachments != null && !attachments.isEmpty() && attachments.stream().anyMatch(stack -> !stack.isEmpty());
        }
    }
}






