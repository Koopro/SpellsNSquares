package at.koopro.spells_n_squares.features.spell.network;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import at.koopro.spells_n_squares.features.spell.SpellManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Network payload for syncing spell slots from server to client.
 * Contains spell IDs for all 4 slots.
 */
public record SpellSlotsSyncPayload(List<Identifier> slots) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SpellSlotsSyncPayload> TYPE =
        new CustomPacketPayload.Type<>(ModIdentifierHelper.modId("spell_slots_sync"));
    
    private static final StreamCodec<ByteBuf, Identifier> IDENTIFIER_CODEC =
        ByteBufCodecs.STRING_UTF8.map(Identifier::parse, Identifier::toString);
    
    public static final StreamCodec<ByteBuf, SpellSlotsSyncPayload> STREAM_CODEC = StreamCodec.composite(
        IDENTIFIER_CODEC.apply(ByteBufCodecs.collection(ArrayList::new)),
        SpellSlotsSyncPayload::slots,
        SpellSlotsSyncPayload::new
    );
    
    /**
     * Gets the spell ID for a specific slot.
     * @param slot The slot index (0-3)
     * @return Optional containing the spell ID, or empty if no spell assigned
     */
    public Optional<Identifier> getSlot(int slot) {
        if (!SpellManager.isValidSlot(slot) || slots == null || slot >= slots.size()) {
            return Optional.empty();
        }
        Identifier id = slots.get(slot);
        return Optional.ofNullable(id);
    }
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

