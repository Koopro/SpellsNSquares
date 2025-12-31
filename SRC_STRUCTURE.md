## Source Directory Structure

```
├── datagen/
│   └── java/
│       └── at/
│           └── koopro/
│               └── spells_n_squares/
│                   └── datagen/                    // Data generation providers for blocks, items, recipes, etc.
│                       ├── GeckoLibItemModelProvider.java
│                       ├── ModBiomeDataProvider.java
│                       ├── ModBiomeModifierProvider.java
│                       ├── ModBiomeProvider.java
│                       ├── ModBlockLootProvider.java
│                       ├── ModBlockModelProvider.java
│                       ├── ModBlockTagsProvider.java
│                       ├── ModDataGenerators.java
│                       ├── ModItemModelProvider.java
│                       ├── ModRecipeProvider.java
│                       └── TreeBlockModelProvider.java
│
├── generated/                                       // Auto-generated JSON files
│   └── [40 JSON files]
│
└── main/
    ├── java/
    │   └── at/
    │       └── koopro/
    │           └── spells_n_squares/
    │               ├── core/                        // Core framework and infrastructure
    │               │   ├── api/                     // Public API interfaces and wrappers
    │               │   │   ├── addon/              // Addon system interfaces and events
    │               │   │   │   ├── AddonContext.java
    │               │   │   │   ├── AddonMetadata.java
    │               │   │   │   ├── AddonMod.java
    │               │   │   │   ├── dependency/     // Dependency checking system
    │               │   │   │   ├── events/         // Addon event bus and events
    │               │   │   │   ├── IAddon.java
    │               │   │   │   └── registration/   // Registration interfaces for addons
    │               │   │   ├── client/             // Client-side API markers
    │               │   │   ├── IFeature.java
    │               │   │   ├── INetworkPayload.java
    │               │   │   ├── IPlayerClassManager.java
    │               │   │   ├── ISpellManager.java
    │               │   │   ├── ISpellRegistry.java
    │               │   │   ├── ModContext.java
    │               │   │   ├── PlayerClassManagerWrapper.java
    │               │   │   ├── server/             // Server-side API markers
    │               │   │   ├── SpellFeature.java
    │               │   │   ├── SpellManagerWrapper.java
    │               │   │   └── SpellRegistryWrapper.java
    │               │   ├── client/                 // Client rendering utilities
    │               │   │   ├── KeyStateTracker.java
    │               │   │   ├── RendererConstants.java
    │               │   │   └── RendererUtils.java
    │               │   ├── component/             // Component system
    │               │   ├── config/                 // Configuration management
    │               │   │   └── Config.java
    │               │   ├── fx/                     // Visual effects and particle systems
    │               │   │   ├── FXConfigHelper.java
    │               │   │   ├── ParticleEffectRegistry.java
    │               │   │   └── ParticlePool.java
    │               │   ├── network/                // Network communication
    │               │   │   ├── FXTestPayload.java
    │               │   │   └── ModNetwork.java
    │               │   ├── registry/               // Game object registries
    │               │   │   ├── addon/              // Addon-specific registries
    │               │   │   ├── AddonRegistry.java
    │               │   │   ├── CreatureRegistry.java
    │               │   │   ├── DataComponentRegistry.java
    │               │   │   ├── EnchantmentRegistry.java
    │               │   │   ├── FeatureRegistry.java
    │               │   │   ├── ModArmorMaterials.java
    │               │   │   ├── ModBiomes.java
    │               │   │   ├── ModBlocks.java
    │               │   │   ├── ModCreativeTabs.java
    │               │   │   ├── ModDataComponents.java
    │               │   │   ├── ModEntities.java
    │               │   │   ├── ModItems.java
    │               │   │   ├── ModMenus.java
    │               │   │   ├── ModSounds.java
    │               │   │   ├── ModTags.java
    │               │   │   ├── ModTreeBlocks.java
    │               │   │   ├── ParticleEffectRegistry.java
    │               │   │   ├── PlayerDataManager.java
    │               │   │   ├── PlayerDataManagerAdapters.java
    │               │   │   ├── PlayerDataManagerRegistry.java
    │               │   │   └── SpellRegistry.java
    │               │   └── util/                   // Utility classes
    │               │       ├── EventUtils.java
    │               │       ├── LightBlockManager.java
    │               │       ├── LightConstants.java
    │               │       ├── ModIdentifierHelper.java
    │               │       ├── PlayerItemUtils.java
    │               │       ├── PlayerValidationUtils.java
    │               │       └── TranslationUtils.java
    │               │
    │               ├── features/                   // Feature implementations
    │               │   ├── artifacts/              // Magical artifacts (Elder Wand, Pensieve, etc.)
    │               │   │   ├── client/            // Client-side artifact screens
    │               │   │   └── [31 artifact item classes]
    │               │   ├── automation/             // Automated farming and crafting systems
    │               │   │   ├── block/              // Automation blocks
    │               │   │   ├── AutoHarvestTool.java
    │               │   │   └── EnchantedWorkbenchItem.java
    │               │   ├── building/               // Building and warding systems
    │               │   │   ├── block/              // Interactive building blocks
    │               │   │   ├── WardHandler.java
    │               │   │   ├── WardSystem.java
    │               │   │   └── WizardTowerItem.java
    │               │   ├── cloak/                  // Invisibility cloak mechanics
    │               │   │   ├── CloakChargeData.java
    │               │   │   ├── CloakChargeHelper.java
    │               │   │   ├── CloakShimmerHandler.java
    │               │   │   ├── DeathlyHallowCloakItem.java
    │               │   │   ├── DemiguiseCloakItem.java
    │               │   │   ├── InvisibilityCloakHandler.java
    │               │   │   └── RevealerDustItem.java
    │               │   ├── combat/                 // Combat and dueling systems
    │               │   │   ├── block/              // Duel arena blocks
    │               │   │   ├── CombatStatsData.java
    │               │   │   ├── DuelingClubManager.java
    │               │   │   ├── DuelingSystem.java
    │               │   │   ├── ShieldSystem.java
    │               │   │   ├── SpellComboSystem.java
    │               │   │   └── SpellResistanceSystem.java
    │               │   ├── communication/          // Messaging and communication systems
    │               │   │   ├── block/              // Notice board blocks
    │               │   │   ├── MirrorData.java
    │               │   │   ├── OwlEntity.java
    │               │   │   ├── PatronusMessageHandler.java
    │               │   │   ├── PatronusMessagingSystem.java
    │               │   │   └── TwoWayMirrorItem.java
    │               │   ├── contracts/              // Contract and agreement systems
    │               │   │   └── [5 contract-related classes]
    │               │   ├── convenience/            // Quality-of-life convenience features
    │               │   │   └── [6 convenience item classes]
    │               │   ├── creatures/              // Magical creature entities and systems
    │               │   │   ├── aquatic/           // Water-dwelling creatures
    │               │   │   ├── base/              // Base creature classes
    │               │   │   ├── client/            // Creature renderers
    │               │   │   ├── companion/         // Companion creatures
    │               │   │   ├── hostile/           // Hostile creatures
    │               │   │   ├── mount/             // Mountable creatures
    │               │   │   ├── neutral/           // Neutral creatures
    │               │   │   ├── special/          // Special creature types
    │               │   │   ├── spiritual/         // Spiritual entities
    │               │   │   ├── util/              // Creature utilities
    │               │   │   ├── CreatureData.java
    │               │   │   ├── CreatureTamingHandler.java
    │               │   │   ├── CreatureType.java
    │               │   │   ├── FamiliarSystem.java
    │               │   │   └── ModCreatures.java
    │               │   ├── economy/                // Currency and trading systems
    │               │   │   ├── block/             // Trading blocks (vaults, shops)
    │               │   │   ├── CurrencyData.java
    │               │   │   ├── CurrencyItem.java
    │               │   │   ├── CurrencySystem.java
    │               │   │   ├── GringottsSystem.java
    │               │   │   └── TradingSystem.java
    │               │   ├── education/              // Educational and learning systems
    │               │   │   └── [19 education-related classes]
    │               │   ├── enchantments/           // Custom enchantment implementations
    │               │   │   └── [4 enchantment classes]
    │               │   ├── environment/           // Environmental and world interaction features
    │               │   │   └── [25 environment-related classes]
    │               │   ├── flashlight/            // Lumos spell and lighting systems
    │               │   │   └── [5 flashlight-related classes]
    │               │   ├── fx/                     // Visual effects and animations
    │               │   │   └── [7 FX-related classes]
    │               │   ├── gear/                   // Equipment and gear items
    │               │   │   └── [4 gear item classes]
    │               │   ├── ghosts/                 // Ghost entity and interaction systems
    │               │   │   └── [4 ghost-related classes]
    │               │   ├── magic/                  // Core magic systems (Animagus, Patronus)
    │               │   │   ├── AnimagusSystem.java
    │               │   │   └── PatronusSystem.java
    │               │   ├── mail/                   // Mail and delivery systems
    │               │   │   └── [7 mail-related classes]
    │               │   ├── misc/                   // Miscellaneous utility features
    │               │   │   └── [16 miscellaneous classes]
    │               │   ├── navigation/             // Navigation and mapping items
    │               │   │   ├── LocationCompassItem.java
    │               │   │   ├── MagicalJournalItem.java
    │               │   │   └── MagicalMapItem.java
    │               │   ├── npcs/                   // NPC system
    │               │   │   └── NPCSystem.java
    │               │   ├── organizations/          // Organization and faction systems
    │               │   │   └── [4 organization-related classes]
    │               │   ├── playerclass/            // Player class and house system
    │               │   │   ├── client/            // Client-side class UI
    │               │   │   ├── network/           // Class synchronization network packets
    │               │   │   ├── PlayerClass.java
    │               │   │   ├── PlayerClassData.java
    │               │   │   └── PlayerClassManager.java
    │               │   ├── portraits/              // Portrait and painting systems
    │               │   │   └── [4 portrait-related classes]
    │               │   ├── potions/                // Potion brewing and ingredient systems
    │               │   │   ├── PotionBrewingHandler.java
    │               │   │   ├── PotionBrewingManager.java
    │               │   │   ├── PotionData.java
    │               │   │   ├── PotionIngredientItem.java
    │               │   │   ├── PotionItem.java
    │               │   │   ├── PotionRecipe.java
    │               │   │   └── [43 potion and ingredient item classes]
    │               │   ├── quidditch/              // Quidditch game mechanics
    │               │   │   └── [4 quidditch-related classes]
    │               │   ├── robes/                 // Robe items and customization
    │               │   │   └── [3 robe-related classes]
    │               │   ├── social/                 // Social systems (friendship, reputation)
    │               │   │   ├── FriendshipItem.java
    │               │   │   ├── FriendshipSystem.java
    │               │   │   ├── ReputationSystem.java
    │               │   │   └── SocialData.java
    │               │   ├── spell/                  // Spell casting and management systems
    │               │   │   ├── client/            // Client-side spell rendering
    │               │   │   ├── entity/            // Spell effect entities
    │               │   │   ├── network/           // Spell network synchronization
    │               │   │   ├── EntityDetectionSpell.java
    │               │   │   ├── EvanescoSpell.java
    │               │   │   ├── ExtensionCharmSpell.java
    │               │   │   ├── FireballSpell.java
    │               │   │   ├── HealSpell.java
    │               │   │   ├── ImmobulusSpell.java
    │               │   │   ├── ItemRecallSpell.java
    │               │   │   ├── LanglockSpell.java
    │               │   │   ├── LevicorpusSpell.java
    │               │   │   ├── LiberacorpusSpell.java
    │               │   │   ├── LightningSpell.java
    │               │   │   ├── LocomotorSpell.java
    │               │   │   ├── LumosManager.java
    │               │   │   ├── LumosSpell.java
    │               │   │   ├── ModSpells.java
    │               │   │   ├── MuffliatoSpell.java
    │               │   │   ├── OrchideousSpell.java
    │               │   │   ├── PericulumSpell.java
    │               │   │   ├── PortableCraftingSpell.java
    │               │   │   ├── ProtegoSpell.java
    │               │   │   ├── RiddikulusSpell.java
    │               │   │   ├── SilencioSpell.java
    │               │   │   ├── SonorusSpell.java
    │               │   │   ├── Spell.java
    │               │   │   ├── SpellManager.java
    │               │   │   ├── TarantallegraSpell.java
    │               │   │   └── TeleportSpell.java
    │               │   ├── storage/                // Storage and inventory systems
    │               │   │   ├── block/              // Storage blocks (chests, trunks)
    │               │   │   ├── client/             // Storage UI screens
    │               │   │   ├── BagInventoryData.java
    │               │   │   ├── BagMenu.java
    │               │   │   ├── BagMenuProvider.java
    │               │   │   ├── EnchantedBagItem.java
    │               │   │   ├── PocketDimensionData.java
    │               │   │   ├── PocketDimensionItem.java
    │               │   │   └── TrunkInventoryData.java
    │               │   ├── transportation/         // Transportation and teleportation systems
    │               │   │   └── [8 transportation-related classes]
    │               │   ├── wand/                   // Wand items and mechanics
    │               │   │   ├── client/            // Wand rendering and models
    │               │   │   ├── WandAffinity.java
    │               │   │   ├── WandAffinityManager.java
    │               │   │   ├── WandAttunementHandler.java
    │               │   │   ├── WandCore.java
    │               │   │   ├── WandData.java
    │               │   │   ├── WandDataHelper.java
    │               │   │   ├── WandGlowHandler.java
    │               │   │   ├── WandGlowTickHandler.java
    │               │   │   ├── WandItem.java
    │               │   │   ├── WandVisualEffects.java
    │               │   │   └── WandWood.java
    │               │   └── worldgen/               // World generation configuration
    │               │       └── CreatureSpawnConfig.java
    │               │
    │               ├── init/                       // Initialization classes
    │               │   ├── client/                // Client initialization
    │               │   │   └── ModKeybinds.java
    │               │   ├── ClientInitialization.java
    │               │   ├── ModInitialization.java
    │               │   └── ServerEventHandler.java
    │               │
    │               └── SpellsNSquares.java        // Main mod class
    │
    ├── resources/                                  // Game resources
    │   ├── assets/                                // Client-side assets
    │   │   └── spells_n_squares/                 // Mod asset namespace
    │   │       └── [282 files: textures, models, sounds, etc.]
    │   ├── data/                                  // Data-driven content
    │   │   └── spells_n_squares/
    │   │       └── tags/
    │   │           └── item/
    │   │               └── wands.json
    │   └── spells_n_squares.mixins.json           // Mixin configuration
    │
    └── templates/                                 // Template files
            └── META-INF/
            └── neoforge.mods.toml
```

## Core API Conventions (summary)

- **Public API location**: Modder-facing APIs live under `core/api` (and `core/api/addon` for addons), with helper registries for addons under `core/registry/addon`. Internal wiring and implementation details stay in `core/registry` and `features/*`.
- **Nullability & collections**: Methods that may not find a value document this explicitly (for example `ISpellRegistry.get` may return `null` when a spell is missing). Collections returned from registries are unmodifiable views where appropriate (see `SpellRegistry.getAll` and `FeatureRegistry.getFeatures`).
- **Error handling**: Programmer or configuration errors (such as duplicate registrations, invalid namespaces, or unsatisfied dependencies) use `IllegalArgumentException` or `IllegalStateException` and are logged with addon/feature identifiers (see `SpellRegistry.register` and `AddonRegistry`).
- **Ordering guarantees**: `FeatureRegistry` and `AddonRegistry` intentionally use `LinkedHashSet` / `LinkedHashMap` so that initialization follows registration order. Code depending on ordering should register in the correct sequence instead of assuming sorted order.
- **Logging**: Core lifecycle and addon operations log via SLF4J (`LogUtils.getLogger()`), and log messages are expected to include the addon ID or feature name to aid debugging.








