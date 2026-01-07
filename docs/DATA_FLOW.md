# Data Flow Documentation

This document describes how data flows through the Spells N Squares mod.

## Player Data Flow

### Data Storage Architecture

```
Player Entity
    │
    └─► PersistentData (NBT)
        │
        └─► Key: "spells_n_squares:player_data"
            │
            └─► PlayerDataComponent.PlayerData
                ├─► SpellData
                ├─► WandData
                └─► (Future: More data types)
```

### Data Access Pattern

```
Feature Code
    │
    ▼
DataComponentHelper.get()
    │
    ├─► Null Check
    ├─► DataAccessLayer.load()
    │   │
    │   └─► PersistentDataAccessLayer
    │       │
    │       ├─► Client Side Check → Return Default
    │       ├─► Load from NBT
    │       ├─► DataMigrationSystem.migrateIfNeeded()
    │       │   │
    │       │   ├─► Check Version
    │       │   ├─► Apply Migrations
    │       │   └─► Update Version
    │       │
    │       └─► Deserialize with Codec
    │
    └─► Return Data (or Default)
```

### Data Update Pattern

```
Feature Code
    │
    ▼
DataComponentHelper.set()
    │
    └─► DataAccessLayer.save()
        │
        └─► PersistentDataAccessLayer
            │
            ├─► Client Side Check → Skip
            ├─► Serialize with Codec
            ├─► Set Current Version
            └─► Save to NBT
```

## Spell Data Flow

### Spell Slot Assignment

```
Client: SpellSelectionScreen
    │
    ├─► Player Selects Spell
    │
    └─► SpellSlotAssignPayload → Server
        │
        ▼
    Server: ModNetwork Handler
        │
        └─► SpellManager.setSpellInSlot()
            │
            ├─► Get PlayerData
            ├─► Update SpellSlotData
            ├─► Save PlayerData
            └─► SpellSlotsSyncPayload → Client
                │
                ▼
            Client: ClientSpellData
                └─► Update UI
```

### Spell Casting

```
Client: Player Presses Cast Key
    │
    └─► SpellCastPayload → Server
        │
        ▼
    Server: SpellManager.castSpellInSlot()
        │
        ├─► Get Spell from Slot
        ├─► Check Cooldown
        ├─► Spell.cast()
        │   │
        │   └─► Spell Effects
        │
        ├─► Update Cooldown
        ├─► Save PlayerData
        └─► SpellCooldownSyncPayload → Client
```

## Wand Data Flow

### Wand Crafting

```
Player Uses Wand Lathe
    │
    ▼
WandLatheScreen.craftWand()
    │
    ├─► Validate Materials
    ├─► Create Wand ItemStack
    ├─► Set WandData Component
    │   │
    │   └─► WandCore, WandWood
    │
    └─► Add to Player Inventory
```

### Wand Attunement

```
Player Casts Spell Sequence
    │
    ▼
WandAttunementHandler.onSpellCast()
    │
    ├─► Check Sequence Progress
    ├─► Update Progress
    └─► On Complete:
        │
        ├─► WandDataHelper.setAttuned()
        │   │
        │   └─► Update WandData Component
        │       │
        │       └─► Save to ItemStack
        │
        └─► Visual Feedback
```

## Storage Data Flow

### Pocket Dimension Creation

```
Player Places Newt's Case
    │
    ▼
NewtsCaseBlockEntity (Created)
    │
    └─► PocketDimensionData
        │
        ├─► Generate UUID
        ├─► Set Dimension Type
        └─► Save to BlockEntity
            │
            └─► NBT Persistence
```

### Pocket Dimension Access

```
Player Right-Clicks Case
    │
    ▼
NewtsCaseBlock.onServerInteract()
    │
    ├─► Get PocketDimensionData
    │   │
    │   └─► From BlockEntity NBT
    │
    └─► PocketDimensionManager.getOrCreateDimension()
        │
        ├─► Create Dimension (if needed)
        ├─► Initialize Structure
        └─► Store Player Entry
            │
            └─► PocketDimensionManager.storePlayerEntry()
                │
                └─► Map<Player, PlayerEntryData>
```

## Network Data Flow

### Client to Server

```
Client Action
    │
    ▼
Create Payload
    │
    ├─► Urgent: Send Immediately
    │   └─► PacketDistributor.sendToServer()
    │
    └─► Non-Urgent: Queue
        │
        └─► NetworkPayloadBatcher.queuePayload()
            │
            └─► End of Tick:
                │
                └─► NetworkPayloadBatcher.flush()
                    └─► Send All Queued
```

### Server to Client

```
Server State Change
    │
    ▼
Create Payload
    │
    └─► NetworkPayloadBatcher.queuePayload()
        │
        └─► End of Tick:
            │
            └─► NetworkPayloadBatcher.flush()
                │
                └─► For Each Player:
                    │
                    └─► PacketDistributor.sendToPlayer()
```

## Config Data Flow

### Config Loading

```
Mod Initialization
    │
    ▼
modContainer.registerConfig()
    │
    └─► Config.SPEC
        │
        └─► Load from File
            │
            ├─► Parse Values
            └─► Make Available
```

### Config Access

```
Code Calls Config.getXXX()
    │
    ▼
ConfigCache.get()
    │
    ├─► Cache Hit → Return
    │
    └─► Cache Miss:
        │
        ├─► ConfigAccessor.getXXX()
        │   │
        │   └─► ModConfigSpec.get()
        │       │
        │       └─► Return Value
        │
        └─► Cache Value
            └─► Return
```

### Config Reload

```
Config File Changed
    │
    ▼
Config Reload Event
    │
    └─► ConfigCache.onConfigReload()
        │
        └─► ConfigCache.invalidateAll()
            │
            └─► Clear Cache
                │
                └─► Next Access Recomputes
```

## Migration Data Flow

### Data Migration

```
Data Load Request
    │
    ▼
PersistentDataAccessLayer.load()
    │
    ├─► Load NBT
    │
    └─► DataMigrationSystem.migrateIfNeeded()
        │
        ├─► Get Data Version
        ├─► Compare to Current Version
        │
        ├─► If Older:
        │   │
        │   ├─► Find Migration Path
        │   ├─► Apply Migrations Sequentially
        │   │   │
        │   │   └─► Migration.migrate()
        │   │       │
        │   │       └─► Transform Data
        │   │
        │   └─► Update Version
        │       │
        │       └─► Save Back to NBT
        │
        └─► Return Migrated Data
```

## Event Data Flow

### Spell Cast Event

```
SpellManager.castSpellInSlot()
    │
    └─► Before Cast:
        │
        └─► AddonEventBus.post(SpellCastEvent)
            │
            ├─► Addon Handlers
            │   │
            │   ├─► Can Cancel Event
            │   ├─► Can Modify Event Data
            │   └─► Can Add Side Effects
            │
            └─► If Not Cancelled:
                │
                └─► Spell.cast()
```

### Spell Slot Change Event

```
SpellManager.setSpellInSlot()
    │
    ├─► Update Data
    │
    └─► AddonEventBus.post(SpellSlotChangeEvent)
        │
        └─► Addon Handlers
            │
            └─► React to Slot Changes
```

## Performance Optimizations in Data Flow

### Caching

- Config values cached after first access
- Cache invalidated on config reload
- Reduces repeated config file reads

### Batching

- Particles batched per tick
- Network payloads batched per player per tick
- Reduces network overhead

### Lazy Loading

- Data loaded only when needed
- Defaults returned for missing data
- Migrations applied only when data exists

### Distance-Based Optimization

- Particles culled beyond max distance
- Particle count reduced at distance
- Per-chunk particle limits enforced





