## Shader Usage Guide

This document describes how shaders are structured and used in the Spells_n_Squares mod.

### Shader lifecycle overview

- **Where shader files live**
  - Core entity / geometry shaders are stored under:
    - `src/main/resources/assets/spells_n_squares/shaders/core/`
      - Examples: `lumos_orb.vsh`, `lumos_orb.fsh`, `grayscale.vsh`, `grayscale.fsh`, `cut_effect.vsh`, `cut_effect.fsh`
  - Full-screen post-processing shaders are stored under:
    - `src/main/resources/assets/spells_n_squares/shaders/post/`
      - Examples: `inverted_colors.vsh`, `inverted_colors.fsh`, `grayscale.vsh`, `grayscale.fsh`
  - Each shader **program** is described by a JSON file (Minecraft-style) alongside the GLSL files:
    - `*.json` files define:
      - Vertex shader id (e.g. `spells_n_squares:lumos_orb.vsh`)
      - Fragment shader id (e.g. `spells_n_squares:lumos_orb.fsh`)
      - Attributes: `Position`, `Color`, `UV0`, `UV1`, `UV2`, `Normal`
      - Uniforms:
        - `ModelViewMat` (matrix4)
        - `ProjMat` (matrix4)
        - `ColorModulator` (vec4)
        - Optional effect-specific uniforms like `Time` (float)

- **How shaders are loaded**
  - At resource reload / client startup Minecraft loads shader JSON + GLSL resources from the `assets/spells_n_squares/shaders/**` folders.
  - For **post-processing** shaders (screen-wide effects), the mod uses NeoForge’s `PostChain` system, managed by:
    - `features/fx/PostProcessingManager.java`
      - Creates `PostChain` instances lazily from the JSON/post shader ids (e.g. `spells_n_squares:shaders/post/grayscale`).
      - Caches `PostChain` instances in a map for reuse.
  - For **core geometry shaders** (used with custom render pipelines), the mod uses NeoForge’s `RenderPipeline` API:
    - `PostProcessingManager` registers two pipelines:
      - `LUMOS_ORB_PIPELINE` using vertex + fragment `core/lumos_orb`
      - `CUT_EFFECT_PIPELINE` using vertex + fragment `core/cut_effect`
    - Pipelines are registered during `RegisterRenderPipelinesEvent` and precompiled once via `RenderSystem.getDevice().precompilePipeline(...)`.
    - Pipeline validity is tracked in the `pipelineValidity` map.

- **How Java code checks shader availability**
  - `features/fx/ShaderEffectHandler.java` is the central helper for shader availability:
    - Global flag `shadersSupported` and config `Config.areShaderEffectsEnabled()` gate all shader usage.
    - Public helper methods:
      - `areShadersAvailable()` – returns `true` if shaders are allowed and enabled in config.
      - `isShaderLoaded(Identifier shaderId)` – checks:
        - For core shaders like `LUMOS_ORB_SHADER`, `CUT_EFFECT_SHADER`:
          - Uses `PostProcessingManager.getLumosOrbRenderPipeline()` / `getCutEffectRenderPipeline()` and `PostProcessingManager.isPipelineValid(...)`.
        - For post-processing shaders like `INVERTED_COLORS_POST_SHADER`, `GRAYSCALE_POST_SHADER`:
          - Uses `PostProcessingManager.isPostProcessingShaderAvailable(...)` which tries to build a `PostChain`.
      - Results are cached per `Identifier` in `shaderCache`.
    - High-level helpers:
      - `getLumosOrbShaderId()` – returns `LUMOS_ORB_SHADER` only if shaders are enabled and loaded, otherwise `null`.
      - `getShaderId(Identifier)` – generic wrapper for other shaders.

