package at.koopro.spells_n_squares.features.spell.client;

import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Tracks animation state for spell selection screen elements.
 */
public class AnimationState {
    private final Map<Identifier, Long> spellButtonAppearTimes = new HashMap<>();
    private float scrollAnimationProgress = 1.0f;
    private long lastScrollTime = 0;
    private final Map<Identifier, Float> hoverProgress = new HashMap<>();
    private final Map<Identifier, Float> selectionGlowProgress = new HashMap<>();
    private long tooltipShowTime = 0;
    private boolean tooltipVisible = false;
    
    // Animation durations (in milliseconds)
    private static final long FADE_IN_DURATION = 200; // 200ms
    private static final long SCROLL_ANIMATION_DURATION = 150; // 150ms
    private static final long HOVER_ANIMATION_DURATION = 100; // 100ms
    private static final long SELECTION_GLOW_DURATION = 300; // 300ms
    private static final long TOOLTIP_FADE_DURATION = 150; // 150ms
    
    /**
     * Initializes animation state when screen opens.
     */
    public void initialize(long currentTime) {
        spellButtonAppearTimes.clear();
        scrollAnimationProgress = 1.0f;
        lastScrollTime = currentTime;
        hoverProgress.clear();
        selectionGlowProgress.clear();
        tooltipShowTime = 0;
        tooltipVisible = false;
    }
    
    /**
     * Records when a spell button appears (for fade-in animation).
     */
    public void recordSpellButtonAppear(Identifier spellId, long currentTime) {
        if (!spellButtonAppearTimes.containsKey(spellId)) {
            spellButtonAppearTimes.put(spellId, currentTime);
        }
    }
    
    /**
     * Gets the fade-in progress for a spell button.
     */
    public float getSpellButtonFadeIn(Identifier spellId, long currentTime) {
        Long appearTime = spellButtonAppearTimes.get(spellId);
        if (appearTime == null) {
            return 1.0f; // Already visible
        }
        return AnimationHelper.fadeInProgress(appearTime, FADE_IN_DURATION, currentTime);
    }
    
    /**
     * Starts a scroll animation.
     */
    public void startScrollAnimation(long currentTime) {
        scrollAnimationProgress = 0.0f;
        lastScrollTime = currentTime;
    }
    
    /**
     * Gets the scroll animation progress.
     */
    public float getScrollAnimationProgress(long currentTime) {
        if (scrollAnimationProgress < 1.0f) {
            long elapsed = currentTime - lastScrollTime;
            scrollAnimationProgress = Math.min(1.0f, (float) elapsed / SCROLL_ANIMATION_DURATION);
        }
        return AnimationHelper.applyEasing(scrollAnimationProgress, AnimationHelper.Easing.EASE_OUT);
    }
    
    /**
     * Updates hover progress for a spell button.
     */
    public void updateHover(Identifier spellId, boolean isHovered, long currentTime) {
        float currentProgress = hoverProgress.getOrDefault(spellId, 0.0f);
        float targetProgress = isHovered ? 1.0f : 0.0f;
        
        if (Math.abs(currentProgress - targetProgress) < 0.01f) {
            hoverProgress.put(spellId, targetProgress);
            return;
        }
        
        float delta = (currentTime - lastScrollTime) / (float) HOVER_ANIMATION_DURATION;
        if (isHovered) {
            currentProgress = Math.min(1.0f, currentProgress + delta);
        } else {
            currentProgress = Math.max(0.0f, currentProgress - delta);
        }
        hoverProgress.put(spellId, currentProgress);
    }
    
    /**
     * Gets the hover progress for a spell button.
     */
    public float getHoverProgress(Identifier spellId) {
        return hoverProgress.getOrDefault(spellId, 0.0f);
    }
    
    /**
     * Updates selection glow progress.
     */
    public void updateSelectionGlow(Identifier spellId, boolean isSelected, long currentTime) {
        float currentProgress = selectionGlowProgress.getOrDefault(spellId, 0.0f);
        float targetProgress = isSelected ? 1.0f : 0.0f;
        
        if (Math.abs(currentProgress - targetProgress) < 0.01f) {
            selectionGlowProgress.put(spellId, targetProgress);
            return;
        }
        
        float delta = (currentTime - lastScrollTime) / (float) SELECTION_GLOW_DURATION;
        if (isSelected) {
            currentProgress = Math.min(1.0f, currentProgress + delta);
        } else {
            currentProgress = Math.max(0.0f, currentProgress - delta);
        }
        selectionGlowProgress.put(spellId, currentProgress);
    }
    
    /**
     * Gets the selection glow progress for a spell button.
     */
    public float getSelectionGlowProgress(Identifier spellId) {
        return selectionGlowProgress.getOrDefault(spellId, 0.0f);
    }
    
    /**
     * Shows the tooltip (starts fade-in).
     */
    public void showTooltip(long currentTime) {
        if (!tooltipVisible) {
            tooltipShowTime = currentTime;
            tooltipVisible = true;
        }
    }
    
    /**
     * Hides the tooltip (starts fade-out).
     */
    public void hideTooltip(long currentTime) {
        if (tooltipVisible) {
            tooltipShowTime = currentTime;
            tooltipVisible = false;
        }
    }
    
    /**
     * Gets the tooltip fade progress.
     */
    public float getTooltipFadeProgress(long currentTime) {
        if (tooltipVisible) {
            return AnimationHelper.fadeInProgress(tooltipShowTime, TOOLTIP_FADE_DURATION, currentTime);
        } else {
            return AnimationHelper.fadeOutProgress(tooltipShowTime, TOOLTIP_FADE_DURATION, currentTime);
        }
    }
    
    /**
     * Cleans up animation state for spells that are no longer visible.
     */
    public void cleanup(java.util.Set<Identifier> visibleSpells) {
        hoverProgress.entrySet().removeIf(entry -> !visibleSpells.contains(entry.getKey()));
        selectionGlowProgress.entrySet().removeIf(entry -> !visibleSpells.contains(entry.getKey()));
        spellButtonAppearTimes.entrySet().removeIf(entry -> !visibleSpells.contains(entry.getKey()));
    }
}

