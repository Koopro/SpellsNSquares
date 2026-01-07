# Testing Guide: Philosopher's Stone

This guide will help you test all aspects of the Philosopher's Stone implementation.

## Prerequisites

1. **Start the game** (client and server if testing multiplayer)
2. **Creative mode recommended** for initial testing (easier to get materials)
3. **Have these items ready:**
   - Heart of the Sea
   - Coal Blocks (4x)
   - Wither Roses (4x)
   - Lava Bucket
   - Nether Star
   - Totem of Undying
   - Gold Block
   - Magma Cream
   - Various blocks for transmutation (Iron Blocks, Lead, etc.)

---

## Part 1: Testing The Magnum Opus (Acquisition)

### Stage 1: Nigredo - Creating Prima Materia

**Steps:**
1. Open a Crafting Table
2. Place items in this pattern:
   ```
   [W] [R] [W]
   [C] [H] [C]
   [W] [R] [W]
   ```
   Where:
   - W = Wither Rose
   - C = Coal Block
   - H = Heart of the Sea
3. Check output slot for **Prima Materia**

**Expected Result:**
- Prima Materia appears in output slot
- Item should be dark/black in appearance
- Stacks to 1

---

### Stage 2: Albedo - Creating White Stone

**Note:** This stage currently requires manual completion or a custom blast furnace handler.

**Steps:**
1. Place a Blast Furnace
2. Put **Prima Materia** in the input slot
3. Put **Lava Bucket** in the fuel slot
4. Wait for smelting to complete
5. Check output slot

**Expected Result:**
- **White Stone** appears in output slot
- Item should glow faintly (has foil/enchantment glint)
- Stacks to 1

**Alternative:** If the blast furnace handler isn't working, you can:
- Use `/give` command: `/give @p spells_n_squares:white_stone`

---

### Stage 3: Rubedo - Creating Philosopher's Stone

**Steps:**
1. Build a **Beacon** (Tier 1 minimum - Iron/Gold base)
2. Ensure the beacon beam is active (has valid pyramid base)
3. Stand near the beacon
4. Throw these items into the beacon beam simultaneously:
   - **White Stone**
   - **Nether Star**
   - **Totem of Undying**
5. Items should hover in the beam area

**Expected Result:**
- Lightning strikes the beacon
- Beam turns red (visual effect)
- Items merge and disappear
- **Philosopher's Stone** drops at the location
- Visual effects: flames, enchant particles, end rods
- Sound effects: thunder and chime

**Troubleshooting:**
- Make sure all 3 items are within 1 block of each other
- Ensure beacon beam is active (check beacon has effects configured)
- Items must be in the beam (straight up from beacon)

---

## Part 2: Testing Transmutation Mechanics

### Test 1: Dissolution (Adding Materia)

**Steps:**
1. Hold **Philosopher's Stone** in main hand
2. Hold any item in **offhand** (e.g., Iron Ingot, Diamond, etc.)
3. **Shift + Right-Click** with the stone

**Expected Result:**
- Offhand item is consumed (reduced by 1)
- Stone gains materia (100 materia per item)
- Visual effects: enchant particles around player
- Sound: amethyst chime
- Message: "Dissolved [item] into materia"

**Test Multiple Items:**
- Try different items to ensure materia accumulates
- Check that materia value increases

---

### Test 2: Block Transmutation

**Steps:**
1. Ensure stone has materia (dissolve some items first)
2. Place a transmutable block (e.g., **Iron Block**)
3. **Right-Click** the block with the stone

**Expected Result:**
- Block transforms (Iron Block → Gold Block)
- Materia decreases by 100
- Entropy increases by 5%
- Visual effects:
  - Normal: Enchant particles
  - Danger zone (50%+): Smoke particles
- Sound: Amethyst chime

**Test Transmutation Pairs:**
- Iron Block → Gold Block ✓
- Iron Ore → Gold Ore ✓
- Copper Block → Gold Block ✓
- Stone → Gold Block ✓

**Test Entropy Warnings:**
- At 50% entropy: Warning message appears
- At 100% entropy: Critical mass warning

---

### Test 3: Entropy Backfire

**Steps:**
1. Build up entropy to 100% (transmute 20 blocks)
2. Attempt one more transmutation

**Expected Result:**
- **EXPLOSION** occurs at block location
- Player receives:
  - Slowness IV (40 ticks)
  - Poison II (40 ticks)
- 1-3 random items in inventory turn into Cobblestone
- Entropy resets to 0%
- Visual effects: explosion and smoke particles
- Sound: Explosion sound
- Message: "ENTROPY BACKFIRE! The Philosopher's Stone has exploded!"

**Safety Note:** Stand back when testing this!

---

## Part 3: Testing Elixir of Life Creation

