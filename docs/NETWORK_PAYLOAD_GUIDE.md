# Network Payload Guide

This guide explains how to create and use network payloads for client-server communication in the Spells 'n Squares mod.

## Overview

Network payloads are used to synchronize data and events between the server and client. The mod uses NeoForge's network system with payload batching for performance.

## Payload Architecture

```
Server → NetworkPayloadBatcher → Client
    ↓
Batching (default) or Immediate
    ↓
Payload Handler
```

## Creating a Payload

### Step 1: Define the Payload Record

Create a record implementing `CustomPacketPayload`:

```java
package at.koopro.spells_n_squares.features.example.network;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ExampleSyncPayload(Identifier itemId, int count) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ExampleSyncPayload> TYPE =
        new CustomPacketPayload.Type<>(ModIdentifierHelper.modId("example_sync"));
    
    private static final StreamCodec<ByteBuf, Identifier> IDENTIFIER_CODEC =
        ByteBufCodecs.STRING_UTF8.map(Identifier::parse, Identifier::toString);
    
    public static final StreamCodec<ByteBuf, ExampleSyncPayload> STREAM_CODEC = 
        StreamCodec.composite(
            IDENTIFIER_CODEC,
            ExampleSyncPayload::itemId,
            ByteBufCodecs.VAR_INT,
            ExampleSyncPayload::count,
            ExampleSyncPayload::new
        );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
```

### Step 2: Register the Payload Handler

In `ModNetwork.java`, register your payload:

```java
// Server -> Client
registerToClient(registrar, ExampleSyncPayload.TYPE, ExampleSyncPayload.STREAM_CODEC, payload -> {
    // Handle on client
    ExampleData.updateClientCache(payload.itemId(), payload.count());
});

// Client -> Server
registerToServer(registrar, ExampleRequestPayload.TYPE, ExampleRequestPayload.STREAM_CODEC, 
    (payload, serverPlayer) -> {
        // Handle on server
        ExampleData.processRequest(serverPlayer, payload.requestId());
    });
```

### Step 3: Send the Payload

```java
// On server, send to client
ExampleSyncPayload payload = new ExampleSyncPayload(itemId, count);
PlayerDataSyncUtils.syncToClient(serverPlayer, payload, "example data");

// For urgent payloads, send immediately
PlayerDataSyncUtils.syncToClientImmediate(serverPlayer, payload, "urgent data");
```

## Batching vs Immediate Sending

### When to Use Batching (Default)

Batching is the default and should be used for:
- **Non-critical updates**: Stat changes, inventory updates, currency changes
- **Frequent updates**: Data that changes often (e.g., cooldowns, health)
- **Bulk data**: Large payloads that benefit from combining
- **Performance**: When network overhead reduction is important

**Example:**
```java
// Currency update - can wait 1 tick
PlayerDataSyncUtils.syncToClient(player, currencyPayload, "currency");
```

### When to Use Immediate Sending

Use immediate sending for:
- **Critical gameplay**: Spell casting, damage dealt, combat events
- **User actions**: Button clicks, item interactions
- **Time-sensitive**: Data that affects immediate gameplay (e.g., spell cooldown during combat)
- **Feedback**: Actions that need instant visual/audio feedback

**Example:**
```java
// Spell cast result - needs instant feedback
PlayerDataSyncUtils.syncToClientImmediate(player, spellCastPayload, "spell cast");
```

## Payload Registration Patterns

### Pattern 1: Simple Client Handler (No Context Needed)

```java
registerToClient(registrar, MyPayload.TYPE, MyPayload.STREAM_CODEC, payload -> {
    // Handler logic - payload is available, no player context needed
    MyData.update(payload.data());
});
```

### Pattern 2: Client Handler with Player Context

```java
registerToClientWithContext(registrar, MyPayload.TYPE, MyPayload.STREAM_CODEC, 
    (payload, context) -> {
        if (context.player() != null) {
            // Handler logic with player context
            MyData.updateForPlayer(context.player().getUUID(), payload.data());
        }
    });
```

### Pattern 3: Server Handler

```java
registerToServer(registrar, MyPayload.TYPE, MyPayload.STREAM_CODEC, 
    (payload, serverPlayer) -> {
        // Handler logic on server
        MySystem.process(serverPlayer, payload.data());
    });
```

## Error Handling

All payload handlers are automatically wrapped with error handling in the registration helpers. However, you should still:

1. **Validate payload data** in handlers
2. **Check for null values** before use
3. **Handle edge cases** gracefully
4. **Log errors** for debugging

**Example:**
```java
registerToServer(registrar, MyPayload.TYPE, MyPayload.STREAM_CODEC, 
    (payload, serverPlayer) -> {
        // Validation
        if (payload.data() == null) {
            return; // Ignore invalid payload
        }
        
        // Process
        MySystem.process(serverPlayer, payload.data());
    });
```

## Common Codec Patterns

### Identifier Codec

```java
private static final StreamCodec<ByteBuf, Identifier> IDENTIFIER_CODEC =
    ByteBufCodecs.STRING_UTF8.map(Identifier::parse, Identifier::toString);
```

### Optional Codec

```java
private static final StreamCodec<ByteBuf, Optional<Identifier>> OPTIONAL_IDENTIFIER_CODEC =
    ByteBufCodecs.optional(IDENTIFIER_CODEC);
```

