package at.koopro.spells_n_squares.core.client;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import net.minecraft.resources.Identifier;

/**
 * Constants for rendering operations.
 * Centralizes magic numbers and shared identifiers used across renderers.
 */
public final class RendererConstants {
    private RendererConstants() {
        // Utility class - prevent instantiation
    }
    
    // Overlay texture bits (used in vertex buffer)
    public static final int OVERLAY_BITS = 0xF000F0;
    
    // Shared texture identifiers
    public static final Identifier GLOW_TEXTURE = ModIdentifierHelper.modId("textures/misc/lumos_white.png");
    
    // Renderer-specific constants
    public static final float LIGHT_ORB_CORE_SIZE = 0.06f;
    public static final float LIGHT_ORB_SHELL_SIZE = 0.10f;
    public static final float LIGHT_ORB_TRANSLATE_Y = 0.1f;
    public static final float LIGHT_ORB_ROTATION_SPEED = 0.15f;
    public static final float LIGHT_ORB_BOB_SPEED = 0.08f;
    public static final float LIGHT_ORB_BOB_AMPLITUDE = 0.07f;
    
    // Color constants (ARGB format)
    public static final int COLOR_WHITE = 0xFFFFFFFF;
    public static final int COLOR_LIGHT_ORB_CORE = 0xFFFFFFFF;
    public static final int COLOR_LIGHT_ORB_SHELL = 0x60FFE080;
    public static final int COLOR_SHIELD_ORB = 0x70A0C0FF;
    public static final int COLOR_DUMMY_BODY = 0xFF80D8FF;
    public static final int COLOR_DUMMY_HEAD = 0xFFFFFFFF;
    public static final int COLOR_LIGHTNING_BEAM = 0xFFFFFFFF;
    
    // Dummy player renderer constants
    public static final float DUMMY_BODY_WIDTH = 0.25f;
    public static final float DUMMY_BODY_HEIGHT = 0.5f;
    public static final float DUMMY_BODY_DEPTH = 0.15f;
    public static final float DUMMY_BODY_Y_OFFSET = 0.5f;
    public static final float DUMMY_HEAD_SIZE = 0.2f;
    public static final float DUMMY_HEAD_Y_OFFSET = 0.5f;
    
    // Lightning beam renderer constants
    public static final int LIGHTNING_BEAM_SEGMENTS = 10;
    public static final float LIGHTNING_BEAM_THICKNESS = 0.04f;
    public static final float LIGHTNING_BEAM_WOBBLE_SPEED_1 = 0.08f;
    public static final float LIGHTNING_BEAM_WOBBLE_SPEED_2 = 0.05f;
    public static final float LIGHTNING_BEAM_WOBBLE_FREQ_1 = 10.0f;
    public static final float LIGHTNING_BEAM_WOBBLE_FREQ_2 = 11.0f;
    public static final float LIGHTNING_BEAM_WOBBLE_AMPLITUDE = 0.15f;
    public static final float LIGHTNING_BEAM_MIN_LENGTH = 0.01f;
}

