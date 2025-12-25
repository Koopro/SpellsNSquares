package at.koopro.spells_n_squares.core.api;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Interface for network payloads that can self-register.
 * Features implementing this interface can register their own network payloads.
 */
public interface INetworkPayload<T extends CustomPacketPayload> {
    /**
     * Gets the payload type.
     * @return The CustomPacketPayload.Type
     */
    CustomPacketPayload.Type<T> getType();
    
    /**
     * Gets the stream codec for this payload.
     * @return The StreamCodec
     */
    StreamCodec<ByteBuf, T> getCodec();
    
    /**
     * Handles the payload when received.
     * @param payload The payload data
     * @param context The payload context
     */
    void handle(T payload, IPayloadContext context);
    
    /**
     * Registers this payload with the registrar.
     * @param registrar The payload registrar
     */
    default void register(PayloadRegistrar registrar) {
        // Default implementation - can be overridden for custom registration
        if (isClientToServer()) {
            registrar.playToServer(getType(), getCodec(), this::handle);
        } else {
            registrar.playToClient(getType(), getCodec(), this::handle);
        }
    }
    
    /**
     * Determines if this payload is client-to-server or server-to-client.
     * @return true if client-to-server, false if server-to-client
     */
    boolean isClientToServer();
}

















