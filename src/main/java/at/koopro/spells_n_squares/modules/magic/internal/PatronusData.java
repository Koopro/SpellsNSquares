package at.koopro.spells_n_squares.modules.magic.internal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Patronus data for a player.
 */
public record PatronusData(
    String animalForm,  // Patronus animal form ID (e.g., "stag", "otter")
    long discoveredDate,
    String discoveryMethod  // "spell", "item", "event", etc.
) {
    public static final Codec<PatronusData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.STRING.optionalFieldOf("animalForm", "").forGetter(PatronusData::animalForm),
            Codec.LONG.optionalFieldOf("discoveredDate", 0L).forGetter(PatronusData::discoveredDate),
            Codec.STRING.optionalFieldOf("discoveryMethod", "").forGetter(PatronusData::discoveryMethod)
        ).apply(instance, PatronusData::new)
    );
    
    /**
     * Creates default empty Patronus data.
     */
    public static PatronusData empty() {
        return new PatronusData("", 0L, "");
    }
    
    /**
     * Checks if the player has discovered their Patronus.
     */
    public boolean hasPatronus() {
        return animalForm != null && !animalForm.isEmpty();
    }
}


