package at.koopro.spells_n_squares.features.spell.network;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.Optional;

/**
 * Network payload for assigning a spell to a slot.
 * Sent from client to server when player assigns a spell to a slot.
 */
public record SpellSlotAssignPayload(int slot, Optional<Identifier> spellId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SpellSlotAssignPayload> TYPE =
        new CustomPacketPayload.Type<>(ModIdentifierHelper.modId("spell_slot_assign"));
    
    private static final StreamCodec<ByteBuf, Optional<Identifier>> OPTIONAL_IDENTIFIER_CODEC =
        ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8.map(Identifier::parse, Identifier::toString));
    
    public static final StreamCodec<ByteBuf, SpellSlotAssignPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        SpellSlotAssignPayload::slot,
        OPTIONAL_IDENTIFIER_CODEC,
        SpellSlotAssignPayload::spellId,
        SpellSlotAssignPayload::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}









