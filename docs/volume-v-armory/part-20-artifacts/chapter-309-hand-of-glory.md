---
sidebar_position: 309
title: 'Chapter 309: Hand of Glory'
description: Hand of Glory - Implementation details and reference
tags:
  - v armory
  - 20 artifacts
---

## Overview

The **Hand of Glory** is a Artifact that serves important functions in the wizarding world. This item provides powerful abilities and is Common in the wizarding world.

This chapter provides comprehensive documentation for implementing the Hand of Glory, including its properties, crafting requirements, special abilities, usage mechanics, and all technical specifications needed for integration into the item system.

## Implementation Details

### Item Properties

The Hand of Glory has specific properties that define its behavior and capabilities.

**Base Properties:**
- Base functionality
- Durability/charges
- Usage requirements

**Special Properties:**
Item-specific special properties that make it unique.

### Crafting and Obtainment

The Hand of Glory can be obtained through:

- Quest rewards
- Rare drops
- Special events

**Crafting Recipe:**
Crafting recipe details for the item.

### Usage Mechanics

The Hand of Glory is used through specific mechanics appropriate to its type.

### Special Abilities

The Hand of Glory may have special abilities that activate under certain conditions.

### Detailed Item Properties

The Hand of Glory has comprehensive properties that define its behavior, capabilities, and interactions.

#### Base Properties

**Physical Properties:**
- **Weight**: Light kg
- **Size**: 0.5 x 0.5 x 0.5 blocks
- **Material**: Various
- **Texture**: Item-specific texture
- **Model**: 3D model or sprite

**Functional Properties:**
- **Durability**: Unlimited uses
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
- **Magical Properties**: Magical energy channeling, spell enhancement
- **Combat Properties**: Varies by item type
- **Utility Properties**: Various utility functions based on item type

### Detailed Crafting and Obtainment

The Hand of Glory can be obtained through various methods depending on its rarity and purpose.

#### Obtainment Methods

- Quest rewards
- Rare drops
- Special events

**Method Details:**
- **Crafting**: Varies
- **Vendor Purchase**: Yes (for most items)
- **Quest Reward**: Yes
- **Loot Drop**: Varies by rarity
- **Special Event**: Yes

#### Detailed Crafting Recipe

Crafting recipe details for the item.

**Crafting Specifications:**
- **Crafting Station**: Crafting Table
- **Skill Requirement**: 1 level
- **Crafting Time**: 10-60 seconds
- **Success Rate**: 80-100%
- **Material Cost**: 50-500 gold

### Detailed Usage Mechanics

The Hand of Glory is used through specific mechanics appropriate to its type.

#### Usage Instructions

**Primary Usage:**
Right-click to use

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
**Combat Application**: The Hand of Glory can be used in combat situations.

**Exploration Usage:**
**Exploration Application**: The Hand of Glory can aid in exploration.

**Crafting Usage:**
**Crafting Application**: The Hand of Glory may be used as a crafting ingredient.

**Social Usage:**
**Social Application**: The Hand of Glory can be used in social situations.

### Detailed Special Abilities

The Hand of Glory may have special abilities that activate under certain conditions.

#### Ability Specifications

**Active Abilities:**
Special artifact abilities

**Ability Details:**
- **Ability Count**: 1-3 abilities
- **Ability Types**: Active, Passive, Triggered
- **Ability Cooldowns**: 5-60 seconds
- **Ability Costs**: Durability or charges
- **Ability Ranges**: 5-50 blocks

**Passive Abilities:**
Constant passive effects while equipped

**Passive Effect Details:**
- **Passive Effect Type**: Stat boost, effect enhancement
- **Effect Strength**: 5-20%
- **Effect Duration**: While equipped
- **Effect Conditions**: Item must be equipped

### Detailed Enchantments and Modifications

The Hand of Glory can be enchanted with specific enchantments that enhance its properties.

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
| **Item Name** | Hand of Glory | Official name |
| **Item Type** | Artifact | Classification |
| **Rarity** | Common | Item rarity level |
| **Durability** | Unlimited | Maximum uses |
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
 * Implementation of the Hand of Glory Artifact.
 * serves important functions in the wizarding world
 */
public class HandofGloryItem extends Item {
    
    public static final Item.Properties PROPERTIES = new Item.Properties()
        .stacksTo(1)
        
        .rarity(Rarity.COMMON);
    
    public HandofGloryItem() {
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
    
    
}
```

### Item Registration

```java
// In ItemRegistry.java
public static final Item HANDOFGLORY = register(
    "hand_of_glory",
    new HandofGloryItem()
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
