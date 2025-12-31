package at.koopro.spells_n_squares.features.fx.shader.strategies;

import at.koopro.spells_n_squares.features.fx.system.PostProcessingManager;
import at.koopro.spells_n_squares.features.fx.ScreenEffectManager;
import at.koopro.spells_n_squares.features.fx.handler.ShaderEffectHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for shader effect strategies.
 * Maps effect names (and aliases) to their strategy implementations.
 */
public final class ShaderEffectStrategyRegistry {
    private static final Map<String, ShaderEffectStrategy> strategies = new HashMap<>();
    private static boolean initialized = false;
    
    private ShaderEffectStrategyRegistry() {
    }
    
    /**
     * Initializes the strategy registry with all available effects.
     */
    public static void initialize() {
        if (initialized) {
            return;
        }
        
        // Register all strategies
        registerStrategy(new NoIntensityEffectStrategy("spell_cast", ShaderEffectHandler::triggerSpellCastEffect));
        registerStrategy(new NoIntensityEffectStrategy("invisibility", ShaderEffectHandler::triggerInvisibilityDistortion));
        registerStrategy(new NoIntensityEffectStrategy("time_distortion", ShaderEffectHandler::triggerTimeDistortion));
        registerStrategy(new NoIntensityEffectStrategy("magical_aura", ShaderEffectHandler::triggerMagicalAura));
        
        registerStrategy(new HueMaxStrategy());
        registerStrategy(new InvertedColorsStrategy());
        registerStrategy(new GrayscaleStrategy());
        registerStrategy(new ChromaticAberrationStrategy());
        registerStrategy(new SepiaStrategy());
        registerStrategy(new PolaroidStrategy());
        registerStrategy(new RetroStrategy());
        registerStrategy(new BlackAndWhiteStrategy());
        registerStrategy(new SaturatedStrategy());
        registerStrategy(new GlitchStrategy());
        registerStrategy(new KaleidoscopeStrategy());
        registerStrategy(new RgbShiftStrategy());
        registerStrategy(new WaveDistortionStrategy());
        registerStrategy(new BloomStrategy());
        registerStrategy(new EdgeDetectionStrategy());
        registerStrategy(new PixelationStrategy());
        registerStrategy(new HeatHazeStrategy());
        registerStrategy(new ColorCycleStrategy());
        registerStrategy(new MirrorStrategy());
        registerStrategy(new NoiseStrategy());
        registerStrategy(new ZoomBlurStrategy());
        registerStrategy(new NightVisionStrategy());
        registerStrategy(new AcidTripStrategy());
        registerStrategy(new PulsingGlowStrategy());
        registerStrategy(new ScrollingStripesStrategy());
        registerStrategy(new WarpingVortexStrategy());
        registerStrategy(new ParticleRainStrategy());
        registerStrategy(new ColorWaveStrategy());
        registerStrategy(new RippleEffectStrategy());
        registerStrategy(new BreathingVignetteStrategy());
        registerStrategy(new AnimatedNoiseStrategy());
        registerStrategy(new RotatingKaleidoscopeStrategy());
        registerStrategy(new EnergyPulseStrategy());
        
        initialized = true;
    }
    
    /**
     * Registers a strategy and all its aliases.
     */
    private static void registerStrategy(ShaderEffectStrategy strategy) {
        strategies.put(strategy.getPrimaryName().toLowerCase(), strategy);
        for (String alias : strategy.getAliases()) {
            strategies.put(alias.toLowerCase(), strategy);
        }
    }
    
    /**
     * Gets a strategy for the given effect type name.
     * @param effectType The effect type name (case-insensitive)
     * @return The strategy, or null if not found
     */
    public static ShaderEffectStrategy getStrategy(String effectType) {
        if (!initialized) {
            initialize();
        }
        return strategies.get(effectType.toLowerCase());
    }
    
    /**
     * Strategy for effects that don't take intensity parameter.
     */
    private static class NoIntensityEffectStrategy implements ShaderEffectStrategy {
        private final String name;
        private final Runnable trigger;
        private final String[] aliases;
        
        public NoIntensityEffectStrategy(String name, Runnable trigger, String... aliases) {
            this.name = name;
            this.trigger = trigger;
            this.aliases = aliases;
        }
        
        @Override
        public void trigger(float intensity) {
            trigger.run();
        }
        
        @Override
        public String getPrimaryName() {
            return name;
        }
        
        @Override
        public String[] getAliases() {
            return aliases;
        }
    }
    
