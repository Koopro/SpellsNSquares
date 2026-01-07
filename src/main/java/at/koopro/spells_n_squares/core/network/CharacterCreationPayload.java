package at.koopro.spells_n_squares.core.network;

import at.koopro.spells_n_squares.core.data.PlayerIdentityData;
import at.koopro.spells_n_squares.core.util.registry.ModIdentifierHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Network payload for character creation (setting player identity).
 * Sent from client to server when player confirms their blood status and magical type.
 */
public record CharacterCreationPayload(
    PlayerIdentityData.BloodStatus bloodStatus,
    PlayerIdentityData.MagicalType magicalType
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CharacterCreationPayload> TYPE =
        new CustomPacketPayload.Type<>(ModIdentifierHelper.modId("character_creation"));
    
    private static final StreamCodec<ByteBuf, String> STRING_CODEC = ByteBufCodecs.STRING_UTF8;
    
    public static final StreamCodec<ByteBuf, CharacterCreationPayload> STREAM_CODEC = StreamCodec.composite(
        STRING_CODEC,
        CharacterCreationPayload::bloodStatusName,
        STRING_CODEC,
        CharacterCreationPayload::magicalTypeName,
        (bloodStatusStr, magicalTypeStr) -> {
            PlayerIdentityData.BloodStatus bloodStatus;
            try {
                bloodStatus = PlayerIdentityData.BloodStatus.valueOf(bloodStatusStr);
            } catch (IllegalArgumentException e) {
                bloodStatus = PlayerIdentityData.BloodStatus.HALF_BLOOD; // Default
            }
            
            PlayerIdentityData.MagicalType magicalType;
            try {
                magicalType = PlayerIdentityData.MagicalType.valueOf(magicalTypeStr);
            } catch (IllegalArgumentException e) {
                magicalType = PlayerIdentityData.MagicalType.WIZARD; // Default
            }
            
            return new CharacterCreationPayload(bloodStatus, magicalType);
        }
    );
    
    private String bloodStatusName() {
        return bloodStatus.name();
    }
    
    private String magicalTypeName() {
        return magicalType.name();
    }
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

