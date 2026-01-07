# Error Handling Guide

This guide describes the standardized error handling patterns used across the Spells 'n Squares mod.

## Overview

All event handlers should use the `SafeEventHandler` utility class to ensure consistent error handling and logging. This prevents unhandled exceptions from crashing the game and provides useful debugging information.

## Usage Patterns

### Basic Event Handler

```java
@SubscribeEvent
public static void onPlayerTick(PlayerTickEvent.Post event) {
    Player player = event.getEntity();
    if (player == null) {
        return;
    }
    
    SafeEventHandler.execute(() -> {
        // Your handler logic here
        doSomething(player);
    }, "ticking player", player);
}
```

### With Additional Context

```java
@SubscribeEvent
public static void onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
    SafeEventHandler.execute(() -> {
        // Handler logic
    }, "handling block interaction", event.getEntity(), 
       "block at " + event.getPos());
}
```

### With Position Context

```java
SafeEventHandler.execute(() -> {
    // Handler logic
}, "processing block", event.getPos().getX(), 
   event.getPos().getY(), event.getPos().getZ());
```

### Simple Actions (No Player Context)

```java
SafeEventHandler.execute(() -> {
    // Handler logic
}, "initializing system");
```

## Error Message Format

All error messages follow this consistent format:
```
Error [action] [context]: [error message]
```

Examples:
- `Error handling player login (context: player Steve): NullPointerException`
- `Error ticking spells (context: player Alice): IndexOutOfBoundsException`
- `Error processing block (context: position (100.0, 64.0, 200.0)): IllegalArgumentException`

## Best Practices

1. **Always use SafeEventHandler** for event handlers that could throw exceptions
2. **Provide meaningful context** - include player names, positions, or other relevant information
3. **Keep action names concise** - use verb phrases like "handling X", "processing Y", "ticking Z"
4. **Don't catch and swallow silently** - SafeEventHandler logs all errors appropriately
5. **Use immediate execution** for critical operations that must not fail silently

## When NOT to Use SafeEventHandler

- **Network payload handlers** - These should handle errors differently (network-specific error handling)
- **Critical initialization** - If initialization fails, you may want the game to crash rather than continue in a broken state
- **Validation logic** - Validation errors should be handled explicitly, not caught generically

## Migration

When updating existing event handlers:

1. Replace try-catch blocks with `SafeEventHandler.execute()`
2. Extract meaningful context (player, position, etc.)
3. Use descriptive action names
4. Remove manual LOGGER.error calls (SafeEventHandler handles logging)

Example migration:

**Before:**
```java
@SubscribeEvent
public static void onPlayerTick(PlayerTickEvent.Post event) {
    try {
        doSomething(event.getEntity());
    } catch (Exception e) {
        LOGGER.error("Error: {}", e.getMessage(), e);
    }
}
```

**After:**
```java
@SubscribeEvent
public static void onPlayerTick(PlayerTickEvent.Post event) {
    SafeEventHandler.execute(() -> {
        doSomething(event.getEntity());
    }, "ticking player", event.getEntity());
}
```









