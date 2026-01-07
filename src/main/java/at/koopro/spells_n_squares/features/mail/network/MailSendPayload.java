package at.koopro.spells_n_squares.features.mail.network;

import at.koopro.spells_n_squares.core.util.registry.ModIdentifierHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Network payload for sending mail.
 * Sent from client to server when player sends a mail message.
 */
public record MailSendPayload(
    String recipientName,
    String subject,
    String message
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MailSendPayload> TYPE =
        new CustomPacketPayload.Type<>(ModIdentifierHelper.modId("mail_send"));

    public static final StreamCodec<ByteBuf, MailSendPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8,
        MailSendPayload::recipientName,
        ByteBufCodecs.STRING_UTF8,
        MailSendPayload::subject,
        ByteBufCodecs.STRING_UTF8,
        MailSendPayload::message,
        MailSendPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

