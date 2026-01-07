# Entity Size Scaling Analysis: How Pehkui and Minecraft Handle It

## Overview
This document analyzes how Pehkui and Minecraft handle entity size changes, and how our implementation compares.

## How Minecraft Handles Entity Dimensions

### 1. Entity Dimensions System
- `Entity.getDimensions(Pose)` - Returns `EntityDimensions` for a given pose
- `EntityType.getDimensions()` - Returns base dimensions for the entity type
- `Entity.refreshDimensions()` - Recalculates bounding box based on current dimensions
- `Entity.makeBoundingBox(Vec3)` - Creates a bounding box from dimensions and position

### 2. Rendering Pipeline
- `EntityRenderer.submit()` - Main rendering entry point (uses `EntityRenderState`)
- `PoseStack` - Transformation matrix for positioning/scaling models
- `ModelPart.xScale/yScale/zScale` - Individual model part scaling
- `HumanoidModel.setupAnim()` - Called to set up model animations and poses

### 3. Key Methods
```java
// Entity dimension calculation
EntityDimensions getDimensions(Pose pose) {
    return getType().getDimensions(); // Base dimensions
}

// Bounding box calculation
AABB makeBoundingBox(Vec3 pos) {
    EntityDimensions dims = getDimensions(getPose());
    // Calculate AABB from dimensions
}

// Rendering
void render(EntityRenderState state, PoseStack stack) {
    // Apply transformations to PoseStack
    // Render model using PoseStack
}
```

## How Pehkui Handles Entity Scaling

### 1. Multi-Layer Approach
Pehkui applies scaling at multiple points in the pipeline:

#### A. Dimension Layer (Bounding Box)
- **Intercepts `getDimensions()`** - Returns scaled dimensions
- **Modifies `refreshDimensions()`** - Ensures bounding box uses scaled dimensions
- **Modifies `makeBoundingBox()`** - Applies scaling to bounding box creation

#### B. Renderer Layer (Visual Scaling)
- **Intercepts `EntityRenderer.render()` or `submit()`** - Applies `PoseStack.scale()` transformation
- **Uses scale API** - Provides a centralized scale calculation system
- **Handles all entity types** - Not just players

#### C. Model Layer (Body Part Scaling)
- **Modifies `ModelPart` scales** - Sets `xScale`, `yScale`, `zScale` for individual parts
- **Applied in `setupAnim()`** - Ensures scales persist through animation updates

### 2. Scale Calculation
Pehkui uses a scale API that:
- Calculates final scale from multiple sources (base scale, modifiers, etc.)
- Caches scale values for performance
- Provides hooks for other mods to add scale modifiers

### 3. Key Implementation Points
```java
// Dimension modification
@ModifyReturnValue(method = "getDimensions")
EntityDimensions modifyDimensions(EntityDimensions original) {
    float scale = getScale(entity);
    return EntityDimensions.scalable(
        original.width() * scale,
        original.height() * scale
    );
}

// Renderer transformation
@Inject(method = "render", at = @At("HEAD"))
void onRender(PoseStack stack, ...) {
    float scale = getScale(entity);
    stack.pushPose();
    stack.scale(scale, scale, scale);
    // Render happens here
    stack.popPose();
}

// Model part scaling
@Inject(method = "setupAnim", at = @At("RETURN"))
void onSetupAnim(...) {
    float scale = getScale(entity);
    head.xScale = scale;
    head.yScale = scale;
    head.zScale = scale;
    // ... other parts
}
```

## Our Current Implementation

### What We're Doing Right
1. ✅ **Bounding Box Scaling** - `refreshDimensions()` and `makeBoundingBox()` mixins modify bounding box
2. ✅ **Renderer Scaling** - `LivingEntityRendererMixin` applies `PoseStack.scale()`
3. ✅ **Model Part Scaling** - `HumanoidModelMixin` sets `ModelPart` scales
4. ✅ **Client-Side Data Sync** - Network sync ensures client has model data

### What's Not Working
1. ❌ **`getDimensions()` Mixin** - Not intercepting (no logs appear)
2. ❌ **Visual Scaling** - Model size not changing despite bounding box changes
3. ❌ **Entity Access in Renderer** - May not be accessing entity correctly from `EntityRenderState`

### Potential Issues

