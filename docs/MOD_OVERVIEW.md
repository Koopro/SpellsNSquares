# Spells N Squares - Complete Mod Overview

This document provides a comprehensive list of everything that exists in the Spells N Squares mod.

**Last Updated**: Based on current codebase analysis

---

## Table of Contents

1. [Core Systems](#core-systems)
2. [Spells](#spells)
3. [Items](#items)
4. [Blocks](#blocks)
5. [Entities](#entities)
6. [Creatures](#creatures)
7. [Biomes](#biomes)
8. [Features & Systems](#features--systems)
9. [Creative Tabs](#creative-tabs)

---

## Core Systems

### Registry System
- **ModBlocks** - Block registry
- **ModItems** - Item registry
- **ModEntities** - Entity registry
- **ModBiomes** - Biome registry
- **ModSounds** - Sound registry
- **ModCreativeTabs** - Creative tab registry
- **SpellRegistry** - Spell registration system
- **CreatureRegistry** - Creature registration system
- **EnchantmentRegistry** - Enchantment registration system
- **ParticleEffectRegistry** - Particle effect registry

### Data Components
- **WandData** - Wand information storage
- **CloakChargeData** - Cloak charge tracking
- **SocketData** - Gear socketing system
- **BagInventoryData** - Enchanted bag storage
- **TrunkInventoryData** - Magical trunk storage
- **PocketDimensionData** - Pocket dimension storage
- **PortkeyData** - Portkey destination data
- **BroomstickData** - Broomstick flight data
- **MirrorData** - Two-way mirror connection data
- **FlashlightItem Data Components** - Flashlight state
- **TimeTurnerItem Data Components** - Time turner state

### Player Data Managers
- **SpellManagerAdapter** - Manages player spell data
- **PlayerClassManagerAdapter** - Manages player class assignments
- **LumosManagerAdapter** - Manages Lumos spell state

### API System
- **Addon API** - System for mod addons
- **Feature Registry** - Feature registration system
- **Addon Registry** - Addon discovery and registration
- **Event System** - Custom events (SpellCastEvent, SpellSlotChangeEvent, PlayerClassChangeEvent)

---

## Spells

### Currently Implemented (12 spells)
1. **Accio** - Summoning charm
2. **Riddikulus** - Boggart-banishing spell
3. **Immobulus** - Freezing spell
4. **Silencio** - Silencing spell
5. **Evanesco** - Vanishing spell
6. **Locomotor** - Movement spell
7. **Tarantallegra** - Dancing jinx
8. **Langlock** - Tongue-tying jinx
9. **Levicorpus** - Levitation jinx
10. **Liberacorpus** - Counter-jinx for Levicorpus
11. **Sonorus** - Voice amplification spell
12. **Muffliato** - Privacy charm
13. **Orchideous** - Flower-conjuring spell
14. **Periculum** - Signal spell

### Spell Entities
- **ShieldOrbEntity** - Protego shield visual
- **LightOrbEntity** - Lumos light source
- **LightningBeamEntity** - Lightning spell visual
- **DummyPlayerEntity** - Training dummy for spell practice

### Spell System Features
- Spell cooldown system
- Spell slot management
- Wand affinity system
- Spell casting animations
- Visual effects system
- Sound-visual synchronization

---

## Items

### Wands
- **Demo Wand** - Full wand system with:
  - Wood types (26+ types: Ash, Beech, Blackthorn, Cedar, Chestnut, Cypress, Dogwood, Elder, Elm, Fir, Hawthorn, Holly, Hornbeam, etc.)
  - Wand cores (Phoenix Feather, Dragon Heartstring, Unicorn Hair, etc.)
  - Wand affinity system
  - Wand attunement system
  - Visual glow effects

### Artifacts (31 items)
1. **Elder Wand** - Most powerful wand
2. **Resurrection Stone** - Deathly Hallow
3. **Deathly Hallow Cloak** - Invisibility cloak
4. **Marauder's Map** - Shows player locations
5. **Pensieve** - Memory viewing device
6. **Philosopher's Stone** - Alchemy stone
7. **Sorting Hat** - House assignment
8. **Deluminator** - Light control device
9. **Sneakoscope** - Lie detection device
10. **Time Turner** - Time manipulation
11. **Remembrall** - Memory reminder
12. **Goblet of Fire** - Tournament selection
13. **Omnioculars** - Enhanced vision
14. **Extendable Ear** - Eavesdropping device
15. **Decoy Detonator** - Distraction device
16. **Darkness Powder** - Area darkness
17. **Mirror of Erised** - Desire reflection
18. **Crystal Ball** - Divination tool
19. **Foe Glass** - Enemy detection

### Transportation Items
- **Portkey** - Teleportation item
- **Floo Powder** - Network teleportation
- **Broomstick Basic** - Basic flying broom
- **Broomstick Racing** - Racing broom
- **Broomstick Firebolt** - Premium racing broom

### Storage Items
- **Enchanted Bag Small** - Small storage bag
- **Enchanted Bag Medium** - Medium storage bag
- **Enchanted Bag Large** - Large storage bag
- **Enchanted Bag Bottomless** - Unlimited storage bag
- **Pocket Dimension** - Personal dimension storage

### Cloaks & Armor
- **Demiguise Cloak** - Invisibility cloak
- **Deathly Hallow Cloak** - Ultimate invisibility cloak
- **Revealer Dust** - Reveals invisible entities

### House Robes (12 items)
- **Gryffindor Robe** (Chest, Legs, Boots)
- **Slytherin Robe** (Chest, Legs, Boots)
- **Hufflepuff Robe** (Chest, Legs, Boots)
- **Ravenclaw Robe** (Chest, Legs, Boots)

### Potions (7 potions)
1. **Draught of Living Death** - Deep sleep potion
2. **Draught of Peace** - Calming potion
3. **Felix Felicis** - Luck potion
4. **Murtlap Essence** - Healing potion
5. **Shrinking Solution** - Size reduction potion
6. **Swelling Solution** - Size increase potion
7. **Veritaserum** - Truth serum

### Potion Ingredients (49+ ingredients)
- Asphodel, Babbling Beverage, Bezoar, Bicorn Horn, Boomslang Skin, Bubotuber Pus, Dittany, Dragon Blood, Dragon Scale, Flobberworm Mucus, Forgetfulness Potion, Healing Potion, Invisibility Potion, Knotgrass, Lacewing Flies, Leeches, Love Potion, Mandrake Root, Moonstone, Newt Eyes, Pepperup Potion, Phoenix Feather, Polyjuice Potion, Powdered Bicorn Horn, Powdered Moonstone, Shrivelfig, Skele-Gro Potion, Strength Potion, Unicorn Hair, Valerian Sprigs, Wideye Potion, Wit-Sharpening Potion, Wolfsbane, Wolfsbane Potion, Wormwood, Fire Protection Potion

### Communication Items
- **Two-Way Mirror** - Instant communication device
- **Mail Item** - Mail sending system
- **Howler** - Angry mail item

### Navigation Items
- **Magical Map** - Enhanced map
- **Location Compass** - Location tracking compass
- **Magical Journal** - Location journal

### Education Items
- **Bestiary** - Creature encyclopedia
- **Spell Journal** - Spell learning journal
- **Spell Book** - Spell reference book
- **Recipe Book** - Potion recipe book
- **Daily Prophet** - News item
- **Textbooks** - Various educational books
- **Portrait Frame** - Portrait creation item

### Automation Items
- **Enchanted Workbench** - Enhanced crafting table
- **Auto Harvest Hoe** - Automatic harvesting tool

### Building Items
- **Wizard Tower** - Structure builder item

### Utility Items
- **Flashlight** - Light source item
- **Rubber Duck** - Debug/test item

### Gear System
- **CharmItem** - Enchantable charms
- **RuneItem** - Enchantable runes
- **Socket System** - Gear socketing for charms/runes

---

## Blocks

### Storage Blocks
- **Magical Trunk** - Large storage container
- **Auto Sort Chest** - Auto-sorting storage

### Automation Blocks
- **Self-Stirring Cauldron** - Automatic potion brewing
- **Magical Furnace** - Enhanced smelting furnace
- **Magical Farm** - Automatic farming block
- **Item Collector** - Item collection block
- **Magical Composter** - Enhanced composter
- **Resource Generator** - Resource generation block

### Building Blocks
- **Magical Light White** - White magical light source
- **Magical Light Blue** - Blue magical light source
- **Magical Light Green** - Green magical light source
- **Magical Light Red** - Red magical light source
- **Magical Light Purple** - Purple magical light source
- **Magical Light Gold** - Gold magical light source

### Tree Blocks (26+ wood types)
Each wood type includes:
- Log, Planks, Stairs, Slab, Fence, Fence Gate, Door, Trapdoor, Button, Pressure Plate, Leaves, Sapling

Wood types: Ash, Beech, Blackthorn, Cedar, Chestnut, Cypress, Dogwood, Elder, Elm, Fir, Hawthorn, Holly, Hornbeam, Larch, Maple, Oak, Pine, Poplar, Rowan, Silver Lime, Spruce, Sycamore, Vine, Walnut, Willow, Yew

### Economy Blocks
- **Trading Post** - Player trading block
- **Automated Shop** - Automated shop block
- **Vault** - Secure storage vault

### Education Blocks
- **House Points Hourglass** - House points display

### Combat Blocks
- **Duel Arena** - PvP dueling arena

### Enchantment Blocks
- **Enchantment Table** - Custom enchantment block

### Communication Blocks
- **Notice Board** - Community message board

### Magical Plants
- **Mandrake Plant** - Screaming plant
- **Wolfsbane Plant** - Wolfsbane source
- **Gillyweed Block** - Underwater breathing plant
- **Devil's Snare** - Hostile plant
- **Whomping Willow** - Animated tree
- **Venomous Tentacula** - Poisonous plant

### Portrait Blocks
- **Magical Portrait** - Animated portrait block

### Quidditch Blocks
- **Quidditch Pitch** - Quidditch arena block

---

## Entities

### Spell Entities
- **ShieldOrbEntity** - Shield spell visual entity
- **LightOrbEntity** - Light spell entity
- **LightningBeamEntity** - Lightning spell entity
- **DummyPlayerEntity** - Spell practice dummy

### Communication Entities
- **OwlEntity** - Mail delivery owl

---

## Creatures

### Companion Creatures (20+)
- Owl, Cat, Toad, Niffler, Bowtruckle, Puffskein, Kneazle, Erumpent, Mooncalf, Raven Familiar, Rat Familiar, Snake Familiar, Ferret Familiar, Augurey, Demiguise, Fwooper, Jobberknoll

### Neutral Creatures (18)
- Billywig, Centaur, Clabbert, Diricawl, Fairy, Fire Crab, Ghoul, Gnome, Horklump, Imp, Jarvey, Leprechaun, Re'em, Sphinx, Streeler, Troll, Unicorn, Yeti

### Mount Creatures (6)
- Hippogriff, Thestral, Occamy, Thunderbird, Graphorn, Zouwu

### Hostile Creatures (20+)
- Dementor, Boggart, Acromantula, Dragon (multiple breeds), Swooping Evil, Basilisk, Chimaera, Ashwinder, Doxy, Erkling, Lethifold, Manticore, Nundu, Pixie, Quintaped, Red Cap, Hungarian Horntail, Chinese Fireball, Swedish Short-Snout, Common Welsh Green, Hebridean Black, Peruvian Vipertooth, Romanian Longhorn, Ukrainian Ironbelly

### Aquatic Creatures (6)
- Hippocampus, Kappa, Kelpie, Merpeople, Ramora, Grindylow

### Spiritual Creatures (1)
- Veela

### Special Creatures (1)
- Werewolf

---

## Biomes

### Custom Biomes (5)
1. **Forbidden Forest** - Dark magical forest
2. **Black Lake** - Underwater magical lake
3. **Azkaban** - Prison island biome
4. **Magical Meadow** - Peaceful magical biome
5. **Dark Forest Edge** - Forest transition biome

---

## Features & Systems

### Spell System
- Spell registry and management
- Spell casting mechanics
- Cooldown system
- Spell slot system
- Wand-spell compatibility
- Visual effects system
- Sound-visual synchronization

### Wand System
- Wand wood types (26+)
- Wand cores (Phoenix Feather, Dragon Heartstring, Unicorn Hair)
- Wand affinity calculation
- Wand attunement system
- Wand visual effects (glow)
- Wand data persistence

### Player Class System
- Class assignment system
- Class-specific abilities
- Class progression tracking

### Education System
- **Bestiary System** - Creature discovery tracking
- **Class System** - Class management framework
- **House Points System** - Point tracking framework
- **Homework System** - Assignment framework
- **Skill Tree** - Skill progression framework
- **Exam System** - Testing framework
- **Prefect System** - Prefect management framework

### Economy System
- **Currency System** - Galleon, Sickle, Knut
- **Trading System** - Player trading
- **Gringotts System** - Banking framework
- **Trading Post** - Trading block
- **Automated Shop** - Shop automation
- **Vault** - Secure storage

### Combat System
- Spell-based combat
- Combat stats tracking
- Spell resistance system (framework)
- Duel arena block

### Potion System
- **Potion Brewing** - Full brewing system
- **Potion Recipes** - Recipe management
- **Potion Ingredients** - 49+ ingredients
- **Self-Stirring Cauldron** - Automated brewing
- **Potion Effects** - Various potion effects

### Transportation System
- **Apparition** - Teleportation spell
- **Floo Network** - Network teleportation
- **Portkey System** - Item-based teleportation
- **Broomstick Flight** - Flying mechanics
- **Waypoint System** - Location waypoints

### Mail System
- **Owl Post** - Mail delivery system
- **Mailbox Block** - Mail reception
- **Mail Item** - Mail sending
- **Howler** - Angry mail

### Social System
- **Friendship System** - Friend management
- **Reputation System** - Player reputation tracking
- **Friendship Bracelet** - Friendship item
- **Contract System** - Contract creation and management
- **Unbreakable Vow** - Special contract type

### Ghost System
- **Ghost Entities** - Ghost and House Ghost entities
- **Ghost Dialogue** - Dialogue system
- **Ghost Data** - Ghost information storage

### Portrait System
- **Portrait Block** - Animated portraits
- **Portrait Dialogue** - Portrait interaction
- **Portrait Frame** - Portrait creation item

### Enchantment System
- **Enchantment Registry** - Enchantment registration
- **Enchantment Application** - Enchantment system
- **Enchantment Table** - Enchantment block
- **Wand Enchantments** - Power, Range, Efficiency, Accuracy
- **Item Enchantments** - Various item enchantments

### Building System
- **Ward System** - Protection wards
- **Wizard Tower** - Structure builder
- **Ward Handler** - Ward management

### Creature System
- **Creature Registry** - 60+ creatures registered
- **Creature Taming** - Taming framework
- **Creature Categories** - Companion, Mount, Hostile, Neutral, Aquatic, Spiritual
- **Bestiary** - Creature discovery system

### Storage System
- **Enchanted Bags** - Portable storage (4 tiers)
- **Magical Trunk** - Large storage block
- **Pocket Dimension** - Personal dimension
- **Auto Sort Chest** - Auto-sorting storage

### Communication System
- **Two-Way Mirror** - Instant communication
- **Notice Board** - Community board
- **Patronus Messaging** - Patronus communication framework

### Navigation System
- **Magical Map** - Enhanced mapping
- **Location Compass** - Location tracking
- **Magical Journal** - Location journal
- **Waypoint System** - Waypoint management

### Automation System
- **Self-Stirring Cauldron** - Auto brewing
- **Magical Furnace** - Enhanced smelting
- **Magical Farm** - Auto farming
- **Item Collector** - Item collection
- **Resource Generator** - Resource generation
- **Enchanted Workbench** - Enhanced crafting
- **Auto Harvest Hoe** - Auto harvesting

### FX System
- **Particle Effects** - Particle system
- **Screen Effects** - Screen flash/shake
- **Shader Effects** - Custom shaders
- **Post Processing** - Visual post-processing
- **Sound-Visual Sync** - Synchronized effects
- **Environmental Effects** - World effects
- **Cut Effects** - Screen cut effects

### Addon System
- **Addon API** - Mod addon support
- **Addon Registry** - Addon discovery
- **Addon Events** - Custom events for addons
- **Addon Registries** - Spell, Item, Entity, Network, PlayerClass registries for addons

### Network System
- **ModNetwork** - Network payload system
- **FX Test Payload** - Effect testing
- **Spell Network Payloads** - Spell synchronization
- **Player Class Network** - Class synchronization
- **Waypoint Network** - Waypoint synchronization

### World Generation
- **Creature Spawn Config** - Creature spawning configuration
- **Custom Biomes** - 5 custom biomes
- **Biome Modifiers** - Biome modification system

---

## Creative Tabs

1. **Spells N Squares Tab** - Main tab (all items)
2. **Wands & Spells Tab** - Wand and spell items
3. **Economy Tab** - Economy-related blocks
4. **Quality of Life Tab** - Utility items
5. **Transportation Tab** - Transportation items
6. **Storage Tab** - Storage blocks and items
7. **Building Tab** - Building blocks and lights
8. **Automation Tab** - Automation blocks and items
9. **Communication Tab** - Communication blocks and items
10. **Navigation Tab** - Navigation items
11. **Education & Combat Tab** - Education and combat blocks
12. **Robes Tab** - House robes

---

## Statistics

- **Spells**: 12+ implemented (50+ planned)
- **Items**: 100+ items
- **Blocks**: 40+ blocks (plus 26+ wood types × 12 block variants = 300+ tree blocks)
- **Entities**: 5 entities
- **Creatures**: 60+ registered
- **Biomes**: 5 custom biomes
- **Systems**: 20+ major systems
- **Creative Tabs**: 12 tabs

---

## Notes

- Many spells are registered but commented out (waiting for spell class implementations)
- Some systems have frameworks but need data component integration
- Some blocks exist but need GUI implementations
- The mod supports addons through a comprehensive API system
- All major systems are modular and extensible

---

**Document Version**: 1.0  
**Last Updated**: Current codebase analysis



