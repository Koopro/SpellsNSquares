# Rendering System Analysis

## Overview
This document analyzes the rendering system used in the Spells_n_Squares mod, focusing on custom shaders, screen shake effects, and the entity rendering pipeline using NeoForge's deferred rendering system.

## 1. Entity Rendering Pipeline

### Architecture
The rendering system uses NeoForge's deferred rendering pattern (similar to SpectreLib) for custom entity rendering. This allows for efficient batching and custom geometry submission.

### Key Components

#### EntityRenderState
- **Purpose**: State object that holds rendering data extracted from entities
- **Location**: `net.minecraft.client.renderer.entity.state.EntityRenderState`
- **Contains**: Position, age, bounding box dimensions, and other render-time data
- **Usage**: Created per entity renderer via `createRenderState()`

#### SubmitNodeCollector
- **Purpose**: Collects rendering nodes for deferred execution
- **Location**: `net.minecraft.client.renderer.SubmitNodeCollector`
- **Key Method**: `submitCustomGeometry(PoseStack, RenderType, GeometryCallback)`
- **Usage**: Allows submitting custom geometry that will be batched and rendered later

#### submitCustomGeometry Pattern
```java
collector.submitCustomGeometry(
    poseStack,
    RenderTypes.entityTranslucentEmissive(texture),
    (pose, buffer) -> {
        // Custom geometry rendering code
        PoseStack ps = new PoseStack();
        ps.last().set(pose);
        ps.pushPose();
        
        // Apply transformations and render geometry
        RendererUtils.renderCube(buffer, ps, size, color);
        
        ps.popPose();
    }
);
```

### Renderer Implementations

#### ShieldOrbRenderer
**Location**: `src/main/java/at/koopro/spells_n_squares/features/spell/client/ShieldOrbRenderer.java`

- **Type**: Simple cube-based renderer
- **Features**: Single large blue shell, no animation
- **Size**: Based on entity bounding box
- **Color**: `COLOR_SHIELD_ORB` (0x70A0C0FF - semi-transparent blue)

#### LightOrbRenderer
**Location**: `src/main/java/at/koopro/spells_n_squares/features/spell/client/LightOrbRenderer.java`

- **Type**: Animated orb with rotation and bobbing
- **Features**:
  - Core and shell cubes
  - Y-axis rotation based on time
  - X-axis bobbing using sine wave
  - Vertical translation offset
- **Animation**:
  ```java
  float time = renderState.ageInTicks;
  ps.mulPose(Axis.YP.rotation(time * LIGHT_ORB_ROTATION_SPEED));
  ps.mulPose(Axis.XP.rotation(Mth.sin(time * LIGHT_ORB_BOB_SPEED) * LIGHT_ORB_BOB_AMPLITUDE));
  ```

#### LightningBeamRenderer
**Location**: `src/main/java/at/koopro/spells_n_squares/features/spell/client/LightningBeamRenderer.java`

- **Type**: Segmented beam with wobble animation
- **Features**:
  - Custom `BeamRenderState` extending `EntityRenderState`
  - Segmented beam (10 segments by default)
  - Per-segment wobble using sine/cosine waves
  - Square cross-section with orthonormal basis
- **Wobble Calculation**:
  ```java
  float wobble = sin((t + time * WOBBLE_SPEED_1) * WOBBLE_FREQ_1) * WOBBLE_AMPLITUDE;
  float wobble2 = cos((t + time * WOBBLE_SPEED_2) * WOBBLE_FREQ_2) * WOBBLE_AMPLITUDE;
  ```

#### DummyPlayerRenderer
**Location**: `src/main/java/at/koopro/spells_n_squares/features/spell/client/DummyPlayerRenderer.java`

- **Type**: Cuboid-based humanoid renderer
- **Features**: Simple body + head cuboids, no animation

### Rendering Flow