#### 1. `getDimensions()` Not Intercepting
**Problem**: The mixin for `getDimensions()` is not being called.
**Possible Causes**:
- Method signature mismatch
- Mixin not applied correctly
- Method inlined by JVM
- Wrong target class

**Solution**: 
- Use `@Redirect` on `getType().getDimensions()` instead
- Or rely on `refreshDimensions()` and `makeBoundingBox()` for bounding box
- Focus on visual scaling in renderer

#### 2. Visual Scaling Not Working
**Problem**: Bounding box scales but model doesn't.
**Possible Causes**:
- `LivingEntityRendererMixin` not being called for players
- Entity access failing (reflection not finding field)
- `PoseStack` transformation being overridden
- `ModelPart` scales being reset after we set them

**Solution**:
- Verify `LivingEntityRendererMixin` is being called (check logs)
- Find correct field name for entity in `EntityRenderState`
- Ensure `PoseStack` transformation happens at correct point
- Apply `ModelPart` scales in `setupAnim()` at `RETURN` to ensure they persist

#### 3. Entity Access in Renderer
**Problem**: Reflection may not be finding the entity field.
**Solution**:
- Log all fields in `LivingEntityRenderState` to find correct field name
- Use `@Shadow` if possible
- Or use a different injection point that provides entity directly

## Recommended Approach

### 1. Fix Entity Access
- Log all fields in `LivingEntityRenderState` to find the correct field name
- Use that field to access the entity

### 2. Ensure Visual Scaling
- Verify `LivingEntityRendererMixin.onSubmit()` is being called
- Ensure `PoseStack.scale()` is applied correctly
- Verify `HumanoidModelMixin.applyPlayerModelScaling()` is being called
- Ensure `ModelPart` scales are set at the right time (after animations)

### 3. Alternative: Use PlayerRenderer Directly
- If `LivingEntityRenderer` doesn't work, try mixing into `PlayerRenderer` specifically
- `PlayerRenderer` extends `LivingEntityRenderer`, so it should work similarly

### 4. Debug Strategy
1. Add extensive logging to verify mixins are being called
2. Log entity access attempts
3. Log scale values being applied
4. Log `PoseStack` operations
5. Log `ModelPart` scale assignments

## Next Steps

1. **Run client and check logs** - Verify which mixins are being called
2. **Find entity field name** - Use field listing to find correct field
3. **Verify scale values** - Ensure model data is being read correctly
4. **Test PoseStack transformation** - Verify it's being applied
5. **Test ModelPart scaling** - Verify scales persist through animation updates

## Implementation Updates

### Improved Entity Access (Latest)
- Added support for `getEntity()` method access (if available)
- Expanded field name attempts: `entity`, `f_entity`, `livingEntity`, `f_livingEntity`, `f_234567_`, `f_234568_`
- Added comprehensive field listing when access fails
- Applied same improvements to `onSubmitReturn` method

### AvatarRenderer Mixin (New - Based on CustomPlayerModels)
- Created `AvatarRendererMixin` specifically for player rendering
- Based on [CustomPlayerModels' approach](https://github.com/tom5454/CustomPlayerModels/blob/master/CustomPlayerModels-1.21.11/src/main/java/com/tom/cpm/mixin/AvatarRendererMixinNeo.java)
- `AvatarRenderer` is player-specific, so no need to check if entity is a player
- Uses `AvatarRenderState` which may have easier entity access
- Tries `getPlayer()`, `getEntity()`, and field access with multiple field names
- More targeted and efficient than `LivingEntityRenderer` for players
- Both mixins are active: `AvatarRendererMixin` for players, `LivingEntityRendererMixin` as fallback

### Key Insight from Pehkui
Pehkui uses a multi-layer approach:
1. **Dimension Layer**: Intercepts `getDimensions()` to return scaled dimensions (we're using `refreshDimensions()` and `makeBoundingBox()` as fallback)
2. **Renderer Layer**: Applies `PoseStack.scale()` transformation in `submit()` or `render()` method
3. **Model Layer**: Sets `ModelPart.xScale/yScale/zScale` in `setupAnim()` at `RETURN` to ensure scales persist

Our implementation follows this pattern, but we need to verify:
- Entity access in renderer mixin (improved with multiple field name attempts)
- PoseStack transformation timing (currently at HEAD, might need adjustment)
- ModelPart scale persistence (currently at RETURN of setupAnim, should be correct)

