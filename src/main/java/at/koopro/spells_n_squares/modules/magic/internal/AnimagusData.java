package at.koopro.spells_n_squares.modules.magic.internal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Animagus data for a player.
 */
public record AnimagusData(
    String animalForm,  // Animal form ID (e.g., "cat", "dog")
    long registrationDate,
    String registrationMethod,  // "spell", "item", "command", etc.
    boolean isTransformed  // Whether currently transformed
) {
    public static final Codec<AnimagusData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.STRING.optionalFieldOf("animalForm", "").forGetter(AnimagusData::animalForm),
            Codec.LONG.optionalFieldOf("registrationDate", 0L).forGetter(AnimagusData::registrationDate),
            Codec.STRING.optionalFieldOf("registrationMethod", "").forGetter(AnimagusData::registrationMethod),
            Codec.BOOL.optionalFieldOf("isTransformed", false).forGetter(AnimagusData::isTransformed)
        ).apply(instance, AnimagusData::new)
    );
    
    /**
     * Creates default empty Animagus data.
     */
    public static AnimagusData empty() {
        return new AnimagusData("", 0L, "", false);
    }
    
    /**
     * Checks if the player is registered as an Animagus.
     */
    public boolean isRegistered() {
        return animalForm != null && !animalForm.isEmpty();
    }
    
    /**
     * Creates a new data with updated transformation state.
     */
    public AnimagusData withTransformed(boolean transformed) {
        return new AnimagusData(animalForm, registrationDate, registrationMethod, transformed);
    }
}


