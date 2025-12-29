---
sidebar_position: 173
title: 'Chapter 173: Plant: Whomping Willow'
description: 'Plant: Whomping Willow - Implementation details and reference'
tags:
  - iii apothecary
  - 12 herbology
---

## Overview

The **Plant: Whomping Willow** is a Utility potion that provides beneficial effects. This potion is essential for various magical purposes and requires careful brewing following specific procedures and ingredient combinations.

This chapter provides complete documentation for implementing the Plant: Whomping Willow, including its recipe, brewing steps, effects, duration, and all technical specifications needed for integration into the potion brewing system.

## Implementation Details

### Potion Recipe

The Plant: Whomping Willow requires specific ingredients and brewing procedures to create successfully.

**Required Ingredients:**
- Base Ingredient (1x)
- Secondary Ingredient (2x)
- Catalyst (1x)

**Brewing Steps:**
1. Add base ingredient to cauldron
2. Heat to appropriate temperature
3. Add secondary ingredients in order
4. Stir in correct direction and count
5. Allow to brew for required time

**Brewing Time**: 5-10 minutes
**Difficulty Level**: Intermediate
**Skill Requirement**: 3 level in Potions

### Potion Effects

When consumed, the Plant: Whomping Willow applies the following effects:

**Primary Effects:**
- Beneficial Effect

**Secondary Effects:**
- None

**Duration**: 5:00 minutes
**Potency**: Level II

### Detailed Brewing Procedure

The Plant: Whomping Willow requires careful attention to detail during the brewing process. Each step must be executed precisely to achieve the desired result.

#### Step-by-Step Brewing Instructions

**Step 1: Preparation**
Add base ingredient to cauldron

**Step 2: Base Creation**
Heat to appropriate temperature

**Step 3: Ingredient Addition**
Add secondary ingredients in order

**Step 4: Refinement**
Stir in correct direction and count

**Step 5: Finalization**
Allow to brew for required time

#### Detailed Brewing Specifications

**Temperature Control:**
- **Initial Temperature**: 20°C
- **Brewing Temperature**: 150-200°C
- **Final Temperature**: 100-150°C
- **Temperature Tolerance**: ±10°C
- **Heating Method**: Cauldron with heat source

**Stirring Requirements:**
- **Stir Direction**: Clockwise
- **Stir Count**: 3-5 times
- **Stir Speed**: 30-60 RPM
- **Stir Timing**: After each ingredient addition
- **Stir Pattern**: Consistent circular motion

**Timing Requirements:**
- **Total Brewing Time**: 5-10 minutes
- **Step 1 Duration**: 1-2 minutes
- **Step 2 Duration**: 2-3 minutes
- **Step 3 Duration**: 1-2 minutes
- **Step 4 Duration**: 1 minutes
- **Step 5 Duration**: 1-2 minutes
- **Critical Timing Windows**: Ingredient addition must occur within 5 seconds of previous step

**Ingredient Preparation:**
**Base Ingredient**: Must be fresh, properly prepared according to type
**Secondary Ingredients**: May require chopping, grinding, or other preparation
**Catalyst**: Usually requires specific preparation method

### Detailed Potion Effects

When consumed, the Plant: Whomping Willow applies comprehensive effects that impact the player's capabilities and status.

#### Primary Effects

- Beneficial Effect

**Primary Effect Specifications:**
- **Effect Type**: Beneficial Effect
- **Effect Level**: I-II
- **Base Duration**: 5:00 minutes
- **Base Potency**: Level II
- **Effect Scaling**: Scales with potion skill level and brewing quality
- **Stacking Rules**: Does not stack with similar effects

#### Secondary Effects

- None

**Secondary Effect Specifications:**
- **Effect Type**: None
- **Effect Level**: N/A
- **Duration**: N/A
- **Potency**: N/A
- **Trigger Conditions**: N/A

#### Side Effects

No known side effects when brewed correctly. Incorrect brewing may result in negative effects.

**Side Effect Specifications:**
- **Side Effect Type**: None (when brewed correctly)
- **Occurrence Rate**: 0%
- **Severity**: None
- **Duration**: N/A
- **Mitigation**: Brew correctly to avoid side effects

#### Effect Interactions

**Interaction with Other Potions:**
- **Compatibility**: Compatible with most other potions
- **Synergy Effects**: May enhance effects of related potions
- **Antagonistic Effects**: May conflict with opposite effect potions
- **Stacking Behavior**: Replaces similar effects rather than stacking

**Interaction with Spells:**
- **Spell Enhancement**: May enhance related spell effects
- **Spell Interference**: No known interference
- **Magical Resonance**: Creates magical resonance with compatible spells

**Interaction with Status Effects:**
- **Status Effect Override**: May override weaker status effects
- **Status Effect Amplification**: May amplify compatible status effects
- **Status Effect Removal**: Does not remove status effects

### Detailed Usage Mechanics

The Plant: Whomping Willow is consumed by drinking from a potion bottle. Effects apply immediately upon consumption.

#### Consumption Mechanics

**Consumption Specifications:**
- **Consumption Time**: 1-2 seconds
- **Consumption Animation**: Drinking animation (1-2 seconds)
- **Consumption Sound**: Drinking sound effect
- **Consumption Particles**: Magical particles during consumption

#### Application Methods

**Direct Consumption:**
- Right-click with potion bottle to consume
- Effects apply immediately upon consumption
- Bottle is consumed in the process

**Splash Potion:**
- Throw potion to create area effect
- Affects all entities in 4-6 block radius
- Reduced duration compared to direct consumption

**Lingering Potion:**
- Creates lingering cloud effect
- Lasts 30-60 seconds
- Entities passing through cloud receive effects

