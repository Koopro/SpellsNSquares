package at.koopro.spells_n_squares.features.organizations.network;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Network payload for Dark Mark summoning.
 * Sent from server to client to display Dark Mark in sky and notify Death Eaters.
 */
public record DarkMarkPayload(BlockPos location, String reason, String summonerName) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<DarkMarkPayload> TYPE =
        new CustomPacketPayload.Type<>(ModIdentifierHelper.modId("dark_mark"));
    
    public static final StreamCodec<ByteBuf, DarkMarkPayload> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        DarkMarkPayload::location,
        ByteBufCodecs.STRING_UTF8,
        DarkMarkPayload::reason,
        ByteBufCodecs.STRING_UTF8,
        DarkMarkPayload::summonerName,
        DarkMarkPayload::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}


