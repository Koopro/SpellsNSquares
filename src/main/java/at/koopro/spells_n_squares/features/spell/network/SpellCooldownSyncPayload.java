package at.koopro.spells_n_squares.features.spell.network;

import at.koopro.spells_n_squares.core.util.registry.ModIdentifierHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Network payload for syncing spell cooldowns from server to client.
 * Contains a map of spell IDs to remaining cooldown ticks.
 */
public record SpellCooldownSyncPayload(Map<Identifier, Integer> cooldowns) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SpellCooldownSyncPayload> TYPE =
        new CustomPacketPayload.Type<>(ModIdentifierHelper.modId("spell_cooldown_sync"));
    
    private static final StreamCodec<ByteBuf, Identifier> IDENTIFIER_CODEC =
        ByteBufCodecs.STRING_UTF8.map(Identifier::parse, Identifier::toString);
    
    private static final StreamCodec<ByteBuf, Map<Identifier, Integer>> MAP_CODEC =
        ByteBufCodecs.map(HashMap::new, IDENTIFIER_CODEC, ByteBufCodecs.VAR_INT);
    
    public static final StreamCodec<ByteBuf, SpellCooldownSyncPayload> STREAM_CODEC = StreamCodec.composite(
        MAP_CODEC,
        SpellCooldownSyncPayload::cooldowns,
        SpellCooldownSyncPayload::new
    );
    
    /**
     * Converts the cooldowns to a map.
     * @return Map of spell IDs to cooldown ticks
     */
    public Map<Identifier, Integer> toMap() {
        return cooldowns != null ? cooldowns : new HashMap<>();
    }
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}














