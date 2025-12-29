---
sidebar_position: 302
title: 'Chapter 302: Time-Turner'
description: Time-Turner - Implementation details and reference
tags:
  - v armory
  - 20 artifacts
---

## Overview

The **Time-Turner** is a Legendary Artifact that grants the wielder control over the flow of time. Unlike standard utility items, it serves two distinct, powerful functions: modifying the server's time flow (Day/Night cycle) and acting as a safeguard against death.

This chapter provides comprehensive documentation for implementing the Time-Turner, specifically focusing on **multiplayer-safe time acceleration** and the **Temporal Anchor** rewind mechanic.

## Implementation Details

### Item Properties

The Time-Turner is a delicate, high-value tool designed for endgame utility.

**Base Properties:**
- **Functionality**: Channeling (Hold Right-Click) & State Saving (Shift-Click)
- **Durability**: 1000 Uses
- **Fuel**: Experience Points (XP)
- **Rarity**: Epic / Legendary

**Special Properties:**
- **Chronos Acceleration**: Rapidly advances the Day/Night cycle without skipping game logic (crops grow, furnaces smelt).
- **Temporal Anchor**: Acts as a specific "recall point" to prevent death, rewinding the player to a previous physical location.

### Crafting and Obtainment

The Time-Turner is an endgame artifact, typically crafted using lost materials found in the Deep Dark.

**Crafting Recipe:**
* **Core**: 1x Echo Shard (Ancient City drop - represents memory/time)
* **Frame**: 4x Gold Ingots
* **Mechanism**: 1x Clock
* **Catalyst**: 1x Nether Star

### Usage Mechanics

**Primary Interaction (Time Acceleration):**
* **Input**: Hold `Right-Click`
* **Effect**: The sun/moon moves at +100 ticks per tick.
* **Cost**: Drains Player XP every second.

**Secondary Interaction (Anchor Setting):**
* **Input**: `Shift + Right-Click` while standing on a block.
* **Effect**: Sets a "Temporal Anchor" at that location (saved to Item NBT).

**Passive Interaction (Death Prevention):**
* **Trigger**: Receiving fatal damage.
* **Effect**: Cancels death, heals player, and teleports them to the Anchor.

### Detailed Item Properties

#### Base Properties

**Physical Properties:**
- **Weight**: 0.2 kg
- **Size**: Handheld
- **Material**: Gold, Glass, Dust
- **Texture**: Animated spinning hourglass
- **Model**: Custom 3D Model (Necklace/Hourglass)

**Functional Properties:**
- **Durability**: 1000 Uses
- **Stack Size**: 1
- **Max Stack**: 1
- **Use Time**: Continuous (Channeling)
- **Cooldown**: 5 Minutes (after Death Prevention triggers)

**Economic Properties:**
- **Base Value**: Priceless
- **Rarity**: Legendary

#### Special Properties

**Multiplayer Safety (Anti-Grief):**
To prevent players from spamming night cycles on servers, the Time-Turner implementation includes an XP cost and audible cues so other players are aware time is being manipulated.

### Detailed Usage Mechanics

#### Usage Instructions

**Primary Usage: The Fast Forward**
Holding the item winds the hourglass.
1.  **Audio**: A winding clock sound plays, increasing in pitch.
2.  **Visual**: Time advances rapidly.
3.  **Feedback**: A chat message "The flow of time accelerates..." (Action Bar).

**Secondary Usage: The Death Undo**
If the Time-Turner is in the inventory (Main or Offhand) when the player takes fatal damage:
1.  **Trigger**: `LivingDeathEvent`
2.  **Effect**: Death is canceled.
3.  **Action**: Player is teleported to their last set "Temporal Anchor" (or World Spawn if none set).
4.  **Cost**: The Time-Turner loses 500 durability and applies a cooldown.

#### Usage Scenarios

**Combat Usage:**
**Combat Application**: Acts as a "Second Chance." If a boss fight goes wrong, you are "rewound" to safety rather than dying and losing items.

**Exploration Usage:**
**Exploration Application**: Quickly skip nights to avoid phantom spawns without needing a bed, or speed up crop growth cycles at a base.

### Detailed Special Abilities

#### Ability Specifications

**Active Ability: Temporal Acceleration**
- **Type**: Channeling
- **Speed**: +100 Ticks/tick
- **Range**: Global (affects World Time)
- **Cost**: 1 XP Level per 5 seconds of use

**Passive Ability: Chronos Protection**
- **Type**: Triggered (On Death)
- **Effect**: Teleport & Heal
- **Cooldown**: 5 Minutes (Real time)
- **Condition**: Must have "Anchor" set via Shift-Right-Click.

## Technical Specifications

### Item Properties

| Property | Value | Description |
|----------|-------|-------------|
| **Item Name** | Time-Turner | ID: `wizarding:time_turner` |
| **Item Type** | Artifact | |
| **Rarity** | Legendary | Purple/Gold tooltip color |
| **Durability** | 1000 | Can be repaired with Echo Shards |
| **Stack Size** | 1 | Non-stackable |

### Crafting Requirements

| Ingredient | Quantity | Source | Notes |
|------------|----------|--------|-------|
| Echo Shard | 1 | Ancient City | The "Memory" of time |
| Clock | 1 | Crafting | The mechanism |
| Nether Star | 1 | Wither Boss | The power source |
| Gold Ingot | 4 | Smelting | The frame |

