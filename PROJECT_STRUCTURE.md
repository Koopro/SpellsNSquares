# Project Structure: src/main Directory

## Java Source (`src/main/java`)

```
src/main/java/
└── at/
    └── koopro/
        └── spells_n_squares/
            ├── block/                          // Block implementations organized by feature category
            │   ├── automation/                 // Automation blocks (furnaces, cauldrons)
            │   ├── building/                   // Building blocks (magical lights)
            │   ├── combat/                     // Combat-related blocks (duel arenas)
            │   ├── communication/              // Communication blocks (notice boards)
            │   ├── economy/                    // Economy blocks (shops, trading posts, vaults)
            │   ├── education/                  // Education blocks (house points hourglass)
            │   ├── enchantments/               // Enchantment-related blocks
            │   ├── mail/                       // Mail system blocks (mailboxes)
            │   ├── plants/                     // Magical plant blocks
            │   ├── portraits/                  // Portrait blocks
            │   ├── quidditch/                  // Quidditch-related blocks
            │   ├── resource/                   // Resource generation blocks
            │   ├── storage/                    // Storage blocks (chests, trunks)
            │   └── tree/                       // Tree block variants (logs, planks, etc.)
            │
            ├── core/                           // Core mod infrastructure and systems
            │   ├── api/                        // Public API interfaces and contracts
            │   │   ├── addon/                  // Addon system API (registration, events, dependencies)
            │   │   ├── client/                 // Client-side API interfaces
            │   │   └── server/                 // Server-side API interfaces
            │   ├── client/                     // Client-side utilities (renderers, key tracking)
            │   ├── config/                     // Configuration management
            │   ├── fx/                         // Visual effects and particle systems
            │   ├── network/                    // Network packet handling
            │   ├── registry/                   // Main registry classes (blocks, items, entities, etc.)
            │   │   └── addon/                  // Addon-specific registries
            │   └── util/                       // Utility classes (helpers, validators, constants)
            │
            ├── features/                       // Feature implementations organized by domain
            │   ├── artifacts/                  // Magical artifacts (Elder Wand, Sorting Hat, etc.)
            │   │   └── client/                 // Client-side artifact rendering
            │   ├── automation/                 // Automation tools and systems
            │   ├── building/                   // Building systems (wards, wizard towers)
            │   ├── cloak/                      // Cloak items and functionality
            │   ├── combat/                     // Combat systems and mechanics
            │   ├── communication/              // Communication features (mirrors, owls, patronus)
            │   ├── contracts/                  // Magical contract system
            │   ├── convenience/                // Convenience features
            │   │   ├── client/                 // Client-side convenience features
            │   │   └── network/                // Network handlers for convenience features
            │   ├── creatures/                  // Entity/creature implementations
            │   │   ├── aquatic/                // Aquatic creatures
            │   │   ├── base/                   // Base creature classes
            │   │   ├── client/                 // Client-side creature renderers
            │   │   ├── companion/              // Companion creatures (pets)
            │   │   │   └── ai/                 // AI behaviors for companions
            │   │   ├── hostile/                // Hostile creatures
            │   │   ├── mount/                  // Mountable creatures
            │   │   ├── neutral/                // Neutral creatures
            │   │   ├── special/                // Special creature types
            │   │   ├── spiritual/              // Spiritual entities
            │   │   └── util/                   // Creature utility helpers
            │   ├── economy/                    // Economy systems (currency, trading, Gringotts)
            │   ├── education/                  // Education features (classes, houses)
            │   │   └── client/                 // Client-side education UI
            │   ├── enchantments/               // Custom enchantment system
            │   ├── environment/                // World environment features
            │   ├── flashlight/                 // Flashlight item functionality
            │   │   └── client/                 // Client-side flashlight rendering
            │   ├── fx/                         // Visual effects implementations
            │   ├── gear/                       // Gear items and systems
            │   ├── ghosts/                     // Ghost entities and systems
            │   ├── items/                      // Feature-specific items
            │   │   ├── books/                  // Book items
            │   │   ├── food/                   // Food items
            │   │   └── quidditch/              // Quidditch items
            │   ├── magic/                      // Core magic system
            │   ├── mail/                      // Mail system implementation
            │   ├── misc/                       // Miscellaneous items
            │   │   └── client/                 // Client-side misc item rendering
            │   ├── navigation/                 // Navigation items (compasses, maps, journals)
            │   ├── npcs/                       // NPC entities
            │   ├── organizations/              // Organization systems (Death Eaters, Order, etc.)
            │   ├── playerclass/                // Player class system
            │   │   ├── client/                 // Client-side player class UI
            │   │   └── network/                // Network handlers for player classes
            │   ├── portraits/                  // Portrait system
            │   ├── potions/                    // Potion system and recipes
            │   ├── quidditch/                  // Quidditch game mechanics
            │   ├── robes/                      // Robe items and systems
            │   ├── social/                     // Social features
            │   ├── spell/                      // Spell casting system
            │   │   ├── client/                 // Client-side spell rendering
            │   │   ├── entity/                 // Spell entity implementations
            │   │   └── network/                // Network handlers for spells
            │   ├── storage/                    // Storage systems (bags, trunks, pocket dimensions)
            │   │   └── client/                 // Client-side storage UI
            │   ├── transportation/             // Transportation systems (broomsticks, floo powder, portkeys)
            │   ├── wand/                       // Wand system (cores, woods, affinities)
            │   │   └── client/                 // Client-side wand rendering
            │   └── worldgen/                   // World generation features
            │
            ├── init/                           // Mod initialization classes
            │   ├── client/                     // Client-side initialization (keybinds)
            │   └── [ModInitialization.java, ClientInitialization.java, ServerEventHandler.java]
            │
            └── item/                           // Item implementations organized by category
                ├── cloak/                      // Cloak items
                ├── flashlight/                // Flashlight items
                │   └── client/                 // Client-side flashlight rendering
                ├── gear/                       // Gear items (charms, runes)
                ├── misc/                       // Miscellaneous items
                │   └── client/                 // Client-side misc rendering
                ├── robes/                      // Robe items
                ├── transportation/             // Transportation items
                └── wand/                       // Wand items
                    └── client/                 // Client-side wand rendering
```