1. **Entity Tick** → Entity updates position/state
2. **extractRenderState()** → Extracts entity data into render state
   - Called every frame with `partialTick` for interpolation
   - Stores position, rotation, custom data (e.g., beam direction)
3. **submit()** → Submits custom geometry to collector
   - Called during render phase
   - Geometry is batched and rendered later
4. **Deferred Rendering** → NeoForge batches and renders geometry
5. **Shader Application** → Custom shaders applied if configured

## 2. Custom Shader System

### Shader Files
**Location**: `src/main/resources/assets/spells_n_squares/shaders/core/`

#### lumos_orb.json
Shader configuration file defining:
- **Blend Mode**: Additive blending (`"func": "add"`)
- **Vertex Shader**: `spells_n_squares:lumos_orb.vsh`
- **Fragment Shader**: `spells_n_squares:lumos_orb.fsh`
- **Attributes**: Position, Color, UV0, UV1, UV2, Normal
- **Uniforms**:
  - `ModelViewMat` (matrix4) - Model-view transformation
  - `ProjMat` (matrix4) - Projection matrix
  - `ColorModulator` (vec4) - Color modulation
  - `Time` (float) - Time uniform for animation

#### lumos_orb.vsh (Vertex Shader)
```glsl
#version 150
// Standard vertex shader
// Transforms position, passes color and UV to fragment shader
```

**Key Features**:
- Standard vertex transformation
- Color modulation support
- UV coordinate passing

#### lumos_orb.fsh (Fragment Shader)
```glsl
#version 150
// Radial gradient with time-based wobble
```

**Key Features**:
- **Radial Gradient**: Uses centered UV coordinates
- **Time-based Wobble**:
  ```glsl
  float wobble = 0.05 * sin(Time * 0.8) + 0.04 * sin((uv.x + uv.y + Time) * 2.3);
  float radius = 0.45 + wobble;
  ```
- **Smooth Falloff**: Uses `smoothstep()` for edge blending
- **Core/Rim Mixing**: White core transitions to colored rim

### Shader Integration Status
**Issue**: Shaders are defined but not actively integrated into entity renderers. The `ShaderEffectHandler` class exists but currently falls back to particle effects.

**Location**: `src/main/java/at/koopro/spells_n_squares/features/fx/ShaderEffectHandler.java`

## 3. Screen Shake System

### Implementation
**Location**: `src/main/java/at/koopro/spells_n_squares/features/fx/ScreenEffectManager.java`

### ScreenShake Class
```java
private static class ScreenShake {
    float intensity;  // Shake intensity
    int duration;      // Duration in ticks
    int age;           // Current age in ticks
    
    float getCurrentIntensity() {
        float progress = (float) age / duration;
        return intensity * (1.0f - progress);  // Fade out over time
    }
}
```

### Triggering Shake
```java
public static void triggerShake(float intensity, int duration) {
    float adjustedIntensity = intensity * Config.getScreenEffectIntensity();
    activeShakes.add(new ScreenShake(adjustedIntensity, duration));
}
```

**Usage Example**:
```java
// In ModSpells.java
if (getVisualEffectIntensity() > 0.7f && level.isClientSide()) {
    ScreenEffectManager.triggerShake(0.1f * getVisualEffectIntensity(), 10);
}
```

### Shake Offset Calculation
```java
public static Vec3 getShakeOffset() {
    if (activeShakes.isEmpty()) {
        return Vec3.ZERO;
    }
    
    float totalIntensity = sum of all active shake intensities;
    
    // Apply random offset based on intensity
    Random random = new Random();
    double offsetX = (random.nextDouble() - 0.5) * totalIntensity * 2.0;
    double offsetY = (random.nextDouble() - 0.5) * totalIntensity * 2.0;
    
    return new Vec3(offsetX, offsetY, 0.0);
}
```

### Critical Issue: Shake Not Applied
**Problem**: `getShakeOffset()` is defined but **never called** to modify the camera or view.

