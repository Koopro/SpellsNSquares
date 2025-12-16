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

## Example Spell

```java
public class MySpell extends ModSpells.SimpleSpell {
    public MySpell() {
        super(SpellRegistry.spellId("my_spell"), "My Spell", "Does something cool", 60);
    }

    @Override
    public boolean cast(Player player, Level level) {
        // Your spell logic
        return true;
    }
}
```

## Service Loader File

`META-INF/services/at.koopro.spells_n_squares.core.api.addon.IAddon`
```
com.example.myaddon.MyAddon
```

## Notes

- Addon discovery currently uses ServiceLoader; manual registration via `AddonRegistry.registerAddon` is also supported.
- Keep namespaces consistent: spell/item/entity IDs must use your addon’s `modId`.
- Events fire on the server side for gameplay actions; ensure client-only logic checks `level.isClientSide()` where appropriate.
