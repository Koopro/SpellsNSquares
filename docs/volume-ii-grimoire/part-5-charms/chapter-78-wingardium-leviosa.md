---
sidebar_position: 78
title: 'Chapter 78: Wingardium Leviosa (Entity Drag)'
description: Wingardium Leviosa (Entity Drag) - Implementation details and reference
tags:
  - ii grimoire
  - 5 charms
---

## Overview

The **Wingardium Leviosa** spell is a fundamental component of the wizarding combat and utility system. This spell allows players to levitate objects. Understanding its mechanics, requirements, and implementation is essential for creating an authentic magical experience.

This chapter provides comprehensive technical documentation for implementing Wingardium Leviosa, including spell casting mechanics, wand movements, incantation requirements, mana consumption, cooldown systems, and integration with the broader spell framework.



## Implementation Status

✅ **Status: Implemented**

Fully implemented with hold-to-cast

## Implementation Details

### Spell Casting Mechanics

The Wingardium Leviosa spell is cast through a combination of wand movement, incantation, and magical energy channeling. The implementation requires careful coordination between the client-side input system and server-side validation.

**Casting Process:**
1. Player initiates spell cast through wand movement or keybind
2. Client validates wand movement pattern matches spell requirements
3. Server receives cast request and validates player has learned the spell
4. Mana cost is deducted from player's mana pool
5. Spell effect is applied based on target and context
6. Cooldown timer begins

**Wand Movement Pattern:**
The Wingardium Leviosa spell requires a specific wand movement pattern: "Point wand at target". This pattern must be executed correctly for the spell to activate. The pattern is tracked using gesture recognition algorithms that analyze the player's wand trajectory in 3D space.

**Incantation:**
The verbal component "Wingardium Leviosa" must be spoken (or activated via keybind) during the casting window. The incantation timing is synchronized with the wand movement for optimal spell power.

### Spell Effects

The Wingardium Leviosa spell produces specific magical effects when successfully cast.

The effect varies based on the target, caster's skill level, and environmental conditions.

### Physics Interactions

The spell interacts with Minecraft's physics system, affecting entity movement, block states, and environmental conditions as appropriate for its magical nature.

### Particle Effects

The Wingardium Leviosa spell generates distinctive visual effects that enhance player feedback and immersion:

- **Casting Particles**: Glowing particles emanate from the wand tip during casting
- **Impact Particles**: Explosive particle burst on successful impact
- **Trail Effects**: Magical trail follows the spell effect to target

### Sound Design

Audio feedback is crucial for spell casting:
- **Incantation Voice Line**: "Wingardium Leviosa" spoken by player character
- **Casting Sound**: Whooshing magical energy sound
- **Impact Sound**: Impact sound appropriate to spell effect

## Technical Specifications

### Spell Properties

| Property | Value | Description |
|----------|-------|-------------|
| **Spell Name** | Wingardium Leviosa | Official name of the spell |
| **Incantation** | "Wingardium Leviosa" | Verbal component required |
| **Mana Cost** | 15 | Base mana consumption per cast |
| **Cooldown** | 3 | Time between casts (seconds) |
| **Range** | 32 | Maximum effective range (blocks) |
| **Casting Time** | 0.5-1.5 | Time to complete cast (seconds) |
| **Difficulty** | Intermediate | Learning difficulty level |
| **Category** | Charm | Spell classification |
| **Target Type** | Entity/Block | Valid target types |

### Learning Requirements

| Requirement | Value | Description |
|-------------|-------|-------------|
| **Minimum Level** | 3 | Character level required |
| **Skill Prerequisite** | Charms | Required skill level |
| **Book/Quest** | Spell Book / Class | How spell is learned |
| **Practice Casts** | 25 | Casts needed to master |

### Spell Progression

| Level | Mana Cost | Cooldown | Effect Power | Unlock Condition |
|-------|-----------|----------|-------------|------------------|
| **Novice** | 15 | 3 | 100% | Initial learning |
| **Apprentice** | 13.5 | 2.7 | 125% | 50 successful casts |
| **Adept** | 12.0 | 2.4 | 150% | 200 successful casts |
| **Expert** | 10.5 | 2.1 | 175% | 500 successful casts |
| **Master** | 9.0 | 1.8 | 200% | 1000 successful casts |