**Steps:**
1. Find or place a **Cauldron**
2. Fill it with **Water** (Level 3 - full)
3. Drop a **Gold Block** into the cauldron area
4. Drop **Magma Cream** into the cauldron area
5. **Right-Click** the cauldron with **Philosopher's Stone**

**Expected Result:**
- Gold Block and Magma Cream are consumed
- Cauldron empties
- 3x **Elixir of Life** items drop above cauldron
- Visual effects: Flame and enchant particles
- Sound: Brewing stand brew sound
- Message: "Elixir of Life created!"

**Troubleshooting:**
- Make sure cauldron is full (level 3)
- Items must be close to cauldron (within 0.5 blocks)
- Both ingredients must be present

---

## Part 4: Testing Immortality System

### Test 1: Drinking Elixir

**Steps:**
1. Hold **Elixir of Life** in hand
2. **Right-Click** to drink (like a potion)
3. Wait for drinking animation

**Expected Result:**
- Elixir is consumed
- Player is now **cursed** (permanent flag set)
- Timer starts: 72000 ticks (60 minutes / 3 Minecraft days)
- **HUD appears** showing gold droplet (right side of hotbar)

---

### Test 2: God Mode (Active Immortality)

**While timer is active (ticksRemaining > 0):**

**Expected Behaviors:**
- **Hunger locked at 100%** - food bar stays full
- **Rapid regeneration** - heals 1 HP per second
- **Immunities:**
  - Fire damage (test with fire/lava)
  - Drowning (test underwater)
  - Poison (test with poison potion)
  - Wither (test with wither effect)
- **HUD shows:** Gold droplet that drains over time

**Test Each Immunity:**
1. Stand in fire/lava - should not take damage
2. Stay underwater - should not drown
3. Drink poison potion - should not be affected
4. Get hit by wither skeleton - should not wither

---

### Test 3: Death Prevention

**Steps:**
1. While immortal (timer active), take fatal damage
2. Health should reach 0

**Expected Result:**
- **Death is prevented**
- Player keeps all inventory (soulbound effect)
- Player immediately enters **Withered** state
- Health set to 1 HP (or 6 HP max)
- Timer set to 0
- Visual effects: Smoke and soul particles
- Sound: Wither death sound
- **HUD changes** to cracked grey droplet (pulsing)

---

### Test 4: Withered State

**When timer reaches 0 (naturally or after death prevention):**

**Expected Behaviors:**
- **Max health capped at 6 HP** (3 hearts)
- **Movement speed reduced** by 30% (Slowness effect)
- **Natural regeneration disabled**
- **HUD shows:** Cracked grey droplet with pulsing red warning
- Player remains in this state until drinking another Elixir

**Test Withered State:**
1. Wait for timer to expire OR trigger death prevention
2. Check health bar - should show max 3 hearts
3. Try to heal naturally - should not regenerate
4. Check movement - should be slower
5. Check HUD - should show cracked droplet

---

### Test 5: Drinking Another Elixir (Cure)

**Steps:**
1. While in withered state, drink another **Elixir of Life**
2. Timer resets to full duration

**Expected Result:**
- Timer resets to 72000 ticks
- Player returns to god mode
- HUD changes back to gold droplet
- All god mode benefits return

---

## Part 5: Testing HUD Overlay

### Test 1: HUD Visibility

**Steps:**
1. **Before drinking elixir:** HUD should be hidden
2. **After drinking elixir:** HUD should appear

**Expected Result:**
- HUD only appears if player has ever drunk elixir
- Position: Right side of hotbar
- Size: 16x16 pixels

---

### Test 2: Gold Droplet (Immortal State)

**While immortal (timer > 0):**

**Expected Appearance:**
- Gold droplet icon
- Drains from bottom as timer decreases
- Dark overlay on drained portion
- Smooth animation

**Test Draining:**
- Watch droplet drain over time
- Should match remaining time percentage

---

### Test 3: Cracked Droplet (Withered State)

**When withered (timer = 0):**

**Expected Appearance:**
- Cracked grey droplet icon
- Pulsing alpha effect (0.5 to 1.0)
- Red warning overlay (pulsing)
- Warning visual effect

**Test Pulsing:**
- Should pulse smoothly
- Red overlay should pulse in sync

---

## Part 6: Data Persistence Testing

### Test 1: Stone Data Persistence

**Steps:**
1. Use stone to build up materia and entropy
2. Save and quit game
3. Reload game
4. Check stone in inventory

**Expected Result:**
- Materia value persists
- Entropy value persists
- Stone state is preserved

---

### Test 2: Immortality Data Persistence

**Steps:**
1. Drink elixir (become cursed)
2. Wait some time (let timer decrease)
3. Save and quit game
4. Reload game
5. Check player state