- **How shaders are actually used at render time**
  - **Entity / geometry rendering (deferred pipeline)**
    - Renderers use `SubmitNodeCollector.submitCustomGeometry(...)` to push geometry:
      - Example: `features/spell/client/LightOrbRenderer.java`
        - Currently uses a texture-only render type:
          - `RenderTypes.entityTranslucent(RendererConstants.GLOW_TEXTURE)`
        - Geometry is generated via `RendererUtils.renderCube(...)`.
    - To use a shader-backed render type, the pattern is:
      - Ask `ShaderEffectHandler` for a shader id (e.g. `ShaderEffectHandler.getLumosOrbShaderId()`).
      - If non-null, use a shader-based `RenderType` (e.g. a helper such as `RenderTypes.entityTranslucentEmissive(shaderId)`).
      - Otherwise, fall back to a texture-based render type.
  - **Post-processing (full screen)**
    - High-level calls from gameplay trigger post-processing via:
      - `ShaderEffectHandler.triggerInvertedColors(...)`
      - `ShaderEffectHandler.triggerGrayscale(...)`
      - etc.
    - These delegate to `PostProcessingManager.addEffect(shaderId, intensity, duration)` where:
      - Active effects are stored in `activeEffects`.
      - Each tick, intensities fade out over `duration`.
    - During rendering (`RenderLevelStageEvent.AfterLevel`):
      - `PostProcessingManager.onRenderLevelStage(...)`:
        - Ensures render pipelines are checked (single run).
        - For each active effect:
          - Builds or looks up a `PostChain` for the shader id.
          - Calls `chain.process(mc.getMainRenderTarget(), GraphicsResourceAllocator.UNPOOLED)` to apply the effect.

- **Where time and uniforms come from**
  - Game time / animation source:
    - `ShaderEffectHandler.getShaderTime()` returns `level.getGameTime()` when available.
    - For post-processing shaders, time/intensity is typically encoded as uniforms defined in the shader JSON, but currently most intensity is baked via JSON defaults and the effect’s fade logic.
  - For geometry shaders like `lumos_orb`:
    - The JSON defines `Time` as a uniform, but the current code still has a placeholder `updateShaderTime(...)` method and does not yet push `Time` into the shader.
    - Our shader helper framework (see implementation) will wrap this in a simple, reusable API.

- **Typical lifecycle for an entity/geometry shader (lumos orb example)**
  1. **Resource definition**: Add `lumos_orb.vsh`, `lumos_orb.fsh`, and `lumos_orb.json` under `assets/spells_n_squares/shaders/core/`.
  2. **Pipeline registration**: `PostProcessingManager` registers `LUMOS_ORB_PIPELINE` (vertex + fragment = `core/lumos_orb`) during `RegisterRenderPipelinesEvent`.
  3. **Pipeline validation**: On first world render, `PostProcessingManager.checkPipelineValidity()` precompiles the pipeline and records whether it is valid.
  4. **Availability check**: `ShaderEffectHandler.isShaderLoaded(LUMOS_ORB_SHADER)` asks `PostProcessingManager` for the pipeline and tests `isPipelineValid(...)`.
  5. **Render-time usage**:
     - An entity renderer (e.g. light orb) uses `ShaderRenderHelper.getLumosOrbRenderType()` to obtain a `RenderType`:
       - Internally, this asks `ShaderEffectHandler.getLumosOrbShaderId()`.
       - If a shader is available, it returns `RenderTypes.entityTranslucentEmissive(shaderId)`.
       - Otherwise, it returns the original `RenderTypes.entityTranslucent(RendererConstants.GLOW_TEXTURE)` fallback.
     - The renderer then submits its geometry via `SubmitNodeCollector.submitCustomGeometry(...)` using this `RenderType`.
     - Optionally, it can call `ShaderEffectHandler.updateShaderTime(...)` to provide a time value for shaders that need it.

### Using the shader helper framework

- **Helper location**
  - The small framework lives in `core/client/ShaderRenderHelper.java`.
  - It is intentionally minimal and built on top of:
    - `ShaderEffectHandler` for availability checks
    - Vanilla `RenderTypes` for actual `RenderType` instances

