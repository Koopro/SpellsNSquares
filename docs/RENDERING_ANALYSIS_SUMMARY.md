# Rendering System Analysis - Summary

## Quick Overview

The codebase uses **NeoForge's deferred rendering system** (similar to SpectreLib patterns) for custom entity rendering. The system is well-architected but has some integration gaps.

## Key Findings

### ✅ What Works Well

1. **Entity Rendering Pipeline**
   - Uses `EntityRenderState` and `SubmitNodeCollector` for deferred rendering
   - Custom geometry submission via `submitCustomGeometry()`
   - Multiple renderers: ShieldOrb, LightOrb, LightningBeam, DummyPlayer
   - Smooth animations using time-based transformations

2. **Geometry Utilities**
   - `RendererUtils.renderCube()` for cuboid rendering
   - `RendererUtils.addQuad()` for custom quads
   - Proper vertex buffer management

3. **Screen Overlays**
   - Vignette, flash, and glow effects working
   - Rendered via `RenderGuiEvent.Post`

### ❌ Critical Issues

1. **Screen Shake Not Applied** ⚠️
   - `getShakeOffset()` calculates shake but is **never used**
   - No camera/view integration
   - **Impact**: Shake effects have no visual effect

2. **Random Shake Quality** ⚠️
   - Uses `new Random()` each frame → jittery motion
   - Should use smooth noise or seeded random

3. **Shaders Not Integrated** ⚠️
   - Shaders defined (`lumos_orb.vsh/fsh`) but not used
   - Falls back to particle effects
   - **Impact**: Custom shader effects not visible

## Architecture

```
Entity → extractRenderState() → submit() → submitCustomGeometry()
                                              ↓
                                    Deferred Rendering
                                              ↓
                                    Shader Application (if configured)
```

## Files to Review

- **Renderers**: `features/spell/client/*Renderer.java`
- **Utilities**: `core/client/RendererUtils.java`
- **Constants**: `core/client/RendererConstants.java`
- **Screen Effects**: `features/fx/ScreenEffectManager.java`
- **Shaders**: `resources/assets/spells_n_squares/shaders/core/`

## Quick Fixes Needed

1. **Screen Shake**: Apply `getShakeOffset()` to camera/view
2. **Shake Quality**: Replace random with smooth noise
3. **Shader Integration**: Connect shaders to entity renderers

## Detailed Analysis

See `RENDERING_SYSTEM_ANALYSIS.md` for complete documentation.
