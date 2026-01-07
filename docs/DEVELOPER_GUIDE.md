# Developer Guide

This guide provides information for developers working on or contributing to the Spells N Squares mod.

## Getting Started

### Prerequisites

- Java 21 or higher
- Gradle 8.0 or higher
- NeoForge for Minecraft (version specified in `gradle.properties`)
- An IDE with Java support (IntelliJ IDEA, Eclipse, or VS Code)

### Setting Up the Development Environment

1. Clone the repository
2. Open the project in your IDE
3. Run `./gradlew build` (or `gradlew.bat build` on Windows) to build the mod
4. Run `./gradlew runClient` to start the development client
5. Run `./gradlew runServer` to start the development server

### Project Structure

The mod follows a feature-based organization:

```
src/main/java/at/koopro/spells_n_squares/
├── core/              # Core systems and utilities
│   ├── config/       # Configuration management
│   ├── data/         # Data persistence and components
│   ├── entity/       # Base entity classes
│   ├── fx/           # Visual effects and particles
│   ├── network/      # Network communication
│   ├── registry/     # Registry systems
│   └── util/         # Utility classes
├── features/         # Feature implementations
│   ├── spell/        # Spell system
│   ├── wand/         # Wand system
│   ├── storage/      # Storage systems
│   └── ...
└── init/             # Initialization code
```

## Code Style and Patterns

### Error Handling

Always use `SafeEventHandler` for event handlers:

```java
@SubscribeEvent
public static void onPlayerTick(PlayerTickEvent.Post event) {
    SafeEventHandler.execute(() -> {
        // Your handler logic
    }, "ticking player", event.getEntity());
}
```

### Data Component Access

Use `DataComponentHelper` for player data operations:

```java
// Get data
MyData data = DataComponentHelper.get(player, "my_data", MyData.CODEC, MyData::createDefault);

// Update data
DataComponentHelper.update(player, "my_data", MyData.CODEC, MyData::createDefault,
    current -> current.withNewValue(newValue));
```

### Configuration Access

Use `Config` class methods which automatically cache values:

```java
if (Config.isHighFxMode()) {
    // High FX mode enabled
}
```

### Logging

Use `DevLogger` for development logging (respects config settings):

```java
DevLogger.logMethodEntry(this, "myMethod", "param=" + param);
DevLogger.logStateChange(this, "setValue", "value=" + value);
```

## Creating New Features

### 1. Create Feature Directory

Create a directory under `features/` for your feature:

```
features/myfeature/
├── MyFeatureItem.java
├── MyFeatureBlock.java
├── client/          # Client-side code
└── network/         # Network payloads
```

### 2. Register Your Feature

Create a registry class:

```java
public final class MyFeatureRegistry {
    public static final DeferredRegister<Item> ITEMS = 
        DeferredRegister.create(Registries.ITEM, SpellsNSquares.MODID);
    
    public static final DeferredHolder<Item, Item> MY_ITEM = 
        ITEMS.register("my_item", () -> new MyFeatureItem(...));
    
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
```

### 3. Register in ModInitialization

Add your registry to `ModInitialization.registerRegistries()`:

```java
MyFeatureRegistry.register(modEventBus);
```

## Testing

### Running Tests

```bash
./gradlew test
```

### Writing Tests

Create test files in `src/test/java/` following the same package structure:

```java
class MyFeatureTest {
    @Test
    void testMyFeature() {
        // Test implementation
    }
}
```

## Building and Distribution

### Building the Mod

```bash
./gradlew build
```

The built JAR will be in `build/libs/`.

### Data Generation

```bash
./gradlew runData
./gradlew runServerData
```

## Common Tasks

### Adding a New Spell

1. Create a class implementing `Spell` interface
2. Register it in `ModSpells.register()`
3. Add spell icon texture at `assets/spells_n_squares/textures/spell/{spell_name}.png`

### Adding a New Item

1. Create item class extending appropriate base class
2. Register in feature registry
3. Add item model and texture
4. Add to creative tab if needed

### Adding Network Communication

1. Create payload class implementing `CustomPacketPayload`
2. Register in `ModNetwork.registerPayloadHandlers()`
3. Handle on client/server as appropriate

## Debugging

### Enable Verbose Logging

Set in config file or via command:

```
/spells_n_squares config set enableVerboseLogging true
```

### Common Issues

- **Mod not loading**: Check logs in `run/logs/`
- **Missing textures**: Run data generation
- **Network errors**: Check payload registration and handlers
- **Data not persisting**: Verify data component registration

## Contributing

See `CONTRIBUTING.md` for contribution guidelines.

## Resources

- [NeoForge Documentation](https://docs.neoforged.net/)
- [Minecraft Development Wiki](https://minecraft.wiki/w/Mods)
- [Project Documentation](./MOD_OVERVIEW.md)





