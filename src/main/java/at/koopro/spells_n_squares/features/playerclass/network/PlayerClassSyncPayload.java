package at.koopro.spells_n_squares.features.playerclass.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.features.playerclass.PlayerClass;

/**
 * Network payload for syncing player class from server to client.
 */
public record PlayerClassSyncPayload(
    PlayerClass playerClass
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PlayerClassSyncPayload> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(SpellsNSquares.MODID, "player_class_sync"));
    
    public static final StreamCodec<ByteBuf, PlayerClassSyncPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8.map(
            PlayerClass::fromName,
            PlayerClass::name
        ),
        PlayerClassSyncPayload::playerClass,
        PlayerClassSyncPayload::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