    // Individual strategy implementations
    private static class HueMaxStrategy implements ShaderEffectStrategy {
        @Override
        public void trigger(float intensity) {
            // Rainbow/vibrant overlay - cycle through colors
            int[] vibrantColors = {
                0xFFFF0000, // Red
                0xFFFF00FF, // Magenta
                0xFF0000FF, // Blue
                0xFF00FFFF, // Cyan
                0xFF00FF00, // Green
                0xFFFFFF00  // Yellow
            };
            int color = vibrantColors[(int)(System.currentTimeMillis() / 200) % vibrantColors.length];
            float opacity = Math.min(0.4f, intensity * 0.3f);
            ScreenEffectManager.triggerOverlay(color, opacity, 40, 
                ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
        }
        
        @Override
        public String getPrimaryName() {
            return "hue_max";
        }
        
        @Override
        public String[] getAliases() {
            return new String[]{"vibrant"};
        }
    }
    
    private static class InvertedColorsStrategy extends BaseShaderEffectStrategy {
        public InvertedColorsStrategy() {
            super(PostProcessingManager.INVERTED_COLORS_POST_SHADER, 30);
        }
        
        @Override
        public String getPrimaryName() {
            return "inverted";
        }
        
        @Override
        public String[] getAliases() {
            return new String[]{"invert", "negative"};
        }
        
        @Override
        protected void triggerFallback(float intensity) {
            float opacity = Math.min(1.0f, intensity * 0.8f);
            ScreenEffectManager.triggerOverlay(0xFFFFFFFF, opacity, 30, 
                ScreenEffectManager.ScreenOverlay.OverlayType.FLASH);
            ScreenEffectManager.triggerShake(0.02f * intensity, 15);
        }
    }
    
    private static class GrayscaleStrategy extends BaseShaderEffectStrategy {
        public GrayscaleStrategy() {
            super(PostProcessingManager.GRAYSCALE_POST_SHADER, 35);
        }
        
        @Override
        public String getPrimaryName() {
            return "grayscale";
        }
        
        @Override
        protected void triggerFallback(float intensity) {
            float opacity = Math.min(0.5f, intensity * 0.4f);
            ScreenEffectManager.triggerOverlay(0xFF808080, opacity, 35, 
                ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
        }
    }
    
    private static class ChromaticAberrationStrategy extends BaseShaderEffectStrategy {
        public ChromaticAberrationStrategy() {
            super(PostProcessingManager.CHROMATIC_ABERRATION_POST_SHADER, 25);
        }
        
        @Override
        public String getPrimaryName() {
            return "chromatic";
        }
        
        @Override
        public String[] getAliases() {
            return new String[]{"chromatic_aberration"};
        }
        
        @Override
        protected void triggerFallback(float intensity) {
            float opacity = Math.min(0.3f, intensity * 0.25f);
            ScreenEffectManager.triggerOverlay(0xFFFF0000, opacity, 20, 
                ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
            ScreenEffectManager.triggerShake(0.01f * intensity, 10);
        }
    }
    
    private static class SepiaStrategy extends BaseShaderEffectStrategy {
        public SepiaStrategy() {
            super(PostProcessingManager.SEPIA_POST_SHADER, 30);
        }
        
        @Override
        public String getPrimaryName() {
            return "sepia";
        }
        
        @Override
        protected void triggerFallback(float intensity) {
            float opacity = Math.min(0.35f, intensity * 0.3f);
            ScreenEffectManager.triggerOverlay(0xFF8B4513, opacity, 30, 
                ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
        }
    }
    
    private static class PolaroidStrategy extends BaseShaderEffectStrategy {
        public PolaroidStrategy() {
            super(PostProcessingManager.POLAROID_POST_SHADER, 30);
        }
        
        @Override
        public String getPrimaryName() {
            return "polaroid";
        }
        
        @Override
        protected void triggerFallback(float intensity) {
            float opacity = Math.min(0.4f, intensity * 0.35f);
            ScreenEffectManager.triggerOverlay(0xFFD4A574, opacity, 30, 
                ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
        }
    }
    
    private static class RetroStrategy extends BaseShaderEffectStrategy {
        public RetroStrategy() {
            super(PostProcessingManager.RETRO_POST_SHADER, 35);
        }
        
        @Override
        public String getPrimaryName() {
            return "retro";
        }
        
        @Override
        public String[] getAliases() {
            return new String[]{"vintage"};
        }
        
