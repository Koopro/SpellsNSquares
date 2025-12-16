package at.koopro.spells_n_squares.features.education;

import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
import net.minecraft.resources.Identifier;

import java.util.*;

/**
 * Comprehensive registry of all magical creatures from the Wizarding World.
 * Contains detailed information about each creature including lore, behavior, and abilities.
 */
public final class BestiaryCreatureRegistry {
    private BestiaryCreatureRegistry() {
    }
    
    /**
     * Represents a creature entry in the bestiary with comprehensive information.
     */
    public static class CreatureEntry {
        private final Identifier id;
        private final String name;
        private final CreatureCategory category;
        private final String description;
        private final String habitat;
        private final String behavior;
        private final String abilities;
        private final String stats;
        private final String taming;
        private final String lore;
        private final boolean isImplemented;
        
        public CreatureEntry(Identifier id, String name, CreatureCategory category, String description,
                            String habitat, String behavior, String abilities, String stats, 
                            String taming, String lore, boolean isImplemented) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.description = description;
            this.habitat = habitat;
            this.behavior = behavior;
            this.abilities = abilities;
            this.stats = stats;
            this.taming = taming;
            this.lore = lore;
            this.isImplemented = isImplemented;
        }
        
        public Identifier getId() { return id; }
        public String getName() { return name; }
        public CreatureCategory getCategory() { return category; }
        public String getDescription() { return description; }
        public String getHabitat() { return habitat; }
        public String getBehavior() { return behavior; }
        public String getAbilities() { return abilities; }
        public String getStats() { return stats; }
        public String getTaming() { return taming; }
        public String getLore() { return lore; }
        public boolean isImplemented() { return isImplemented; }
    }
    
    /**
     * Categories of magical creatures.
     */
    public enum CreatureCategory {
        COMPANION,  // Pet companions (owls, cats, toads)
        MOUNT,      // Mountable creatures (hippogriffs, thestrals)
        HOSTILE,    // Hostile creatures (dementors, boggarts, dragons)
        NEUTRAL,    // Neutral creatures
        AQUATIC,    // Water-dwelling creatures
        SPIRITUAL   // Spiritual or ethereal creatures
    }
    
    private static final Map<Identifier, CreatureEntry> CREATURES = new LinkedHashMap<>();
    private static boolean initialized = false;
    
    /**
     * Initializes the creature registry with all Wizarding World creatures.
     * Safe to call multiple times - will only initialize once.
     */
    public static void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        // Already implemented creatures
        registerCreature("owl", "Owl", CreatureCategory.COMPANION,
            "A magical owl that can deliver mail and items across great distances.",
            "Wizarding homes, Hogwarts, magical aviaries",
            "Nocturnal, intelligent, loyal to their owners",
            "Long-distance mail delivery, navigation, night vision",
            "Small size, moderate speed, low health",
            "Can be purchased from Eeylops Owl Emporium. Requires regular care.",
            "Owls are the primary method of communication in the wizarding world. They are highly intelligent and can find recipients anywhere.",
            true);
        
        registerCreature("cat", "Cat", CreatureCategory.COMPANION,
            "A familiar cat companion with loyalty mechanics.",
            "Wizarding homes, magical pet shops",
            "Independent, curious, territorial",
            "Loyalty tracking, pest control, companionship",
            "Small size, agile, moderate health",
            "Can be tamed with fish or cat treats. Loyalty increases with care.",
            "Cats are common familiars for witches and wizards. They are known for their independence and magical sensitivity.",
            true);
        
        registerCreature("toad", "Toad", CreatureCategory.COMPANION,
            "A pet toad companion, often kept by students.",
            "Ponds, marshes, magical pet shops",
            "Docile, slow-moving, amphibious",
            "Basic companionship, moisture detection",
            "Very small, slow, low health",
            "Can be purchased from magical pet shops. Requires aquatic environment.",
            "Toads were once popular pets at Hogwarts, though they fell out of fashion. Neville Longbottom famously had a toad named Trevor.",
            true);
        
        registerCreature("niffler", "Niffler", CreatureCategory.COMPANION,
            "A treasure-hunting creature that finds valuable items.",
            "Underground burrows, treasure-rich areas",
            "Mischievous, attracted to shiny objects, burrowing",
            "Treasure detection, burrowing, item collection",
            "Small size, moderate speed, low health",
            "Can be tamed with gold or shiny items. Requires secure containment.",
            "Nifflers are attracted to anything shiny and will steal valuable items. They have pouches like marsupials for storing treasures.",
            true);
        
        registerCreature("bowtruckle", "Bowtruckle", CreatureCategory.COMPANION,
            "A small tree guardian that helps with plant growth.",
            "Wand-quality trees, forests",
            "Shy, protective of trees, twig-like appearance",
            "Plant growth enhancement, tree protection, camouflage",
            "Very small, slow, very low health",
            "Can be tamed with woodlice or fairy eggs. Must respect their tree.",
            "Bowtruckles guard trees used for wand-making. They are extremely difficult to spot and will defend their trees fiercely.",
            true);
        
        registerCreature("puffskein", "Puffskein", CreatureCategory.COMPANION,
            "A fluffy pet creature that provides comfort and regeneration.",
            "Magical pet shops, wizarding homes",
            "Docile, affectionate, content",
            "Regeneration aura, comfort, low maintenance",
            "Small size, slow, moderate health",
            "Easy to care for. Requires minimal attention and provides passive benefits.",
            "Puffskeins are popular pets due to their easy care and pleasant humming when content. They eat anything, including leftovers.",
            true);
        
        registerCreature("kneazle", "Kneazle", CreatureCategory.COMPANION,
            "An intelligent cat-like creature that can detect untrustworthy people.",
            "Wizarding homes, magical pet shops",
            "Intelligent, suspicious, loyal to owners",
            "Untrustworthy person detection, intelligence, loyalty",
            "Medium size, agile, moderate health",
            "Can be tamed with patience and respect. Requires license in some areas.",
            "Kneazles are highly intelligent and can detect suspicious or untrustworthy individuals. They have large ears and spotted fur.",
            true);
        
        registerCreature("erumpent", "Erumpent", CreatureCategory.COMPANION,
            "An explosive horned creature, dangerous but tamable.",
            "African savannas, magical reserves",
            "Aggressive when threatened, territorial, powerful",
            "Explosive horn attacks, charging, strength",
            "Large size, moderate speed, high health",
            "Extremely dangerous. Requires expert handling and respect. Not recommended for beginners.",
            "Erumpents have horns filled with explosive fluid. They are classified as XXXX by the Ministry of Magic due to their danger.",
            true);
        
        registerCreature("mooncalf", "Mooncalf", CreatureCategory.COMPANION,
            "A shy creature that appears during full moon.",
            "Meadows, clearings, moonlit areas",
            "Nocturnal, shy, dances during full moon",
            "Moonlight detection, dancing, shyness",
            "Medium size, moderate speed, moderate health",
            "Can be approached during full moon dances. Requires patience and respect.",
            "Mooncalves only emerge during full moons to perform intricate dances. They are very shy and will hide if approached carelessly.",
            true);
        
        registerCreature("phoenix", "Phoenix", CreatureCategory.COMPANION,
            "Rare companion with resurrection and healing abilities.",
            "Mountain peaks, remote areas",
            "Loyal, intelligent, immortal",
            "Resurrection, healing tears, fire immunity, teleportation",
            "Large size, fast flight, high health",
            "Extremely rare. Cannot be tamed conventionally - must earn loyalty through deeds.",
            "Phoenixes are immortal birds that burst into flames upon death and are reborn from ashes. Their tears have healing properties.",
            true);
        
        registerCreature("house_elf", "House Elf", CreatureCategory.COMPANION,
            "A magical servant bound to wizarding families.",
            "Wizarding homes, estates",
            "Loyal, subservient, powerful magic",
            "Household magic, apparition, loyalty bonds",
            "Small size, fast, moderate health",
            "Bound through ancient magic. Cannot be 'tamed' - requires proper treatment.",
            "House elves are bound to serve wizarding families. They possess powerful magic but are often mistreated. Dobby was a notable exception.",
            true);
        
        registerCreature("raven_familiar", "Raven", CreatureCategory.COMPANION,
            "An intelligent bird familiar companion.",
            "Forests, magical aviaries",
            "Intelligent, adaptable, social",
            "Intelligence, mimicry, flight",
            "Small size, fast flight, low health",
            "Can be tamed with food and patience. Forms strong bonds.",
            "Ravens are highly intelligent birds often chosen as familiars. They can learn to speak and are very loyal.",
            true);
        
        registerCreature("rat_familiar", "Rat", CreatureCategory.COMPANION,
            "A small and quick familiar companion.",
            "Urban areas, magical pet shops",
            "Quick, intelligent, adaptable",
            "Speed, agility, small size",
            "Very small, very fast, very low health",
            "Easy to tame with food. Common familiar choice.",
            "Rats are common familiars due to their intelligence and adaptability. However, some may be Animagi in disguise.",
            true);
        
        registerCreature("snake_familiar", "Snake", CreatureCategory.COMPANION,
            "A snake familiar companion.",
            "Forests, grasslands, magical pet shops",
            "Solitary, patient, predatory",
            "Parseltongue communication, stealth, constriction",
            "Small to medium size, moderate speed, moderate health",
            "Can be tamed by Parselmouths. Requires understanding of snake behavior.",
            "Snakes are rare familiars, often associated with dark wizards. Only Parselmouths can communicate with them naturally.",
            true);
        
        registerCreature("ferret_familiar", "Ferret", CreatureCategory.COMPANION,
            "A playful and quick familiar companion.",
            "Forests, grasslands, magical pet shops",
            "Playful, curious, energetic",
            "Speed, agility, playfulness",
            "Small size, very fast, low health",
            "Can be tamed with play and treats. Requires active engagement.",
            "Ferrets are playful familiars known for their energy and curiosity. They make excellent companions for active wizards.",
            true);
        
        // Mount creatures
        registerCreature("hippogriff", "Hippogriff", CreatureCategory.MOUNT,
            "A mountable flying creature that requires respect (bowing) before taming.",
            "Mountainous regions, magical reserves",
            "Proud, requires respect, territorial",
            "Flight, powerful talons, respect-based bonding",
            "Large size, fast flight, high health",
            "Must bow first to show respect. Then can be approached and mounted.",
            "Hippogriffs are proud creatures that require proper respect. Buckbeak was a notable hippogriff who played a role in Harry Potter's adventures.",
            true);
        
        registerCreature("thestral", "Thestral", CreatureCategory.MOUNT,
            "A mountable flying creature visible only to those who've seen death.",
            "Forbidden Forest, areas of death",
            "Gentle, invisible to most, intelligent",
            "Flight, invisibility to most, death perception",
            "Large size, fast flight, high health",
            "Can only be seen and tamed by those who have witnessed death. Requires understanding.",
            "Thestrals are only visible to those who have seen death and accepted it. They pull the carriages to Hogwarts.",
            true);
        
        registerCreature("occamy", "Occamy", CreatureCategory.MOUNT,
            "A shape-shifting serpent that can grow/shrink, protective of eggs.",
            "Far East, India, magical reserves",
            "Aggressive when protecting eggs, shape-shifting",
            "Size manipulation, flight, egg protection",
            "Variable size, moderate speed, moderate health",
            "Extremely dangerous. Not recommended for taming. Requires expert handling.",
            "Occamies can grow or shrink to fit available space. They are very protective of their eggs, which are made of pure silver.",
            true);
        
        registerCreature("thunderbird", "Thunderbird", CreatureCategory.MOUNT,
            "A large bird that creates storms, powerful flying mount.",
            "Arizona, North America, stormy regions",
            "Powerful, storm-creating, majestic",
            "Storm creation, flight, weather control",
            "Very large size, very fast flight, very high health",
            "Extremely rare and powerful. Requires exceptional skill and respect.",
            "Thunderbirds are native to North America and can sense danger. They create storms as they fly and are extremely powerful.",
            true);
        
        registerCreature("graphorn", "Graphorn", CreatureCategory.MOUNT,
            "A large aggressive beast, very tough and powerful mount.",
            "European mountains, magical reserves",
            "Aggressive, tough, powerful",
            "Extreme durability, strength, charging attacks",
            "Very large size, moderate speed, very high health",
            "Extremely dangerous. Requires expert handling and respect. Not for beginners.",
            "Graphorns are large, aggressive creatures with tough hides. They are difficult to subdue and make powerful mounts.",
            true);
        
        registerCreature("zouwu", "Zouwu", CreatureCategory.MOUNT,
            "A fast cat-like mount that can teleport short distances.",
            "China, magical reserves",
            "Fast, teleporting, cat-like",
            "Short-range teleportation, speed, agility",
            "Large size, very fast, high health",
            "Can be tamed with respect and proper handling. Requires understanding of their nature.",
            "Zouwu are large cat-like creatures from China that can teleport short distances. They are extremely fast and agile.",
            true);
        
        // Hostile creatures
        registerCreature("dementor", "Dementor", CreatureCategory.HOSTILE,
            "A soul-sucking entity that requires a Patronus to defeat.",
            "Azkaban, dark places, areas of despair",
            "Soul-sucking, despair-inducing, cold",
            "Soul removal, despair aura, Patronus weakness",
            "Large size, slow movement, high health",
            "Cannot be tamed. Extremely dangerous. Requires Patronus charm for defense.",
            "Dementors guard Azkaban prison and feed on human happiness. They can only be repelled by the Patronus charm.",
            true);
        
        registerCreature("boggart", "Boggart", CreatureCategory.HOSTILE,
            "A shape-shifting fear entity.",
            "Dark places, closets, under beds",
            "Shape-shifting, fear-based, hidden",
            "Shape-shifting into worst fear, fear manifestation",
            "Variable size, moderate speed, moderate health",
            "Cannot be tamed. Defeated with Riddikulus charm.",
            "Boggarts take the form of whatever a person fears most. They are defeated by turning the fear into something humorous.",
            true);
        
        registerCreature("acromantula", "Acromantula", CreatureCategory.HOSTILE,
            "A large spider enemy.",
            "Dark forests, Aragog's colony",
            "Aggressive, territorial, carnivorous",
            "Venomous bite, web-spinning, size",
            "Very large size, fast movement, high health",
            "Cannot be tamed. Extremely dangerous. Avoid their territory.",
            "Acromantulas are giant spiders capable of human speech. Aragog was a notable acromantula who lived in the Forbidden Forest.",
            true);
        
        registerCreature("dragon", "Dragon", CreatureCategory.HOSTILE,
            "A rare, powerful boss creature.",
            "Mountain ranges, dragon reserves",
            "Aggressive, territorial, powerful",
            "Fire breath, flight, immense strength",
            "Very large size, fast flight, very high health",
            "Cannot be tamed. Extremely dangerous boss creatures.",
            "Dragons are among the most dangerous magical creatures. Different breeds exist worldwide, each with unique characteristics.",
            true);
        
        registerCreature("swooping_evil", "Swooping Evil", CreatureCategory.HOSTILE,
            "A venomous flying creature for combat.",
            "Australia, magical reserves",
            "Aggressive, venomous, flying",
            "Venom extraction, flight, aggressive attacks",
            "Medium size, fast flight, moderate health",
            "Cannot be tamed. Dangerous combat creature.",
            "Swooping Evils are venomous creatures native to Australia. Their venom can erase bad memories when properly extracted.",
            true);
        
        registerCreature("basilisk", "Basilisk", CreatureCategory.HOSTILE,
            "A giant serpent with petrifying gaze, extremely dangerous boss.",
            "Chambers, underground lairs",
            "Extremely aggressive, petrifying gaze, giant",
            "Petrifying gaze, venom, immense size",
            "Giant size, moderate speed, very high health",
            "Cannot be tamed. Extremely dangerous boss. Requires special methods to defeat.",
            "Basilisks are giant serpents whose gaze can petrify or kill. The one in the Chamber of Secrets was over 1000 years old.",
            true);
        
        registerCreature("chimaera", "Chimaera", CreatureCategory.HOSTILE,
            "A multi-headed beast, dangerous hybrid creature.",
            "Greece, remote mountains",
            "Aggressive, multi-headed, dangerous",
            "Multiple attack points, fire breath, strength",
            "Large size, moderate speed, high health",
            "Cannot be tamed. Extremely dangerous hybrid creature.",
            "Chimaeras are rare Greek monsters with the head of a lion, body of a goat, and tail of a dragon. They are extremely dangerous.",
            true);
        
        // Additional creatures from Wizarding World lore (not yet implemented)
        registerCreature("ashwinder", "Ashwinder", CreatureCategory.HOSTILE,
            "A serpent that arises from magical fires left burning too long.",
            "Areas with magical fires",
            "Destructive, fire-based, temporary",
            "Fire creation, egg-laying in ashes",
            "Small size, moderate speed, low health",
            "Cannot be tamed. Prevent by extinguishing magical fires.",
            "Ashwinders are created when magical fires burn too long. They lay eggs that can burn down buildings.",
            true);
        
        registerCreature("augurey", "Augurey", CreatureCategory.COMPANION,
            "Also known as the Irish Phoenix, a greenish-black bird whose cry was once believed to foretell death.",
            "Ireland, Britain, damp areas",
            "Shy, rain-loving, mournful",
            "Rain prediction, mournful cry",
            "Small size, moderate flight, low health",
            "Can be kept as pets. Requires damp environment.",
            "Augureys were once thought to foretell death with their cry, but they actually predict rain.",
            true);
        
        registerCreature("billywig", "Billywig", CreatureCategory.NEUTRAL,
            "A sapphire-blue insect native to Australia; its sting causes giddiness and levitation.",
            "Australia, tropical areas",
            "Fast-flying, stinging, colorful",
            "Sting causes levitation and giddiness",
            "Very small size, very fast flight, very low health",
            "Cannot be tamed. Sting is used in potions.",
            "Billywig stings are used in potions. The sting causes temporary levitation and giddiness.",
            true);
        
        registerCreature("centaur", "Centaur", CreatureCategory.NEUTRAL,
            "A creature with the upper body of a human and the lower body of a horse.",
            "Forbidden Forest, magical forests",
            "Proud, intelligent, territorial",
            "Archery, divination, intelligence",
            "Large size, fast movement, high health",
            "Cannot be tamed. Must be treated with respect as equals.",
            "Centaurs are highly intelligent and proud. They live in herds and are skilled in archery and divination.",
            true);
        
        registerCreature("clabbert", "Clabbert", CreatureCategory.NEUTRAL,
            "A tree-dwelling creature resembling a cross between a monkey and a frog.",
            "Southern US states, trees",
            "Tree-dwelling, wart-covered, alert",
            "Wart flashes when danger approaches",
            "Small size, agile, low health",
            "Cannot be tamed. Warts flash to warn of danger.",
            "Clabberts have warts that flash when danger approaches. They were once kept as pets but are now protected.",
            true);
        
        registerCreature("demiguise", "Demiguise", CreatureCategory.COMPANION,
            "A peaceful, ape-like creature that can turn invisible and has precognitive abilities.",
            "Far East, magical reserves",
            "Peaceful, invisible, precognitive",
            "Invisibility, precognition, hair for invisibility cloaks",
            "Medium size, moderate speed, moderate health",
            "Can be tamed with respect. Their hair is used in invisibility cloaks.",
            "Demiguises can turn invisible and see the future. Their hair is used to make invisibility cloaks.",
            true);
        
        registerCreature("diricawl", "Diricawl", CreatureCategory.NEUTRAL,
            "A plump, flightless bird capable of vanishing and reappearing elsewhere; known to Muggles as the dodo.",
            "Mauritius, various locations",
            "Vanishing, reappearing, flightless",
            "Apparition-like vanishing",
            "Medium size, slow movement, low health",
            "Cannot be tamed. Vanishes when threatened.",
            "Diricawls can vanish and reappear elsewhere when threatened. Muggles know them as the extinct dodo.",
            true);
        
        registerCreature("doxy", "Doxy", CreatureCategory.HOSTILE,
            "A small, fairy-like creature with venomous bites.",
            "Northern Europe, cold climates",
            "Aggressive, venomous, fairy-like",
            "Venomous bite, flight",
            "Very small size, fast flight, very low health",
            "Cannot be tamed. Venomous bite requires antidote.",
            "Doxies are small, aggressive creatures with venomous bites. They are often mistaken for fairies.",
            true);
        
        registerCreature("erkling", "Erkling", CreatureCategory.HOSTILE,
            "An elf-like creature that lures children with music before eating them.",
            "Black Forest, Germany",
            "Musical, child-luring, dangerous",
            "Musical enchantment, child attraction",
            "Small size, moderate speed, moderate health",
            "Cannot be tamed. Extremely dangerous to children.",
            "Erklings lure children with music before attacking. They are native to the Black Forest in Germany.",
            true);
        
        registerCreature("fairy", "Fairy", CreatureCategory.NEUTRAL,
            "A small, humanoid creature with insect-like wings.",
            "Gardens, forests, magical areas",
            "Dim-witted, decorative, weak",
            "Flight, decorative use",
            "Very small size, moderate flight, very low health",
            "Cannot be tamed. Often used decoratively.",
            "Fairies are dim-witted creatures often used for decoration. They are not particularly intelligent.",
            true);
        
        registerCreature("fire_crab", "Fire Crab", CreatureCategory.NEUTRAL,
            "A large, turtle-like creature that shoots flames from its rear end.",
            "Fiji, volcanic islands",
            "Defensive, flame-shooting, turtle-like",
            "Flame shooting from rear, shell protection",
            "Large size, slow movement, high health",
            "Cannot be tamed. Protected species.",
            "Fire Crabs shoot flames from their rear ends for defense. They are a protected species.",
            true);
        
        registerCreature("fwooper", "Fwooper", CreatureCategory.COMPANION,
            "A brightly colored bird whose song can drive listeners insane.",
            "Africa, magical reserves",
            "Colorful, dangerous song, kept silenced",
            "Madness-inducing song",
            "Small size, moderate flight, low health",
            "Can be kept but requires silencing charm. Song causes madness.",
            "Fwoopers have beautiful but dangerous songs that drive listeners insane. They must be silenced with charms.",
            true);
        
        registerCreature("ghoul", "Ghoul", CreatureCategory.NEUTRAL,
            "A slimy, buck-toothed creature that often inhabits attics and barns.",
            "Attics, barns, wizarding homes",
            "Harmless, noisy, slimy",
            "Noise-making, harmless",
            "Medium size, slow movement, moderate health",
            "Cannot be tamed. Generally harmless but noisy.",
            "Ghouls are harmless but noisy creatures that often live in wizarding attics. They are considered nuisances.",
            true);
        
        registerCreature("gnome", "Gnome", CreatureCategory.NEUTRAL,
            "A small, mischievous creature that infests gardens.",
            "Gardens, wizarding homes",
            "Mischievous, garden-infesting, annoying",
            "Garden infestation, mischief",
            "Very small size, fast movement, very low health",
            "Cannot be tamed. Considered pests. Can be thrown.",
            "Gnomes are garden pests that can be thrown out of gardens. They are annoying but relatively harmless.",
            true);
        
        registerCreature("hippocampus", "Hippocampus", CreatureCategory.AQUATIC,
            "A sea creature with the head and front legs of a horse and the tail of a fish.",
            "Mediterranean Sea, oceans",
            "Aquatic, horse-like, fish-tailed",
            "Aquatic movement, horse-like appearance",
            "Large size, fast swimming, high health",
            "Cannot be tamed. Aquatic creature.",
            "Hippocampi are sea creatures with the front of a horse and tail of a fish. They are found in the Mediterranean.",
            true);
        
        registerCreature("horklump", "Horklump", CreatureCategory.NEUTRAL,
            "A pink, bristly creature that resembles a mushroom.",
            "Scandinavia, Northern Europe",
            "Mushroom-like, burrowing, fast reproduction",
            "Fast reproduction, burrowing",
            "Small size, stationary, very low health",
            "Cannot be tamed. Considered pests.",
            "Horklumps reproduce extremely quickly and are considered pests. They resemble pink mushrooms.",
            true);
        
        registerCreature("imp", "Imp", CreatureCategory.NEUTRAL,
            "A small, mischievous creature similar to a pixie but less dangerous.",
            "Britain, Ireland",
            "Mischievous, less dangerous than pixies",
            "Minor mischief",
            "Very small size, fast movement, very low health",
            "Cannot be tamed. Less dangerous than pixies.",
            "Imps are small, mischievous creatures found in Britain and Ireland. They are less dangerous than pixies.",
            true);
        
        registerCreature("jarvey", "Jarvey", CreatureCategory.NEUTRAL,
            "A ferret-like creature capable of human speech, though it usually speaks in rude and fast phrases.",
            "Britain, Ireland, North America",
            "Rude speech, ferret-like, fast-talking",
            "Human speech (rude), burrowing",
            "Small size, fast movement, low health",
            "Cannot be tamed. Known for rude speech.",
            "Jarveys can speak but usually do so in rude, fast phrases. They resemble large ferrets.",
            true);
        
        registerCreature("jobberknoll", "Jobberknoll", CreatureCategory.COMPANION,
            "A small, blue speckled bird that remains silent until its death, at which point it lets out a long scream consisting of all the sounds it has ever heard, in reverse order.",
            "Northern Europe, Scandinavia",
            "Silent, death-scream, blue-speckled",
            "Silence until death, reverse sound scream",
            "Very small size, moderate flight, very low health",
            "Can be kept as pets. Feathers used in memory potions.",
            "Jobberknolls remain silent their entire lives, then scream all heard sounds in reverse upon death. Their feathers are used in memory potions.",
            true);
        
        registerCreature("kappa", "Kappa", CreatureCategory.AQUATIC,
            "A water-dwelling creature from Japan that resembles a monkey with fish-like scales.",
            "Japan, rivers, ponds",
            "Water-dwelling, dangerous, monkey-like",
            "Water manipulation, strength",
            "Medium size, fast swimming, moderate health",
            "Cannot be tamed. Dangerous water creature.",
            "Kappas are water-dwelling creatures from Japan. They are dangerous and can be tricked by bowing.",
            true);
        
        registerCreature("kelpie", "Kelpie", CreatureCategory.AQUATIC,
            "A shape-shifting water demon that often appears as a horse.",
            "Scotland, Ireland, bodies of water",
            "Shape-shifting, water-dwelling, dangerous",
            "Shape-shifting, water manipulation, drowning",
            "Large size, fast movement, high health",
            "Cannot be tamed. Extremely dangerous water demon.",
            "Kelpies are shape-shifting water demons that appear as horses to lure victims into water to drown them.",
            true);
        
        registerCreature("leprechaun", "Leprechaun", CreatureCategory.NEUTRAL,
            "A small, mischievous creature known for its love of gold.",
            "Ireland, magical areas",
            "Mischievous, gold-loving, small",
            "Gold creation (temporary), mischief",
            "Small size, fast movement, low health",
            "Cannot be tamed. Known for gold and mischief.",
            "Leprechauns are small Irish creatures known for their love of gold. However, leprechaun gold disappears after a few hours.",
            true);
        
        registerCreature("lethifold", "Lethifold", CreatureCategory.HOSTILE,
            "A dangerous, black, cloak-like creature that suffocates its victims in their sleep.",
            "Tropical regions",
            "Nocturnal, suffocating, cloak-like",
            "Suffocation, invisibility in darkness",
            "Variable size, moderate speed, moderate health",
            "Cannot be tamed. Extremely dangerous. Requires Patronus charm.",
            "Lethifolds are extremely dangerous creatures that suffocate victims in their sleep. They can only be repelled by the Patronus charm.",
            true);
        
        registerCreature("manticore", "Manticore", CreatureCategory.HOSTILE,
            "A beast with the head of a man, body of a lion, and tail of a scorpion.",
            "Greece, remote areas",
            "Aggressive, dangerous, hybrid",
            "Scorpion tail venom, human head intelligence",
            "Large size, fast movement, high health",
            "Cannot be tamed. Extremely dangerous.",
            "Manticores are extremely dangerous creatures with the head of a man, body of a lion, and tail of a scorpion.",
            true);
        
        registerCreature("merpeople", "Merpeople", CreatureCategory.AQUATIC,
            "Aquatic beings with the upper body of a human and the tail of a fish.",
            "Oceans, lakes, underwater",
            "Aquatic, intelligent, territorial",
            "Aquatic movement, intelligence, water magic",
            "Medium to large size, fast swimming, moderate health",
            "Cannot be tamed. Must be treated with respect.",
            "Merpeople are intelligent aquatic beings. They have their own language and culture, as seen in the Triwizard Tournament.",
            true);
        
        registerCreature("nundu", "Nundu", CreatureCategory.HOSTILE,
            "A giant leopard-like creature whose breath causes disease and death.",
            "East Africa, remote areas",
            "Extremely dangerous, disease-breathing, giant",
            "Disease breath, immense size, strength",
            "Giant size, fast movement, very high health",
            "Cannot be tamed. Extremely dangerous. Rarely defeated.",
            "Nundus are among the most dangerous creatures. It takes at least 100 wizards working together to subdue one.",
            true);
        
        registerCreature("pixie", "Pixie", CreatureCategory.HOSTILE,
            "A small, mischievous creature known for its pranks.",
            "Cornwall, Britain",
            "Mischievous, prank-loving, blue",
            "Flight, mischief, pranks",
            "Very small size, fast flight, very low health",
            "Cannot be tamed. Known for causing trouble.",
            "Pixies are small, blue, mischievous creatures known for their pranks. Gilderoy Lockhart released them in class.",
            true);
        
        registerCreature("quintaped", "Quintaped", CreatureCategory.HOSTILE,
            "A five-legged creature with a taste for human flesh.",
            "Isle of Drear, Scotland",
            "Cannibalistic, five-legged, dangerous",
            "Five-legged movement, human flesh preference",
            "Large size, moderate speed, high health",
            "Cannot be tamed. Extremely dangerous.",
            "Quintapeds are five-legged creatures with a taste for human flesh. They are found on the Isle of Drear.",
            true);
        
        registerCreature("ramora", "Ramora", CreatureCategory.AQUATIC,
            "A silver fish native to the Indian Ocean, known for its power to anchor ships.",
            "Indian Ocean, tropical waters",
            "Ship-anchoring, silver, fish-like",
            "Ship anchoring, water magic",
            "Medium size, fast swimming, moderate health",
            "Cannot be tamed. Protects ships.",
            "Ramoras are silver fish that can anchor ships. They are protective of vessels and their crews.",
            true);
        
        registerCreature("red_cap", "Red Cap", CreatureCategory.HOSTILE,
            "A dwarf-like creature that lurks in places where blood has been shed.",
            "Battlefields, areas of violence",
            "Violent, blood-seeking, dwarf-like",
            "Violence, blood-seeking",
            "Small size, fast movement, moderate health",
            "Cannot be tamed. Dangerous in areas of violence.",
            "Red Caps are dwarf-like creatures that lurk where blood has been shed. They are violent and dangerous.",
            true);
        
        registerCreature("reem", "Re'em", CreatureCategory.NEUTRAL,
            "A giant oxen with golden hides, whose blood grants immense strength.",
            "North America, Far East",
            "Rare, powerful, golden",
            "Strength-granting blood, immense size",
            "Very large size, moderate speed, very high health",
            "Cannot be tamed. Extremely rare.",
            "Re'ems are giant oxen with golden hides. Their blood grants immense strength but is extremely rare.",
            true);
        
        registerCreature("sphinx", "Sphinx", CreatureCategory.NEUTRAL,
            "A creature with the head of a human and the body of a lion, known for posing riddles.",
            "Egypt, remote areas",
            "Riddle-posing, intelligent, protective",
            "Riddle posing, intelligence, protection",
            "Large size, moderate speed, high health",
            "Cannot be tamed. Must answer riddles correctly.",
            "Sphinxes pose riddles to those who approach. They are intelligent and protective of their territory.",
            true);
        
        registerCreature("streeler", "Streeler", CreatureCategory.NEUTRAL,
            "A giant snail that changes color hourly and leaves a poisonous trail.",
            "Africa, tropical regions",
            "Color-changing, poisonous, slow",
            "Color change, poisonous trail",
            "Large size, very slow movement, moderate health",
            "Cannot be tamed. Poisonous trail is dangerous.",
            "Streelers are giant snails that change color every hour. Their trail is highly poisonous.",
            true);
        
        registerCreature("troll", "Troll", CreatureCategory.HOSTILE,
            "A large, dim-witted creature known for its immense strength.",
            "Mountains, remote areas",
            "Dim-witted, strong, aggressive",
            "Immense strength, durability",
            "Very large size, slow movement, very high health",
            "Cannot be tamed. Extremely strong but dim-witted.",
            "Trolls are large, dim-witted creatures with immense strength. They are dangerous but not particularly intelligent.",
            true);
        
        registerCreature("unicorn", "Unicorn", CreatureCategory.NEUTRAL,
            "A pure-white, horse-like creature with a single horn on its forehead.",
            "Forests, magical areas",
            "Pure, gentle, horned",
            "Horn has magical properties, purity",
            "Large size, fast movement, moderate health",
            "Cannot be tamed. Prefers female wizards.",
            "Unicorns are pure creatures whose blood can sustain life. They prefer female wizards and are symbols of purity.",
            true);
        
        registerCreature("veela", "Veela", CreatureCategory.SPIRITUAL,
            "Beautiful, semi-human magical beings who can enchant men with their dance.",
            "Bulgaria, various locations",
            "Enchanting, beautiful, semi-human",
            "Enchantment through dance, beauty",
            "Medium size, moderate speed, moderate health",
            "Cannot be tamed. Must be treated with respect.",
            "Veela are beautiful, semi-human beings who can enchant men with their dance. They become harpy-like when angered.",
            true);
        
        registerCreature("werewolf", "Werewolf", CreatureCategory.HOSTILE,
            "A human who transforms into a wolf-like creature during the full moon.",
            "Various locations, during full moon",
            "Human by day, wolf by full moon, dangerous",
            "Transformation, enhanced senses, strength",
            "Large size, fast movement, high health",
            "Cannot be tamed. Cursed humans.",
            "Werewolves are humans cursed to transform into wolves during the full moon. Remus Lupin was a notable werewolf.",
            true);
        
        registerCreature("yeti", "Yeti", CreatureCategory.NEUTRAL,
            "Also known as the Abominable Snowman, a giant humanoid creature native to the Himalayas.",
            "Himalayas, cold mountain regions",
            "Giant, humanoid, cold-dwelling",
            "Size, cold resistance, strength",
            "Very large size, moderate speed, very high health",
            "Cannot be tamed. Extremely rare.",
            "Yetis are giant humanoid creatures native to the Himalayas. They are extremely rare and elusive.",
            true);
        
        // Additional creatures from official canon to reach ~75 total
        registerCreature("grindylow", "Grindylow", CreatureCategory.AQUATIC,
            "A pale-green water demon with long, thin fingers and sharp horns.",
            "Lakes, ponds, Britain and Ireland",
            "Aggressive, water-dwelling, dangerous",
            "Drowning attacks, water manipulation, sharp claws",
            "Small size, fast swimming, moderate health",
            "Cannot be tamed. Dangerous water demon.",
            "Grindylows are aggressive water demons found in British and Irish lakes. They attack with their long fingers and attempt to drown victims.",
            true);
        
        registerCreature("hungarian_horntail", "Hungarian Horntail", CreatureCategory.HOSTILE,
            "A dragon breed with black scales, bronze horns, and a spiked tail.",
            "Hungary, dragon reserves",
            "Aggressive, territorial, powerful",
            "Fire breath, horn attacks, tail spikes",
            "Very large size, fast flight, very high health",
            "Cannot be tamed. Extremely dangerous dragon breed.",
            "Hungarian Horntails are among the most dangerous dragon breeds. They were featured in the Triwizard Tournament.",
            true);
        
        registerCreature("chinese_fireball", "Chinese Fireball", CreatureCategory.HOSTILE,
            "A dragon breed with smooth red scales and golden spikes around its face.",
            "China, dragon reserves",
            "Aggressive, territorial, powerful",
            "Fire breath, golden spikes, flight",
            "Very large size, fast flight, very high health",
            "Cannot be tamed. Extremely dangerous dragon breed.",
            "Chinese Fireballs are red dragons native to China. They are known for their distinctive golden facial spikes.",
            true);
        
        registerCreature("swedish_short_snout", "Swedish Short-Snout", CreatureCategory.HOSTILE,
            "A silvery-blue dragon breed with a short snout and powerful fire breath.",
            "Sweden, dragon reserves",
            "Aggressive, territorial, powerful",
            "Fire breath, silvery-blue scales, flight",
            "Very large size, fast flight, very high health",
            "Cannot be tamed. Extremely dangerous dragon breed.",
            "Swedish Short-Snouts are silvery-blue dragons known for their powerful flame breath and short snouts.",
            true);
        
        registerCreature("common_welsh_green", "Common Welsh Green", CreatureCategory.HOSTILE,
            "A green dragon breed that is less aggressive than other breeds.",
            "Wales, dragon reserves",
            "Less aggressive, territorial, powerful",
            "Fire breath, green scales, flight",
            "Very large size, fast flight, very high health",
            "Cannot be tamed. Dangerous but less aggressive than other breeds.",
            "Common Welsh Greens are green dragons native to Wales. They are known for being less aggressive than other dragon breeds.",
            true);
        
        registerCreature("hebridean_black", "Hebridean Black", CreatureCategory.HOSTILE,
            "A black dragon breed native to the Hebrides islands.",
            "Hebrides, Scotland, dragon reserves",
            "Aggressive, territorial, powerful",
            "Fire breath, black scales, flight",
            "Very large size, fast flight, very high health",
            "Cannot be tamed. Extremely dangerous dragon breed.",
            "Hebridean Blacks are native to the Hebrides islands off Scotland. They are aggressive and territorial.",
            true);
        
        registerCreature("peruvian_vipertooth", "Peruvian Vipertooth", CreatureCategory.HOSTILE,
            "A small but fast dragon breed with copper-colored scales and venomous fangs.",
            "Peru, dragon reserves",
            "Fast, aggressive, venomous",
            "Venomous bite, fire breath, speed",
            "Medium size, very fast flight, high health",
            "Cannot be tamed. Fast and venomous dragon breed.",
            "Peruvian Vipertooths are smaller dragons but are extremely fast and have venomous fangs. They are the smallest known dragon breed.",
            true);
        
        registerCreature("romanian_longhorn", "Romanian Longhorn", CreatureCategory.HOSTILE,
            "A dark green dragon breed with long, golden horns.",
            "Romania, dragon reserves",
            "Aggressive, territorial, powerful",
            "Fire breath, long golden horns, flight",
            "Very large size, fast flight, very high health",
            "Cannot be tamed. Extremely dangerous dragon breed.",
            "Romanian Longhorns are dark green dragons known for their distinctive long, golden horns.",
            true);
        
        registerCreature("ukrainian_ironbelly", "Ukrainian Ironbelly", CreatureCategory.HOSTILE,
            "A massive gray dragon breed with metallic scales and powerful fire breath.",
            "Ukraine, dragon reserves",
            "Aggressive, territorial, extremely powerful",
            "Fire breath, metallic scales, immense size",
            "Giant size, fast flight, extremely high health",
            "Cannot be tamed. One of the largest and most dangerous dragon breeds.",
            "Ukrainian Ironbellies are among the largest dragon breeds. They have metallic gray scales and are extremely powerful.",
            true);
    }
    
    /**
     * Registers a creature in the bestiary.
     */
    private static void registerCreature(String path, String name, CreatureCategory category,
                                        String description, String habitat, String behavior,
                                        String abilities, String stats, String taming,
                                        String lore, boolean isImplemented) {
        Identifier id = ModIdentifierHelper.modId(path);
        CreatureEntry entry = new CreatureEntry(id, name, category, description, habitat,
            behavior, abilities, stats, taming, lore, isImplemented);
        CREATURES.put(id, entry);
    }
    
    /**
     * Gets a creature entry by ID.
     */
    public static CreatureEntry getCreature(Identifier id) {
        return CREATURES.get(id);
    }
    
    /**
     * Gets all creature entries.
     */
    public static Collection<CreatureEntry> getAllCreatures() {
        return CREATURES.values();
    }
    
    /**
     * Gets creatures by category.
     */
    public static List<CreatureEntry> getCreaturesByCategory(CreatureCategory category) {
        List<CreatureEntry> result = new ArrayList<>();
        for (CreatureEntry entry : CREATURES.values()) {
            if (entry.getCategory() == category) {
                result.add(entry);
            }
        }
        return result;
    }
    
    /**
     * Gets all creature IDs.
     */
    public static Set<Identifier> getAllCreatureIds() {
        return CREATURES.keySet();
    }
    
    /**
     * Finds creatures by name (case-insensitive partial match).
     */
    public static List<CreatureEntry> searchCreatures(String query) {
        List<CreatureEntry> result = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        for (CreatureEntry entry : CREATURES.values()) {
            if (entry.getName().toLowerCase().contains(lowerQuery) ||
                entry.getDescription().toLowerCase().contains(lowerQuery)) {
                result.add(entry);
            }
        }
        return result;
    }
    
    /**
     * Maps entity types to creature IDs for discovery.
     */
    public static Identifier getCreatureIdFromEntityType(net.minecraft.world.entity.EntityType<?> entityType) {
        String entityPath = entityType.toShortString();
        // Extract the entity name from the string representation
        if (entityPath.contains(":")) {
            String[] parts = entityPath.split(":");
            if (parts.length >= 2) {
                String name = parts[1];
                // Map common entity names to creature IDs
                Identifier id = ModIdentifierHelper.modId(name);
                if (CREATURES.containsKey(id)) {
                    return id;
                }
            }
        }
        return null;
    }
}