## Resources (`src/main/resources`)

```
src/main/resources/
├── assets/
│   └── spells_n_squares/                      // Mod asset namespace
│       ├── geckolib/                          // GeckoLib animation models
│       │   ├── animations/                    // Animation JSON files
│       │   └── models/                        // GeckoLib model files
│       ├── items/                             // Item model JSON files
│       ├── lang/                              // Language files (translations)
│       ├── models/                            // Block/item model definitions
│       │   ├── entity/                        // Entity model definitions
│       │   └── item/                          // Item model definitions
│       ├── shaders/                           // Custom shader programs
│       │   ├── core/                          // Core shaders (cut effects, grayscale, lumos)
│       │   └── post/                          // Post-processing shaders
│       ├── sounds/                            // Sound effect files
│       ├── sounds.json                        // Sound event definitions
│       └── textures/                          // Texture files (PNG images)
│           ├── block/                         // Block textures
│           │   └── tree/                      // Tree block textures
│           ├── entity/                        // Entity textures
│           ├── gui/                           // GUI textures
│           │   └── container/                 // Container GUI textures
│           ├── item/                          // Item textures
│           │   └── tree/                      // Tree item textures
│           ├── misc/                          // Miscellaneous textures
│           └── spell/                         // Spell effect textures
│
├── data/
│   └── spells_n_squares/                      // Data-driven content
│       └── tags/                              // Tag definitions
│           └── item/                          // Item tags (e.g., wands.json)
│
├── spells_n_squares.mixins.json              // Mixin configuration
│
└── templates/                                 // Template files
    └── META-INF/
        └── neoforge.mods.toml                 // Mod metadata template
```

## Notes

- **Package Structure**: The project follows a domain-driven organization with `block/`, `item/`, `features/`, and `core/` as main organizational units.
- **Client/Server Separation**: Client-specific code is consistently separated into `client/` subdirectories.
- **Feature Organization**: Features are organized by domain (spell, wand, potions, creatures, etc.) rather than by technical layer.
- **Registry Pattern**: Core registries are centralized in `core/registry/` with addon-specific registries in subdirectories.
- **API Layer**: A clear API layer exists in `core/api/` for addon support and feature integration.