        @Override
        protected void triggerFallback(float intensity) {
            float opacity = Math.min(0.3f, intensity * 0.25f);
            ScreenEffectManager.triggerOverlay(0xFFFFCC99, opacity, 35, 
                ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
            ScreenEffectManager.triggerShake(0.01f * intensity, 10);
        }
    }
    
    private static class BlackAndWhiteStrategy extends BaseShaderEffectStrategy {
        public BlackAndWhiteStrategy() {
            super(PostProcessingManager.BLACK_AND_WHITE_POST_SHADER, 35);
        }
        
        @Override
        public String getPrimaryName() {
            return "black_and_white";
        }
        
        @Override
        public String[] getAliases() {
            return new String[]{"bw"};
        }
        
        @Override
        protected void triggerFallback(float intensity) {
            float opacity = Math.min(0.6f, intensity * 0.5f);
            ScreenEffectManager.triggerOverlay(0xFF808080, opacity, 35, 
                ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
        }
    }
    
    private static class SaturatedStrategy extends BaseShaderEffectStrategy {
        public SaturatedStrategy() {
            super(PostProcessingManager.SATURATED_POST_SHADER, 30);
        }
        
        @Override
        public String getPrimaryName() {
            return "saturated";
        }
        
        @Override
        public String[] getAliases() {
            return new String[]{"saturation"};
        }
        
        @Override
        protected void triggerFallback(float intensity) {
            float opacity = Math.min(0.25f, intensity * 0.2f);
            int[] vibrantColors = {0xFFFF0000, 0xFF00FF00, 0xFF0000FF};
            int color = vibrantColors[(int)(System.currentTimeMillis() / 300) % vibrantColors.length];
            ScreenEffectManager.triggerOverlay(color, opacity, 30, 
                ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
        }
    }
    
    private static class GlitchStrategy extends BaseShaderEffectStrategy {
        public GlitchStrategy() {
            super(PostProcessingManager.GLITCH_POST_SHADER, 25);
        }
        
        @Override
        public String getPrimaryName() {
            return "glitch";
        }
        
        @Override
        public String[] getAliases() {
            return new String[]{"corruption"};
        }
        
        @Override
        protected void triggerFallback(float intensity) {
            float opacity = Math.min(0.3f, intensity * 0.25f);
            ScreenEffectManager.triggerOverlay(0xFFFF00FF, opacity, 20, 
                ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
            ScreenEffectManager.triggerShake(0.02f * intensity, 10);
        }
    }
    
    private static class KaleidoscopeStrategy extends BaseShaderEffectStrategy {
        public KaleidoscopeStrategy() {
            super(PostProcessingManager.KALEIDOSCOPE_POST_SHADER, 35);
        }
        
        @Override
        public String getPrimaryName() {
            return "kaleidoscope";
        }
        
        @Override
        public String[] getAliases() {
            return new String[]{"kaleido"};
        }
        
        @Override
        protected void triggerFallback(float intensity) {
            float opacity = Math.min(0.25f, intensity * 0.2f);
            ScreenEffectManager.triggerOverlay(0xFFFF00FF, opacity, 35, 
                ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
        }
    }
    
    private static class RgbShiftStrategy extends BaseShaderEffectStrategy {
        public RgbShiftStrategy() {
            super(PostProcessingManager.RGB_SHIFT_POST_SHADER, 30);
        }
        
        @Override
        public String getPrimaryName() {
            return "rgb_shift";
        }
        
        @Override
        public String[] getAliases() {
            return new String[]{"rgb"};
        }
        
        @Override
        protected void triggerFallback(float intensity) {
            float opacity = Math.min(0.2f, intensity * 0.15f);
            ScreenEffectManager.triggerOverlay(0xFFFF0000, opacity, 25, 
                ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
        }
    }
    
    private static class WaveDistortionStrategy extends BaseShaderEffectStrategy {
        public WaveDistortionStrategy() {
            super(PostProcessingManager.WAVE_DISTORTION_POST_SHADER, 30);
        }
        
        @Override
        public String getPrimaryName() {
            return "wave";
        }
        
        @Override
        public String[] getAliases() {
            return new String[]{"wave_distortion", "ripple"};
        }
        
        @Override
        protected void triggerFallback(float intensity) {
            float opacity = Math.min(0.2f, intensity * 0.15f);
            ScreenEffectManager.triggerOverlay(0xFF00FFFF, opacity, 30, 
                ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
        }
    }
    
    private static class BloomStrategy extends BaseShaderEffectStrategy {
        public BloomStrategy() {
            super(PostProcessingManager.BLOOM_POST_SHADER, 30);
        }
        
