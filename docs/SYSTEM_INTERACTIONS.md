# System Interactions

This document describes how different systems in Spells N Squares interact with each other.

## Spell System Interactions

### Spell Casting Flow

```
Player Input
    │
    ├─► Client: SpellSelectionScreen
    │   └─► SpellSlotAssignPayload → Server
    │
    └─► Client: Spell Cast Key
        └─► SpellCastPayload → Server
            │
            ▼
        Server: ModNetwork Handler
            │
            ▼
        SpellManager.castSpellInSlot()
            │
            ├─► Spell.cast() → Spell Effects
            ├─► SpellManager.updateCooldown()
            ├─► WandAttunementHandler (if attuning)
            └─► SpellCastEvent → AddonEventBus
                │
                └─► Addon Handlers
```

### Spell Data Synchronization

```
Server: SpellManager
    │
    ├─► Player Persistent Data (NBT)
    │   └─► SpellSlotData
    │
    └─► SpellSlotsSyncPayload → Client
        │
        ▼
    Client: ClientSpellData
        └─► SpellSelectionScreen Display
```

## Wand System Interactions

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
        ├─► WandData Component Update
        └─► Visual Effects
```

### Wand Visual Effects

```
Spell Cast
    │
    ▼
WandAttunementHandler.onSpellCast()
    │
    └─► WandVisualEffects.spawnCastTrail()
        │
        ├─► Check Config.areWandParticlesEnabled()
        ├─► FXConfigHelper.calculateParticleCountWithLOD()
        └─► ParticlePool.queueParticle()
```

## Storage System Interactions

### Pocket Dimension Access

```
Player Right-Clicks Newt's Case
    │
    ▼
NewtsCaseBlock.onServerInteract()
    │
    ├─► Get/Create BlockEntity
    ├─► Get PocketDimensionData
    │
    └─► PocketDimensionManager.getOrCreateDimension()
        │
        ├─► Create Dimension (if needed)
        ├─► Initialize Structure
        └─► Teleport Player
            │
            └─► Store Entry Point
                └─► PocketDimensionManager.storePlayerEntry()
```

### Pocket Dimension Exit

```
Player in Pocket Dimension
    │
    ▼
PocketDimensionExitHandler.onPlayerTick()
    │
    ├─► Check Exit Platform
    ├─► Validate Entry Point (Case still open)
    └─► Teleport Back
        │
        └─► PocketDimensionManager.getPlayerEntry()
```

## FX System Interactions

### Particle Spawning

```
Feature Requests Particles
    │
    ▼
ParticlePool.queueParticle()
    │
    ├─► Check Per-Chunk Limit
    ├─► Add to Queue
    └─► End of Tick:
        │
        └─► FXPerformanceHandler.onLevelTick()
            │
            └─► ParticlePool.flush()
                └─► Send All Queued Particles
```

### Screen Effects

```
Spell Cast / Event
    │
    ▼
ScreenEffectManager.triggerXXX()
    │
    ├─► Check Config Settings
    ├─► Apply Intensity Multiplier
    └─► Render Effect
```

## Network System Interactions

### Payload Batching

```
Feature Sends Payload
    │
    ├─► Urgent: NetworkHelper.sendImmediate()
    │   └─► Send Immediately
    │
    └─► Non-Urgent: NetworkPayloadBatcher.queuePayload()
        │
        └─► End of Tick:
            │
            └─► NetworkPayloadBatcher.flush()
                └─► Send All Queued Payloads
```

## Data System Interactions

### Player Data Access

```
Feature Needs Player Data
    │
    ▼
DataComponentHelper.get()
    │
    ├─► DataAccessLayer.load()
    │   │
    │   └─► PersistentDataAccessLayer
    │       │
    │       ├─► Check Client Side → Return Default
    │       ├─► Load from NBT
    │       ├─► DataMigrationSystem.migrateIfNeeded()
    │       └─► Deserialize with Codec
    │
    └─► Return Data (or Default)
```

### Data Updates

```
Feature Updates Player Data
    │
    ▼
DataComponentHelper.set()
    │
    └─► DataAccessLayer.save()
        │
        └─► PersistentDataAccessLayer
            │
            ├─► Check Client Side → Skip
            ├─► Serialize with Codec
            ├─► Set Data Version
            └─► Save to NBT
```

## Config System Interactions

### Config Access

```
Code Calls Config.getXXX()
    │
    ▼
ConfigCache.get()
    │
    ├─► Cache Hit → Return Cached Value
    │
    └─► Cache Miss:
        │
        └─► ConfigAccessor.getXXX()
            │
            ├─► Try: ModConfigSpec.get()
            └─► Catch: Return Default
                │
                └─► Cache and Return
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
            └─► Clear All Cached Values
```

## Addon System Interactions

### Addon Discovery

```
Mod Initialization
    │
    ▼
AddonRegistry.discoverAddons()
    │
    ├─► ServiceLoader Discovery
    ├─► Validate Dependencies
    ├─► Check API Version
    └─► Register Addon
        │
        └─► Addon.initialize(AddonContext)
```

### Addon Event Handling

```
Game Event Occurs
    │
    ▼
Feature Publishes Event
    │
    └─► AddonEventBus.post()
        │
        └─► Addon Event Handlers
            │
            ├─► Can Cancel (SpellCastEvent)
            └─► Can Modify Event Data
```

## Cross-System Dependencies

### Spell → Wand

- Spells check for wand in hand
- Wand visual effects on spell cast
- Wand attunement uses spell sequences

### Storage → Data

- Pocket dimensions use data components
- Storage data persisted via DataAccessLayer

### FX → Config

- All FX respects config settings
- Particle counts use config multipliers
- Screen effects use config intensity

### Network → All Systems

- All systems use network for sync
- Payloads batched for performance
- Client/server separation enforced

## Performance Critical Paths

### Every Tick

- Player tick handlers
- Entity updates
- Particle flushing
- Network payload batching

### On Spell Cast

- Spell validation
- Cooldown checks
- Effect spawning
- Network sync
- Event publishing

### On Data Access

- Config cache lookup
- Data component access
- Migration checks (if needed)

## Error Propagation

```
Error in Feature
    │
    ├─► SafeEventHandler.execute()
    │   └─► Log Error with Context
    │       └─► Continue Execution
    │
    └─► Direct Exception
        │
        └─► Logged by System
            └─► May Crash (if critical)
```





