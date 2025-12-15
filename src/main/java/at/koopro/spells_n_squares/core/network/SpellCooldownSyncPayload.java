package at.koopro.spells_n_squares.core.network;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.koopro.spells_n_squares.SpellsNSquares;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Network payload for syncing spell cooldowns from server to client.
 * Sends a list of cooldown entries (spell ID -> remaining ticks).
 */
public record SpellCooldownSyncPayload(List<CooldownEntry> entries) implements CustomPacketPayload {
    public record CooldownEntry(Identifier spellId, int ticks) {}
    
    public static final CustomPacketPayload.Type<SpellCooldownSyncPayload> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(SpellsNSquares.MODID, "spell_cooldown_sync"));
    
    private static final StreamCodec<ByteBuf, CooldownEntry> ENTRY_CODEC = StreamCodec.composite(
        ByteBufCodecs.fromCodec(Identifier.CODEC),
        CooldownEntry::spellId,
        ByteBufCodecs.VAR_INT,
        CooldownEntry::ticks,
        CooldownEntry::new
    );
    
    public static final StreamCodec<ByteBuf, SpellCooldownSyncPayload> STREAM_CODEC = StreamCodec.composite(
        ENTRY_CODEC.apply(ByteBufCodecs.list()),
        SpellCooldownSyncPayload::entries,
        SpellCooldownSyncPayload::new
    );
    
    /**
     * Converts the entries list to a map for easier use.
     */
    public Map<Identifier, Integer> toMap() {
        Map<Identifier, Integer> map = new HashMap<>();
        for (CooldownEntry entry : entries) {
            map.put(entry.spellId(), entry.ticks());
        }
        return map;
    }
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