### List/Set Codec

```java
private static final StreamCodec<ByteBuf, List<Identifier>> IDENTIFIER_LIST_CODEC =
    IDENTIFIER_CODEC.apply(ByteBufCodecs.collection(ArrayList::new));

private static final StreamCodec<ByteBuf, Set<Identifier>> IDENTIFIER_SET_CODEC =
    IDENTIFIER_CODEC.apply(ByteBufCodecs.collection(HashSet::new));
```

### Map Codec

```java
private static final StreamCodec<ByteBuf, Map<Identifier, Integer>> MAP_CODEC =
    ByteBufCodecs.map(HashMap::new, IDENTIFIER_CODEC, ByteBufCodecs.VAR_INT);
```

### Composite Codec

```java
public static final StreamCodec<ByteBuf, MyPayload> STREAM_CODEC = StreamCodec.composite(
    IDENTIFIER_CODEC,
    MyPayload::itemId,
    ByteBufCodecs.VAR_INT,
    MyPayload::count,
    ByteBufCodecs.STRING_UTF8,
    MyPayload::name,
    MyPayload::new
);
```

## Best Practices

### 1. Payload Naming

- Use descriptive names: `SpellCooldownSyncPayload`, not `Payload1`
- Include direction: `SyncPayload` (server→client), `RequestPayload` (client→server)
- Be specific: `BestiarySyncPayload`, not `DataSyncPayload`

### 2. Payload Size

- Keep payloads small (< 1KB when possible)
- For large data, consider splitting into multiple payloads
- Use compression for very large payloads (if needed)

### 3. Payload Frequency

- Batch frequent updates (e.g., cooldowns)
- Send immediate updates for user actions
- Consider update rate limits for high-frequency data

### 4. Error Handling

- Always validate payload data
- Handle missing/null values gracefully
- Log errors for debugging
- Don't crash on invalid payloads

### 5. Client-Server Separation

- Never trust client data - validate on server
- Server is authoritative for game state
- Client can request, but server decides

## Examples

### Example 1: Simple Data Sync

```java
// Payload
public record CurrencySyncPayload(int galleons, int sickles, int knuts) 
    implements CustomPacketPayload { ... }

// Registration
registerToClient(registrar, CurrencySyncPayload.TYPE, CurrencySyncPayload.STREAM_CODEC, 
    payload -> {
        ClientCurrencyData.update(payload.galleons(), payload.sickles(), payload.knuts());
    });

// Usage
CurrencySyncPayload payload = new CurrencySyncPayload(galleons, sickles, knuts);
PlayerDataSyncUtils.syncToClient(player, payload, "currency");
```

### Example 2: Complex Data with Context

```java
// Payload with set
public record BestiarySyncPayload(Set<Identifier> discoveredCreatures) 
    implements CustomPacketPayload { ... }

// Registration with context
registerToClientWithContext(registrar, BestiarySyncPayload.TYPE, BestiarySyncPayload.STREAM_CODEC,
    (payload, context) -> {
        if (context.player() != null) {
            BestiaryData.updateClientCache(
                context.player().getUUID(), 
                new BestiaryComponent(payload.discoveredCreatures())
            );
        }
    });
```

### Example 3: Client Request to Server

```java
// Request payload
public record SpellCastRequestPayload(int slot) implements CustomPacketPayload { ... }

// Registration
registerToServer(registrar, SpellCastRequestPayload.TYPE, SpellCastRequestPayload.STREAM_CODEC,
    (payload, serverPlayer) -> {
        if (SpellManager.isValidSlot(payload.slot())) {
            SpellManager.castSpellInSlot(serverPlayer, payload.slot());
        }
    });
```

## Performance Considerations

### Batching Benefits

- Reduces packet overhead (fewer headers)
- Combines small payloads efficiently
- Throttles rapid updates automatically
- Better network utilization

### Immediate Sending Trade-offs

- Higher network overhead (more packets)
- No throttling (can spam network)
- Instant delivery (no 1-tick delay)
- Better for critical events

### Recommendations

- **Default to batching** for most cases
- **Use immediate** only when necessary
- **Monitor network usage** in development
- **Profile payload frequency** to optimize

## Troubleshooting

### Payload Not Received

- Check payload is registered in `ModNetwork.java`
- Verify payload type matches on both sides
- Check codec handles all data types correctly
- Ensure player is connected (not null)

### Payload Handler Errors

- Check error logs for specific exceptions
- Validate payload data before use
- Handle null values gracefully
- Test with various data scenarios

### Performance Issues

- Use batching for frequent updates
- Reduce payload size if possible
- Limit update frequency if needed
- Profile network usage

## Integration with SyncableDataComponent

For data systems implementing `SyncableDataComponent`:

```java
public class MyDataSyncAdapter implements SyncableDataComponent {
    @Override
    public CustomPacketPayload createSyncPayload(ServerPlayer serverPlayer) {
        MyDataComponent data = MyData.getMyData(serverPlayer);
        return new MyDataSyncPayload(data.toNetworkFormat());
    }
    
    @Override
    public void applySyncPayload(CustomPacketPayload payload, Player player) {
        if (payload instanceof MyDataSyncPayload myPayload) {
            MyData.updateClientCache(player.getUUID(), myPayload.getData());
        }
    }
}
```

This integrates with `SyncableDataRegistry` for automatic syncing on player join.

