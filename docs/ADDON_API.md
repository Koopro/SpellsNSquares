# Spells_n_Squares Addon API (v1)

This document describes how to create addons for Spells_n_Squares using the new addon API.

## Quick Start

1. Add a dependency on Spells_n_Squares (ensure version >= 1.0.0 API).
2. Implement an addon class:
   ```java
   @AddonMod(modId = "my_addon", name = "My Addon", version = "1.0.0")
   public class MyAddon implements IAddon {
       @Override
       public void initialize(AddonContext context) {
           // Register spells
           context.getSpellRegistryHelper().registerSpell(new MySpell());
       }
   }
   ```
3. Register your addon via ServiceLoader:
   - Create `META-INF/services/at.koopro.spells_n_squares.core.api.addon.IAddon`
   - Add the fully-qualified class name of your addon class to the file.
4. Provide assets under your namespace (e.g., `assets/my_addon/`).

## Key Concepts

- **IAddon**: Implement to define your addon. Lifecycle methods:
  - `initialize(AddonContext)` – register spells/items/entities, subscribe to events
  - `registerRegistries(IEventBus)` – register DeferredRegisters
  - `clientInit(AddonContext)` – client-only setup
- **AddonMod**: Annotation carrying addon metadata (modId, name, version, dependencies, minApiVersion).
- **AddonContext**: Access to core APIs and helpers:
  - `getSpellManager()`, `getPlayerClassManager()`, `getSpellRegistry()`
  - Helpers: `AddonSpellRegistry`, `AddonItemRegistry`, `AddonEntityRegistry`, `AddonNetworkRegistry`
  - Event access: `getEventBus()` (AddonEventBus), `getModEventBus()` (NeoForge)
- **Events**: Subscribe via `AddonEventBus` to:
  - `SpellCastEvent` (cancellable)
  - `SpellSlotChangeEvent`
  - `PlayerClassChangeEvent`
- **Network**: Use `AddonNetworkRegistry` to register payloads during initialization; payloads are hooked into the mod’s registrar automatically.
- **Dependencies**: Declare via `dependencies` on `@AddonMod` (e.g., `"othermod@>=1.0.0"`). `minApiVersion` enforces API compatibility.

## Asset Conventions

- Spell icons: `assets/<your_modid>/textures/spell/<spell_id>.png`
- Translation keys: `spell.<namespace>.<path>.name`

## Examples

## Example Spell

```java
public class MySpell implements Spell {
    @Override
    public Identifier getId() {
        return Identifier.fromNamespaceAndPath("my_addon", "my_spell");
    }
    
    @Override
    public String getName() {
        return "My Spell";
    }
    
    @Override
    public String getDescription() {
        return "Does something cool";
    }
    
    @Override
    public int getCooldown() {
        return 60;
    }
    
    @Override
    public boolean cast(Player player, Level level) {
        // Your spell logic
        return true;
    }
    
    @Override
    public float getVisualEffectIntensity() {
        return 0.5f;
    }
}
```

### Registering Custom Registries

If you need to register custom registries (beyond blocks, items, entities), you can override `registerRegistries()`:

```java
@Override
public void registerRegistries(IEventBus modEventBus) {
    // Register custom registries here
    MyCustomRegistry.REGISTRY.register(modEventBus);
}
```

## Service Loader File

`META-INF/services/at.koopro.spells_n_squares.core.api.addon.IAddon`
```
com.example.myaddon.MyAddon
```

## Registry Helpers Reference

### AddonBlockRegistry
- `registerBlock(String name, Supplier<Block> blockSupplier)` - Register a block
- `registerBlock(String name, Function<Identifier, Block> blockFactory)` - Register a block with custom factory
- `blockId(String path)` - Create an Identifier for a block in your namespace
- `getDeferredRegister()` - Get the DeferredRegister (automatically registered, usually not needed)

### AddonItemRegistry
- `registerItem(String name, Supplier<Item> itemSupplier)` - Register an item
- `registerItem(String name, Function<Identifier, Item> itemFactory)` - Register an item with custom factory
- `itemId(String path)` - Create an Identifier for an item in your namespace
- `getDeferredRegister()` - Get the DeferredRegister (automatically registered, usually not needed)

### AddonEntityRegistry
- `registerEntity(String name, Supplier<EntityType<?>> entityTypeSupplier)` - Register an entity type
- `registerEntity(String name, EntityType.Builder<T> builder)` - Register an entity type with builder
- `entityId(String path)` - Create an Identifier for an entity in your namespace
- `getDeferredRegister()` - Get the DeferredRegister (automatically registered, usually not needed)

### AddonSpellRegistry
- `registerSpell(Spell spell)` - Register a spell (validates namespace)
- `spellId(String path)` - Create an Identifier for a spell in your namespace

## Notes

- **Automatic Registration**: Blocks, items, and entities registered through registry helpers are automatically registered with the mod event bus. You don't need to manually call `register()` on their DeferredRegisters.
- Addon discovery currently uses ServiceLoader; manual registration via `AddonRegistry.registerAddon` is also supported.
- Keep namespaces consistent: spell/item/entity IDs must use your addon’s `modId`.
- Events fire on the server side for gameplay actions; ensure client-only logic checks `level.isClientSide()` where appropriate.
- Registry registration order: Blocks are registered before items (as items may depend on blocks), then entities.