        @Override
        public String getPrimaryName() {
            return "bloom";
        }
        
        @Override
        public String[] getAliases() {
            return new String[]{"glow"};
        }
        
        @Override
        protected void triggerFallback(float intensity) {
            float opacity = Math.min(0.15f, intensity * 0.12f);
            ScreenEffectManager.triggerOverlay(0xFFFFFFFF, opacity, 30, 
                ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
        }
    }
    
    private static class EdgeDetectionStrategy extends BaseShaderEffectStrategy {
        public EdgeDetectionStrategy() {
            super(PostProcessingManager.EDGE_DETECTION_POST_SHADER, 35);
        }
        
        @Override
        public String getPrimaryName() {
            return "edge";
        }
        
        @Override
        public String[] getAliases() {
            return new String[]{"edge_detection", "outline"};
        }
        
        @Override
        protected void triggerFallback(float intensity) {
            float opacity = Math.min(0.4f, intensity * 0.35f);
            ScreenEffectManager.triggerOverlay(0xFF000000, opacity, 35, 
                ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
        }
    }
    
    private static class PixelationStrategy extends BaseShaderEffectStrategy {
        public PixelationStrategy() {
            super(PostProcessingManager.PIXELATION_POST_SHADER, 30);
        }
        
        @Override
        public String getPrimaryName() {
            return "pixelation";
        }
        
        @Override
        public String[] getAliases() {
            return new String[]{"pixel"};
        }
        
        @Override
        protected void triggerFallback(float intensity) {
            float opacity = Math.min(0.2f, intensity * 0.15f);
            ScreenEffectManager.triggerOverlay(0xFF808080, opacity, 30, 
                ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
        }
    }
    
    private static class HeatHazeStrategy extends BaseShaderEffectStrategy {
        public HeatHazeStrategy() {
            super(PostProcessingManager.HEAT_HAZE_POST_SHADER, 30);
        }
        
        @Override
        public String getPrimaryName() {
            return "heat_haze";
        }
        
        @Override
        public String[] getAliases() {
            return new String[]{"heat", "haze"};
        }
        
        @Override
        protected void triggerFallback(float intensity) {
            float opacity = Math.min(0.2f, intensity * 0.15f);
            ScreenEffectManager.triggerOverlay(0xFFFF8800, opacity, 30, 
                ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
        }
    }
    
    private static class ColorCycleStrategy extends BaseShaderEffectStrategy {
        public ColorCycleStrategy() {
            super(PostProcessingManager.COLOR_CYCLE_POST_SHADER, 40);
        }
        
        @Override
        public String getPrimaryName() {
            return "color_cycle";
        }
        
        @Override
        public String[] getAliases() {
            return new String[]{"cycle", "rainbow"};
        }
        
        @Override
        protected void triggerFallback(float intensity) {
            float opacity = Math.min(0.25f, intensity * 0.2f);
            int[] cycleColors = {0xFFFF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF, 0xFF00FF00, 0xFFFFFF00};
            int color = cycleColors[(int)(System.currentTimeMillis() / 200) % cycleColors.length];
            ScreenEffectManager.triggerOverlay(color, opacity, 40, 
                ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
        }
    }
    
    private static class MirrorStrategy extends BaseShaderEffectStrategy {
        public MirrorStrategy() {
            super(PostProcessingManager.MIRROR_POST_SHADER, 30);
        }
        
        @Override
        public String getPrimaryName() {
            return "mirror";
        }
        
        @Override
        public String[] getAliases() {
            return new String[]{"flip"};
        }
        
        @Override
        protected void triggerFallback(float intensity) {
            float opacity = Math.min(0.15f, intensity * 0.12f);
            ScreenEffectManager.triggerOverlay(0xFF808080, opacity, 30, 
                ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
        }
    }
    
    private static class NoiseStrategy extends BaseShaderEffectStrategy {
        public NoiseStrategy() {
            super(PostProcessingManager.NOISE_POST_SHADER, 30);
        }
        
        @Override
        public String getPrimaryName() {
            return "noise";
        }
        
        @Override
        public String[] getAliases() {
            return new String[]{"grain", "static"};
        }
        
        @Override
        protected void triggerFallback(float intensity) {
            float opacity = Math.min(0.1f, intensity * 0.08f);
            ScreenEffectManager.triggerOverlay(0xFF808080, opacity, 30, 
                ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
        }
    }
    
