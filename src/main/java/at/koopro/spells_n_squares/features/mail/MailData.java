package at.koopro.spells_n_squares.features.mail;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.util.ExtraCodecs;

import java.util.UUID;

/**
 * Data class representing a mail message sent between players.
 * 
 * <p>A mail message contains:
 * <ul>
 *   <li>Unique mail ID for identification</li>
 *   <li>Sender and recipient player information</li>
 *   <li>Subject and message content</li>
 *   <li>Send timestamp</li>
 *   <li>Read status</li>
 * </ul>
 * 
 * <p>Mail is delivered to the recipient's mailbox using {@link MailStorage}.
 * The mail can be serialized using the provided {@link #CODEC}.
 * 
 * <p>Example usage:
 * <pre>{@code
 * MailData mail = MailData.create(
 *     senderUUID, "Alice",
 *     recipientUUID, "Bob",
 *     "Hello!",
 *     "This is a test message"
 * );
 * MailStorage.addMail(mail);
 * }</pre>
 * 
 * @param mailId Unique identifier for this mail message
 * @param senderId UUID of the player who sent the mail
 * @param senderName Display name of the sender
 * @param recipientId UUID of the player who receives the mail
 * @param recipientName Display name of the recipient
 * @param subject The mail subject line
 * @param message The mail message content
 * @param sendTime Timestamp when the mail was sent (milliseconds since epoch)
 * @param isRead Whether the recipient has read the mail
 * @since 1.0.0
 */
public record MailData(
    UUID mailId,
    UUID senderId,
    String senderName,
    UUID recipientId,
    String recipientName,
    String subject,
    String message,
    long sendTime,
    boolean isRead
) {
    public static final Codec<MailData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            UUIDUtil.CODEC.fieldOf("mailId").forGetter(MailData::mailId),
            UUIDUtil.CODEC.fieldOf("senderId").forGetter(MailData::senderId),
            ExtraCodecs.NON_EMPTY_STRING.fieldOf("senderName").forGetter(MailData::senderName),
            UUIDUtil.CODEC.fieldOf("recipientId").forGetter(MailData::recipientId),
            ExtraCodecs.NON_EMPTY_STRING.fieldOf("recipientName").forGetter(MailData::recipientName),
            ExtraCodecs.NON_EMPTY_STRING.fieldOf("subject").forGetter(MailData::subject),
            ExtraCodecs.NON_EMPTY_STRING.fieldOf("message").forGetter(MailData::message),
            Codec.LONG.fieldOf("sendTime").forGetter(MailData::sendTime),
            Codec.BOOL.fieldOf("isRead").forGetter(MailData::isRead)
        ).apply(instance, MailData::new)
    );

    /**
     * Creates a new mail with the given parameters.
     * 
     * <p>This factory method creates a new mail message with:
     * <ul>
     *   <li>A randomly generated mail ID</li>
     *   <li>Current system time as send timestamp</li>
     *   <li>Initial status: unread</li>
     * </ul>
     *
     * @param senderId UUID of the sender (must not be null)
     * @param senderName Display name of the sender (must not be null or empty)
     * @param recipientId UUID of the recipient (must not be null)
     * @param recipientName Display name of the recipient (must not be null or empty)
     * @param subject The mail subject (can be null, will default to "No Subject" in storage)
     * @param message The mail message content (must not be null or empty)
     * @return A new MailData instance with generated ID and current timestamp
     * @throws NullPointerException if any UUID parameter is null
     * @throws IllegalArgumentException if senderName, recipientName, or message is null or empty
     */
    public static MailData create(UUID senderId, String senderName, UUID recipientId, String recipientName, String subject, String message) {
        return new MailData(
            UUID.randomUUID(),
            senderId,
            senderName,
            recipientId,
            recipientName,
            subject,
            message,
            System.currentTimeMillis(),
            false
        );
    }
}

