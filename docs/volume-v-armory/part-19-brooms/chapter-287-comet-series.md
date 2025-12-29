---
sidebar_position: 287
title: 'Chapter 287: Comet Series'
description: Comet Series - Implementation details and reference
tags:
  - v armory
  - 19 brooms
---

## Overview

The **Comet Series** is a Broom that serves important functions in the wizarding world. This item enables flight and is Common in the wizarding world.

This chapter provides comprehensive documentation for implementing the Comet Series, including its properties, crafting requirements, special abilities, usage mechanics, and all technical specifications needed for integration into the item system.

## Implementation Details

### Item Properties

The Comet Series has specific properties that define its behavior and capabilities.

**Base Properties:**
- Base functionality
- Durability/charges
- Usage requirements

**Special Properties:**
Item-specific special properties that make it unique.

### Crafting and Obtainment

The Comet Series can be obtained through:

- Purchased from shops
- Quest rewards

**Crafting Recipe:**
Crafting recipe details for the item.

### Usage Mechanics

The Comet Series is used through specific mechanics appropriate to its type.

### Special Abilities

The Comet Series may have special abilities that activate under certain conditions.

### Detailed Item Properties

The Comet Series has comprehensive properties that define its behavior, capabilities, and interactions.

#### Base Properties

**Physical Properties:**
- **Weight**: Light kg
- **Size**: 0.5 x 0.5 x 0.5 blocks
- **Material**: Wood and Twigs
- **Texture**: Item-specific texture
- **Model**: 3D model or sprite

**Functional Properties:**
- **Durability**: 500 uses
- **Stack Size**: 1 items
- **Max Stack**: 1 items
- **Use Time**: 1-3 seconds
- **Cooldown**: 5-30 seconds

**Economic Properties:**
- **Base Value**: 10-100 Galleons gold
- **Vendor Buy Price**: 10-100 Galleons gold
- **Vendor Sell Price**: 10 Galleons gold
- **Rarity**: Common

#### Special Properties

Item-specific special properties that make it unique.

**Special Property Details:**
- **Unique Properties**: Item-specific unique properties
- **Magical Properties**: None
- **Combat Properties**: Varies by item type
- **Utility Properties**: Various utility functions based on item type

### Detailed Crafting and Obtainment

The Comet Series can be obtained through various methods depending on its rarity and purpose.

#### Obtainment Methods

- Purchased from shops
- Quest rewards

**Method Details:**
- **Crafting**: Yes
- **Vendor Purchase**: Yes (for most items)
- **Quest Reward**: Varies
- **Loot Drop**: Varies by rarity
- **Special Event**: Rare

#### Detailed Crafting Recipe

Crafting recipe details for the item.

**Crafting Specifications:**
- **Crafting Station**: Crafting Table
- **Skill Requirement**: 3 level
- **Crafting Time**: 10-60 seconds
- **Success Rate**: 80-100%
- **Material Cost**: 50-500 gold

### Detailed Usage Mechanics

The Comet Series is used through specific mechanics appropriate to its type.

#### Usage Instructions

**Primary Usage:**
Right-click to mount and fly

**Secondary Usage:**
May have secondary functions or alternate uses

**Usage Specifications:**
- **Activation Method**: Right-click
- **Usage Duration**: 1-10 seconds
- **Usage Cooldown**: 5-30 seconds
- **Resource Cost**: Durability
- **Usage Conditions**: Item must be in inventory and meet usage requirements

#### Usage Scenarios

**Combat Usage:**
**Combat Application**: The Comet Series can be used in combat situations.

**Exploration Usage:**
**Exploration Application**: Use broom to fly and explore areas quickly.

**Crafting Usage:**
**Crafting Application**: The Comet Series may be used as a crafting ingredient.

**Social Usage:**
**Social Application**: The Comet Series can be used in social situations.

### Detailed Special Abilities

The Comet Series may have special abilities that activate under certain conditions.

#### Ability Specifications

**Active Abilities:**
Item-specific active abilities

**Ability Details:**
- **Ability Count**: 0-1 abilities
- **Ability Types**: Active, Passive, Triggered
- **Ability Cooldowns**: 5-60 seconds
- **Ability Costs**: Durability or charges
- **Ability Ranges**: 5-50 blocks

**Passive Abilities:**
Item-specific passive abilities

**Passive Effect Details:**
- **Passive Effect Type**: Stat boost, effect enhancement
- **Effect Strength**: 5-20%
- **Effect Duration**: While equipped
- **Effect Conditions**: Item must be equipped