## Code Structure

### Spell Class

```java
package com.wizardingworld.mod.spells;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Implementation of the Wingardium Leviosa spell.
 * This spell allows wizards to levitate objects.
 */
public class WingardiumLeviosaSpell extends BaseSpell {
    
    public static final String SPELL_ID = "wingardium_leviosa";
    public static final String INCANTATION = "Wingardium Leviosa";
    public static final int BASE_MANA_COST = 15;
    public static final int BASE_COOLDOWN = 3;
    public static final double BASE_RANGE = 32;
    
    public WingardiumLeviosaSpell() {
        super(SPELL_ID, INCANTATION, BASE_MANA_COST, BASE_COOLDOWN);
    }
    
    @Override
    public boolean canCast(SpellCaster caster, Level level) {
        if (!super.canCast(caster, level)) {
            return false;
        }
        
        // Additional validation logic
        // Check if target is valid
if (target != null && target.isAlive()) {
    return true;
}
        
        return true;
    }
    
    @Override
    public void cast(SpellCaster caster, Level level, Vec3 targetPos, LivingEntity target) {
        super.cast(caster, level, targetPos, target);
        
        // Apply spell effects
        // Apply spell-specific effects
applySpellEffect(caster, target, targetPos);
        
        // Spawn particles
        spawnCastingParticles(level, caster.getPosition(), targetPos);
        
        // Play sounds
        level.playSound(null, caster.getPosition(), 
            SoundRegistry.WINGARDIUMLEVIOSA_CAST.get(), 
            SoundSource.PLAYERS, 1.0f, 1.0f);
    }
    
    @Override
    protected void spawnCastingParticles(Level level, Vec3 start, Vec3 end) {
        ParticleSystem.spellTrail(level, start, end, 
    ParticleType.WINGARDIUM_LEVIOSA);
    }
    
    @Override
    public SpellCategory getCategory() {
        return SpellCategory.CHARMS;
    }
    
    @Override
    public int getRequiredLevel() {
        return 3;
    }
}
```

### Spell Registration

```java
// In SpellRegistry.java
public static final Spell WINGARDIUMLEVIOSA = register(
    new WingardiumLeviosaSpell(),
    SpellLearningSource.SPELL_BOOK
);
```

## Integration Points

### Spell System Integration
- Registered in `SpellRegistry` during mod initialization
- Available through `SpellBook` UI for learned spells
- Tracked in player's `SpellProgression` data

### Mana System
- Consumes mana through `ManaCore` component
- Respects player's current mana pool limits
- Triggers magical exhaustion if overused

### Wand System
- Requires valid wand equipped
- Wand core and wood affect spell power
- Wand condition affects casting success rate

### Skill System
- Contributes to Charms skill progression
- Unlocks advanced variants at higher skill levels
- Affects spell power and efficiency

## Development Notes

### Implementation Considerations
- Ensure spell respects server-side validation to prevent cheating
- Ensure proper server-client synchronization for spell effects
- Handle edge cases where target is invalid or out of range
- Consider performance impact of particle effects in multiplayer

### Future Enhancements
- Add spell variants for different skill levels
- Implement spell combinations with other spells
- Add visual customization options for spell effects
- Consider adding spell mastery achievements

### Testing Checklist
- [ ] Verify spell can be learned through intended method
- [ ] Test mana cost deduction and cooldown mechanics
- [ ] Validate spell effects apply correctly to targets
- [ ] Check particle and sound effects render properly
- [ ] Test spell progression system
- [ ] Verify multiplayer synchronization
- [ ] Test edge cases (invalid targets, out of range, etc.)

## Related Chapters


- [Wand Mechanics](/volume-v-armory/part-18-wandlore/chapter-268-the-wand-lathe-block)
- [Mana Core System](/volume-i-wizard/part-3-stats-conditions/chapter-19-mana-core)
- [Spell Particle Engine](/volume-ix-code/part-45-visuals/chapter-569-spell-particle-engine)
