# Coding Standards

This document defines the coding standards and best practices for the Spells N Squares mod.

## General Principles

1. **Simplicity**: Prefer simple solutions over complex ones
2. **Consistency**: Follow existing patterns in the codebase
3. **Maintainability**: Write code that's easy to understand and modify
4. **Performance**: Consider performance implications, especially for frequently called code
5. **Safety**: Always handle errors gracefully

## Code Organization

### File Structure

- Keep files under 200-300 lines
- Refactor when files exceed this limit
- Use feature-based organization
- Separate client/server code clearly

### Package Structure

```
at.koopro.spells_n_squares/
├── core/              # Core systems (shared)
├── features/          # Feature implementations
│   └── {feature}/
│       ├── client/    # Client-only code
│       ├── network/   # Network code
│       └── ...       # Feature-specific code
└── init/             # Initialization
```

## Naming Conventions

### Classes

- Use `PascalCase`
- Be descriptive: `WandAttunementHandler` not `Handler`
- Use nouns: `SpellManager` not `ManageSpells`

### Methods

- Use `camelCase`
- Use verbs: `getSpell()`, `registerFeature()`
- Be descriptive: `calculateParticleCount()` not `calc()`

### Variables

- Use `camelCase`
- Be descriptive: `playerData` not `pd`
- Use meaningful names: `spellCooldown` not `sc`

### Constants

- Use `UPPER_SNAKE_CASE`
- Group related constants in classes
- Document purpose and valid values

### Packages

- Use `lowercase`
- One word per segment when possible
- Reflect feature/module structure

## Code Style

### Indentation

- Use 4 spaces (not tabs)
- Consistent indentation throughout

### Braces

- Always use braces, even for single statements
- Opening brace on same line
- Closing brace on new line at same indentation

```java
if (condition) {
    doSomething();
}
```

### Line Length

- Keep lines under 120 characters
- Break long lines logically
- Align continuation lines appropriately

### Imports

- Use explicit imports (no wildcards)
- Organize imports: java, then net.minecraft, then mod packages
- Remove unused imports

## Error Handling

### Use SafeEventHandler

Always wrap event handlers:

```java
@SubscribeEvent
public static void onEvent(SomeEvent event) {
    SafeEventHandler.execute(() -> {
        // Handler logic
    }, "handling event", event.getEntity());
}
```

### Exception Handling

- Never silently catch and ignore exceptions
- Log errors with context
- Provide meaningful error messages
- Use appropriate exception types

### Null Checks

- Always check for null before use
- Use early returns for null checks
- Document when null is allowed

## Data Access

### Use DataComponentHelper

For player data operations:

```java
// Get
MyData data = DataComponentHelper.get(player, "key", MyData.CODEC, MyData::createDefault);

// Set
DataComponentHelper.set(player, "key", MyData.CODEC, data);

// Update
DataComponentHelper.update(player, "key", MyData.CODEC, MyData::createDefault,
    current -> current.withNewValue(value));
```

## Configuration

### Use Config Class

Access config through `Config` class methods (automatically cached):

```java
if (Config.isHighFxMode()) {
    // High FX enabled
}
```

## Logging

### Development Logging

Use `DevLogger` for development logging (respects config):

```java
DevLogger.logMethodEntry(this, "methodName", "param=" + param);
DevLogger.logStateChange(this, "setValue", "value=" + value);
DevLogger.logMethodExit(this, "methodName", result);
```

### Production Logging

Use SLF4J logger for production logging:

```java
private static final Logger LOGGER = LogUtils.getLogger();
LOGGER.info("Information message");
LOGGER.warn("Warning message");
LOGGER.error("Error message", exception);
```

## Documentation

### JavaDoc

- Document all public APIs
- Include parameter descriptions
- Include return value descriptions
- Include usage examples for complex methods
- Document exceptions thrown

### Code Comments

- Explain "why", not "what"
- Use comments for complex logic
- Keep comments up to date
- Remove commented-out code

## Performance

### Avoid Unnecessary Work

- Cache frequently accessed values
- Use lazy initialization when appropriate
- Avoid creating objects in hot paths
- Reuse objects when possible

### Particle Optimization

- Use `ParticlePool.queueParticle()` for batching
- Check distance before spawning particles
- Use `FXConfigHelper` for LOD calculations
- Respect per-chunk particle limits

### Network Optimization

