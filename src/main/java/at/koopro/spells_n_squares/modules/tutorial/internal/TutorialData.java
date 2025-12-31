package at.koopro.spells_n_squares.modules.tutorial.internal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Tutorial progress data for a player.
 */
public record TutorialData(
    String currentStep,  // Current tutorial step ID
    boolean completed    // Whether tutorial is completed
) {
    public static final Codec<TutorialData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.STRING.optionalFieldOf("currentStep", "").forGetter(TutorialData::currentStep),
            Codec.BOOL.optionalFieldOf("completed", false).forGetter(TutorialData::completed)
        ).apply(instance, TutorialData::new)
    );
    
    /**
     * Creates default empty tutorial data.
     */
    public static TutorialData empty() {
        return new TutorialData("", false);
    }
    
    /**
     * Updates the current step.
     */
    public TutorialData withStep(String stepId) {
        boolean isComplete = "complete".equals(stepId);
        return new TutorialData(stepId, isComplete);
    }
    
    /**
     * Marks tutorial as completed.
     */
    public TutorialData withCompleted(boolean completed) {
        return new TutorialData(currentStep, completed);
    }
}