**Missing Integration**: The shake offset needs to be applied to:
- Camera position/rotation via `RenderLevelEvent` or similar
- Or GUI rendering (already has `RenderGuiEvent` but shake not applied)

**Current State**: Shake effects are tracked and calculated but have no visual impact.

### Issues with Current Implementation
1. **Random Shake**: Uses `new Random()` each frame - should use seeded random or Perlin noise for smooth motion
2. **No Camera Integration**: Offset calculated but not applied to view
3. **No Smooth Interpolation**: Random values cause jittery motion

## 4. Geometry Rendering Utilities

### RendererUtils
**Location**: `src/main/java/at/koopro/spells_n_squares/core/client/RendererUtils.java`

### renderCube() Methods
```java
// Uniform cube
public static void renderCube(VertexConsumer buffer, PoseStack poseStack, 
                             float half, int color)

// Cuboid with dimensions
public static void renderCube(VertexConsumer buffer, PoseStack poseStack,
                             float halfX, float halfY, float halfZ, 
                             float yOffset, int color)
```

**Implementation Details**:
- Renders 6 faces of a cube/cuboid
- Uses `Matrix4f` for transformations
- Each face has 4 vertices with:
  - Position (transformed)
  - Color (ARGB format)
  - UV coordinates
  - Normal vectors
  - Overlay texture bits
  - Light texture bits

### addQuad() Method
```java
public static void addQuad(VertexConsumer buffer, Matrix4f m,
                          Vector3f v0, Vector3f v1, Vector3f v2, Vector3f v3,
                          int color)
```

**Usage**: For rendering beam segments and other custom geometry

### Vertex Buffer Pattern
```java
buffer.addVertex(
    x, y, z,                    // Position
    color,                      // ARGB color
    u, v,                       // UV coordinates
    OverlayTexture.NO_OVERLAY,   // Overlay
    RendererConstants.OVERLAY_BITS,  // Light bits (emissive)
    nx, ny, nz                  // Normal vector
);
```

## 5. Render Types and Constants

### RendererConstants
**Location**: `src/main/java/at/koopro/spells_n_squares/core/client/RendererConstants.java`

### Key Constants

#### Render Types
- `RenderTypes.entityTranslucentEmissive(Identifier)` - For glowing effects

#### Colors (ARGB Format)
- `COLOR_WHITE`: 0xFFFFFFFF
- `COLOR_LIGHT_ORB_CORE`: 0xFFFFFFFF
- `COLOR_LIGHT_ORB_SHELL`: 0x60FFE080 (semi-transparent green)
- `COLOR_SHIELD_ORB`: 0x70A0C0FF (semi-transparent blue)
- `COLOR_DUMMY_BODY`: 0xFF80D8FF (cyan)
- `COLOR_DUMMY_HEAD`: 0xFFFFFFFF
- `COLOR_LIGHTNING_BEAM`: 0xFFFFFFFF

#### Animation Constants
- `LIGHT_ORB_ROTATION_SPEED`: 0.15f
- `LIGHT_ORB_BOB_SPEED`: 0.08f
- `LIGHT_ORB_BOB_AMPLITUDE`: 0.07f
- `LIGHTNING_BEAM_WOBBLE_SPEED_1`: 0.08f
- `LIGHTNING_BEAM_WOBBLE_SPEED_2`: 0.05f
- `LIGHTNING_BEAM_WOBBLE_FREQ_1`: 10.0f
- `LIGHTNING_BEAM_WOBBLE_FREQ_2`: 11.0f
- `LIGHTNING_BEAM_WOBBLE_AMPLITUDE`: 0.15f

#### Render Dimensions
- `LIGHT_ORB_CORE_SIZE`: 0.06f
- `LIGHT_ORB_SHELL_SIZE`: 0.10f
- `LIGHTNING_BEAM_THICKNESS`: 0.04f
- `LIGHTNING_BEAM_SEGMENTS`: 10

