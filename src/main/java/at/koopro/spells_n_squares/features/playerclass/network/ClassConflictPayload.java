package at.koopro.spells_n_squares.features.playerclass.network;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import at.koopro.spells_n_squares.features.playerclass.PlayerClass;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Network payload for showing class conflict to client.
 * Sent from server to client when a class conflict is detected.
 */
public record ClassConflictPayload(
    String spellId,
    String newClassName,
    List<String> conflictingClassNames
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClassConflictPayload> TYPE =
        new CustomPacketPayload.Type<>(ModIdentifierHelper.modId("class_conflict"));
    
    public static final StreamCodec<ByteBuf, ClassConflictPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8,
        ClassConflictPayload::spellId,
        ByteBufCodecs.STRING_UTF8,
        ClassConflictPayload::newClassName,
        ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.collection(ArrayList::new)),
        ClassConflictPayload::conflictingClassNames,
        ClassConflictPayload::new
    );
    
    /**
     * Gets the new class.
     */
    public PlayerClass getNewClass() {
        return PlayerClass.fromName(newClassName);
    }
    
    /**
     * Gets the conflicting classes.
     */
    public List<PlayerClass> getConflictingClasses() {
        List<PlayerClass> classes = new ArrayList<>();
        for (String name : conflictingClassNames) {
            PlayerClass clazz = PlayerClass.fromName(name);
            if (clazz != PlayerClass.NONE) {
                classes.add(clazz);
            }
        }
        return classes;
    }
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
















