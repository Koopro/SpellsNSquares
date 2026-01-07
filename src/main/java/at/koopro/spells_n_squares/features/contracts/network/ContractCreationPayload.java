package at.koopro.spells_n_squares.features.contracts.network;

import at.koopro.spells_n_squares.core.util.registry.ModIdentifierHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Network payload for contract creation requests.
 * Sent from client to server when player creates a contract.
 */
public record ContractCreationPayload(
    String targetPlayerName,
    String contractText
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ContractCreationPayload> TYPE =
        new CustomPacketPayload.Type<>(ModIdentifierHelper.modId("contract_creation"));

    public static final StreamCodec<ByteBuf, ContractCreationPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8,
        ContractCreationPayload::targetPlayerName,
        ByteBufCodecs.STRING_UTF8,
        ContractCreationPayload::contractText,
        ContractCreationPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