    private static class ZoomBlurStrategy extends BaseShaderEffectStrategy {
        public ZoomBlurStrategy() {
            super(PostProcessingManager.ZOOM_BLUR_POST_SHADER, 30);
        }
        
        @Override
        public String getPrimaryName() {
            return "zoom_blur";
        }
        
        @Override
        public String[] getAliases() {
            return new String[]{"zoom", "radial_blur"};
        }
        
        @Override
        protected void triggerFallback(float intensity) {
            float opacity = Math.min(0.2f, intensity * 0.15f);
            ScreenEffectManager.triggerOverlay(0xFF808080, opacity, 30, 
                ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
        }
    }
    
    private static class NightVisionStrategy implements ShaderEffectStrategy {
        @Override
        public void trigger(float intensity) {
            // Green night vision overlay (no post-processing shader available)
            float opacity = Math.min(0.25f, intensity * 0.2f);
            ScreenEffectManager.triggerOverlay(0xFF00FF00, opacity, 50, 
                ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
        }
        
        @Override
        public String getPrimaryName() {
            return "night_vision";
        }
        
        @Override
        public String[] getAliases() {
            return new String[]{"nightvision"};
        }
    }
    
    private static class AcidTripStrategy extends BaseShaderEffectStrategy {
        public AcidTripStrategy() {
            super(PostProcessingManager.ACID_TRIP_POST_SHADER, 30);
        }
        
        @Override
        public String getPrimaryName() {
            return "acid";
        }
        
        @Override
        public String[] getAliases() {
            return new String[]{"trippy"};
        }
        
        @Override
        protected void triggerFallback(float intensity) {
            float opacity = Math.min(0.3f, intensity * 0.25f);
            int[] trippyColors = {0xFFFF00FF, 0xFF00FFFF, 0xFFFFFF00, 0xFFFF0000};
            for (int i = 0; i < trippyColors.length; i++) {
                ScreenEffectManager.triggerOverlay(trippyColors[i], opacity, 25 + i * 5, 
                    ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
            }
            ScreenEffectManager.triggerShake(0.05f * intensity, 20);
        }
    }
    
    private static class PulsingGlowStrategy extends BaseShaderEffectStrategy {
        public PulsingGlowStrategy() {
            super(PostProcessingManager.PULSING_GLOW_POST_SHADER, 40);
        }
        
        @Override
        public String getPrimaryName() {
            return "pulsing_glow";
        }
        
        @Override
        public String[] getAliases() {
            return new String[]{"pulse_glow", "glow_pulse"};
        }
        
        @Override
        protected void triggerFallback(float intensity) {
            float opacity = Math.min(0.2f, intensity * 0.15f);
            ScreenEffectManager.triggerOverlay(0xFFFFFFFF, opacity, 40, 
                ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
        }
    }
    
    private static class ScrollingStripesStrategy extends BaseShaderEffectStrategy {
        public ScrollingStripesStrategy() {
            super(PostProcessingManager.SCROLLING_STRIPES_POST_SHADER, 35);
        }
        
        @Override
        public String getPrimaryName() {
            return "scrolling_stripes";
        }
        
        @Override
        public String[] getAliases() {
            return new String[]{"stripes", "scrolling"};
        }
        
        @Override
        protected void triggerFallback(float intensity) {
            float opacity = Math.min(0.2f, intensity * 0.15f);
            ScreenEffectManager.triggerOverlay(0xFF808080, opacity, 35, 
                ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
        }
    }
    
    private static class WarpingVortexStrategy extends BaseShaderEffectStrategy {
        public WarpingVortexStrategy() {
            super(PostProcessingManager.WARPING_VORTEX_POST_SHADER, 40);
        }
        
        @Override
        public String getPrimaryName() {
            return "warping_vortex";
        }
        
        @Override
        public String[] getAliases() {
            return new String[]{"vortex", "warp"};
        }
        
        @Override
        protected void triggerFallback(float intensity) {
            float opacity = Math.min(0.3f, intensity * 0.25f);
            ScreenEffectManager.triggerOverlay(0xFFFF00FF, opacity, 40, 
                ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
            ScreenEffectManager.triggerShake(0.03f * intensity, 20);
        }
    }
    
    private static class ParticleRainStrategy extends BaseShaderEffectStrategy {
        public ParticleRainStrategy() {
            super(PostProcessingManager.PARTICLE_RAIN_POST_SHADER, 50);
        }
        
        @Override
        public String getPrimaryName() {
            return "particle_rain";
        }
        
