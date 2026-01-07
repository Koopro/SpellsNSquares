package at.koopro.spells_n_squares.core.network;

import at.koopro.spells_n_squares.core.util.registry.ModIdentifierHelper;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Utility class for common screen-opening payload patterns.
 * 
 * <p>Since network payloads are typically implemented as records (which cannot extend classes),
 * this utility provides common methods and patterns for creating payload types and codecs.
 * 
 * <p>Usage example:
 * <pre>{@code
 * public record MyScreenPayload(Data data) implements CustomPacketPayload {
 *     public static final CustomPacketPayload.Type<MyScreenPayload> TYPE =
 *         BaseScreenPayload.createType("my_screen");
 *     
 *     public static final StreamCodec<ByteBuf, MyScreenPayload> STREAM_CODEC = 
 *         BaseScreenPayload.createCodec(...);
 *     
 *     @Override
 *     public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
 *         return TYPE;
 *     }
 * }
 * }</pre>
 */
public final class BaseScreenPayload {
    private BaseScreenPayload() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Creates a CustomPacketPayload.Type for a screen-opening payload.
     * Uses ModIdentifierHelper to ensure consistent namespace.
     * 
     * @param path The path for the payload identifier (e.g., "pensieve_open_screen")
     * @return The payload type
     */
    public static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> createType(String path) {
        return new CustomPacketPayload.Type<>(ModIdentifierHelper.modId(path));
    }
}