## 6. Screen Effects System

### ScreenEffectManager
**Location**: `src/main/java/at/koopro/spells_n_squares/features/fx/ScreenEffectManager.java`

### Screen Overlays
```java
public static class ScreenOverlay {
    int color;      // ARGB format
    float opacity;
    int duration;
    int age;
    OverlayType type;  // VIGNETTE, FLASH, GLOW
}
```

### Overlay Types
- **VIGNETTE**: Darker at edges (damage overlay)
- **FLASH**: Brief full-screen flash
- **GLOW**: Subtle glow effect

### Rendering
- Overlays rendered in `onRenderGui(RenderGuiEvent.Post)`
- Vignette uses pixel-by-pixel distance calculation
- Flash/Glow use full-screen fill

## 7. Issues and Recommendations

### Critical Issues

1. **Screen Shake Not Applied**
   - `getShakeOffset()` exists but is never used
   - Need to integrate with camera/view rendering
   - **Fix**: Apply offset via `RenderLevelEvent` or camera modification

2. **Random Shake Implementation**
   - Uses `new Random()` each frame causing jittery motion
   - **Fix**: Use seeded random or Perlin noise for smooth motion

3. **Shader Integration Missing**
   - Shaders defined but not used in entity renderers
   - Falls back to particle effects
   - **Fix**: Integrate shaders into rendering pipeline

### Recommendations

1. **Apply Screen Shake**
   - Hook into `RenderLevelEvent` or similar
   - Apply shake offset to camera/view matrix
   - Use smooth noise function instead of random

2. **Improve Shake Quality**
   - Use Perlin noise or seeded random for smooth motion
   - Add interpolation between frames
   - Consider frequency-based shake (different frequencies for X/Y)

3. **Integrate Shaders**
   - Connect shader system to entity renderers
   - Use shaders for lumos orb and other effects
   - Remove fallback to particles when shaders available

4. **Performance Optimization**
   - Consider caching render states
   - Batch similar geometry
   - Use instancing for repeated geometry

## 8. Implementation Guide for Screen Shake

### Current Problem
The `getShakeOffset()` method calculates shake offsets but they are never applied to the camera or view.

### Solution Options

#### Option 1: Apply to GUI (Simpler, but less immersive)
Apply shake to GUI rendering in `onRenderGui()`:
```java
@SubscribeEvent
public static void onRenderGui(RenderGuiEvent.Post event) {
    Vec3 shake = getShakeOffset();
    GuiGraphics guiGraphics = event.getGuiGraphics();
    
    // Apply shake by translating GUI rendering
    guiGraphics.pose().pushPose();
    guiGraphics.pose().translate(shake.x, shake.y, 0);
    
    // Render overlays...
    
    guiGraphics.pose().popPose();
}
```

**Pros**: Simple, works immediately
**Cons**: Only affects GUI, not world rendering

#### Option 2: Apply to Camera (More complex, but better)
Use `RenderLevelEvent` or modify camera directly:
```java
@SubscribeEvent
public static void onRenderLevel(RenderLevelEvent event) {
    Vec3 shake = getShakeOffset();
    if (shake.lengthSqr() > 0.001) {
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(shake.x, shake.y, 0);
        // Camera is already set up, this modifies the view
    }
}
```

**Pros**: Affects world rendering, more immersive
**Cons**: Requires careful handling of pose stack

#### Option 3: Modify Camera Position Directly
Access camera and modify position:
```java
@SubscribeEvent
public static void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
    Vec3 shake = getShakeOffset();
    if (shake.lengthSqr() > 0.001) {
        // Modify camera angles or position
        event.setYaw(event.getYaw() + (float)shake.x * 10);
        event.setPitch(event.getPitch() + (float)shake.y * 10);
    }
}
```

### Improving Shake Quality

