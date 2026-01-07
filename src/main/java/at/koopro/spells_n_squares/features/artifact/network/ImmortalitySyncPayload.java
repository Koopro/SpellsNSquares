package at.koopro.spells_n_squares.features.artifact.network;

import at.koopro.spells_n_squares.core.util.registry.ModIdentifierHelper;
import at.koopro.spells_n_squares.features.artifact.ImmortalityData;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Network payload for syncing immortality data from server to client.
 */
public record ImmortalitySyncPayload(
    int ticksRemaining,
    boolean hasEverDrunk
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ImmortalitySyncPayload> TYPE =
        new CustomPacketPayload.Type<>(ModIdentifierHelper.modId("immortality_sync"));
    
    public static final StreamCodec<ByteBuf, ImmortalitySyncPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        ImmortalitySyncPayload::ticksRemaining,
        ByteBufCodecs.BOOL,
        ImmortalitySyncPayload::hasEverDrunk,
        ImmortalitySyncPayload::new
    );
    
    /**
     * Creates a payload from ImmortalityComponent.
     */
    public static ImmortalitySyncPayload from(ImmortalityData.ImmortalityComponent data) {
        return new ImmortalitySyncPayload(data.ticksRemaining(), data.hasEverDrunk());
    }
    
    /**
     * Converts this payload to ImmortalityComponent.
     */
    public ImmortalityData.ImmortalityComponent toComponent() {
        return new ImmortalityData.ImmortalityComponent(ticksRemaining, hasEverDrunk);
    }
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}