        @Override
        public String[] getAliases() {
            return new String[]{"particles", "rain"};
        }
        
        @Override
        protected void triggerFallback(float intensity) {
            float opacity = Math.min(0.15f, intensity * 0.12f);
            ScreenEffectManager.triggerOverlay(0xFF808080, opacity, 50, 
                ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
        }
    }
    
    private static class ColorWaveStrategy extends BaseShaderEffectStrategy {
        public ColorWaveStrategy() {
            super(PostProcessingManager.COLOR_WAVE_POST_SHADER, 35);
        }
        
        @Override
        public String getPrimaryName() {
            return "color_wave";
        }
        
        @Override
        public String[] getAliases() {
            return new String[]{"wave_color"};
        }
        
        @Override
        protected void triggerFallback(float intensity) {
            float opacity = Math.min(0.2f, intensity * 0.15f);
            ScreenEffectManager.triggerOverlay(0xFFFF00FF, opacity, 35, 
                ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
        }
    }
    
    private static class RippleEffectStrategy extends BaseShaderEffectStrategy {
        public RippleEffectStrategy() {
            super(PostProcessingManager.RIPPLE_EFFECT_POST_SHADER, 30);
        }
        
        @Override
        public String getPrimaryName() {
            return "ripple_effect";
        }
        
        @Override
        public String[] getAliases() {
            return new String[]{"ripples"};
        }
        
        @Override
        protected void triggerFallback(float intensity) {
            float opacity = Math.min(0.2f, intensity * 0.15f);
            ScreenEffectManager.triggerOverlay(0xFF00FFFF, opacity, 30, 
                ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
        }
    }
    
    private static class BreathingVignetteStrategy extends BaseShaderEffectStrategy {
        public BreathingVignetteStrategy() {
            super(PostProcessingManager.BREATHING_VIGNETTE_POST_SHADER, 40);
        }
        
        @Override
        public String getPrimaryName() {
            return "breathing_vignette";
        }
        
        @Override
        public String[] getAliases() {
            return new String[]{"breathing"};
        }
        
        @Override
        protected void triggerFallback(float intensity) {
            float opacity = Math.min(0.3f, intensity * 0.25f);
            ScreenEffectManager.triggerOverlay(0xFF000000, opacity, 40, 
                ScreenEffectManager.ScreenOverlay.OverlayType.VIGNETTE);
        }
    }
    
    private static class AnimatedNoiseStrategy extends BaseShaderEffectStrategy {
        public AnimatedNoiseStrategy() {
            super(PostProcessingManager.ANIMATED_NOISE_POST_SHADER, 30);
        }
        
        @Override
        public String getPrimaryName() {
            return "animated_noise";
        }
        
        @Override
        public String[] getAliases() {
            return new String[]{"moving_noise"};
        }
        
        @Override
        protected void triggerFallback(float intensity) {
            float opacity = Math.min(0.15f, intensity * 0.12f);
            ScreenEffectManager.triggerOverlay(0xFF808080, opacity, 30, 
                ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
        }
    }
    
    private static class RotatingKaleidoscopeStrategy extends BaseShaderEffectStrategy {
        public RotatingKaleidoscopeStrategy() {
            super(PostProcessingManager.ROTATING_KALEIDOSCOPE_POST_SHADER, 40);
        }
        
        @Override
        public String getPrimaryName() {
            return "rotating_kaleidoscope";
        }
        
        @Override
        public String[] getAliases() {
            return new String[]{"kaleidoscope_rotate"};
        }
        
        @Override
        protected void triggerFallback(float intensity) {
            float opacity = Math.min(0.25f, intensity * 0.2f);
            ScreenEffectManager.triggerOverlay(0xFFFF00FF, opacity, 40, 
                ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
        }
    }
    
    private static class EnergyPulseStrategy extends BaseShaderEffectStrategy {
        public EnergyPulseStrategy() {
            super(PostProcessingManager.ENERGY_PULSE_POST_SHADER, 35);
        }
        
        @Override
        public String getPrimaryName() {
            return "energy_pulse";
        }
        
        @Override
        public String[] getAliases() {
            return new String[]{"energy_wave"};
        }
        
        @Override
        protected void triggerFallback(float intensity) {
            float opacity = Math.min(0.2f, intensity * 0.15f);
            ScreenEffectManager.triggerOverlay(0xFF00FFFF, opacity, 35, 
                ScreenEffectManager.ScreenOverlay.OverlayType.GLOW);
        }
    }
}

