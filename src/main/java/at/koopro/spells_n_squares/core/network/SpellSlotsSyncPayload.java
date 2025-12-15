package at.koopro.spells_n_squares.core.network;

import java.util.Optional;

import at.koopro.spells_n_squares.SpellsNSquares;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Network payload for syncing spell slot assignments from server to client.
 */
public record SpellSlotsSyncPayload(
    Optional<Identifier> top,
    Optional<Identifier> bottom,
    Optional<Identifier> left,
    Optional<Identifier> right
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SpellSlotsSyncPayload> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(SpellsNSquares.MODID, "spell_slots_sync"));
    
    public static final StreamCodec<ByteBuf, SpellSlotsSyncPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.optional(ByteBufCodecs.fromCodec(Identifier.CODEC)),
        SpellSlotsSyncPayload::top,
        ByteBufCodecs.optional(ByteBufCodecs.fromCodec(Identifier.CODEC)),
        SpellSlotsSyncPayload::bottom,
        ByteBufCodecs.optional(ByteBufCodecs.fromCodec(Identifier.CODEC)),
        SpellSlotsSyncPayload::left,
        ByteBufCodecs.optional(ByteBufCodecs.fromCodec(Identifier.CODEC)),
        SpellSlotsSyncPayload::right,
        SpellSlotsSyncPayload::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