### Detailed Enchantments and Modifications

The Comet Series can be enchanted with specific enchantments that enhance its properties.

#### Enchantment Compatibility

**Compatible Enchantments:**
Item-specific compatible enchantments

**Enchantment Specifications:**
- **Max Enchantments**: 1-3 enchantments
- **Enchantment Slots**: 1-3 slots
- **Enchantment Levels**: I-V levels
- **Enchantment Cost**: 100-1000 gold per level

#### Modification System

**Available Modifications:**
Item-specific modifications

**Modification Specifications:**
- **Modification Types**: Enhancement, Customization, Upgrade
- **Modification Slots**: 0-2 slots
- **Modification Cost**: 200-2000 gold
- **Modification Requirements**: Specific materials and skill level

## Technical Specifications

### Item Properties

| Property | Value | Description |
|----------|-------|-------------|
| **Item Name** | Comet Series | Official name |
| **Item Type** | Broom | Classification |
| **Rarity** | Common | Item rarity level |
| **Durability** | 500 | Maximum uses |
| **Stack Size** | 1 | Max stack amount |
| **Weight** | Light | Item weight |
| **Value** | 10-100 Galleons | Base gold value |

### Special Properties

| Property | Value | Description |
|----------|-------|-------------|
| Special Ability | Active | Provides unique effect | On use |
| Enchantment Slot | 1-3 | Can be enchanted | Always |

### Crafting Requirements

| Ingredient | Quantity | Source | Notes |
|------------|----------|--------|-------|
| Base Material | 1 | Crafting | Primary component |
| Secondary Material | 2 | Crafting | Enhancement |

### Ability Properties

| Ability | Type | Effect | Cooldown | Mana Cost |
|---------|------|--------|----------|-----------|
| Primary Ability | Active | Special effect | 30s | 20 mana |
| Secondary Ability | Passive | Constant effect | N/A | N/A |

## Code Structure

### Item Class

```java
package com.wizardingworld.mod.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

/**
 * Implementation of the Comet Series Broom.
 * serves important functions in the wizarding world
 */
public class CometSeriesItem extends BroomItem {
    
    public static final Item.Properties PROPERTIES = new Item.Properties()
        .stacksTo(1)
        .durability(500)
        .rarity(Rarity.COMMON);
    
    public CometSeriesItem() {
        super(PROPERTIES);
    }
    
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        // Handle item usage
        return super.finishUsingItem(stack, level, entity);
    }
    
    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        // Handle continuous use
        super.onUseTick(level, entity, stack, remainingUseDuration);
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        // Handle item activation
        return super.use(level, player, hand);
    }
    
    @Override
    public void activateFlight(ItemStack broom, Player player) {
        // Broom flight activation
    }
}
```

### Item Registration

```java
// In ItemRegistry.java
public static final Item COMETSERIES = register(
    "comet_series",
    new CometSeriesItem()
);
```

## Integration Points

### Item Registry
- Registered in `ItemRegistry` during mod initialization
- Available in creative menu
- Included in item database

### Crafting System
- Integrates with crafting table recipes
- Uses ingredient system
- Tracks crafting requirements

### Usage System
- Integrates with player interaction system
- Triggers item-specific behaviors
- Consumes durability/charges

### Ability System
- Uses shared ability framework
- Integrates with mana system
- Respects cooldown mechanics

### Enchantment System
- Compatible with enchantment system
- Can receive specific enchantments
- Enchantments affect item properties

## Development Notes

### Implementation Considerations
- Ensure item properties are balanced and provide engaging gameplay
- Ensure item properties are balanced
- Test crafting recipes and requirements
- Verify special abilities function correctly

### Future Enhancements
- Add item variants or upgrades
- Implement item sets with bonuses
- Add visual effects for special items
- Consider adding item customization options

### Testing Checklist
- [ ] Verify item can be crafted/obtained
- [ ] Test all item properties and behaviors
- [ ] Validate special abilities function
- [ ] Check durability and usage mechanics
- [ ] Test enchantment compatibility
- [ ] Verify integration with systems
- [ ] Test multiplayer synchronization

## Related Chapters

- [Item System Architecture](/volume-v-armory/part-18-wandlore/chapter-268-the-wand-lathe-block)
- [Crafting System](/volume-v-armory/part-18-wandlore/chapter-268-the-wand-lathe-block)
- [Enchantment System](/volume-v-armory/part-20-artifacts/chapter-294-invisibility-cloak)
- [Ability System](/volume-i-wizard/part-4-combat/chapter-25-dueling-mechanics)
