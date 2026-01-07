# Spells N Squares - Architecture Documentation

This document describes the overall architecture of the Spells N Squares mod.

## Overview

Spells N Squares is built on NeoForge and follows a feature-based, modular architecture. The mod is organized into core systems and feature implementations, with a strong emphasis on extensibility through the addon API.

## Core Architecture Principles

1. **Feature-Based Organization**: Code is organized by feature, not by type
2. **Separation of Concerns**: Clear boundaries between client/server, core/features
3. **Extensibility**: Addon API allows third-party extensions
4. **Performance**: Caching, batching, and optimization throughout
5. **Maintainability**: Consistent patterns, comprehensive documentation

## System Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Mod Initialization                    │
│              (SpellsNSquares, ModInitialization)        │
└────────────────────┬────────────────────────────────────┘
                     │
        ┌────────────┴────────────┐
        │                         │
┌───────▼────────┐      ┌────────▼────────┐
│  Core Systems  │      │  Feature Systems│
│                │      │                 │
│ - Config       │      │ - Spells        │
│ - Data         │      │ - Wands         │
│ - Network      │      │ - Storage       │
│ - Registry     │      │ - FX            │
│ - Utilities    │      │ - ...           │
└────────────────┘      └─────────────────┘
        │                         │
        └────────────┬────────────┘
                     │
        ┌────────────▼────────────┐
        │      Addon API          │
        │  (Extension Point)      │
        └─────────────────────────┘
```

## Core Systems

### Configuration System

**Location**: `core/config/`

- **Config.java**: Main configuration class with all settings
- **ConfigAccessor.java**: Safe config value access utility
- **ConfigCache.java**: Config value caching for performance

**Pattern**: All config access goes through `Config` class methods which use caching.

### Data System

**Location**: `core/data/`

- **DataAccessLayer**: Abstraction for data storage
- **PersistentDataAccessLayer**: NBT-based implementation
- **DataComponentHelper**: Utility for common data operations
- **DataMigrationSystem**: Handles data version migrations

**Pattern**: Use `DataComponentHelper` for all player data operations.

### Network System

**Location**: `core/network/`

- **ModNetwork**: Main network payload registration
- **NetworkHelper**: Utilities for payload registration
- **NetworkPayloadBatcher**: Batches payloads for performance

**Pattern**: Register payloads in `ModNetwork.registerPayloadHandlers()`.

### Registry System

**Location**: `core/registry/`

- **FeatureRegistry**: Manages feature lifecycle
- **SpellRegistry**: Spell registration and retrieval
- **AddonRegistry**: Addon discovery and registration
- **PlayerDataManagerRegistry**: Player data synchronization

**Pattern**: Features register themselves, registries are registered in `ModInitialization`.

### Utility Systems

**Location**: `core/util/`

- **SafeEventHandler**: Error handling for events
- **DevLogger**: Development logging (respects config)
- **PlayerValidationUtils**: Player validation helpers

## Feature Systems

### Spell System

**Location**: `features/spell/`

- **Spell**: Interface for all spells
- **SpellManager**: Manages player spell data
- **SpellRegistry**: Spell registration
- **ModSpells**: Spell registration point

**Pattern**: Spells implement `Spell` interface, registered in `ModSpells.register()`.

### Wand System

**Location**: `features/wand/`

- **WandItem**: Base wand item
- **WandData**: Wand data storage
- **WandAttunementHandler**: Attunement mechanics
- **WandRegistry**: Wand registration

### Storage System

**Location**: `features/storage/`

- **PocketDimensionManager**: Manages pocket dimensions
- **PocketDimensionData**: Dimension data storage
- **NewtsCaseBlock**: Newt's Case implementation

### FX System

**Location**: `features/fx/` and `core/fx/`

- **ParticlePool**: Particle batching and pooling
- **FXConfigHelper**: FX configuration helpers
- **ScreenEffectManager**: Screen effects
- **PostProcessingManager**: Post-processing effects

## Data Flow

### Player Data Flow

```
Player Action
    │
    ▼
Feature System
    │
    ▼
DataComponentHelper
    │
    ▼
DataAccessLayer
    │
    ▼
PersistentDataAccessLayer
    │
    ▼
Player PersistentData (NBT)
```

### Spell Cast Flow

```
Player Input (Client)
    │
    ▼
SpellCastPayload (Network)
    │
    ▼
ModNetwork Handler (Server)
    │
    ▼
SpellManager.castSpellInSlot()
    │
    ▼
Spell.cast()
    │
    ▼
Spell Effects (Server)
    │
    ▼
Sync to Client (Network)
```

### Config Access Flow

```
Code calls Config.getXXX()
    │
    ▼
ConfigCache.get() (cached)
    │
    ▼
ConfigAccessor.getXXX() (safe access)
    │
    ▼
ModConfigSpec.XXX.get()
    │
    ▼
Config Value
```

## Initialization Flow

```
Mod Constructor
    │
    ├─► ModInitialization.registerRegistries()
    │   ├─► Core Registries (Blocks, Items, Entities)
    │   ├─► Feature Registries
    │   └─► Addon Discovery
    │
    └─► ModInitialization.registerEventHandlers()
        ├─► Feature Initialization
        ├─► Network Registration
        └─► Config Registration
```

## Addon Integration

```
ServiceLoader Discovery
    │
    ▼
AddonRegistry.registerAddon()
    │
    ▼
Addon.initialize(AddonContext)
    │
    ├─► Register Spells/Items/Entities
    ├─► Subscribe to Events
    └─► Register Network Payloads
```

## Performance Optimizations

1. **Config Caching**: Frequently accessed config values are cached
2. **Particle Batching**: Particles are batched per tick
3. **Network Batching**: Network payloads are batched when possible
4. **Lazy Initialization**: Non-critical systems initialize lazily
5. **Distance-Based LOD**: Particles use distance-based level of detail

## Extension Points

### For Addons

- **Spell Registration**: Via `AddonSpellRegistry`
- **Item/Block/Entity Registration**: Via registry helpers
- **Event Subscription**: Via `AddonEventBus`
- **Network Payloads**: Via `AddonNetworkRegistry`

### For Core Development

- **Feature System**: Implement `IFeature` interface
- **Data Components**: Use `DataComponentHelper`
- **Network**: Register in `ModNetwork`
- **Registry**: Create DeferredRegister and register in `ModInitialization`

## Thread Safety

- All game logic runs on the main server thread
- Static collections are not thread-safe (not needed)
- Network handlers use `context.enqueueWork()` for thread safety
- Client-side code checks `level.isClientSide()`

## Error Handling

- Event handlers use `SafeEventHandler`
- Network handlers have try-catch blocks
- Data operations handle exceptions gracefully
- All errors are logged with context

## Future Architecture Considerations

- Migration to entity data components (when fully supported)
- Enhanced lazy loading for non-critical features
- Improved caching strategies
- More granular performance optimizations