- **Key helper methods**
  - `ShaderRenderHelper.getLumosOrbRenderType()`:
    - Returns a `RenderType` that prefers the `lumos_orb` shader when available.
    - Falls back to the glow texture render type if shaders are disabled or unavailable.
  - `ShaderRenderHelper.getTranslucentEmissiveOrTexture(Identifier preferredShaderId, Identifier fallbackTexture)`:
    - Generic helper for other shaders:
      - Tries `ShaderEffectHandler.getShaderId(preferredShaderId)`.
      - Uses `RenderTypes.entityTranslucentEmissive(shaderId)` if available.
      - Otherwise uses `RenderTypes.entityTranslucent(fallbackTexture)`.

- **Example: Light orb renderer using the helper**

  - In `features/spell/client/LightOrbRenderer.java`, the renderer now does:
    - Computes the orb size from the entity’s bounding box.
    - Asks `ShaderRenderHelper` for the appropriate render type (shader or fallback).
    - Submits its cube geometry via `RendererUtils.renderCube(...)`.
    - Calls `ShaderRenderHelper.updateTimeUniform(ShaderEffectHandler.LUMOS_ORB_SHADER)` as a hook for time-based uniforms.

  - Typical pattern:
    - Prefer the shader via `ShaderRenderHelper`.
    - Always provide a safe, texture-based fallback.
    - Keep per-entity renderer logic focused on geometry and transforms, not shader wiring.

### How to add a new shader and use it

- **1. Create the shader resources**
  - Add `my_effect.vsh`, `my_effect.fsh`, and `my_effect.json` under:
    - `src/main/resources/assets/spells_n_squares/shaders/core/`
  - Follow the structure of `lumos_orb.json`:
    - Define attributes you need (Position, Color, UVs, Normal).
    - Define uniforms for matrices, color modulation, and any custom parameters.

- **2. Register a render pipeline (optional but recommended for core effects)**
  - In `PostProcessingManager`, register a new `RenderPipeline` similar to the existing ones:
    - Use `withVertexShader(ModIdentifierHelper.modId("core/my_effect"))`.
    - Use `withFragmentShader(ModIdentifierHelper.modId("core/my_effect"))`.
  - Precompile it in `checkPipelineValidity()` and track validity in `pipelineValidity`.

- **3. Add a shader identifier in ShaderEffectHandler**
  - Define a new `Identifier` constant:
    - `public static final Identifier MY_EFFECT_SHADER = ModIdentifierHelper.modId("shaders/core/my_effect");`
  - Optionally add a helper:
    - `public static Identifier getMyEffectShaderId()` mirroring `getLumosOrbShaderId()`.

- **4. Use the helper from a renderer**
  - For a specific entity/geometry renderer:
    - Add a helper method in `ShaderRenderHelper`:
      - e.g. `getMyEffectRenderType()` or use the generic `getTranslucentEmissiveOrTexture(...)`.
    - Replace the direct `RenderTypes.entityTranslucent(...)` call with the helper:
      - This automatically prefers the shader and falls back to a texture.

- **5. Expose adjustable parameters**
  - For time-based or intensity-based effects:
    - Use existing config values such as `Config.getScreenEffectIntensity()` to drive behavior.
    - Pass time/intensity values through a small helper (e.g. `ShaderRenderHelper.updateTimeUniform(...)` or a similar method you add for new uniforms).
  - Keep these hooks thin:
    - The framework provides a single place to adjust how uniforms are passed once you decide how to wire them through the shader API.

### Inverted colors test (post-processing)
- Assets already present under `shaders/post/inverted_colors.*`.
- Use `ShaderEffectHandler.triggerInvertedColorsTest()` to run a quick inverted-colors post effect:
  - Prefers the post-processing shader if available/enabled.
  - Falls back to an overlay + shake when shaders are disabled or unavailable.
- This is a safe way to validate that post-processing shaders are wired without impacting regular rendering when shaders are off.



