## Code Structure (NeoForge)

### Item Class

```java
package com.wizardingworld.mod.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.network.chat.Component;

public class TimeTurnerItem extends Item {

    public TimeTurnerItem() {
        super(new Item.Properties()
            .stacksTo(1)
            .durability(1000)
            .rarity(Rarity.EPIC));
    }

    // 1. Activation (Right Click)
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (player.isCrouching()) {
            // Logic: Set "Temporal Anchor" (Save coordinates to NBT)
            if (!level.isClientSide) {
                stack.getOrCreateTag().putDouble("anchorX", player.getX());
                stack.getOrCreateTag().putDouble("anchorY", player.getY());
                stack.getOrCreateTag().putDouble("anchorZ", player.getZ());
                stack.getOrCreateTag().putString("anchorDim", level.dimension().location().toString());
                
                player.displayClientMessage(Component.literal("§6Temporal Anchor Set."), true);
                level.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.5f, 2.0f);
            }
            return InteractionResultHolder.success(stack);
        } else {
            // Logic: Start Winding (Fast Forward)
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
    }

    // 2. Active Use (Holding Right Click) - The Fast Forward
    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        if (!level.isClientSide && entity instanceof Player player) {
            
            // Accelerate Time (Add 100 ticks per game tick)
            long explicitTime = level.getDayTime();
            level.setDayTime(explicitTime + 100); 

            // FX: Play ticking sound every 0.5 seconds
            if (remainingUseDuration % 10 == 0) {
                 level.playSound(null, player.getX(), player.getY(), player.getZ(), 
                 SoundEvents.CLOCK_CLICK, SoundSource.PLAYERS, 0.3F, 1.5F);
            }

            // Cost: Drain XP every second
            if (remainingUseDuration % 20 == 0) {
                player.giveExperiencePoints(-1);
            }
        }
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW; 
    }
    
    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000; // Allows holding for a long time
    }
}

```

### Item Registration

```java
// In ItemRegistry.java
public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(WizardingWorldMod.MODID);

public static final DeferredItem<Item> TIME_TURNER = ITEMS.register("time_turner", TimeTurnerItem::new);

```

### Event Handler (Death Prevention)

```java
// In TimeTurnerEvents.java
@EventBusSubscriber(modid = WizardingWorldMod.MODID, bus = EventBusSubscriber.Bus.GAME)
public class TimeTurnerEvents {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack heldItem = player.getMainHandItem();
            
            // Check if holding Time Turner & Not on Cooldown
            if (heldItem.getItem() instanceof TimeTurnerItem && !player.getCooldowns().isOnCooldown(heldItem.getItem())) {
                
                event.setCanceled(true); // Stop death
                player.setHealth(4.0f); // Heal to 2 hearts
                player.removeAllEffects(); // Clear bad effects
                
                // Teleport Logic
                if (heldItem.hasTag() && heldItem.getTag().contains("anchorX")) {
                    double x = heldItem.getTag().getDouble("anchorX");
                    double y = heldItem.getTag().getDouble("anchorY");
                    double z = heldItem.getTag().getDouble("anchorZ");
                    player.teleportTo(x, y, z);
                    player.displayClientMessage(Component.literal("§eThe timeline rewinds..."), true);
                } else {
                    player.displayClientMessage(Component.literal("§cTime Turner shattered! No anchor set!"), false);
                }

                // Penalties
                player.getCooldowns().addCooldown(heldItem.getItem(), 6000); // 5 Min Cooldown
                heldItem.hurtAndBreak(500, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));
            }
        }
    }
}

```

## Integration Points

### Item Registry

* Registered via `DeferredRegister` in `ItemRegistry` class.
* Requires bus registration in Main Class constructor.

### Event System

* Subscribes to `LivingDeathEvent` on the **GAME** bus.
* **Note**: Ensure `event.setCanceled(true)` is supported by other mods installed; usually standardized in NeoForge.

### Usage System

* Uses standard vanilla `onUseTick` for low-overhead time acceleration.
* No complex packet handling required (Server-side time sync is automatic).

## Development Notes

### Implementation Considerations

* **Lag Warning**: Rapidly changing time causes lighting updates. Ensure `setDayTime` is used efficiently.
* **Safety**: The "Death Prevention" mechanic must be tested against "Void Damage" (falling out of the world). Usually, you should add a check: `if (event.getSource() == DamageSource.OUT_OF_WORLD) return;` to prevent saving players who fall into the Void.

### Testing Checklist

* [ ] Verify time speeds up only when holding right click.
* [ ] Verify XP drains while using.
* [ ] **Critical**: Test Death Prevention to ensure inventory is not lost.
* [ ] Verify Cooldown applies correctly after death prevention.
* [ ] Test Anchor setting persists across dimension changes (if implemented).

## Related Chapters

* [Item System Architecture](https://www.google.com/search?q=/volume-v-armory/part-18-wandlore/chapter-268-the-wand-lathe-block)
* [Crafting System](https://www.google.com/search?q=/volume-v-armory/part-18-wandlore/chapter-268-the-wand-lathe-block)
* [Enchantment System](https://www.google.com/search?q=/volume-v-armory/part-20-artifacts/chapter-294-invisibility-cloak)
* [Ability System](https://www.google.com/search?q=/volume-i-wizard/part-4-combat/chapter-25-dueling-mechanics)