- Batch network payloads when possible
- Use `NetworkPayloadBatcher` for non-urgent payloads
- Minimize payload size
- Avoid sending unnecessary updates

## Testing

### Test Coverage

- Write tests for core functionality
- Test edge cases
- Test error conditions
- Aim for good coverage of critical paths

### Test Organization

- Mirror source structure in test directory
- Use descriptive test names
- Group related tests
- Use `@BeforeEach` for setup

## Best Practices

### Avoid Code Duplication

- Extract common patterns into utilities
- Use base classes for shared functionality
- Create helper methods for repeated logic

### Keep It Simple

- Prefer simple solutions
- Avoid over-engineering
- Don't optimize prematurely
- Make it work, then make it better

### Follow Existing Patterns

- Look for similar code in the codebase
- Follow established patterns
- Maintain consistency
- Ask if unsure

## Utility Classes

### CollectionFactory

Use `CollectionFactory` for creating collections instead of `new ArrayList<>()`, `new HashMap<>()`, etc.:

```java
// Instead of: List<String> list = new ArrayList<>();
List<String> list = CollectionFactory.createList();

// With capacity: List<String> list = new ArrayList<>(10);
List<String> list = CollectionFactory.createList(10);

// Pre-populated: List<String> list = CollectionFactory.createListOf("a", "b", "c");
List<String> list = CollectionFactory.createListOf("a", "b", "c");

// Maps and Sets
Map<String, Integer> map = CollectionFactory.createMap();
Set<String> set = CollectionFactory.createSet();
```

### ValidationUtils

Use `ValidationUtils` for common validation patterns:

```java
// Require non-null
MyObject obj = ValidationUtils.requireNonNull(value, "Value cannot be null");

// Require server-side
Level level = ValidationUtils.requireServerSide(level);

// Require valid entity
Entity entity = ValidationUtils.requireValidEntity(entity);

// Boolean checks (non-throwing)
if (ValidationUtils.isServerSide(level)) {
    // Server-side logic
}
```

### NBTUtils

Use `NBTUtils` for common NBT operations:

```java
// Write BlockPos
NBTUtils.writeBlockPos(tag, "position", pos);

// Read BlockPos
BlockPos pos = NBTUtils.readBlockPos(tag, "position");

// Write UUID
NBTUtils.writeUUID(tag, "owner", ownerUUID);

// Merge tags
NBTUtils.mergeTags(targetTag, sourceTag);

// Copy tag
CompoundTag copy = NBTUtils.copyTag(originalTag);
```

### NetworkPayloadHelper

Use `NetworkPayloadHelper` for creating network payload codecs:

```java
public record MyPayload(Identifier id, int count) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MyPayload> TYPE =
        NetworkPayloadHelper.createType("my_payload");
    
    public static final StreamCodec<ByteBuf, MyPayload> STREAM_CODEC =
        NetworkPayloadHelper.composite(
            NetworkPayloadHelper.IDENTIFIER_CODEC, MyPayload::id,
            ByteBufCodecs.VAR_INT, MyPayload::count,
            MyPayload::new
        );
}
```

### ComponentInitializer

Use `ComponentInitializer` to reduce boilerplate when setting up components:

```java
// In constructor
ComponentInitializer.setupModEntityComponent(
    modEntityComponent,
    this::defineCustomSynchedData,
    this::saveCustomData,
    this::loadCustomData,
    this::onHurt
);
```

## Common Patterns

### Registry Pattern

```java
public final class MyRegistry {
    public static final DeferredRegister<Item> ITEMS = 
        DeferredRegister.create(Registries.ITEM, SpellsNSquares.MODID);
    
    public static final DeferredHolder<Item, Item> MY_ITEM = 
        ITEMS.register("my_item", () -> new MyItem(...));
    
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
```

### Feature Pattern

```java
public final class MyFeature implements IFeature {
    @Override
    public void initialize(IEventBus modEventBus, ModContainer modContainer) {
        // Initialize feature
    }
    
    @Override
    public void registerRegistries(IEventBus modEventBus) {
        // Register registries
    }
    
    @Override
    public String getFeatureName() {
        return "MyFeature";
    }
}
```

## Tools

### Linting

Run linter to check code style:

```bash
./gradlew check
```

### Formatting

Use IDE auto-format with project settings.

### Static Analysis

Use IDE inspections to catch potential issues.