#### Usage Scenarios

**Combat Usage:**
**Combat Application**: The Plant: Whomping Willow can be used strategically in combat situations.

**Exploration Usage:**
**Exploration Application**: The Plant: Whomping Willow can aid in exploration and navigation.

**Social Usage:**
**Social Application**: The Plant: Whomping Willow can be used in social situations.

**Crafting Usage:**
**Crafting Application**: The Plant: Whomping Willow may be used as an ingredient in advanced crafting recipes.

## Technical Specifications

### Potion Properties

| Property | Value | Description |
|----------|-------|-------------|
| **Potion Name** | Plant: Whomping Willow | Official name |
| **Type** | Utility | Potion classification |
| **Rarity** | Common | How rare the potion is |
| **Base Duration** | 5:00 minutes | Default effect duration |
| **Base Potency** | Level II | Effect strength level |
| **Brewing Time** | 5-10 | Time to brew (minutes) |
| **Difficulty** | Intermediate | Brewing difficulty |
| **Skill Level** | 3 | Required potions skill |

### Ingredient Requirements

| Ingredient | Quantity | Preparation | Timing |
|------------|----------|-------------|--------|
| Base Ingredient | 1 | Prepared | Step 1 |
| Secondary Ingredient | 2 | Chopped | Step 3 |
| Catalyst | 1 | Ground | Step 4 |

### Effect Properties

| Effect Type | Level | Duration | Notes |
|-------------|-------|----------|-------|
| Beneficial Effect | I | 5:00 | Provides beneficial effect |

### Brewing Configuration

| Setting | Value | Description |
|---------|-------|-------------|
| **Heat Level** | Medium (150-200°C) | Required cauldron temperature |
| **Stir Direction** | Clockwise | Clockwise or counter-clockwise |
| **Stir Count** | 3-5 | Number of stirs required |
| **Aging Required** | No | Whether potion needs aging |
| **Aging Time** | N/A | Time to age (if required) |

## Code Structure

### Potion Class

```java
package com.wizardingworld.mod.potions;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;

/**
 * Implementation of the Plant: Whomping Willow potion.
 * A potion that provides beneficial effects.
 */
public class PlantWhompingWillowPotion extends BasePotion {
    
    public static final String POTION_ID = "plant__whomping_willow";
    public static final int BASE_DURATION = 300;
    public static final int BASE_AMPLIFIER = 0;
    
    public PlantWhompingWillowPotion() {
        super(POTION_ID, UTILITY);
    }
    
    @Override
    public PotionRecipe getRecipe() {
        return PotionRecipe.builder()
            .addIngredient(IngredientRegistry.BASE_INGREDIENT.get(), 1)
            .addIngredient(IngredientRegistry.SECONDARY_INGREDIENT.get(), 2)
            .addIngredient(IngredientRegistry.CATALYST.get(), 1)
            .build();
    }
    
    @Override
    public MobEffectInstance createEffectInstance(int amplifier) {
        return new MobEffectInstance(
            EffectRegistry.BENEFICIAL_EFFECT.get(),
            BASE_DURATION * 20, // Convert to ticks
            amplifier,
            false, // Ambient
            true,  // Visible
            true   // Show particles
        );
    }
    
    @Override
    public int getBrewingTime() {
        return 300; // In seconds
    }
    
    @Override
    public PotionDifficulty getDifficulty() {
        return PotionDifficulty.INTERMEDIATE;
    }
    
    @Override
    public int getRequiredSkillLevel() {
        return 3;
    }
    
    @Override
    protected void applyAdditionalEffects(ItemStack potion, LivingEntity entity) {
        // Apply any additional custom effects
    }
}
```

### Potion Registration

```java
// In PotionRegistry.java
public static final Potion PLANTWHOMPINGWILLOW = register(
    new PlantWhompingWillowPotion(),
    PotionCategory.UTILITY
);
```

## Integration Points

### Potion Brewing System
- Registered in `PotionRegistry` during mod initialization
- Available in cauldron brewing interface
- Tracked in player's potion recipe book

### Effect System
- Integrates with Minecraft's effect system
- Applies custom magical effects
- Respects effect duration and amplifier

### Skill System
- Requires minimum Potions skill level
- Grants skill experience on successful brew
- Unlocks advanced variants at higher levels

### Ingredient System
- Uses ingredients from herbology system
- Validates ingredient quality and freshness
- Tracks ingredient consumption

## Development Notes

### Implementation Considerations
- Ensure brewing steps are balanced and achievable
- Ensure brewing steps are clear and achievable
- Balance potion effects for gameplay
- Test potion duration and potency values

### Future Enhancements
- Add potion variants (extended duration, increased potency)
- Implement potion combinations
- Add visual effects for potion consumption
- Consider adding potion mastery achievements

### Testing Checklist
- [ ] Verify potion can be brewed with correct recipe
- [ ] Test all brewing steps and timing
- [ ] Validate potion effects apply correctly
- [ ] Check duration and potency values
- [ ] Test skill requirements and progression
- [ ] Verify ingredient consumption
- [ ] Test potion stacking and combination

## Related Chapters

- [Cauldron Block Physics](/volume-iii-apothecary/part-10-brewing-mechanics/chapter-128-cauldron-block-physics)
- [Heat Source Logic](/volume-iii-apothecary/part-10-brewing-mechanics/chapter-129-heat-source-logic)
- [Stirring Mechanics](/volume-iii-apothecary/part-10-brewing-mechanics/chapter-130-stirring-mechanics)
- [Herbology System](/volume-iii-apothecary/part-12-herbology/chapter-161-magical-soil-nbt)