#### Replace Random with Smooth Noise
```java
private static float shakeX = 0;
private static float shakeY = 0;

public static Vec3 getShakeOffset() {
    if (activeShakes.isEmpty()) {
        return Vec3.ZERO;
    }
    
    float totalIntensity = 0.0f;
    for (ScreenShake shake : activeShakes) {
        totalIntensity += shake.getCurrentIntensity();
    }
    
    // Use time-based sine waves for smooth motion
    float time = Minecraft.getInstance().level != null ? 
        Minecraft.getInstance().level.getGameTime() : 0;
    
    // Different frequencies for X and Y to avoid circular motion
    shakeX = (float) (Math.sin(time * 0.3) * totalIntensity);
    shakeY = (float) (Math.cos(time * 0.5) * totalIntensity);
    
    return new Vec3(shakeX, shakeY, 0.0);
}
```

#### Use Perlin Noise (Better Quality)
For even smoother motion, use Perlin noise or a seeded random:
```java
private static final Random seededRandom = new Random(12345);
private static float noiseTime = 0;

public static Vec3 getShakeOffset() {
    // ... calculate totalIntensity ...
    
    noiseTime += 0.1f; // Increment time
    
    // Use seeded random with interpolation
    float x = (float) (Math.sin(noiseTime * 2.3) * totalIntensity);
    float y = (float) (Math.cos(noiseTime * 1.7) * totalIntensity);
    
    return new Vec3(x, y, 0.0);
}
```

## 9. Comparison with SpectreLib

### Similarities
- Deferred rendering pattern
- Custom geometry submission
- Render state objects
- Batched rendering

### Differences
- This codebase uses NeoForge's built-in rendering system
- SpectreLib may provide additional utilities and abstractions
- Current implementation is more direct/verbose

### Potential Benefits of SpectreLib
- Additional rendering utilities
- Better abstraction layer
- More advanced effects support
- Performance optimizations

## 10. Code Examples

### Example: Adding Screen Shake to Camera
```java
@EventBusSubscriber(modid = SpellsNSquares.MODID, value = Dist.CLIENT)
public class CameraShakeHandler {
    
    @SubscribeEvent
    public static void onRenderLevel(RenderLevelEvent event) {
        Vec3 shake = ScreenEffectManager.getShakeOffset();
        
        if (shake.lengthSqr() > 0.001) {
            PoseStack poseStack = event.getPoseStack();
            poseStack.pushPose();
            
            // Apply shake as translation
            poseStack.translate(shake.x, shake.y, 0);
            
            // Note: Need to pop before event completes
            // This is tricky - may need to use different event
        }
    }
}
```

### Example: Using Shaders in Entity Renderer
```java
@Override
public void submit(EntityRenderState renderState, PoseStack poseStack, 
                   SubmitNodeCollector collector, CameraRenderState cameraState) {
    
    // Check if shaders are available
    if (ShaderEffectHandler.areShadersAvailable()) {
        // Use shader-based rendering
        Identifier shaderId = Identifier.fromNamespaceAndPath(
            SpellsNSquares.MODID, "shaders/core/lumos_orb"
        );
        
        collector.submitCustomGeometry(
            poseStack,
            RenderTypes.entityTranslucentEmissive(shaderId), // Use shader
            (pose, buffer) -> {
                // Render with shader
            }
        );
    } else {
        // Fallback to standard rendering
        collector.submitCustomGeometry(
            poseStack,
            RenderTypes.entityTranslucentEmissive(GLOW_TEXTURE),
            (pose, buffer) -> {
                // Standard rendering
            }
        );
    }
}
```

## Conclusion

The rendering system uses NeoForge's deferred rendering pattern effectively for custom entity rendering. The main issues are:
1. Screen shake is calculated but not applied
2. Shaders are defined but not integrated
3. Random shake implementation needs improvement

The architecture is solid and follows good patterns, but needs integration work to make effects functional. The implementation guide above provides concrete solutions for fixing these issues.
