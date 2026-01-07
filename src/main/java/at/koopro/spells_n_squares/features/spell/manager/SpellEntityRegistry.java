package at.koopro.spells_n_squares.features.spell.manager;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.util.registry.ModIdentifierHelper;
import at.koopro.spells_n_squares.features.spell.entity.DummyPlayerEntity;
import at.koopro.spells_n_squares.features.spell.entity.DummyPlayerModelType;
import at.koopro.spells_n_squares.features.spell.entity.LightOrbEntity;
import at.koopro.spells_n_squares.features.spell.entity.LightningBeamEntity;
import at.koopro.spells_n_squares.features.spell.entity.ShieldOrbEntity;
import at.koopro.spells_n_squares.features.spell.item.DummyPlayerSpawnEggItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for spell feature entities and related items.
 */
public class SpellEntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, SpellsNSquares.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpellsNSquares.MODID);
    
    public static final DeferredHolder<EntityType<?>, EntityType<ShieldOrbEntity>> SHIELD_ORB = ENTITIES.register(
        "shield_orb",
        () -> {
            ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, 
                ModIdentifierHelper.modId("shield_orb"));
            return EntityType.Builder.<ShieldOrbEntity>of(ShieldOrbEntity::new, MobCategory.MISC)
                .sized(3.6f, 3.6f) // Shield radius
                .clientTrackingRange(64)
                .updateInterval(1)
                .build(key);
        }
    );

    public static final DeferredHolder<EntityType<?>, EntityType<LightOrbEntity>> LIGHT_ORB = ENTITIES.register(
        "light_orb",
        () -> {
            ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE,
                ModIdentifierHelper.modId("light_orb"));
            return EntityType.Builder.<LightOrbEntity>of(LightOrbEntity::new, MobCategory.MISC)
                .sized(0.4f, 0.4f)
                .clientTrackingRange(64)
                .updateInterval(1)
                .build(key);
        }
    );

    public static final DeferredHolder<EntityType<?>, EntityType<LightningBeamEntity>> LIGHTNING_BEAM = ENTITIES.register(
        "lightning_beam",
        () -> {
            ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE,
                ModIdentifierHelper.modId("lightning_beam"));
            return EntityType.Builder.<LightningBeamEntity>of(LightningBeamEntity::new, MobCategory.MISC)
                .sized(0.1f, 0.1f)
                .clientTrackingRange(64)
                .updateInterval(1)
                .build(key);
        }
    );

    public static final DeferredHolder<EntityType<?>, EntityType<DummyPlayerEntity>> DUMMY_PLAYER = ENTITIES.register(
        "dummy_player",
        () -> {
            ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE,
                ModIdentifierHelper.modId("dummy_player"));
            return EntityType.Builder.<DummyPlayerEntity>of(DummyPlayerEntity::new, MobCategory.MISC)
                .sized(0.6f, 1.8f)
                .clientTrackingRange(64)
                .updateInterval(1)
                .build(key);
        }
    );
    
    // Spawn egg items
    public static final DeferredItem<DummyPlayerSpawnEggItem> DUMMY_PLAYER_SPAWN_EGG_STEVE = ITEMS.register(
        "dummy_player_spawn_egg_steve",
        id -> new DummyPlayerSpawnEggItem(id, DummyPlayerModelType.STEVE)
    );
    
    public static final DeferredItem<DummyPlayerSpawnEggItem> DUMMY_PLAYER_SPAWN_EGG_ALEX = ITEMS.register(
        "dummy_player_spawn_egg_alex",
        id -> new DummyPlayerSpawnEggItem(id, DummyPlayerModelType.ALEX)
    );
    
    public static void register(IEventBus modEventBus) {
        ENTITIES.register(modEventBus);
        ITEMS.register(modEventBus);
    }
}













