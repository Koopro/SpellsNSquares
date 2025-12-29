# Complete Bestiary of Magical Creatures

**Spells 'n Squares Mod - Comprehensive Creature Registry**

This document contains all magical creatures from the Wizarding World, combining information from the codebase implementation and the official documentation.

**Last Updated:** 2025-01-26

---

## Table of Contents

1. [XX - Harmless Creatures](#xx---harmless-creatures)
2. [XXX - Competent Creatures](#xxx---competent-creatures)
3. [XXXX - Dangerous Creatures](#xxxx---dangerous-creatures)
4. [XXXXX - Wizard Killer Creatures](#xxxxx---wizard-killer-creatures)
5. [Spirits & Humanoids](#spirits--humanoids)
6. [Implementation Status Summary](#implementation-status-summary)

---

## Legend

- ✅ **Implemented** - Creature exists in codebase with entity class
- ⚠️ **Registered Only** - Creature registered but entity not fully implemented
- ❌ **Not Implemented** - Creature documented but not in codebase
- 📝 **Documented** - Creature has detailed documentation

---

## XX - Harmless Creatures

*Creatures that are boring or harmless. May be kept as pets.*

### Auger
- **Status:** ❌ Not Implemented | 📝 Documented
- **Description:** A small, worm-like creature that lives in wood
- **Habitat:** Wood, forests
- **Behavior:** Passive, wood-dwelling
- **Abilities:** Wood-boring, small size
- **Taming:** Cannot be tamed
- **Lore:** Small creatures that live inside wood, causing damage to wooden structures

### Augurey
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** Also known as the Irish Phoenix, a greenish-black bird whose cry was once believed to foretell death
- **Habitat:** Ireland, Britain, damp areas
- **Behavior:** Shy, rain-loving, mournful
- **Abilities:** Rain prediction, mournful cry
- **Taming:** Can be kept as pets. Requires damp environment
- **Lore:** Augureys were once thought to foretell death with their cry, but they actually predict rain
- **Codebase:** Registered in ModCreatures.java, has entity class

### Bowtruckle
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A small tree guardian that helps with plant growth
- **Habitat:** Wand-wood trees, forests
- **Behavior:** Shy, protective of trees, twig-like appearance
- **Abilities:** Plant growth enhancement, tree protection, camouflage
- **Taming:** Can be tamed with woodlice or fairy eggs. Must respect their tree
- **Lore:** Bowtruckles guard trees used for wand-making. They are extremely difficult to spot and will defend their trees fiercely
- **Codebase:** Registered in ModCreatures.java, has BowtruckleEntity class
- **Stats:** Health: 10 HP, Attack: 2 HP, Size: 0.6 x 1.8 blocks

### Chizpurfle
- **Status:** ❌ Not Implemented | 📝 Documented
- **Description:** A small, parasitic creature that feeds on magic
- **Habitat:** Magical objects, wands, cauldrons
- **Behavior:** Parasitic, attracted to magic
- **Abilities:** Magic consumption, small size
- **Taming:** Cannot be tamed
- **Lore:** Tiny parasites that feed on magic, often found in cauldrons and wands

### Clabbert
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A tree-dwelling creature resembling a cross between a monkey and a frog
- **Habitat:** Southern US states, trees
- **Behavior:** Tree-dwelling, wart-covered, alert
- **Abilities:** Wart flashes when danger approaches
- **Taming:** Cannot be tamed. Warts flash to warn of danger
- **Lore:** Clabberts have warts that flash when danger approaches. They were once kept as pets but are now protected
- **Codebase:** Registered in ModCreatures.java, has ClabbertEntity class

### Diricawl
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A plump, flightless bird capable of vanishing and reappearing elsewhere; known to Muggles as the dodo
- **Habitat:** Mauritius, various locations
- **Behavior:** Vanishing, reappearing, flightless
- **Abilities:** Apparition-like vanishing
- **Taming:** Cannot be tamed. Vanishes when threatened
- **Lore:** Diricawls can vanish and reappear elsewhere when threatened. Muggles know them as the extinct dodo
- **Codebase:** Registered in ModCreatures.java, has DiricawlEntity class

### Flobberworm
- **Status:** ❌ Not Implemented | 📝 Documented
- **Description:** A thick, brown worm that lives in ditches
- **Habitat:** Ditches, damp areas
- **Behavior:** Docile, slow-moving
- **Abilities:** None significant
- **Taming:** Cannot be tamed
- **Lore:** Thick, brown worms that are completely harmless and rather boring

### Ghoul
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A slimy, buck-toothed creature that often inhabits attics and barns
- **Habitat:** Attics, barns, wizarding homes
- **Behavior:** Harmless, noisy, slimy
- **Abilities:** Noise-making, harmless
- **Taming:** Cannot be tamed. Generally harmless but noisy
- **Lore:** Ghouls are harmless but noisy creatures that often live in wizarding attics. They are considered nuisances
- **Codebase:** Registered in ModCreatures.java, has GhoulEntity class

### Giant Purple Toad
- **Status:** ⚠️ Partial (Generic Toad exists)
- **Description:** A large purple toad, larger than regular toads
- **Habitat:** Ponds, marshes
- **Behavior:** Similar to regular toads but larger
- **Abilities:** Larger size than regular toads
- **Taming:** Can be kept as pets
- **Lore:** Larger variant of the common toad, often kept as pets
- **Codebase:** Generic "toad" exists, but not specifically "giant purple toad" variant

### Gnome
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A small, mischievous creature that infests gardens
- **Habitat:** Gardens, wizarding homes
- **Behavior:** Mischievous, garden-infesting, annoying
- **Abilities:** Garden infestation, mischief
- **Taming:** Cannot be tamed. Considered pests. Can be thrown
- **Lore:** Gnomes are garden pests that can be thrown out of gardens. They are annoying but relatively harmless
- **Codebase:** Registered in ModCreatures.java, has GnomeEntity class

### Horklump
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A pink, bristly creature that resembles a mushroom
- **Habitat:** Scandinavia, Northern Europe
- **Behavior:** Mushroom-like, burrowing, fast reproduction
- **Abilities:** Fast reproduction, burrowing
- **Taming:** Cannot be tamed. Considered pests
- **Lore:** Horklumps reproduce extremely quickly and are considered pests. They resemble pink mushrooms
- **Codebase:** Registered in ModCreatures.java, has HorklumpEntity class

### Imp
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A small, mischievous creature similar to a pixie but less dangerous
- **Habitat:** Britain, Ireland
- **Behavior:** Mischievous, less dangerous than pixies
- **Abilities:** Minor mischief
- **Taming:** Cannot be tamed. Less dangerous than pixies
- **Lore:** Imps are small, mischievous creatures found in Britain and Ireland. They are less dangerous than pixies
- **Codebase:** Registered in ModCreatures.java, has ImpEntity class

### Jobberknoll
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A small, blue speckled bird that remains silent until its death, at which point it lets out a long scream consisting of all the sounds it has ever heard, in reverse order
- **Habitat:** Northern Europe, Scandinavia
- **Behavior:** Silent, death-scream, blue-speckled
- **Abilities:** Silence until death, reverse sound scream
- **Taming:** Can be kept as pets. Feathers used in memory potions
- **Lore:** Jobberknolls remain silent their entire lives, then scream all heard sounds in reverse upon death. Their feathers are used in memory potions
- **Codebase:** Registered in ModCreatures.java, has JobberknollEntity class

### Mooncalf
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A shy creature that appears during full moon
- **Habitat:** Meadows, clearings, moonlit areas
- **Behavior:** Nocturnal, shy, dances during full moon
- **Abilities:** Moonlight detection, dancing, shyness
- **Taming:** Can be approached during full moon dances. Requires patience and respect
- **Lore:** Mooncalves only emerge during full moons to perform intricate dances. They are very shy and will hide if approached carelessly
- **Codebase:** Registered in ModCreatures.java, has MooncalfEntity class

### Puffskein / Pygmy Puff
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A fluffy pet creature that provides comfort and regeneration
- **Habitat:** Magical pet shops, wizarding homes
- **Behavior:** Docile, affectionate, content
- **Abilities:** Regeneration aura, comfort, low maintenance
- **Taming:** Easy to care for. Requires minimal attention and provides passive benefits
- **Lore:** Puffskeins are popular pets due to their easy care and pleasant humming when content. They eat anything, including leftovers. Pygmy Puffs are smaller variants
- **Codebase:** Registered in ModCreatures.java as "puffskein"

### Ramora
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A silver fish native to the Indian Ocean, known for its power to anchor ships
- **Habitat:** Indian Ocean, tropical waters
- **Behavior:** Ship-anchoring, silver, fish-like
- **Abilities:** Ship anchoring, water magic
- **Taming:** Cannot be tamed. Protects ships
- **Lore:** Ramoras are silver fish that can anchor ships. They are protective of vessels and their crews
- **Codebase:** Registered in ModCreatures.java, has RamoraEntity class

---

## XXX - Competent Creatures

*Creatures that a competent wizard can handle.*

### Ashwinder
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A serpent that arises from magical fires left burning too long
- **Habitat:** Areas with magical fires
- **Behavior:** Destructive, fire-based, temporary
- **Abilities:** Fire creation, egg-laying in ashes
- **Taming:** Cannot be tamed. Prevent by extinguishing magical fires
- **Lore:** Ashwinders are created when magical fires burn too long. They lay eggs that can burn down buildings
- **Codebase:** Registered in ModCreatures.java, has AshwinderEntity class
- **Stats:** Health: 20 HP, Attack: 4 HP, Classification: XXX (Competent)

### Billywig
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A sapphire-blue insect native to Australia; its sting causes giddiness and levitation
- **Habitat:** Australia, tropical areas
- **Behavior:** Fast-flying, stinging, colorful
- **Abilities:** Sting causes levitation and giddiness
- **Taming:** Cannot be tamed. Sting is used in potions
- **Lore:** Billywig stings are used in potions. The sting causes temporary levitation and giddiness
- **Codebase:** Registered in ModCreatures.java, has BillywigEntity class

### Bundimun
- **Status:** ❌ Not Implemented | 📝 Documented
- **Description:** A creature that infests houses, causing decay
- **Habitat:** Damp houses, old buildings
- **Behavior:** Destructive, house-infesting
- **Abilities:** Causes decay, infestation
- **Taming:** Cannot be tamed
- **Lore:** Creatures that infest houses and cause them to decay

### Crup
- **Status:** ❌ Not Implemented | 📝 Documented
- **Description:** A dog-like creature with a forked tail
- **Habitat:** Wizarding homes, magical pet shops
- **Behavior:** Dog-like, loyal, forked tail
- **Abilities:** Loyalty, forked tail
- **Taming:** Can be tamed like dogs
- **Lore:** Dog-like creatures with forked tails, popular as pets among wizards

### Doxy
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A small, fairy-like creature with venomous bites
- **Habitat:** Northern Europe, cold climates
- **Behavior:** Aggressive, venomous, fairy-like
- **Abilities:** Venomous bite, flight
- **Taming:** Cannot be tamed. Venomous bite requires antidote
- **Lore:** Doxies are small, aggressive creatures with venomous bites. They are often mistaken for fairies
- **Codebase:** Registered in ModCreatures.java, has DoxyEntity class

### Dugbog
- **Status:** ❌ Not Implemented | 📝 Documented
- **Description:** A marsh-dwelling creature that resembles dead wood
- **Habitat:** Marshes, wetlands
- **Behavior:** Camouflaged, marsh-dwelling
- **Abilities:** Camouflage, marsh adaptation
- **Taming:** Cannot be tamed
- **Lore:** Marsh-dwelling creatures that look like dead wood when motionless

### Fire Crab
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A large, turtle-like creature that shoots flames from its rear end
- **Habitat:** Fiji, volcanic islands
- **Behavior:** Defensive, flame-shooting, turtle-like
- **Abilities:** Flame shooting from rear, shell protection
- **Taming:** Cannot be tamed. Protected species
- **Lore:** Fire Crabs shoot flames from their rear ends for defense. They are a protected species
- **Codebase:** Registered in ModCreatures.java, has FireCrabEntity class

### Fwooper
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A brightly colored bird whose song can drive listeners insane
- **Habitat:** Africa, magical reserves
- **Behavior:** Colorful, dangerous song, kept silenced
- **Abilities:** Madness-inducing song
- **Taming:** Can be kept but requires silencing charm. Song causes madness
- **Lore:** Fwoopers have beautiful but dangerous songs that drive listeners insane. They must be silenced with charms
- **Codebase:** Registered in ModCreatures.java, has FwooperEntity class

### Glumbumble
- **Status:** ❌ Not Implemented | 📝 Documented
- **Description:** A grey, furry flying insect that produces melancholy-inducing treacle
- **Habitat:** Northern Europe
- **Behavior:** Flying, treacle-producing
- **Abilities:** Produces melancholy-inducing treacle
- **Taming:** Cannot be tamed
- **Lore:** Flying insects that produce treacle which causes melancholy

### Hippocampus
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A sea creature with the head and front legs of a horse and the tail of a fish
- **Habitat:** Mediterranean Sea, oceans
- **Behavior:** Aquatic, horse-like, fish-tailed
- **Abilities:** Aquatic movement, horse-like appearance
- **Taming:** Cannot be tamed. Aquatic creature
- **Lore:** Hippocampi are sea creatures with the front of a horse and tail of a fish. They are found in the Mediterranean
- **Codebase:** Registered in ModCreatures.java, has HippocampusEntity class

### Jarvey
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A ferret-like creature capable of human speech, though it usually speaks in rude and fast phrases
- **Habitat:** Britain, Ireland, North America
- **Behavior:** Rude speech, ferret-like, fast-talking
- **Abilities:** Human speech (rude), burrowing
- **Taming:** Cannot be tamed. Known for rude speech
- **Lore:** Jarveys can speak but usually do so in rude, fast phrases. They resemble large ferrets
- **Codebase:** Registered in ModCreatures.java, has JarveyEntity class

### Knarl
- **Status:** ❌ Not Implemented | 📝 Documented
- **Description:** A hedgehog-like creature that is easily offended
- **Habitat:** Northern Europe, gardens
- **Behavior:** Easily offended, hedgehog-like
- **Abilities:** Offense detection
- **Taming:** Cannot be tamed
- **Lore:** Hedgehog-like creatures that are easily offended and will destroy gardens if mistreated

### Kneazle
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** An intelligent cat-like creature that can detect untrustworthy people
- **Habitat:** Wizarding homes, magical pet shops
- **Behavior:** Intelligent, suspicious, loyal to owners
- **Abilities:** Untrustworthy person detection, intelligence, loyalty
- **Taming:** Can be tamed with patience and respect. Requires license in some areas
- **Lore:** Kneazles are highly intelligent and can detect suspicious or untrustworthy individuals. They have large ears and spotted fur
- **Codebase:** Registered in ModCreatures.java, has KneazleEntity class

### Leprechaun
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A small, mischievous creature known for its love of gold
- **Habitat:** Ireland, magical areas
- **Behavior:** Mischievous, gold-loving, small
- **Abilities:** Gold creation (temporary), mischief
- **Taming:** Cannot be tamed. Known for gold and mischief
- **Lore:** Leprechauns are small Irish creatures known for their love of gold. However, leprechaun gold disappears after a few hours
- **Codebase:** Registered in ModCreatures.java, has LeprechaunEntity class

### Mackled Malaclaw
- **Status:** ❌ Not Implemented | 📝 Documented
- **Description:** A lobster-like creature found on land
- **Habitat:** Coastal areas, rocky shores
- **Behavior:** Lobster-like, land-dwelling
- **Abilities:** Unlucky bite
- **Taming:** Cannot be tamed
- **Lore:** Lobster-like creatures whose bite causes bad luck for a week

### Moke
- **Status:** ❌ Not Implemented | 📝 Documented
- **Description:** A silver-green lizard that can shrink at will
- **Habitat:** Britain, Ireland
- **Behavior:** Shrinking, lizard-like
- **Abilities:** Size manipulation
- **Taming:** Cannot be tamed
- **Lore:** Silver-green lizards that can shrink at will, making them difficult to catch

### Murtlap
- **Status:** ❌ Not Implemented | 📝 Documented
- **Description:** A rat-like creature with growths on its back
- **Habitat:** Coastal areas, Britain
- **Behavior:** Rat-like, coastal
- **Abilities:** Growths used in potions
- **Taming:** Cannot be tamed
- **Lore:** Rat-like creatures whose growths are used in potions for resistance to curses

### Niffler
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A treasure-hunting creature that finds valuable items
- **Habitat:** Underground burrows, treasure-rich areas
- **Behavior:** Mischievous, attracted to shiny objects, burrowing
- **Abilities:** Treasure detection, burrowing, item collection
- **Taming:** Can be tamed with gold or shiny items. Requires secure containment
- **Lore:** Nifflers are attracted to anything shiny and will steal valuable items. They have pouches like marsupials for storing treasures
- **Codebase:** Registered in ModCreatures.java, has NifflerEntity class
- **Stats:** Health: 20 HP, Attack: 4 HP, Classification: XXX (Competent)

### Nogtail
- **Status:** ❌ Not Implemented | 📝 Documented
- **Description:** A demon found in rural areas that infests pigsties
- **Habitat:** Rural areas, pigsties
- **Behavior:** Pigsty-infesting, demonic
- **Abilities:** Infestation
- **Taming:** Cannot be tamed
- **Lore:** Demons that infest pigsties and cause trouble

### Pixie (Cornish)
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A small, mischievous creature known for its pranks
- **Habitat:** Cornwall, Britain
- **Behavior:** Mischievous, prank-loving, blue
- **Abilities:** Flight, mischief, pranks
- **Taming:** Cannot be tamed. Known for causing trouble
- **Lore:** Pixies are small, blue, mischievous creatures known for their pranks. Gilderoy Lockhart released them in class
- **Codebase:** Registered in ModCreatures.java, has PixieEntity class

### Plimpy
- **Status:** ❌ Not Implemented | 📝 Documented
- **Description:** A spherical fish with long legs
- **Habitat:** Deep lakes
- **Behavior:** Spherical, long-legged
- **Abilities:** Swimming, leg movement
- **Taming:** Cannot be tamed
- **Lore:** Spherical fish with long legs found in deep lakes

### Pogrebin
- **Status:** ❌ Not Implemented | 📝 Documented
- **Description:** A Russian demon that follows humans
- **Habitat:** Russia
- **Behavior:** Following, demonic
- **Abilities:** Shadow-following
- **Taming:** Cannot be tamed
- **Lore:** Russian demons that follow humans and cause despair

### Red Cap
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A dwarf-like creature that lurks in places where blood has been shed
- **Habitat:** Battlefields, areas of violence
- **Behavior:** Violent, blood-seeking, dwarf-like
- **Abilities:** Violence, blood-seeking
- **Taming:** Cannot be tamed. Dangerous in areas of violence
- **Lore:** Red Caps are dwarf-like creatures that lurk where blood has been shed. They are violent and dangerous
- **Codebase:** Registered in ModCreatures.java, has RedCapEntity class

### Salamander
- **Status:** ❌ Not Implemented | 📝 Documented
- **Description:** A fire-dwelling lizard
- **Habitat:** Fire, hot areas
- **Behavior:** Fire-dwelling, lizard-like
- **Abilities:** Fire immunity, fire-dwelling
- **Taming:** Cannot be tamed
- **Lore:** Lizards that live in fire and can survive extreme heat

### Shrake
- **Status:** ❌ Not Implemented | 📝 Documented
- **Description:** A spiny fish found in the Atlantic
- **Habitat:** Atlantic Ocean
- **Behavior:** Spiny, fish-like
- **Abilities:** Spines
- **Taming:** Cannot be tamed
- **Lore:** Spiny fish found in the Atlantic Ocean

### Streeler
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A giant snail that changes color hourly and leaves a poisonous trail
- **Habitat:** Africa, tropical regions
- **Behavior:** Color-changing, poisonous, slow
- **Abilities:** Color change, poisonous trail
- **Taming:** Cannot be tamed. Poisonous trail is dangerous
- **Lore:** Streelers are giant snails that change color every hour. Their trail is highly poisonous
- **Codebase:** Registered in ModCreatures.java, has StreelerEntity class

---

## XXXX - Dangerous Creatures

*Creatures that require specialist knowledge. A wizard may handle them.*

### Centaur
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A creature with the upper body of a human and the lower body of a horse
- **Habitat:** Forbidden Forest, magical forests
- **Behavior:** Proud, intelligent, territorial
- **Abilities:** Archery, divination, intelligence
- **Taming:** Cannot be tamed. Must be treated with respect as equals
- **Lore:** Centaurs are highly intelligent and proud. They live in herds and are skilled in archery and divination
- **Codebase:** Registered in ModCreatures.java, has CentaurEntity class

### Demiguise
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A peaceful, ape-like creature that can turn invisible and has precognitive abilities
- **Habitat:** Far East, magical reserves
- **Behavior:** Peaceful, invisible, precognitive
- **Abilities:** Invisibility, precognition, hair for invisibility cloaks
- **Taming:** Can be tamed with respect. Their hair is used in invisibility cloaks
- **Lore:** Demiguises can turn invisible and see the future. Their hair is used to make invisibility cloaks
- **Codebase:** Registered in ModCreatures.java, has DemiguiseEntity class

### Erumpent
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** An explosive horned creature, dangerous but tamable
- **Habitat:** African savannas, magical reserves
- **Behavior:** Aggressive when threatened, territorial, powerful
- **Abilities:** Explosive horn attacks, charging, strength
- **Taming:** Extremely dangerous. Requires expert handling and respect. Not recommended for beginners
- **Lore:** Erumpents have horns filled with explosive fluid. They are classified as XXXX by the Ministry of Magic due to their danger
- **Codebase:** Registered in ModCreatures.java, has ErumpentEntity class

### Graphorn
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A large aggressive beast, very tough and powerful mount
- **Habitat:** European mountains, magical reserves
- **Behavior:** Aggressive, tough, powerful
- **Abilities:** Extreme durability, strength, charging attacks
- **Taming:** Extremely dangerous. Requires expert handling and respect. Not for beginners
- **Lore:** Graphorns are large, aggressive creatures with tough hides. They are difficult to subdue and make powerful mounts
- **Codebase:** Registered in ModCreatures.java, has GraphornEntity class

### Griffin
- **Status:** ❌ Not Implemented | 📝 Documented
- **Description:** A creature with the head and wings of an eagle and the body of a lion
- **Habitat:** Greece, remote mountains
- **Behavior:** Proud, territorial, powerful
- **Abilities:** Flight, strength, eagle-lion hybrid
- **Taming:** Cannot be tamed. Extremely dangerous
- **Lore:** Griffins are powerful hybrid creatures with the head and wings of an eagle and body of a lion

### Hippogriff
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A mountable flying creature that requires respect (bowing) before taming
- **Habitat:** Mountainous regions, magical reserves
- **Behavior:** Proud, requires respect, territorial
- **Abilities:** Flight, powerful talons, respect-based bonding
- **Taming:** Must bow first to show respect. Then can be approached and mounted
- **Lore:** Hippogriffs are proud creatures that require proper respect. Buckbeak was a notable hippogriff who played a role in Harry Potter's adventures
- **Codebase:** Registered in ModCreatures.java, has HippogriffEntity class
- **Stats:** Health: 40 HP, Attack: 8 HP, Classification: XXXX (Dangerous)

### Kappa
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A water-dwelling creature from Japan that resembles a monkey with fish-like scales
- **Habitat:** Japan, rivers, ponds
- **Behavior:** Water-dwelling, dangerous, monkey-like
- **Abilities:** Water manipulation, strength
- **Taming:** Cannot be tamed. Dangerous water creature
- **Lore:** Kappas are water-dwelling creatures from Japan. They are dangerous and can be tricked by bowing
- **Codebase:** Registered in ModCreatures.java, has KappaEntity class

### Kelpie
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A shape-shifting water demon that often appears as a horse
- **Habitat:** Scotland, Ireland, bodies of water
- **Behavior:** Shape-shifting, water-dwelling, dangerous
- **Abilities:** Shape-shifting, water manipulation, drowning
- **Taming:** Cannot be tamed. Extremely dangerous water demon
- **Lore:** Kelpies are shape-shifting water demons that appear as horses to lure victims into water to drown them
- **Codebase:** Registered in ModCreatures.java, has KelpieEntity class

### Merpeople (Siren & Selkie)
- **Status:** ✅ Implemented (Generic) | 📝 Documented (Variants)
- **Description:** Aquatic beings with the upper body of a human and the tail of a fish
- **Habitat:** Oceans, lakes, underwater
- **Behavior:** Aquatic, intelligent, territorial
- **Abilities:** Aquatic movement, intelligence, water magic
- **Taming:** Cannot be tamed. Must be treated with respect
- **Lore:** Merpeople are intelligent aquatic beings. They have their own language and culture, as seen in the Triwizard Tournament. Variants include Sirens and Selkies
- **Codebase:** Registered in ModCreatures.java as "merpeople", has MerpeopleEntity class
- **Note:** Documentation includes separate Siren and Selkie variants

### Occamy
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A shape-shifting serpent that can grow/shrink, protective of eggs
- **Habitat:** Far East, India, magical reserves
- **Behavior:** Aggressive when protecting eggs, shape-shifting
- **Abilities:** Size manipulation, flight, egg protection
- **Taming:** Extremely dangerous. Not recommended for taming. Requires expert handling
- **Lore:** Occamies can grow or shrink to fit available space. They are very protective of their eggs, which are made of pure silver
- **Codebase:** Registered in ModCreatures.java, has OccamyEntity class

### Phoenix
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** Rare companion with resurrection and healing abilities
- **Habitat:** Mountain peaks, remote areas
- **Behavior:** Loyal, intelligent, immortal
- **Abilities:** Resurrection, healing tears, fire immunity, teleportation
- **Taming:** Extremely rare. Cannot be tamed conventionally - must earn loyalty through deeds
- **Lore:** Phoenixes are immortal birds that burst into flames upon death and are reborn from ashes. Their tears have healing properties
- **Codebase:** Registered in ModCreatures.java, has PhoenixEntity class

### Re'em
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A giant oxen with golden hides, whose blood grants immense strength
- **Habitat:** North America, Far East
- **Behavior:** Rare, powerful, golden
- **Abilities:** Strength-granting blood, immense size
- **Taming:** Cannot be tamed. Extremely rare
- **Lore:** Re'ems are giant oxen with golden hides. Their blood grants immense strength but is extremely rare
- **Codebase:** Registered in ModCreatures.java, has ReemEntity class

### Runespoor
- **Status:** ❌ Not Implemented | 📝 Documented
- **Description:** A three-headed snake native to Africa
- **Habitat:** Africa, magical reserves
- **Behavior:** Three-headed, snake-like
- **Abilities:** Three heads with different personalities
- **Taming:** Cannot be tamed
- **Lore:** Three-headed snakes where each head has a different personality and purpose

### Snallygaster
- **Status:** ❌ Not Implemented | 📝 Documented
- **Description:** A dragon-like creature native to North America
- **Habitat:** North America, remote areas
- **Behavior:** Dragon-like, dangerous
- **Abilities:** Flight, dangerous
- **Taming:** Cannot be tamed
- **Lore:** Dragon-like creatures native to North America

### Sphinx
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A creature with the head of a human and the body of a lion, known for posing riddles
- **Habitat:** Egypt, remote areas
- **Behavior:** Riddle-posing, intelligent, protective
- **Abilities:** Riddle posing, intelligence, protection
- **Taming:** Cannot be tamed. Must answer riddles correctly
- **Lore:** Sphinxes pose riddles to those who approach. They are intelligent and protective of their territory
- **Codebase:** Registered in ModCreatures.java, has SphinxEntity class

### Tebo
- **Status:** ❌ Not Implemented | 📝 Documented
- **Description:** A warthog-like creature that can turn invisible
- **Habitat:** Congo, Zaire
- **Behavior:** Invisible, warthog-like
- **Abilities:** Invisibility
- **Taming:** Cannot be tamed
- **Lore:** Warthog-like creatures that can turn invisible, native to the Congo and Zaire

### Thestral
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A mountable flying creature visible only to those who've seen death
- **Habitat:** Forbidden Forest, areas of death
- **Behavior:** Gentle, invisible to most, intelligent
- **Abilities:** Flight, invisibility to most, death perception
- **Taming:** Can only be seen and tamed by those who have witnessed death. Requires understanding
- **Lore:** Thestrals are only visible to those who have seen death and accepted it. They pull the carriages to Hogwarts
- **Codebase:** Registered in ModCreatures.java, has ThestralEntity class

### Thunderbird
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A large bird that creates storms, powerful flying mount
- **Habitat:** Arizona, North America, stormy regions
- **Behavior:** Powerful, storm-creating, majestic
- **Abilities:** Storm creation, flight, weather control
- **Taming:** Extremely rare and powerful. Requires exceptional skill and respect
- **Lore:** Thunderbirds are native to North America and can sense danger. They create storms as they fly and are extremely powerful
- **Codebase:** Registered in ModCreatures.java, has ThunderbirdEntity class

### Troll (Mountain, Forest, River)
- **Status:** ✅ Implemented (Generic) | 📝 Documented (Variants)
- **Description:** A large, dim-witted creature known for its immense strength
- **Habitat:** Mountains, remote areas
- **Behavior:** Dim-witted, strong, aggressive
- **Abilities:** Immense strength, durability
- **Taming:** Cannot be tamed. Extremely strong but dim-witted
- **Lore:** Trolls are large, dim-witted creatures with immense strength. They are dangerous but not particularly intelligent. Variants include Mountain, Forest, and River trolls
- **Codebase:** Registered in ModCreatures.java as "troll", has TrollEntity class
- **Note:** Documentation includes separate variants for Mountain, Forest, and River trolls

### Unicorn
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A pure-white, horse-like creature with a single horn on its forehead
- **Habitat:** Forests, magical areas
- **Behavior:** Pure, gentle, horned
- **Abilities:** Horn has magical properties, purity
- **Taming:** Cannot be tamed. Prefers female wizards
- **Lore:** Unicorns are pure creatures whose blood can sustain life. They prefer female wizards and are symbols of purity
- **Codebase:** Registered in ModCreatures.java, has UnicornEntity class

### Yeti
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** Also known as the Abominable Snowman, a giant humanoid creature native to the Himalayas
- **Habitat:** Himalayas, cold mountain regions
- **Behavior:** Giant, humanoid, cold-dwelling
- **Abilities:** Size, cold resistance, strength
- **Taming:** Cannot be tamed. Extremely rare
- **Lore:** Yetis are giant humanoid creatures native to the Himalayas. They are extremely rare and elusive
- **Codebase:** Registered in ModCreatures.java, has YetiEntity class

---

## XXXXX - Wizard Killer Creatures

*Known wizard killers. Impossible to train or domesticate.*

### Acromantula
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A large spider enemy
- **Habitat:** Dark forests, Aragog's colony
- **Behavior:** Aggressive, territorial, carnivorous
- **Abilities:** Venomous bite, web-spinning, size
- **Taming:** Cannot be tamed. Extremely dangerous. Avoid their territory
- **Lore:** Acromantulas are giant spiders capable of human speech. Aragog was a notable acromantula who lived in the Forbidden Forest
- **Codebase:** Registered in ModCreatures.java, has AcromantulaEntity class

### Basilisk
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A giant serpent with petrifying gaze, extremely dangerous boss
- **Habitat:** Chambers, underground lairs
- **Behavior:** Extremely aggressive, petrifying gaze, giant
- **Abilities:** Petrifying gaze, venom, immense size
- **Taming:** Cannot be tamed. Extremely dangerous boss. Requires special methods to defeat
- **Lore:** Basilisks are giant serpents whose gaze can petrify or kill. The one in the Chamber of Secrets was over 1000 years old
- **Codebase:** Registered in ModCreatures.java, has BasiliskEntity class
- **Stats:** Health: 80 HP, Attack: 16 HP, Classification: XXXXX (Wizard Killer)

### Chimaera
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A multi-headed beast, dangerous hybrid creature
- **Habitat:** Greece, remote mountains
- **Behavior:** Aggressive, multi-headed, dangerous
- **Abilities:** Multiple attack points, fire breath, strength
- **Taming:** Cannot be tamed. Extremely dangerous hybrid creature
- **Lore:** Chimaeras are rare Greek monsters with the head of a lion, body of a goat, and tail of a dragon. They are extremely dangerous
- **Codebase:** Registered in ModCreatures.java, has ChimaeraEntity class

### Dragon Breeds

#### Antipodean Opaleye
- **Status:** ❌ Not Implemented | 📝 Documented
- **Description:** A dragon breed with pearly scales and multi-colored eyes
- **Habitat:** New Zealand, dragon reserves
- **Behavior:** Less aggressive than other breeds
- **Abilities:** Fire breath, flight, opal-like scales
- **Taming:** Cannot be tamed
- **Lore:** Opal-eyed dragons native to New Zealand, known for being less aggressive

#### Chinese Fireball
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A dragon breed with smooth red scales and golden spikes around its face
- **Habitat:** China, dragon reserves
- **Behavior:** Aggressive, territorial, powerful
- **Abilities:** Fire breath, golden spikes, flight
- **Taming:** Cannot be tamed. Extremely dangerous dragon breed
- **Lore:** Chinese Fireballs are red dragons native to China. They are known for their distinctive golden facial spikes
- **Codebase:** Registered in ModCreatures.java, has ChineseFireballEntity class

#### Common Welsh Green
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A green dragon breed that is less aggressive than other breeds
- **Habitat:** Wales, dragon reserves
- **Behavior:** Less aggressive, territorial, powerful
- **Abilities:** Fire breath, green scales, flight
- **Taming:** Cannot be tamed. Dangerous but less aggressive than other breeds
- **Lore:** Common Welsh Greens are green dragons native to Wales. They are known for being less aggressive than other dragon breeds
- **Codebase:** Registered in ModCreatures.java, has CommonWelshGreenEntity class

#### Hebridean Black
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A black dragon breed native to the Hebrides islands
- **Habitat:** Hebrides, Scotland, dragon reserves
- **Behavior:** Aggressive, territorial, powerful
- **Abilities:** Fire breath, black scales, flight
- **Taming:** Cannot be tamed. Extremely dangerous dragon breed
- **Lore:** Hebridean Blacks are native to the Hebrides islands off Scotland. They are aggressive and territorial
- **Codebase:** Registered in ModCreatures.java, has HebrideanBlackEntity class

#### Hungarian Horntail
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A dragon breed with black scales, bronze horns, and a spiked tail
- **Habitat:** Hungary, dragon reserves
- **Behavior:** Aggressive, territorial, powerful
- **Abilities:** Fire breath, horn attacks, tail spikes
- **Taming:** Cannot be tamed. Extremely dangerous dragon breed
- **Lore:** Hungarian Horntails are among the most dangerous dragon breeds. They were featured in the Triwizard Tournament
- **Codebase:** Registered in ModCreatures.java, has HungarianHorntailEntity class

#### Norwegian Ridgeback
- **Status:** ❌ Not Implemented | 📝 Documented
- **Description:** A dragon breed with black scales and distinctive ridges along its back
- **Habitat:** Norway, dragon reserves
- **Behavior:** Aggressive, territorial, powerful
- **Abilities:** Fire breath, ridgeback, flight
- **Taming:** Cannot be tamed
- **Lore:** Norwegian Ridgebacks are black dragons with distinctive ridges. Norbert was a Norwegian Ridgeback hatched by Hagrid

#### Peruvian Vipertooth
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A small but fast dragon breed with copper-colored scales and venomous fangs
- **Habitat:** Peru, dragon reserves
- **Behavior:** Fast, aggressive, venomous
- **Abilities:** Venomous bite, fire breath, speed
- **Taming:** Cannot be tamed. Fast and venomous dragon breed
- **Lore:** Peruvian Vipertooths are smaller dragons but are extremely fast and have venomous fangs. They are the smallest known dragon breed
- **Codebase:** Registered in ModCreatures.java, has PeruvianVipertoothEntity class

#### Romanian Longhorn
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A dark green dragon breed with long, golden horns
- **Habitat:** Romania, dragon reserves
- **Behavior:** Aggressive, territorial, powerful
- **Abilities:** Fire breath, long golden horns, flight
- **Taming:** Cannot be tamed. Extremely dangerous dragon breed
- **Lore:** Romanian Longhorns are dark green dragons known for their distinctive long, golden horns
- **Codebase:** Registered in ModCreatures.java, has RomanianLonghornEntity class

#### Swedish Short-Snout
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A silvery-blue dragon breed with a short snout and powerful fire breath
- **Habitat:** Sweden, dragon reserves
- **Behavior:** Aggressive, territorial, powerful
- **Abilities:** Fire breath, silvery-blue scales, flight
- **Taming:** Cannot be tamed. Extremely dangerous dragon breed
- **Lore:** Swedish Short-Snouts are silvery-blue dragons known for their powerful flame breath and short snouts
- **Codebase:** Registered in ModCreatures.java, has SwedishShortSnoutEntity class

#### Ukrainian Ironbelly
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A massive gray dragon breed with metallic scales and powerful fire breath
- **Habitat:** Ukraine, dragon reserves
- **Behavior:** Aggressive, territorial, extremely powerful
- **Abilities:** Fire breath, metallic scales, immense size
- **Taming:** Cannot be tamed. One of the largest and most dangerous dragon breeds
- **Lore:** Ukrainian Ironbellies are among the largest dragon breeds. They have metallic gray scales and are extremely powerful
- **Codebase:** Registered in ModCreatures.java, has UkrainianIronbellyEntity class

### Lethifold
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A dangerous, black, cloak-like creature that suffocates its victims in their sleep
- **Habitat:** Tropical regions
- **Behavior:** Nocturnal, suffocating, cloak-like
- **Abilities:** Suffocation, invisibility in darkness
- **Taming:** Cannot be tamed. Extremely dangerous. Requires Patronus charm
- **Lore:** Lethifolds are extremely dangerous creatures that suffocate victims in their sleep. They can only be repelled by the Patronus charm
- **Codebase:** Registered in ModCreatures.java, has LethifoldEntity class

### Manticore
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A beast with the head of a man, body of a lion, and tail of a scorpion
- **Habitat:** Greece, remote areas
- **Behavior:** Aggressive, dangerous, hybrid
- **Abilities:** Scorpion tail venom, human head intelligence
- **Taming:** Cannot be tamed. Extremely dangerous
- **Lore:** Manticores are extremely dangerous creatures with the head of a man, body of a lion, and tail of a scorpion
- **Codebase:** Registered in ModCreatures.java, has ManticoreEntity class

### Nundu
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A giant leopard-like creature whose breath causes disease and death
- **Habitat:** East Africa, remote areas
- **Behavior:** Extremely dangerous, disease-breathing, giant
- **Abilities:** Disease breath, immense size, strength
- **Taming:** Cannot be tamed. Extremely dangerous. Rarely defeated
- **Lore:** Nundus are among the most dangerous creatures. It takes at least 100 wizards working together to subdue one
- **Codebase:** Registered in ModCreatures.java, has NunduEntity class

### Quintaped
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A five-legged creature with a taste for human flesh
- **Habitat:** Isle of Drear, Scotland
- **Behavior:** Cannibalistic, five-legged, dangerous
- **Abilities:** Five-legged movement, human flesh preference
- **Taming:** Cannot be tamed. Extremely dangerous
- **Lore:** Quintapeds are five-legged creatures with a taste for human flesh. They are found on the Isle of Drear
- **Codebase:** Registered in ModCreatures.java, has QuintapedEntity class

### Wampus Cat
- **Status:** ❌ Not Implemented | 📝 Documented
- **Description:** A large cat-like creature native to North America
- **Habitat:** North America, magical reserves
- **Behavior:** Cat-like, dangerous, powerful
- **Abilities:** Strength, speed, cat-like abilities
- **Taming:** Cannot be tamed
- **Lore:** Large cat-like creatures native to North America, known for their power

### Werewolf
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A human who transforms into a wolf-like creature during the full moon
- **Habitat:** Various locations, during full moon
- **Behavior:** Human by day, wolf by full moon, dangerous
- **Abilities:** Transformation, enhanced senses, strength
- **Taming:** Cannot be tamed. Cursed humans
- **Lore:** Werewolves are humans cursed to transform into wolves during the full moon. Remus Lupin was a notable werewolf
- **Codebase:** Registered in ModCreatures.java, has WerewolfEntity class

---

## Spirits & Humanoids

### Dementor
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** A soul-sucking entity that requires a Patronus to defeat
- **Habitat:** Azkaban, dark places, areas of despair
- **Behavior:** Soul-sucking, despair-inducing, cold
- **Abilities:** Soul removal, despair aura, Patronus weakness
- **Taming:** Cannot be tamed. Extremely dangerous. Requires Patronus charm for defense
- **Lore:** Dementors guard Azkaban prison and feed on human happiness. They can only be repelled by the Patronus charm
- **Codebase:** Registered in ModCreatures.java, has DementorEntity class

### Ghost (Standard)
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** The spirit of a deceased wizard or witch
- **Habitat:** Various locations, often tied to specific places
- **Behavior:** Ethereal, can pass through objects, retains personality
- **Abilities:** Intangibility, flight, immortality
- **Taming:** Cannot be tamed. Spirits of the deceased
- **Lore:** Ghosts are the spirits of deceased wizards and witches who chose to remain in the mortal world
- **Codebase:** Has GhostEntity and HouseGhostEntity classes

### Giant
- **Status:** ❌ Not Implemented | 📝 Documented
- **Description:** A large humanoid creature, much larger than humans
- **Habitat:** Remote mountains, isolated areas
- **Behavior:** Large, humanoid, often misunderstood
- **Abilities:** Immense size, strength
- **Taming:** Cannot be tamed
- **Lore:** Large humanoid creatures, often living in isolation. Hagrid is half-giant

### Goblin
- **Status:** ❌ Not Implemented | 📝 Documented
- **Description:** A small, intelligent humanoid creature known for banking
- **Habitat:** Gringotts, goblin settlements
- **Behavior:** Intelligent, business-oriented, skilled in metalwork
- **Abilities:** Banking, metalwork, intelligence
- **Taming:** Cannot be tamed. Intelligent beings with their own culture
- **Lore:** Goblins run Gringotts Bank and are skilled in metalwork. They have their own culture and laws

### House Elf
- **Status:** ⚠️ Registered Only | 📝 Documented
- **Description:** A magical servant bound to wizarding families
- **Habitat:** Wizarding homes, estates
- **Behavior:** Loyal, subservient, powerful magic
- **Abilities:** Household magic, apparition, loyalty bonds
- **Taming:** Bound through ancient magic. Cannot be 'tamed' - requires proper treatment
- **Lore:** House elves are bound to serve wizarding families. They possess powerful magic but are often mistreated. Dobby was a notable exception
- **Codebase:** Registered in BestiaryCreatureRegistry.java but no entity class found

### Inferius
- **Status:** ❌ Not Implemented | 📝 Documented
- **Description:** A reanimated corpse controlled by dark magic
- **Habitat:** Dark magic locations, graveyards
- **Behavior:** Undead, controlled, dangerous
- **Abilities:** Undead, controlled by dark wizards
- **Taming:** Cannot be tamed. Created through dark magic
- **Lore:** Inferi are reanimated corpses controlled by dark wizards. They are mindless and dangerous

### Poltergeist (Peeves)
- **Status:** ❌ Not Implemented | 📝 Documented
- **Description:** A mischievous spirit that causes chaos
- **Habitat:** Hogwarts, magical locations
- **Behavior:** Mischievous, chaotic, immortal
- **Abilities:** Intangibility, chaos-causing, immortality
- **Taming:** Cannot be tamed. Immortal spirit
- **Lore:** Poltergeists are chaotic spirits. Peeves is the poltergeist of Hogwarts, known for causing mischief

### Veela
- **Status:** ✅ Implemented | 📝 Documented
- **Description:** Beautiful, semi-human magical beings who can enchant men with their dance
- **Habitat:** Bulgaria, various locations
- **Behavior:** Enchanting, beautiful, semi-human
- **Abilities:** Enchantment through dance, beauty
- **Taming:** Cannot be tamed. Must be treated with respect
- **Lore:** Veela are beautiful, semi-human beings who can enchant men with their dance. They become harpy-like when angered
- **Codebase:** Registered in ModCreatures.java, has VeelaEntity class

---

## Additional Creatures (Companions & Familiars)

### Cat
- **Status:** ✅ Implemented
- **Description:** A familiar cat companion with loyalty mechanics
- **Habitat:** Wizarding homes, magical pet shops
- **Behavior:** Independent, curious, territorial
- **Abilities:** Loyalty tracking, pest control, companionship
- **Taming:** Can be tamed with fish or cat treats. Loyalty increases with care
- **Lore:** Cats are common familiars for witches and wizards. They are known for their independence and magical sensitivity
- **Codebase:** Registered in ModCreatures.java, has CatEntity class

### Owl
- **Status:** ✅ Implemented
- **Description:** A magical owl that can deliver mail and items across great distances
- **Habitat:** Wizarding homes, Hogwarts, magical aviaries
- **Behavior:** Nocturnal, intelligent, loyal to their owners
- **Abilities:** Long-distance mail delivery, navigation, night vision
- **Taming:** Can be purchased from Eeylops Owl Emporium. Requires regular care
- **Lore:** Owls are the primary method of communication in the wizarding world. They are highly intelligent and can find recipients anywhere
- **Codebase:** Registered in ModCreatures.java, has OwlEntity class

### Toad
- **Status:** ✅ Implemented
- **Description:** A pet toad companion, often kept by students
- **Habitat:** Ponds, marshes, magical pet shops
- **Behavior:** Docile, slow-moving, amphibious
- **Abilities:** Basic companionship, moisture detection
- **Taming:** Can be purchased from magical pet shops. Requires aquatic environment
- **Lore:** Toads were once popular pets at Hogwarts, though they fell out of fashion. Neville Longbottom famously had a toad named Trevor
- **Codebase:** Registered in ModCreatures.java, has ToadEntity class

### Raven Familiar
- **Status:** ✅ Implemented
- **Description:** An intelligent bird familiar companion
- **Habitat:** Forests, magical aviaries
- **Behavior:** Intelligent, adaptable, social
- **Abilities:** Intelligence, mimicry, flight
- **Taming:** Can be tamed with food and patience. Forms strong bonds
- **Lore:** Ravens are highly intelligent birds often chosen as familiars. They can learn to speak and are very loyal
- **Codebase:** Registered in ModCreatures.java

### Rat Familiar
- **Status:** ✅ Implemented
- **Description:** A small and quick familiar companion
- **Habitat:** Urban areas, magical pet shops
- **Behavior:** Quick, intelligent, adaptable
- **Abilities:** Speed, agility, small size
- **Taming:** Easy to tame with food. Common familiar choice
- **Lore:** Rats are common familiars due to their intelligence and adaptability. However, some may be Animagi in disguise
- **Codebase:** Registered in ModCreatures.java

### Snake Familiar
- **Status:** ✅ Implemented
- **Description:** A snake familiar companion
- **Habitat:** Forests, grasslands, magical pet shops
- **Behavior:** Solitary, patient, predatory
- **Abilities:** Parseltongue communication, stealth, constriction
- **Taming:** Can be tamed by Parselmouths. Requires understanding of snake behavior
- **Lore:** Snakes are rare familiars, often associated with dark wizards. Only Parselmouths can communicate with them naturally
- **Codebase:** Registered in ModCreatures.java

### Ferret Familiar
- **Status:** ✅ Implemented
- **Description:** A playful and quick familiar companion
- **Habitat:** Forests, grasslands, magical pet shops
- **Behavior:** Playful, curious, energetic
- **Abilities:** Speed, agility, playfulness
- **Taming:** Can be tamed with play and treats. Requires active engagement
- **Lore:** Ferrets are playful familiars known for their energy and curiosity. They make excellent companions for active wizards
- **Codebase:** Registered in ModCreatures.java

---

## Implementation Status Summary

### Statistics

- **Total Creatures Documented:** ~92
- **Total Creatures in Codebase:** 77
- **Fully Implemented (✅):** ~65
- **Registered Only (⚠️):** ~5
- **Not Implemented (❌):** ~30

### By Classification

- **XX - Harmless:** 15 documented, 12 implemented, 3 missing
- **XXX - Competent:** 26 documented, 18 implemented, 8 missing
- **XXXX - Dangerous:** 24 documented, 19 implemented, 5 missing
- **XXXXX - Wizard Killer:** 19 documented, 16 implemented, 3 missing
- **Spirits & Humanoids:** 8 documented, 4 implemented, 4 missing

### Priority Missing Creatures

**High Priority:**
1. Norwegian Ridgeback Dragon (completes dragon collection)
2. Griffin (popular creature)
3. Giant, Goblin, House Elf (important humanoids)
4. Poltergeist (Peeves) (iconic Hogwarts character)

**Medium Priority:**
5. Crup, Knarl, Moke (common magical creatures)
6. Salamander, Runespoor (interesting mechanics)
7. Troll variants (Forest, Mountain, River)
8. Merpeople variants (Selkie, Siren)

**Low Priority:**
9. Auger, Chizpurfle, Flobberworm (less common)
10. Bundimun, Dugbog, Glumbumble, Mackled Malaclaw (niche)
11. Murtlap, Nogtail, Plimpy, Pogrebin (rare)
12. Shrake, Snallygaster, Tebo, Wampus Cat (regional/rare)

---

## Notes

- This bestiary combines information from:
  - `ModCreatures.java` - Creature registry
  - `BestiaryCreatureRegistry.java` - Bestiary system
  - Entity classes in `features/creatures/`
  - Documentation in `docs/volume-iv-menagerie/`

- Implementation status is based on:
  - Presence in `ModCreatures.java` registry
  - Existence of entity class files
  - Registration in bestiary system

- Some creatures may have variants or subtypes documented separately (e.g., troll types, merpeople types, dragon breeds)

- This document should be updated as new creatures are implemented

---

*Last Updated: 2025-01-26*
*Total Creatures: 92 documented, 77 in codebase*




