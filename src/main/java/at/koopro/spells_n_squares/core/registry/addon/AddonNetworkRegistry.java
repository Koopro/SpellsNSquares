package at.koopro.spells_n_squares.core.registry.addon;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Helper class for addons to register network payloads.
 * Collects payload registrations and applies them during network registration phase.
 */
public final class AddonNetworkRegistry {
    private final List<PayloadRegistration<?>> registrations = new ArrayList<>();
    
    /**
     * Represents a payload registration.
     */
    private record PayloadRegistration<T extends CustomPacketPayload>(
            CustomPacketPayload.Type<T> type,
            StreamCodec<ByteBuf, T> codec,
            boolean isClientToServer,
            BiConsumer<T, ServerPlayer> serverHandler,
            Consumer<T> clientHandler
    ) {}
    
    /**
     * Registers a client-to-server payload handler.
     * @param type The payload type
     * @param codec The stream codec
     * @param handler The handler function that receives the payload and ServerPlayer
     * @param <T> The payload type
     */
    public <T extends CustomPacketPayload> void registerToServer(
            CustomPacketPayload.Type<T> type,
            StreamCodec<ByteBuf, T> codec,
            BiConsumer<T, ServerPlayer> handler
    ) {
        registrations.add(new PayloadRegistration<>(type, codec, true, handler, null));
    }
    
    /**
     * Registers a server-to-client payload handler.
     * @param type The payload type
     * @param codec The stream codec
     * @param handler The handler function that receives the payload
     * @param <T> The payload type
     */
    public <T extends CustomPacketPayload> void registerToClient(
            CustomPacketPayload.Type<T> type,
            StreamCodec<ByteBuf, T> codec,
            Consumer<T> handler
    ) {
        registrations.add(new PayloadRegistration<>(type, codec, false, null, handler));
    }
    
    /**
     * Applies all registered payloads to the registrar.
     * Called during network registration phase.
     * @param registrar The payload registrar
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void applyRegistrations(PayloadRegistrar registrar) {
        for (PayloadRegistration registration : registrations) {
            if (registration.isClientToServer()) {
                registrar.playToServer(
                    registration.type(),
                    registration.codec(),
                    (payload, context) -> context.enqueueWork(() -> {
                        if (context.player() instanceof ServerPlayer serverPlayer) {
                            ((BiConsumer) registration.serverHandler()).accept(payload, serverPlayer);
                        }
                    })
                );
            } else {
                registrar.playToClient(
                    registration.type(),
                    registration.codec(),
                    (payload, context) -> context.enqueueWork(() -> {
                        if (context.player() != null && context.player().level().isClientSide()) {
                            ((Consumer) registration.clientHandler()).accept(payload);
                        }
                    })
                );
            }
        }
    }
    
    /**
     * Gets the number of registered payloads.
     * @return The count
     */
    public int getRegistrationCount() {
        return registrations.size();
    }
}






