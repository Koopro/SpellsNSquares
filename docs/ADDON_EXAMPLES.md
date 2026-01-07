# Addon Development Examples

This document provides comprehensive examples for common addon development patterns.

## Table of Contents

1. [Basic Addon Setup](#basic-addon-setup)
2. [Registering Spells](#registering-spells)
3. [Registering Items](#registering-items)
4. [Registering Entities](#registering-entities)
5. [Event Handling](#event-handling)
6. [Network Communication](#network-communication)
7. [Custom Data Components](#custom-data-components)

## Basic Addon Setup

### Minimal Addon

```java
package com.example.myaddon;

import at.koopro.spells_n_squares.core.api.addon.AddonMod;
import at.koopro.spells_n_squares.core.api.addon.IAddon;
import at.koopro.spells_n_squares.core.api.addon.AddonContext;

@AddonMod(
    modId = "my_addon",
    name = "My Addon",
    version = "1.0.0",
    minApiVersion = "1.0.0"
)
public class MyAddon implements IAddon {
    @Override
    public void initialize(AddonContext context) {
        // Register your content here
        context.getSpellRegistryHelper().registerSpell(new MySpell());
    }
    
    @Override
    public void registerRegistries(net.neoforged.bus.api.IEventBus modEventBus) {
        // Register custom registries if needed
    }
    
    @Override
    public void clientInit(AddonContext context) {
        // Client-side initialization
    }
}
```

### Service Loader Registration

Create `META-INF/services/at.koopro.spells_n_squares.core.api.addon.IAddon`:

```
com.example.myaddon.MyAddon
```

## Registering Spells

### Simple Spell

```java
public class FireballSpell implements Spell {
    @Override
    public Identifier getId() {
        return Identifier.fromNamespaceAndPath("my_addon", "fireball");
    }
    
    @Override
    public String getName() {
        return "Fireball";
    }
    
    @Override
    public String getDescription() {
        return "Launches a fireball";
    }
    
    @Override
    public int getCooldown() {
        return 60; // 3 seconds at 20 TPS
    }
    
    @Override
    public boolean cast(Player player, Level level) {
        if (level.isClientSide()) {
            return false;
        }
        
        // Cast logic
        Vec3 lookVec = player.getLookAngle();
        Vec3 spawnPos = player.getEyePosition().add(lookVec.scale(1.5));
        
        // Spawn fireball entity
        // ... implementation
        
        return true;
    }
    
    @Override
    public float getVisualEffectIntensity() {
        return 0.8f;
    }
}
```

### Hold-to-Cast Spell

```java
public class ChargeSpell implements Spell {
    // ... standard methods ...
    
    @Override
    public boolean isHoldToCast() {
        return true;
    }
    
    @Override
    public void onHoldTick(Player player, Level level, int ticksHeld) {
        // Called every tick while holding
        if (ticksHeld % 5 == 0) {
            // Spawn charge particles every 5 ticks
            spawnChargeParticles(player, level);
        }
    }
    
    @Override
    public void onHoldRelease(Player player, Level level, int ticksHeld) {
        // Called when player releases
        float power = Math.min(1.0f, ticksHeld / 60.0f); // Max power at 3 seconds
        releaseChargedSpell(player, level, power);
    }
}
```

## Registering Items

### Custom Item

```java
public class MagicWandItem extends Item {
    public MagicWandItem() {
        super(new Item.Properties()
            .stacksTo(1)
            .durability(100));
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (!level.isClientSide()) {
            // Use logic
        }
        
        return InteractionResultHolder.success(stack);
    }
}
```

### Registering the Item

```java
@Override
public void initialize(AddonContext context) {
    context.getItemRegistryHelper().registerItem("magic_wand", 
        () -> new MagicWandItem());
}
```

## Registering Entities

### Custom Entity

```java
public class MagicCreatureEntity extends PathfinderMob {
    public MagicCreatureEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PanicGoal(this, 1.25));
        // ... more goals
    }
}
```

### Registering the Entity

```java
@Override
public void initialize(AddonContext context) {
    context.getEntityRegistryHelper().registerEntity("magic_creature",
        EntityType.Builder.of(
            MagicCreatureEntity::new,
            MobCategory.CREATURE
        ).sized(0.6f, 1.8f)
    );
}
```

## Event Handling

### Subscribing to Events

```java
@Override
public void initialize(AddonContext context) {
    // Get the addon event bus
    AddonEventBus eventBus = context.getEventBus();
    
    // Subscribe to spell cast events
    eventBus.subscribe(SpellCastEvent.class, event -> {
        if (event.getSpell().getId().getNamespace().equals("my_addon")) {
            // Handle spell cast from this addon
            onMySpellCast(event);
        }
    });
    
    // Subscribe to spell slot changes
    eventBus.subscribe(SpellSlotChangeEvent.class, event -> {
        // Handle slot change
    });
}
```

### Cancelling Events

```java
eventBus.subscribe(SpellCastEvent.class, event -> {
    if (shouldCancel(event)) {
        event.setCanceled(true);
    }
});
```

## Network Communication

### Creating a Payload

```java
public record MyPayload(String data) implements CustomPacketPayload {
    public static final Type<MyPayload> TYPE = new Type<>(
        Identifier.fromNamespaceAndPath("my_addon", "my_payload")
    );
    
    public static final StreamCodec<ByteBuf, MyPayload> STREAM_CODEC = 
        StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            MyPayload::data,
            MyPayload::new
        );
    
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
```

### Registering Network Payloads

```java
@Override
public void initialize(AddonContext context) {
    AddonNetworkRegistry networkRegistry = context.getNetworkRegistryHelper();
    
    // Client to server
    networkRegistry.registerToServer(
        MyPayload.TYPE,
        MyPayload.STREAM_CODEC,
        (payload, serverPlayer) -> {
            // Handle on server
        }
    );
    
    // Server to client
    networkRegistry.registerToClient(
        MyPayload.TYPE,
        MyPayload.STREAM_CODEC,
        payload -> {
            // Handle on client
        }
    );
}
```

## Custom Data Components

### Creating a Data Component

```java
public record MyPlayerData(int value, String name) {
    public static final Codec<MyPlayerData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.INT.fieldOf("value").forGetter(MyPlayerData::value),
            Codec.STRING.fieldOf("name").forGetter(MyPlayerData::name)
        ).apply(instance, MyPlayerData::new)
    );
    
    public static MyPlayerData createDefault() {
        return new MyPlayerData(0, "");
    }
}
```

### Using Data Components

```java
// In your addon initialization or systems
public static MyPlayerData getMyData(Player player) {
    return DataComponentHelper.get(
        player,
        "my_addon:my_data",
        MyPlayerData.CODEC,
        MyPlayerData::createDefault
    );
}

public static void setMyData(Player player, MyPlayerData data) {
    DataComponentHelper.set(
        player,
        "my_addon:my_data",
        MyPlayerData.CODEC,
        data
    );
}
```

## Complete Example: Custom Spell System

```java
@AddonMod(
    modId = "custom_spells",
    name = "Custom Spells Addon",
    version = "1.0.0"
)
public class CustomSpellsAddon implements IAddon {
    @Override
    public void initialize(AddonContext context) {
        // Register spells
        AddonSpellRegistry spellRegistry = context.getSpellRegistryHelper();
        spellRegistry.registerSpell(new LightningBoltSpell());
        spellRegistry.registerSpell(new HealSpell());
        
        // Subscribe to events
        context.getEventBus().subscribe(SpellCastEvent.class, this::onSpellCast);
    }
    
    private void onSpellCast(SpellCastEvent event) {
        // Custom logic when any spell is cast
        if (event.getSpell().getId().getNamespace().equals("custom_spells")) {
            // Handle custom spell cast
        }
    }
    
    @Override
    public void registerRegistries(IEventBus modEventBus) {
        // No custom registries needed
    }
    
    @Override
    public void clientInit(AddonContext context) {
        // Client-side setup if needed
    }
}
```

## Best Practices

1. **Namespace Consistency**: Always use your addon's modId as the namespace
2. **Error Handling**: Use SafeEventHandler for event handlers
3. **Data Validation**: Validate data before using it
4. **Performance**: Cache frequently accessed values
5. **Documentation**: Document your addon's API

## Troubleshooting

### Addon Not Loading

- Check ServiceLoader file exists and has correct path
- Verify @AddonMod annotation is present
- Check mod dependencies are met
- Review logs for errors

### Spells Not Appearing

- Verify spell is registered in initialize()
- Check spell icon texture exists
- Ensure spell ID uses correct namespace
- Check for registration errors in logs

### Network Issues

- Verify payload registration
- Check payload codec is correct
- Ensure handlers check client/server side
- Review network logs





