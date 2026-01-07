package at.koopro.spells_n_squares.core.util.network;

import at.koopro.spells_n_squares.core.util.collection.CollectionFactory;
import at.koopro.spells_n_squares.core.util.registry.ModIdentifierHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Utility class for creating network payload codecs and common patterns.
 * Reduces boilerplate when creating network payloads.
 * 
 * <p>Example usage:
 * <pre>{@code
 * public record MyPayload(Identifier id, int count) implements CustomPacketPayload {
 *     public static final CustomPacketPayload.Type<MyPayload> TYPE =
 *         NetworkPayloadHelper.createType("my_payload");
 *     
 *     public static final StreamCodec<ByteBuf, MyPayload> STREAM_CODEC =
 *         NetworkPayloadHelper.composite(
 *             NetworkPayloadHelper.IDENTIFIER_CODEC, MyPayload::id,
 *             ByteBufCodecs.VAR_INT, MyPayload::count,
 *             MyPayload::new
 *         );
 * }
 * }</pre>
 */
public final class NetworkPayloadHelper {
    private NetworkPayloadHelper() {
        // Utility class - prevent instantiation
    }
    
    // ========== Common Codecs ==========
    
    /**
     * Codec for Identifier (ResourceLocation).
     * Converts between Identifier and String.
     */
    public static final StreamCodec<ByteBuf, Identifier> IDENTIFIER_CODEC =
        ByteBufCodecs.STRING_UTF8.map(Identifier::parse, Identifier::toString);
    
    /**
     * Codec for Optional Identifier.
     */
    public static final StreamCodec<ByteBuf, Optional<Identifier>> OPTIONAL_IDENTIFIER_CODEC =
        ByteBufCodecs.optional(IDENTIFIER_CODEC);
    
    // ========== Type Creation ==========
    
    /**
     * Creates a CustomPacketPayload.Type with the given identifier name.
     * 
     * @param name The identifier name (will be prefixed with mod ID)
     * @param <T> The payload type
     * @return A new CustomPacketPayload.Type
     */
    public static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> createType(String name) {
        return new CustomPacketPayload.Type<>(ModIdentifierHelper.modId(name));
    }
    
    // ========== Collection Codecs ==========
    
    /**
     * Creates a codec for a List of Identifiers.
     * 
     * @return A StreamCodec for List&lt;Identifier&gt;
     */
    public static StreamCodec<ByteBuf, List<Identifier>> identifierListCodec() {
        return IDENTIFIER_CODEC.apply(ByteBufCodecs.collection(CollectionFactory::createList));
    }
    
    /**
     * Creates a codec for a List of Optional Identifiers.
     * 
     * @return A StreamCodec for List&lt;Optional&lt;Identifier&gt;&gt;
     */
    public static StreamCodec<ByteBuf, List<Optional<Identifier>>> optionalIdentifierListCodec() {
        return OPTIONAL_IDENTIFIER_CODEC.apply(ByteBufCodecs.collection(CollectionFactory::createList));
    }
    
    /**
     * Creates a codec for a Set of Identifiers.
     * 
     * @return A StreamCodec for Set&lt;Identifier&gt;
     */
    public static StreamCodec<ByteBuf, Set<Identifier>> identifierSetCodec() {
        return IDENTIFIER_CODEC.apply(ByteBufCodecs.collection(CollectionFactory::createSet));
    }
    
    /**
     * Creates a codec for a Map of Identifier to Integer.
     * 
     * @return A StreamCodec for Map&lt;Identifier, Integer&gt;
     */
    public static StreamCodec<ByteBuf, Map<Identifier, Integer>> identifierIntMapCodec() {
        return ByteBufCodecs.map(
            CollectionFactory::createMap,
            IDENTIFIER_CODEC,
            ByteBufCodecs.VAR_INT
        );
    }
    
    /**
     * Creates a codec for a List of a given type.
     * 
     * @param elementCodec The codec for list elements
     * @param <T> The element type
     * @return A StreamCodec for List&lt;T&gt;
     */
    public static <T> StreamCodec<ByteBuf, List<T>> listCodec(StreamCodec<ByteBuf, T> elementCodec) {
        return elementCodec.apply(ByteBufCodecs.collection(CollectionFactory::createList));
    }
    
    /**
     * Creates a codec for a Set of a given type.
     * 
     * @param elementCodec The codec for set elements
     * @param <T> The element type
     * @return A StreamCodec for Set&lt;T&gt;
     */
    public static <T> StreamCodec<ByteBuf, Set<T>> setCodec(StreamCodec<ByteBuf, T> elementCodec) {
        return elementCodec.apply(ByteBufCodecs.collection(CollectionFactory::createSet));
    }
    
    /**
     * Creates a codec for a Map of given key and value types.
     * 
     * @param keyCodec The codec for map keys
     * @param valueCodec The codec for map values
     * @param <K> The key type
     * @param <V> The value type
     * @return A StreamCodec for Map&lt;K, V&gt;
     */
    public static <K, V> StreamCodec<ByteBuf, Map<K, V>> mapCodec(
            StreamCodec<ByteBuf, K> keyCodec,
            StreamCodec<ByteBuf, V> valueCodec) {
        return ByteBufCodecs.map(CollectionFactory::createMap, keyCodec, valueCodec);
    }
    
    // ========== Composite Codec Builder ==========
    
    /**
     * Creates a composite codec for a payload with two fields.
     * 
     * @param codec1 The codec for the first field
     * @param getter1 The getter for the first field
     * @param codec2 The codec for the second field
     * @param getter2 The getter for the second field
     * @param constructor The constructor for the payload
     * @param <T> The payload type
     * @param <F1> The type of the first field
     * @param <F2> The type of the second field
     * @return A StreamCodec for the payload
     */
    public static <T, F1, F2> StreamCodec<ByteBuf, T> composite(
            StreamCodec<ByteBuf, F1> codec1, java.util.function.Function<T, F1> getter1,
            StreamCodec<ByteBuf, F2> codec2, java.util.function.Function<T, F2> getter2,
            java.util.function.BiFunction<F1, F2, T> constructor) {
        return StreamCodec.composite(
            codec1, getter1,
            codec2, getter2,
            constructor
        );
    }
    
    /**
     * Creates a composite codec for a payload with three fields.
     * 
     * @param codec1 The codec for the first field
     * @param getter1 The getter for the first field
     * @param codec2 The codec for the second field
     * @param getter2 The getter for the second field
     * @param codec3 The codec for the third field
     * @param getter3 The getter for the third field
     * @param constructor The constructor for the payload
     * @param <T> The payload type
     * @param <F1> The type of the first field
     * @param <F2> The type of the second field
     * @param <F3> The type of the third field
     * @return A StreamCodec for the payload
     */
    public static <T, F1, F2, F3> StreamCodec<ByteBuf, T> composite(
            StreamCodec<ByteBuf, F1> codec1, java.util.function.Function<T, F1> getter1,
            StreamCodec<ByteBuf, F2> codec2, java.util.function.Function<T, F2> getter2,
            StreamCodec<ByteBuf, F3> codec3, java.util.function.Function<T, F3> getter3,
            Function3<F1, F2, F3, T> constructor) {
        return StreamCodec.composite(
            codec1, getter1,
            codec2, getter2,
            codec3, getter3,
            constructor::apply
        );
    }
    
    /**
     * Functional interface for 3-argument constructors.
     */
    @FunctionalInterface
    public interface Function3<A, B, C, R> {
        R apply(A a, B b, C c);
    }
}

