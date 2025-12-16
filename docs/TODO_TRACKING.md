# TODO Tracking

This document tracks incomplete implementations and planned features that need to be completed.

## Creature System

### CreatureTamingHandler.java
- **Status**: Placeholder implementation
- **Location**: `features/creatures/CreatureTamingHandler.java`
- **Tasks**:
  - Implement `attemptTame()` method with creature-specific taming logic
  - Implement `isTamedBy()` method to check ownership via data components or entity fields
- **Notes**: Most entities now use `BaseTamableCreatureEntity` which provides owner management, but this handler could provide centralized taming logic.

### CatEntity.java
- **Status**: Missing NBT persistence
- **Location**: `features/creatures/CatEntity.java`
- **Tasks**:
  - Implement `addAdditionalSaveData()` and `readAdditionalSaveData()` for owner and loyalty data
- **Notes**: Consider refactoring to extend `BaseTamableCreatureEntity` if applicable.

## Education System

### HousePointsSystem.java
- **Status**: Data component integration missing
- **Location**: `features/education/HousePointsSystem.java`
- **Tasks**:
  - Implement `getHousePoints()` to retrieve from player data component
  - Implement `addPoints()` to update player data component
  - Implement `removePoints()` to update player data component
- **Dependencies**: Requires `HOUSE_POINTS_DATA` data component to be fully integrated

### HomeworkSystem.java
- **Status**: Data component integration missing
- **Location**: `features/education/HomeworkSystem.java`
- **Tasks**:
  - Implement retrieval from player data component
  - Implement adding homework assignments to player data component
- **Dependencies**: Requires homework data component integration

## Combat System

### SpellResistanceSystem.java
- **Status**: Resistance calculation not implemented
- **Location**: `features/combat/SpellResistanceSystem.java`
- **Tasks**:
  - Implement resistance calculation logic
  - Check target entity for resistance data component
  - Apply resistance modifiers with minimum damage threshold
- **Notes**: Framework is in place, needs implementation

### CombatStatsData.java
- **Status**: Data component integration missing
- **Location**: `features/combat/CombatStatsData.java`
- **Tasks**:
  - Implement retrieval from player data component
  - Implement storage in player data component

## Economy System

### CurrencyData.java
- **Status**: Data component integration missing
- **Location**: `features/economy/CurrencyData.java`
- **Tasks**:
  - Implement retrieval from player data component
  - Implement storage in player data component

## Block Interactions

### VaultBlock.java
- **Status**: GUI screen missing
- **Location**: `block/economy/VaultBlock.java`
- **Tasks**:
  - Create vault GUI screen
  - Implement vault inventory management
  - Handle vault access permissions

### EnchantmentTableBlock.java
- **Status**: GUI screen missing
- **Location**: `block/enchantments/EnchantmentTableBlock.java`
- **Tasks**:
  - Create enchantment GUI screen
  - Implement enchantment UI and logic

## Potion System

### PotionBrewingManager.java
- **Status**: Result spawning missing
- **Location**: `features/potions/PotionBrewingManager.java`
- **Tasks**:
  - Implement spawning result item or storing in cauldron data
  - Handle potion brewing completion

## Implementation Priority

1. **High Priority** (Core functionality):
   - HousePointsSystem data component integration
   - CombatStatsData data component integration
   - CurrencyData data component integration

2. **Medium Priority** (Feature completion):
   - SpellResistanceSystem implementation
   - CreatureTamingHandler implementation
   - CatEntity NBT persistence

3. **Low Priority** (UI/UX):
   - VaultBlock GUI screen
   - EnchantmentTableBlock GUI screen
   - PotionBrewingManager result handling



