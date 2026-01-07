package at.koopro.spells_n_squares.features.enchantments.network;

import at.koopro.spells_n_squares.core.util.registry.ModIdentifierHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Network payload for enchantment requests.
 * Sent from client to server when player requests to enchant an item.
 */
public record EnchantmentRequestPayload(
    BlockPos tablePos,
    Identifier enchantmentId,
    int enchantmentLevel
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<EnchantmentRequestPayload> TYPE =
        new CustomPacketPayload.Type<>(ModIdentifierHelper.modId("enchantment_request"));

    // BlockPos codec: serialize as 3 integers (x, y, z)
    private static final StreamCodec<ByteBuf, BlockPos> BLOCK_POS_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, BlockPos::getX,
        ByteBufCodecs.VAR_INT, BlockPos::getY,
        ByteBufCodecs.VAR_INT, BlockPos::getZ,
        BlockPos::new
    );
    
    private static final StreamCodec<ByteBuf, String> STRING_CODEC = ByteBufCodecs.STRING_UTF8;

    public static final StreamCodec<ByteBuf, EnchantmentRequestPayload> STREAM_CODEC = StreamCodec.composite(
        BLOCK_POS_CODEC,
        EnchantmentRequestPayload::tablePos,
        STRING_CODEC.map(
            str -> Identifier.parse(str),
            Identifier::toString
        ),
        EnchantmentRequestPayload::enchantmentId,
        ByteBufCodecs.INT,
        EnchantmentRequestPayload::enchantmentLevel,
        EnchantmentRequestPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

