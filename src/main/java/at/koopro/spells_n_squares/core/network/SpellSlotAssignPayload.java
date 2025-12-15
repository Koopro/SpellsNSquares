package at.koopro.spells_n_squares.core.network;

import java.util.Optional;

import at.koopro.spells_n_squares.SpellsNSquares;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Network payload for assigning spells to slots (client -> server).
 */
public record SpellSlotAssignPayload(int slot, Optional<Identifier> spellId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SpellSlotAssignPayload> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(SpellsNSquares.MODID, "spell_slot_assign"));
    
    public static final StreamCodec<ByteBuf, SpellSlotAssignPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        SpellSlotAssignPayload::slot,
        ByteBufCodecs.optional(ByteBufCodecs.fromCodec(Identifier.CODEC)),
        SpellSlotAssignPayload::spellId,
        SpellSlotAssignPayload::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
