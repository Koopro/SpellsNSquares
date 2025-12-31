package at.koopro.spells_n_squares.features.tutorial;

import at.koopro.spells_n_squares.core.data.PlayerDataHelper;
import at.koopro.spells_n_squares.modules.tutorial.internal.TutorialData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Manages the tutorial system for new players.
 * Tracks tutorial progress and displays helpful hints and guidance.
 */
public final class TutorialSystem {
    private TutorialSystem() {
    }
    
    /**
     * Tutorial steps that guide new players.
     */
    public enum TutorialStep {
        WELCOME("welcome", "tutorial.welcome"),
        GET_WAND("get_wand", "tutorial.get_wand"),
        CHOOSE_WAND("choose_wand", "tutorial.choose_wand"),
        CAST_FIRST_SPELL("cast_first_spell", "tutorial.cast_first_spell"),
        LEARN_SPELLS("learn_spells", "tutorial.learn_spells"),
        COMPLETE("complete", null);
        
        private final String id;
        private final String messageKey;
        
        TutorialStep(String id, String messageKey) {
            this.id = id;
            this.messageKey = messageKey;
        }
        
        public String getId() {
            return id;
        }
        
        public String getMessageKey() {
            return messageKey;
        }
        
        public TutorialStep next() {
            TutorialStep[] steps = values();
            int currentIndex = -1;
            for (int i = 0; i < steps.length; i++) {
                if (steps[i] == this) {
                    currentIndex = i;
                    break;
                }
            }
            if (currentIndex >= 0 && currentIndex < steps.length - 1) {
                return steps[currentIndex + 1];
            }
            return COMPLETE;
        }
    }
    
    /**
     * Gets the current tutorial step for a player.
     */
    public static TutorialStep getCurrentStep(Player player) {
        if (player == null || player.level().isClientSide()) {
            return TutorialStep.COMPLETE;
        }
        
        TutorialData tutorialData = PlayerDataHelper.getTutorialData(player);
        String currentStepId = tutorialData.currentStep();
        
        for (TutorialStep step : TutorialStep.values()) {
            if (step.getId().equals(currentStepId)) {
                return step;
            }
        }
        
        // Default to WELCOME for new players
        return TutorialStep.WELCOME;
    }
    
    /**
     * Advances the tutorial to the next step.
     */
    public static void advanceStep(Player player) {
        if (player == null || player.level().isClientSide()) {
            return;
        }
        
        TutorialStep current = getCurrentStep(player);
        if (current == TutorialStep.COMPLETE) {
            return;
        }
        
        TutorialStep next = current.next();
        setStep(player, next);
        
        if (player instanceof ServerPlayer serverPlayer && next.getMessageKey() != null) {
            serverPlayer.sendSystemMessage(
                net.minecraft.network.chat.Component.translatable(next.getMessageKey())
            );
        }
    }
    
    /**
     * Sets the tutorial step for a player.
     */
    public static void setStep(Player player, TutorialStep step) {
        if (player == null || player.level().isClientSide() || step == null) {
            return;
        }
        
        TutorialData current = PlayerDataHelper.getTutorialData(player);
        TutorialData updated = current.withStep(step.getId());
        PlayerDataHelper.setTutorialData(player, updated);
    }
    
    /**
     * Checks if a player has completed the tutorial.
     */
    public static boolean isTutorialComplete(Player player) {
        return getCurrentStep(player) == TutorialStep.COMPLETE;
    }
    
    /**
     * Marks the tutorial as complete for a player.
     */
    public static void completeTutorial(Player player) {
        setStep(player, TutorialStep.COMPLETE);
    }
    
    /**
     * Checks if a player is a new player (hasn't started tutorial).
     */
    public static boolean isNewPlayer(Player player) {
        if (player == null || player.level().isClientSide()) {
            return false;
        }
        
        TutorialData tutorialData = PlayerDataHelper.getTutorialData(player);
        String currentStepId = tutorialData.currentStep();
        return currentStepId == null || currentStepId.isEmpty() || currentStepId.equals(TutorialStep.WELCOME.getId());
    }
    
    /**
     * Shows a tutorial hint to a player.
     */
    public static void showHint(Player player, String hintKey, Object... args) {
        if (player instanceof ServerPlayer serverPlayer) {
            net.minecraft.network.chat.Component message = net.minecraft.network.chat.Component.translatable(
                "tutorial.hint." + hintKey, args
            );
            serverPlayer.sendSystemMessage(message);
        }
    }
}

