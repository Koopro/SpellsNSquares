package at.koopro.spells_n_squares.core.network;

import at.koopro.spells_n_squares.core.data.PlayerModelDataComponent;
import at.koopro.spells_n_squares.core.util.registry.ModIdentifierHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Optional;

/**
 * Network payload for syncing player model data from server to client.
 * Contains scale values for all body parts.
 */
public record PlayerModelSyncPayload(
    float scale,
    float headScale,
    float bodyScale,
    float leftArmScale,
    float rightArmScale,
    float leftLegScale,
    float rightLegScale,
    float hitboxScale,
    Optional<Float> width,
    Optional<Float> height
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PlayerModelSyncPayload> TYPE =
        new CustomPacketPayload.Type<>(ModIdentifierHelper.modId("player_model_sync"));
    
    private static final StreamCodec<ByteBuf, Optional<Float>> OPTIONAL_FLOAT_CODEC =
        ByteBufCodecs.optional(ByteBufCodecs.FLOAT);
    
    public static final StreamCodec<ByteBuf, PlayerModelSyncPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.FLOAT,
        PlayerModelSyncPayload::scale,
        ByteBufCodecs.FLOAT,
        PlayerModelSyncPayload::headScale,
        ByteBufCodecs.FLOAT,
        PlayerModelSyncPayload::bodyScale,
        ByteBufCodecs.FLOAT,
        PlayerModelSyncPayload::leftArmScale,
        ByteBufCodecs.FLOAT,
        PlayerModelSyncPayload::rightArmScale,
        ByteBufCodecs.FLOAT,
        PlayerModelSyncPayload::leftLegScale,
        ByteBufCodecs.FLOAT,
        PlayerModelSyncPayload::rightLegScale,
        ByteBufCodecs.FLOAT,
        PlayerModelSyncPayload::hitboxScale,
        OPTIONAL_FLOAT_CODEC,
        PlayerModelSyncPayload::width,
        OPTIONAL_FLOAT_CODEC,
        PlayerModelSyncPayload::height,
        PlayerModelSyncPayload::new
    );
    
    /**
     * Creates a payload from PlayerModelData.
     */
    public static PlayerModelSyncPayload from(PlayerModelDataComponent.PlayerModelData data) {
        return new PlayerModelSyncPayload(
            data.scale(),
            data.headScale(),
            data.bodyScale(),
            data.leftArmScale(),
            data.rightArmScale(),
            data.leftLegScale(),
            data.rightLegScale(),
            data.hitboxScale(),
            Optional.ofNullable(data.width()),
            Optional.ofNullable(data.height())
        );
    }
    
    /**
     * Converts this payload to PlayerModelData.
     */
    public PlayerModelDataComponent.PlayerModelData toModelData() {
        return new PlayerModelDataComponent.PlayerModelData(
            scale,
            headScale,
            bodyScale,
            leftArmScale,
            rightArmScale,
            leftLegScale,
            rightLegScale,
            hitboxScale,
            width.orElse(null),
            height.orElse(null)
        );
    }
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

