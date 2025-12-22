# Artifact Item Patterns

## Overview

This document outlines common patterns found across artifact items in the mod. These patterns can be used as guidelines when creating new artifacts.

## Common Patterns

### 1. Base Class Usage

- **Most artifacts** extend `Item` directly
- **ElderWandItem** extends `WandItem` (special case - it's a wand artifact)
- All artifacts use `stacksTo(1)` in their constructors to make them unique items

### 2. Data Components

Many artifacts use data components to store state:
- `DeluminatorData` - stores captured light state
- `RemembrallData` - stores forgotten items list
- `SortingHatData` - stores house assignments
- `ElderWandData` - stores owner and mastery
- `TimeTurnerData` - stores snapshots and cooldown
- `GobletOfFireData` - stores tournament entries
- `MaraudersMapData` - stores map state
- `PensieveData` - stores memories
- `PhilosophersStoneData` - stores stone state
- `ResurrectionStoneData` - stores resurrection data

**Pattern**: Data components are typically defined in separate `*Data.java` files and registered via `DataComponentRegistry`.

### 3. Interaction Patterns

- **Right-click interaction**: Most artifacts override `use()` method
- **Block interaction**: Some artifacts (like `DeluminatorItem`) override `useOn()` for block-specific interactions
- **Server-side only**: Most interaction logic runs only on server side (`!level.isClientSide()`)
- **Player messages**: Use `serverPlayer.sendSystemMessage()` for feedback

### 4. Visual Effects

Common particle effects used:
- `ParticleTypes.ENCHANT` - magical effects
- `ParticleTypes.END_ROD` - sparkle effects
- `ParticleTypes.FLAME` - fire effects
- `ParticleTypes.SMOKE` - smoke effects

**Pattern**: Visual effects are typically spawned in `ServerLevel` using `sendParticles()`.

### 5. Sound Effects

Common sound patterns:
- `SoundEvents.AMETHYST_BLOCK_CHIME` - magical activation
- `SoundEvents.VILLAGER_YES` - success/confirmation
- `SoundEvents.SPYGLASS_USE` - viewing/scanning items

### 6. Cooldowns and Timers

Some artifacts implement cooldowns:
- `TimeTurnerItem` - has `COOLDOWN_TICKS` constant
- Cooldowns are typically stored in data components

### 7. Helper Methods

Common helper method patterns:
- `get*Data(ItemStack)` - retrieves data component from item stack
- `shouldGlow(ItemStack)` - determines if item should have glow effect (for rendering)
- Static utility methods for data component access

## Recommendations

1. **Use data components** for artifacts that need to store state
2. **Follow the naming convention**: `*Item.java` for item class, `*Data.java` for data component
3. **Register data components** via `DataComponentRegistry.registerAll()`
4. **Use server-side checks** for all interaction logic
5. **Provide visual and audio feedback** for user actions
6. **Use `stacksTo(1)`** to make artifacts unique items

## When to Create Base Classes

Currently, artifacts do not share enough common functionality to warrant base classes. Each artifact has unique behavior:
- Different interaction patterns
- Different data component structures
- Different visual effects
- Different use cases

If patterns emerge in the future (e.g., multiple artifacts with similar cooldown logic, similar data component access patterns), consider creating utility classes or base classes at that time.






