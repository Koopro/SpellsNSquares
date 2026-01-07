# Datagen TODO Tracking

This document tracks TODO comments in datagen files for unimplemented registries and features.

## Unimplemented Registries

### CombatRegistry
- **Status**: Not implemented
- **Location**: Referenced in multiple datagen files
- **Files**:
  - `ModTextureProvider.java` (lines 10, 113, 174)
  - `ModBlockModelProvider.java` (lines 7, 52)
  - `ModBlockLootProvider.java` (lines 7, 117, 184)
  - `ModItemModelProvider.java` (lines 9, 74)
- **Items/Blocks**: DUEL_ARENA
- **Action**: Implement CombatRegistry when combat system is ready

### CommunicationRegistry
- **Status**: Not implemented
- **Location**: Referenced in multiple datagen files
- **Files**:
  - `ModTextureProvider.java` (lines 11, 104, 171)
  - `ModBlockModelProvider.java` (lines 8, 47)
  - `ModBlockLootProvider.java` (lines 8, 92, 169)
  - `ModItemModelProvider.java` (lines 10, 65)
- **Items/Blocks**: NOTICE_BOARD
- **Action**: Implement CommunicationRegistry when communication system is ready

### EducationRegistry
- **Status**: Not implemented
- **Location**: Referenced in multiple datagen files
- **Files**:
  - `ModTextureProvider.java` (lines 13, 112, 176)
  - `ModBlockModelProvider.java` (lines 10, 51)
  - `ModBlockLootProvider.java` (lines 10, 114, 183)
  - `ModItemModelProvider.java` (lines 12, 73)
- **Items/Blocks**: HOUSE_POINTS_HOURGLASS
- **Action**: Implement EducationRegistry when education system is ready

### QuidditchRegistry
- **Status**: Not implemented
- **Location**: Referenced in multiple datagen files
- **Files**:
  - `ModTextureProvider.java` (lines 18, 110)
  - `ModItemModelProvider.java` (lines 17, 71)
  - `ModRecipeProvider.java` (line 8, 49)
- **Items**: Quidditch-related items
- **Action**: Implement QuidditchRegistry and QuidditchRecipeGenerator when quidditch system is ready

## Unimplemented Recipe Generators

### QuidditchRecipeGenerator
- **Status**: Not implemented
- **Location**: `ModRecipeProvider.java` (lines 8, 49)
- **Action**: Implement when QuidditchRegistry is ready

## Plant Blocks and Items

### Plant Blocks
- **Status**: Not registered in feature registries
- **Location**: `ModBlockLootProvider.java` (lines 125, 166, 188)
- **Blocks**: MANDRAKE_PLANT, WOLFSBANE_PLANT, GILLYWEED_PLANT, DEVILS_SNARE, VENOMOUS_TENTACULA, WHOMPING_WILLOW
- **Action**: Register plant blocks in appropriate feature registries

## Other TODOs

### ModBiomeModifierProvider
- **Status**: API not available
- **Location**: `ModBiomeModifierProvider.java` (lines 26, 31)
- **Action**: Implement biome modifier data map registration when API is available

## Notes

- All TODO comments are for future feature implementation, not bugs
- These registries and features are planned but not yet implemented
- When implementing, remember to uncomment the relevant lines in datagen files







