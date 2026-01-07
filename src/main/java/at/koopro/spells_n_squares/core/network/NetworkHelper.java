package at.koopro.spells_n_squares.core.network;

import at.koopro.spells_n_squares.core.util.player.PlayerValidationUtils;
import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Helper utilities for network registration.
 * Provides consistent patterns for registering client-to-server and server-to-client payloads.
 */
public final class NetworkHelper {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    /**
     * Registers a client-to-server payload.
     * @param registrar The payload registrar
     * @param type The payload type
     * @param codec The stream codec
     * @param handler The handler function that receives the payload and ServerPlayer
     * @param <T> The payload type
     */
    public static <T extends CustomPacketPayload> void registerToServer(
            PayloadRegistrar registrar,
            CustomPacketPayload.Type<T> type,
            StreamCodec<ByteBuf, T> codec,
            BiConsumer<T, ServerPlayer> handler) {
        registrar.playToServer(type, codec, (payload, context) -> {
            context.enqueueWork(() -> {
                ServerPlayer serverPlayer = PlayerValidationUtils.asServerPlayer(context.player());
                if (serverPlayer != null) {
                    try {
                        handler.accept(payload, serverPlayer);
                    } catch (Exception e) {
                        LOGGER.error("Error handling payload {}: {}", 
                            type.id(), e.getMessage(), e);
                    }
                }
            });
        });
    }
    
    /**
     * Registers a server-to-client payload.
     * @param registrar The payload registrar
     * @param type The payload type
     * @param codec The stream codec
     * @param handler The handler function that receives the payload
     * @param <T> The payload type
     */
    public static <T extends CustomPacketPayload> void registerToClient(
            PayloadRegistrar registrar,
            CustomPacketPayload.Type<T> type,
            StreamCodec<ByteBuf, T> codec,
            Consumer<T> handler) {
        registrar.playToClient(type, codec, (payload, context) -> {
            context.enqueueWork(() -> {
                if (context.player() != null && PlayerValidationUtils.isClientSide(context.player())) {
                    try {
                        handler.accept(payload);
                    } catch (Exception e) {
                        LOGGER.error("Error handling payload {}: {}", 
                            type.id(), e.getMessage(), e);
                    }
                }
            });
        });
    }
}

