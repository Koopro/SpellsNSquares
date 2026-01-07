package at.koopro.spells_n_squares.core.util.data;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/**
 * Utility class for common NBT (Named Binary Tag) operations.
 * Provides type-safe helpers for reading and writing common NBT data types using codecs.
 * 
 * <p>Example usage:
 * <pre>{@code
 * CompoundTag tag = new CompoundTag();
 * NBTUtils.writeBlockPos(tag, "position", pos);
 * NBTUtils.writeUUID(tag, "owner", ownerUUID);
 * 
 * BlockPos pos = NBTUtils.readBlockPos(tag, "position");
 * UUID owner = NBTUtils.readUUID(tag, "owner");
 * }</pre>
 */
public final class NBTUtils {
    private NBTUtils() {
        // Utility class - prevent instantiation
    }
    
    // ========== BlockPos Read/Write ==========
    
    /**
     * Writes a BlockPos to a CompoundTag using codec.
     * 
     * @param tag The tag to write to
     * @param key The key to store under
     * @param pos The BlockPos to write
     */
    public static void writeBlockPos(CompoundTag tag, String key, BlockPos pos) {
        if (tag == null || key == null || pos == null) {
            return;
        }
        tag.store(key, BlockPos.CODEC, pos);
    }
    
    /**
     * Reads a BlockPos from a CompoundTag using codec.
     * 
     * @param tag The tag to read from
     * @param key The key to read
     * @return The BlockPos, or null if not found or invalid
     */
    @Nullable
    public static BlockPos readBlockPos(CompoundTag tag, String key) {
        if (tag == null || key == null) {
            return null;
        }
        Optional<BlockPos> result = tag.read(key, BlockPos.CODEC);
        return result.orElse(null);
    }
    
    // ========== UUID Read/Write ==========
    
    /**
     * Writes a UUID to a CompoundTag using codec.
     * 
     * @param tag The tag to write to
     * @param key The key to store under
     * @param uuid The UUID to write
     */
    public static void writeUUID(CompoundTag tag, String key, UUID uuid) {
        if (tag == null || key == null || uuid == null) {
            return;
        }
        tag.store(key, UUIDUtil.CODEC, uuid);
    }
    
    /**
     * Reads a UUID from a CompoundTag using codec.
     * 
     * @param tag The tag to read from
     * @param key The key to read
     * @return The UUID, or null if not found
     */
    @Nullable
    public static UUID readUUID(CompoundTag tag, String key) {
        if (tag == null || key == null) {
            return null;
        }
        Optional<UUID> result = tag.read(key, UUIDUtil.CODEC);
        return result.orElse(null);
    }
    
    // ========== Generic Codec Read/Write ==========
    
    /**
     * Writes a value to a CompoundTag using a codec.
     * 
     * @param tag The tag to write to
     * @param key The key to store under
     * @param codec The codec to use
     * @param value The value to write
     * @param <T> The value type
     */
    public static <T> void write(CompoundTag tag, String key, Codec<T> codec, T value) {
        if (tag == null || key == null || codec == null || value == null) {
            return;
        }
        tag.store(key, codec, value);
    }
    
    /**
     * Reads a value from a CompoundTag using a codec.
     * 
     * @param tag The tag to read from
     * @param key The key to read
     * @param codec The codec to use
     * @param <T> The value type
     * @return The value, or null if not found
     */
    @Nullable
    public static <T> T read(CompoundTag tag, String key, Codec<T> codec) {
        if (tag == null || key == null || codec == null) {
            return null;
        }
        Optional<T> result = tag.read(key, codec);
        return result.orElse(null);
    }
    
    // ========== Tag Operations ==========
    
    /**
     * Merges two CompoundTags, with the source tag taking precedence.
     * 
     * @param target The target tag to merge into
     * @param source The source tag to merge from
     * @return The merged tag (same instance as target)
     */
    public static CompoundTag mergeTags(CompoundTag target, CompoundTag source) {
        if (target == null || source == null) {
            return target;
        }
        for (String key : source.keySet()) {
            Tag value = source.get(key);
            if (value != null) {
                target.put(key, value.copy());
            }
        }
        return target;
    }
    
    /**
     * Creates a deep copy of a CompoundTag.
     * 
     * @param tag The tag to copy
     * @return A new CompoundTag that is a deep copy, or null if input is null
     */
    @Nullable
    public static CompoundTag copyTag(CompoundTag tag) {
        if (tag == null) {
            return null;
        }
        return tag.copy();
    }
    
    /**
     * Checks if a CompoundTag contains a key with a specific type.
     * 
     * @param tag The tag to check
     * @param key The key to check
     * @param type The NBT type to check for
     * @return true if the key exists and has the specified type
     */
    public static boolean contains(CompoundTag tag, String key) {
        return tag != null && key != null && tag.contains(key);
    }
    
    /**
     * Gets a CompoundTag from a CompoundTag, creating it if it doesn't exist.
     * 
     * @param tag The parent tag
     * @param key The key to get or create
     * @return The CompoundTag (existing or newly created)
     */
    public static CompoundTag getOrCreateCompound(CompoundTag tag, String key) {
        if (tag == null || key == null) {
            return new CompoundTag();
        }
        Optional<CompoundTag> result = tag.getCompound(key);
        return result.orElse(new CompoundTag());
    }
    
    /**
     * Gets a ListTag from a CompoundTag, creating it if it doesn't exist.
     * 
     * @param tag The parent tag
     * @param key The key to get or create
     * @param type The type of list elements
     * @return The ListTag (existing or newly created)
     */
    public static ListTag getOrCreateList(CompoundTag tag, String key, int type) {
        if (tag == null || key == null) {
            return new ListTag();
        }
        Optional<ListTag> result = tag.getList(key);
        return result.orElse(new ListTag());
    }
    
    /**
     * Removes a key from a CompoundTag if it exists.
     * 
     * @param tag The tag to modify
     * @param key The key to remove
     */
    public static void remove(CompoundTag tag, String key) {
        if (tag != null && key != null) {
            tag.remove(key);
        }
    }
    
    /**
     * Checks if a CompoundTag is empty (null or has no keys).
     * 
     * @param tag The tag to check
     * @return true if null or empty
     */
    public static boolean isEmpty(CompoundTag tag) {
        return tag == null || tag.isEmpty();
    }
    
    /**
     * Checks if a CompoundTag is not empty (not null and has keys).
     * 
     * @param tag The tag to check
     * @return true if not null and not empty
     */
    public static boolean isNotEmpty(CompoundTag tag) {
        return !isEmpty(tag);
    }
}
