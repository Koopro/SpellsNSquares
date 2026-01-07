package at.koopro.spells_n_squares.core.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Player identity data including blood status and magical race/type.
 * This data represents a player's magical heritage and identity.
 */
public final class PlayerIdentityData {
    private PlayerIdentityData() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Blood status representing magical heritage.
     */
    public enum BloodStatus {
        PURE_BLOOD("Pure-blood"),
        HALF_BLOOD("Half-blood"),
        MUGGLE_BORN("Muggle-born"),
        SQUIB("Squib");
        
        private final String displayName;
        
        BloodStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public static final Codec<BloodStatus> CODEC = Codec.STRING.xmap(
            value -> {
                for (BloodStatus status : values()) {
                    if (status.name().equalsIgnoreCase(value)) {
                        return status;
                    }
                }
                return HALF_BLOOD; // Default
            },
            status -> status.name()
        );
    }
    
    /**
     * Magical race/type representing the player's magical identity.
     */
    public enum MagicalType {
        WIZARD("Wizard"),
        WITCH("Witch"),
        SQUIB("Squib"),
        WEREWOLF("Werewolf"),
        VEELA("Veela"),
        VAMPIRE("Vampire"),
        GOBLIN("Goblin"),
        HOUSE_ELF("House Elf"),
        GIANT("Half-Giant"),
        CENTAUR("Centaur");
        
        private final String displayName;
        
        MagicalType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public static final Codec<MagicalType> CODEC = Codec.STRING.xmap(
            value -> {
                for (MagicalType type : values()) {
                    if (type.name().equalsIgnoreCase(value)) {
                        return type;
                    }
                }
                return WIZARD; // Default
            },
            type -> type.name()
        );
    }
    
    /**
     * Complete player identity data record.
     */
    public record IdentityData(
        BloodStatus bloodStatus,
        MagicalType magicalType
    ) {
        public static final Codec<IdentityData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                BloodStatus.CODEC
                    .optionalFieldOf("bloodStatus", BloodStatus.HALF_BLOOD)
                    .forGetter(IdentityData::bloodStatus),
                MagicalType.CODEC
                    .optionalFieldOf("magicalType", MagicalType.WIZARD)
                    .forGetter(IdentityData::magicalType)
            ).apply(instance, IdentityData::new)
        );
        
        /**
         * Creates default identity data (Half-blood Wizard).
         */
        public static IdentityData empty() {
            return new IdentityData(BloodStatus.HALF_BLOOD, MagicalType.WIZARD);
        }
        
        /**
         * Creates default identity data based on player gender.
         * Male -> Wizard, Female -> Witch
         */
        public static IdentityData defaultForGender(boolean isMale) {
            return new IdentityData(
                BloodStatus.HALF_BLOOD,
                isMale ? MagicalType.WIZARD : MagicalType.WITCH
            );
        }
        
        /**
         * Updates the blood status.
         */
        public IdentityData withBloodStatus(BloodStatus bloodStatus) {
            // Validation: If Squib type, blood status must be Squib
            if (magicalType == MagicalType.SQUIB && bloodStatus != BloodStatus.SQUIB) {
                return this; // Don't allow invalid combination
            }
            // Validation: If Squib blood status, type must be Squib
            if (bloodStatus == BloodStatus.SQUIB && magicalType != MagicalType.SQUIB) {
                return new IdentityData(bloodStatus, MagicalType.SQUIB);
            }
            return new IdentityData(bloodStatus, magicalType);
        }
        
        /**
         * Updates the magical type.
         */
        public IdentityData withMagicalType(MagicalType magicalType) {
            // Validation: If Squib type, blood status must be Squib
            if (magicalType == MagicalType.SQUIB) {
                return new IdentityData(BloodStatus.SQUIB, magicalType);
            }
            // Validation: If Squib blood status, type must be Squib
            if (bloodStatus == BloodStatus.SQUIB && magicalType != MagicalType.SQUIB) {
                return new IdentityData(BloodStatus.HALF_BLOOD, magicalType); // Reset to valid combination
            }
            return new IdentityData(bloodStatus, magicalType);
        }
    }
}

