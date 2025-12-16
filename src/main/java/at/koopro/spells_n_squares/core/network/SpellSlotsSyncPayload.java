package at.koopro.spells_n_squares.core.network;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.Optional;

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
        new CustomPacketPayload.Type<>(ModIdentifierHelper.modId("spell_slots_sync"));
    
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
    
    /**
     * Gets the spell ID for a specific slot index.
     * @param slot The slot index (0-3)
     * @return The spell ID, or empty Optional if no spell assigned
     */
    public Optional<Identifier> getSlot(int slot) {
        return switch (slot) {
            case 0 -> top();
            case 1 -> bottom();
            case 2 -> left();
            case 3 -> right();
            default -> Optional.empty();
        };
    }
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