**Expected Result:**
- Cursed flag persists (hasEverDrunk = true)
- Timer continues from where it left off
- HUD state matches timer value
- Withered state persists if timer was 0

---

## Part 7: Edge Cases & Stress Testing

### Test 1: Multiple Stones

**Steps:**
1. Create multiple Philosopher's Stones
2. Use different stones
3. Check that each maintains separate data

**Expected Result:**
- Each stone has independent materia/entropy
- Data doesn't mix between stones

---

### Test 2: Rapid Transmutation

**Steps:**
1. Transmute many blocks quickly
2. Watch entropy build up

**Expected Result:**
- Entropy accumulates correctly
- Warnings appear at appropriate thresholds
- Backfire triggers at 100%

---

### Test 3: Elixir Stacking

**Steps:**
1. Create multiple elixirs
2. Drink them in sequence

**Expected Result:**
- Each elixir resets timer to full
- Cursed flag remains true (doesn't reset)
- HUD updates correctly

---

### Test 4: Server/Client Sync

**Steps:**
1. Test in multiplayer
2. Have one player use stone
3. Check other players can see effects

**Expected Result:**
- Visual effects sync to all clients
- Sound effects play for all players
- Item states sync correctly

---

## Debug Commands (If Needed)

If something isn't working, you can use these commands:

```bash
# Give yourself items
/give @p spells_n_squares:prima_materia
/give @p spells_n_squares:white_stone
/give @p spells_n_squares:philosophers_stone
/give @p spells_n_squares:elixir_of_life

# Check player data (if debug commands exist)
# This would show immortality state, timer, etc.
```

---

## Common Issues & Solutions

### Issue: Prima Materia recipe doesn't work
**Solution:** Check recipe is generated correctly. Verify items are in correct pattern.

### Issue: White Stone not created in blast furnace
**Solution:** This stage may need manual completion or custom handler. Use `/give` command for now.

### Issue: Beacon ritual doesn't trigger
**Solution:** 
- Ensure all 3 items are within 1 block
- Check beacon has active beam (configure effects)
- Items must be in beam path (straight up from beacon)

### Issue: Transmutation doesn't work
**Solution:**
- Check stone has materia (dissolve items first)
- Ensure you're right-clicking the block (not air)
- Check block is in transmutation list (Iron, Copper, Stone, etc.)

### Issue: HUD doesn't appear
**Solution:**
- Make sure you've drunk elixir at least once
- Check client-side rendering is working
- Verify textures exist (vitality_droplet.png, vitality_droplet_cracked.png)

### Issue: Immortality effects not working
**Solution:**
- Check server-side event handlers are registered
- Verify player tick event is firing
- Check immortality data is being saved/loaded

---

## Testing Checklist

- [ ] Prima Materia can be crafted
- [ ] White Stone can be created (or given via command)
- [ ] Beacon ritual creates Philosopher's Stone
- [ ] Stone can dissolve items (shift-right-click)
- [ ] Stone can transmute blocks
- [ ] Entropy accumulates correctly
- [ ] Entropy warnings appear at 50% and 100%
- [ ] Backfire triggers at 100% entropy
- [ ] Elixir can be created in cauldron
- [ ] Elixir can be consumed
- [ ] Immortality timer starts (72000 ticks)
- [ ] God mode effects work (hunger lock, regen, immunities)
- [ ] Death prevention works
- [ ] Withered state applies correctly
- [ ] HUD appears after drinking elixir
- [ ] HUD shows gold droplet when immortal
- [ ] HUD shows cracked droplet when withered
- [ ] HUD drains correctly over time
- [ ] Data persists across game restarts
- [ ] Multiple stones work independently
- [ ] Effects sync in multiplayer

---

## Performance Testing

**Monitor:**
- FPS impact from HUD rendering
- Server tick performance with multiple players using stones
- Memory usage with persistent data
- Event handler performance (beacon ritual, player ticks)

**Expected:**
- Minimal FPS impact (< 1-2 FPS)
- Event handlers optimized (check every 20 ticks where possible)
- No memory leaks from data persistence

---

## Next Steps After Testing

1. **Fix any bugs** found during testing
2. **Balance values** if needed (materia costs, entropy rates, timer duration)
3. **Add missing textures** for HUD overlay
4. **Complete blast furnace handler** if needed
5. **Add more transmutation pairs** if desired
6. **Consider config options** for balancing

---

## Notes

- The blast furnace (Albedo) stage may need additional implementation
- Attribute modifier for withered health uses a simplified approach (direct health capping)
- HUD textures need to be created: `vitality_droplet.png` and `vitality_droplet_cracked.png`
- Some visual effects may need tuning based on your preferences

Good luck with testing!

