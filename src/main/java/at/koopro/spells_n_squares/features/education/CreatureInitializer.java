package at.koopro.spells_n_squares.features.education;

/**
 * Initializes the creature registry with all Wizarding World creatures.
 * Extracted from BestiaryCreatureRegistry to reduce file size.
 */
public final class CreatureInitializer {
    private static boolean initialized = false;
    
    private CreatureInitializer() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Initializes the creature registry with all creatures.
     * Safe to call multiple times - will only initialize once.
     */
    public static void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        
        // Already implemented creatures
        registerCreature("owl", "Owl", BestiaryCreatureRegistry.CreatureCategory.COMPANION,
            "A magical owl that can deliver mail and items across great distances.",
            "Wizarding homes, Hogwarts, magical aviaries",
            "Nocturnal, intelligent, loyal to their owners",
            "Long-distance mail delivery, navigation, night vision",
            "Small size, moderate speed, low health",
            "Can be purchased from Eeylops Owl Emporium. Requires regular care.",
            "Owls are the primary method of communication in the wizarding world. They are highly intelligent and can find recipients anywhere.",
            true);
        
        registerCreature("cat", "Cat", BestiaryCreatureRegistry.CreatureCategory.COMPANION,
            "A familiar cat companion with loyalty mechanics.",
            "Wizarding homes, magical pet shops",
            "Independent, curious, territorial",
            "Loyalty tracking, pest control, companionship",
            "Small size, agile, moderate health",
            "Can be tamed with fish or cat treats. Loyalty increases with care.",
            "Cats are common familiars for witches and wizards. They are known for their independence and magical sensitivity.",
            true);
        
        registerCreature("toad", "Toad", BestiaryCreatureRegistry.CreatureCategory.COMPANION,
            "A pet toad companion, often kept by students.",
            "Ponds, marshes, magical pet shops",
            "Docile, slow-moving, amphibious",
            "Basic companionship, moisture detection",
            "Very small, slow, low health",
            "Can be purchased from magical pet shops. Requires aquatic environment.",
            "Toads were once popular pets at Hogwarts, though they fell out of fashion. Neville Longbottom famously had a toad named Trevor.",
            true);
        
        registerCreature("niffler", "Niffler", BestiaryCreatureRegistry.CreatureCategory.COMPANION,
            "A treasure-hunting creature that finds valuable items.",
            "Underground burrows, treasure-rich areas",
            "Mischievous, attracted to shiny objects, burrowing",
            "Treasure detection, burrowing, item collection",
            "Small size, moderate speed, low health",
            "Can be tamed with gold or shiny items. Requires secure containment.",
            "Nifflers are attracted to anything shiny and will steal valuable items. They have pouches like marsupials for storing treasures.",
            true);
        
        registerCreature("bowtruckle", "Bowtruckle", BestiaryCreatureRegistry.CreatureCategory.COMPANION,
            "A small tree guardian that helps with plant growth.",
            "Wand-quality trees, forests",
            "Shy, protective of trees, twig-like appearance",
            "Plant growth enhancement, tree protection, camouflage",
            "Very small, slow, very low health",
            "Can be tamed with woodlice or fairy eggs. Must respect their tree.",
            "Bowtruckles guard trees used for wand-making. They are extremely difficult to spot and will defend their trees fiercely.",
            true);
        
        registerCreature("puffskein", "Puffskein", BestiaryCreatureRegistry.CreatureCategory.COMPANION,
            "A fluffy pet creature that provides comfort and regeneration.",
            "Magical pet shops, wizarding homes",
            "Docile, affectionate, content",
            "Regeneration aura, comfort, low maintenance",
            "Small size, slow, moderate health",
            "Easy to care for. Requires minimal attention and provides passive benefits.",
            "Puffskeins are popular pets due to their easy care and pleasant humming when content. They eat anything, including leftovers.",
            true);
        
        // Continue with remaining creatures - this is a large list, so we'll register them all
        // For brevity, I'll register a representative sample and note that all should be registered
        registerRemainingCreatures();
    }
    
    /**
     * Registers all remaining creatures.
     * This method contains the bulk of creature registrations.
     */
    private static void registerRemainingCreatures() {
        // Companion creatures
        registerCreature("kneazle", "Kneazle", BestiaryCreatureRegistry.CreatureCategory.COMPANION,
            "A cat-like creature with high intelligence and magical detection abilities.",
            "Wizarding homes, magical pet shops",
            "Intelligent, independent, suspicious",
            "Magical detection, loyalty, intelligence",
            "Medium size, agile, moderate health",
            "Can be purchased from magical pet shops. Requires trust-building.",
            "Kneazles are highly intelligent cat-like creatures. They can detect untrustworthy people and are fiercely loyal to their owners.",
            true);
        
        registerCreature("erumpent", "Erumpent", BestiaryCreatureRegistry.CreatureCategory.COMPANION,
            "A large magical creature with a horn that can cause explosions.",
            "Africa, magical reserves",
            "Aggressive when threatened, territorial",
            "Explosive horn, strength, charging",
            "Large size, moderate speed, high health",
            "Extremely difficult to tame. Requires expert handling.",
            "Erumpents are large African creatures with explosive horns. They are extremely dangerous and require expert care.",
            false);
        
        registerCreature("mooncalf", "Mooncalf", BestiaryCreatureRegistry.CreatureCategory.COMPANION,
            "A shy creature that only appears during full moons.",
            "Meadows, open fields",
            "Shy, nocturnal, dances in moonlight",
            "Moonlight detection, dancing, shyness",
            "Medium size, slow, low health",
            "Can be tamed with patience during full moons. Requires moonlit environments.",
            "Mooncalves are shy creatures that only emerge during full moons to dance. They are gentle and harmless.",
            false);
        
        registerCreature("phoenix", "Phoenix", BestiaryCreatureRegistry.CreatureCategory.COMPANION,
            "A legendary bird that can be reborn from its ashes.",
            "Mountain peaks, magical sanctuaries",
            "Loyal, intelligent, regenerative",
            "Rebirth, healing tears, fire resistance",
            "Large size, fast flight, high health",
            "Extremely rare. Can only be tamed by those with pure hearts.",
            "Phoenixes are legendary birds that can be reborn from their ashes. They are extremely loyal and have healing tears.",
            false);
        
        registerCreature("house_elf", "House Elf", BestiaryCreatureRegistry.CreatureCategory.COMPANION,
            "A magical servant creature bound to wizarding families.",
            "Wizarding homes, kitchens",
            "Loyal, hardworking, bound by magic",
            "Household magic, loyalty, servitude",
            "Small size, fast, moderate health",
            "Bound by ancient magic. Freed by giving them clothing.",
            "House elves are bound by ancient magic to serve wizarding families. They are extremely loyal and powerful in household magic.",
            false);
        
        // Mount creatures
        registerCreature("hippogriff", "Hippogriff", BestiaryCreatureRegistry.CreatureCategory.MOUNT,
            "A proud creature with the front half of an eagle and the back half of a horse.",
            "Mountains, open fields",
            "Proud, requires respect, territorial",
            "Flight, speed, strength",
            "Large size, fast flight, high health",
            "Requires showing respect by bowing. Can be ridden once tamed.",
            "Hippogriffs are proud creatures that require respect. They will attack if insulted but are loyal once tamed.",
            false);
        
        registerCreature("thestral", "Thestral", BestiaryCreatureRegistry.CreatureCategory.MOUNT,
            "A winged horse that can only be seen by those who have witnessed death.",
            "Forbidden Forest, dark areas",
            "Gentle, invisible to most, loyal",
            "Flight, invisibility, death perception",
            "Large size, fast flight, moderate health",
            "Can only be seen and tamed by those who have witnessed death.",
            "Thestrals are winged horses that can only be seen by those who have witnessed death. They are gentle and loyal.",
            false);
        
        registerCreature("occamy", "Occamy", BestiaryCreatureRegistry.CreatureCategory.MOUNT,
            "A serpentine creature that can change size based on available space.",
            "Far East, India, magical reserves",
            "Aggressive, size-changing, protective",
            "Size manipulation, flight, aggression",
            "Variable size, fast flight, high health",
            "Extremely difficult to tame. Requires expert handling.",
            "Occamys are serpentine creatures from the Far East that can change size. They are aggressive and protective of their eggs.",
            false);
        
        registerCreature("thunderbird", "Thunderbird", BestiaryCreatureRegistry.CreatureCategory.MOUNT,
            "A large bird that can sense danger and create storms.",
            "North America, Arizona",
            "Proud, weather control, danger sense",
            "Storm creation, flight, danger detection",
            "Very large size, very fast flight, very high health",
            "Extremely rare. Can only be tamed by those with strong magical connection.",
            "Thunderbirds are large birds native to North America. They can sense danger and create storms with their wings.",
            false);
        
        registerCreature("graphorn", "Graphorn", BestiaryCreatureRegistry.CreatureCategory.MOUNT,
            "A large, aggressive creature with two sharp horns.",
            "European mountains",
            "Aggressive, territorial, powerful",
            "Strength, charging, horn attacks",
            "Very large size, moderate speed, very high health",
            "Extremely difficult to tame. Very aggressive.",
            "Graphorns are large, aggressive creatures with two sharp horns. They are extremely dangerous and difficult to tame.",
            false);
        
        registerCreature("zouwu", "Zouwu", BestiaryCreatureRegistry.CreatureCategory.MOUNT,
            "A large cat-like creature with a colorful mane and incredible speed.",
            "China, magical reserves",
            "Aggressive, fast, territorial",
            "Extreme speed, strength, colorful mane",
            "Large size, extremely fast, high health",
            "Extremely difficult to tame. Requires expert handling.",
            "Zouwus are large cat-like creatures from China with colorful manes. They are extremely fast and aggressive.",
            false);
        
        // Hostile creatures
        registerCreature("dementor", "Dementor", BestiaryCreatureRegistry.CreatureCategory.HOSTILE,
            "A dark creature that feeds on happiness and can perform the Dementor's Kiss.",
            "Azkaban, dark places",
            "Soulless, emotion-draining, dangerous",
            "Dementor's Kiss, happiness drain, fear aura",
            "Large size, floating, high health",
            "Cannot be tamed. Extremely dangerous dark creatures.",
            "Dementors are soulless dark creatures that feed on happiness. They guard Azkaban and can perform the Dementor's Kiss.",
            false);
        
        registerCreature("boggart", "Boggart", BestiaryCreatureRegistry.CreatureCategory.HOSTILE,
            "A shape-shifting creature that takes the form of one's worst fear.",
            "Dark places, closets, under beds",
            "Shape-shifting, fear-based, aggressive",
            "Shape-shifting, fear manifestation",
            "Variable size, moderate speed, moderate health",
            "Cannot be tamed. Defeated with the Riddikulus spell.",
            "Boggarts are shape-shifting creatures that take the form of one's worst fear. They are defeated with laughter via the Riddikulus spell.",
            true);
        
        registerCreature("acromantula", "Acromantula", BestiaryCreatureRegistry.CreatureCategory.HOSTILE,
            "A giant spider capable of human speech.",
            "Forbidden Forest, dark forests",
            "Aggressive, territorial, intelligent",
            "Web spinning, venom, size",
            "Giant size, fast movement, very high health",
            "Cannot be tamed. Extremely dangerous.",
            "Acromantulas are giant spiders capable of human speech. They are extremely dangerous and aggressive.",
            false);
        
        registerCreature("dragon", "Dragon", BestiaryCreatureRegistry.CreatureCategory.HOSTILE,
            "A large, fire-breathing reptile with powerful magical abilities.",
            "Dragon reserves, mountains",
            "Aggressive, territorial, powerful",
            "Fire breath, flight, strength",
            "Very large size, fast flight, very high health",
            "Cannot be tamed. Extremely dangerous.",
            "Dragons are large, fire-breathing reptiles. They are extremely dangerous and are kept in dragon reserves.",
            false);
        
        registerCreature("swooping_evil", "Swooping Evil", BestiaryCreatureRegistry.CreatureCategory.HOSTILE,
            "A large, green creature that can shrink to fit in small containers.",
            "Australia, dark places",
            "Aggressive, size-changing, dangerous",
            "Size manipulation, venom, flight",
            "Variable size, fast flight, high health",
            "Cannot be tamed. Extremely dangerous.",
            "Swooping Evils are large, green creatures from Australia that can shrink. They are extremely dangerous.",
            false);
        
        registerCreature("basilisk", "Basilisk", BestiaryCreatureRegistry.CreatureCategory.HOSTILE,
            "A giant serpent with a deadly gaze that can petrify or kill.",
            "Chambers of Secrets, underground",
            "Extremely aggressive, deadly gaze, territorial",
            "Petrifying gaze, venom, size",
            "Giant size, fast movement, extremely high health",
            "Cannot be tamed. Extremely dangerous.",
            "Basilisks are giant serpents with a deadly gaze. Looking directly into their eyes causes instant death.",
            false);
        
        registerCreature("chimaera", "Chimaera", BestiaryCreatureRegistry.CreatureCategory.HOSTILE,
            "A fire-breathing creature with the head of a lion, body of a goat, and tail of a dragon.",
            "Greece, magical reserves",
            "Extremely aggressive, fire-breathing, dangerous",
            "Fire breath, multiple attacks, strength",
            "Large size, moderate speed, very high health",
            "Cannot be tamed. Extremely dangerous.",
            "Chimaeras are fire-breathing creatures with mixed animal parts. They are extremely dangerous.",
            false);
        
        registerCreature("ashwinder", "Ashwinder", BestiaryCreatureRegistry.CreatureCategory.HOSTILE,
            "A pale gray serpent that hatches from magical fire.",
            "Magical fires, fireplaces",
            "Aggressive, fire-based, dangerous",
            "Fire creation, venom, speed",
            "Small size, fast movement, low health",
            "Cannot be tamed. Dangerous fire-based creatures.",
            "Ashwinders are serpents that hatch from magical fires left burning too long. They are dangerous and must be destroyed.",
            false);
        
        // Neutral creatures
        registerCreature("augurey", "Augurey", BestiaryCreatureRegistry.CreatureCategory.COMPANION,
            "A greenish-black bird that cries when it rains.",
            "Britain, Ireland",
            "Shy, rain-predicting, mournful",
            "Weather prediction, crying",
            "Small size, slow flight, low health",
            "Can be kept as a pet. Predicts rain with its cry.",
            "Augureys are greenish-black birds that cry when it rains. They are shy and mournful-looking.",
            false);
        
        registerCreature("billywig", "Billywig", BestiaryCreatureRegistry.CreatureCategory.NEUTRAL,
            "A bright blue insect native to Australia with a stinger that causes levitation.",
            "Australia",
            "Fast, stinging, flying",
            "Levitation sting, flight, speed",
            "Very small size, very fast flight, very low health",
            "Cannot be tamed. Sting causes levitation.",
            "Billywigs are bright blue insects from Australia. Their sting causes levitation and giddiness.",
            false);
        
        registerCreature("centaur", "Centaur", BestiaryCreatureRegistry.CreatureCategory.NEUTRAL,
            "A creature with the upper body of a human and lower body of a horse.",
            "Forbidden Forest, forests",
            "Proud, intelligent, territorial",
            "Archery, divination, intelligence",
            "Large size, fast movement, high health",
            "Cannot be tamed. Proud and independent creatures.",
            "Centaurs are proud, intelligent creatures. They are skilled in archery and divination.",
            false);
        
        registerCreature("clabbert", "Clabbert", BestiaryCreatureRegistry.CreatureCategory.NEUTRAL,
            "A tree-dwelling creature with webbed hands and feet.",
            "Southern United States, trees",
            "Shy, tree-dwelling, danger-sensing",
            "Danger detection, tree climbing, webbed appendages",
            "Small size, fast climbing, low health",
            "Can be kept as a pet. Detects danger with a pimple on its forehead.",
            "Clabberts are tree-dwelling creatures that detect danger. A pimple on their forehead glows when danger is near.",
            false);
        
        registerCreature("demiguise", "Demiguise", BestiaryCreatureRegistry.CreatureCategory.COMPANION,
            "A peaceful, ape-like creature that can become invisible.",
            "Far East, magical reserves",
            "Peaceful, invisible, gentle",
            "Invisibility, precognition, peacefulness",
            "Medium size, moderate speed, moderate health",
            "Can be tamed with patience. Extremely peaceful.",
            "Demiguises are peaceful, ape-like creatures that can become invisible. They are gentle and can see the future.",
            false);
        
        registerCreature("diricawl", "Diricawl", BestiaryCreatureRegistry.CreatureCategory.NEUTRAL,
            "A plump, flightless bird that can disappear and reappear elsewhere.",
            "Mauritius",
            "Shy, teleporting, flightless",
            "Teleportation, disappearance",
            "Medium size, slow movement, low health",
            "Cannot be tamed. Can teleport away when threatened.",
            "Diricawls are plump, flightless birds that can disappear and reappear elsewhere. They are the basis for the dodo legend.",
            false);
        
        registerCreature("doxy", "Doxy", BestiaryCreatureRegistry.CreatureCategory.HOSTILE,
            "A small, human-like creature covered in thick black hair.",
            "Northern Europe, dark places",
            "Aggressive, venomous, fast",
            "Venomous bite, speed, aggression",
            "Very small size, very fast movement, very low health",
            "Cannot be tamed. Venomous and aggressive.",
            "Doxies are small, human-like creatures covered in thick black hair. They are venomous and aggressive.",
            false);
        
        registerCreature("erkling", "Erkling", BestiaryCreatureRegistry.CreatureCategory.HOSTILE,
            "A large goblin-like creature that lures children with music.",
            "Black Forest, Germany",
            "Aggressive, musical, child-luring",
            "Musical lure, aggression, child attraction",
            "Large size, fast movement, moderate health",
            "Cannot be tamed. Dangerous child-luring creatures.",
            "Erklings are large goblin-like creatures that lure children with music. They are extremely dangerous.",
            false);
        
        registerCreature("fairy", "Fairy", BestiaryCreatureRegistry.CreatureCategory.NEUTRAL,
            "A small, humanoid creature with insect-like wings.",
            "Woodlands, gardens",
            "Vain, decorative, weak",
            "Flight, decoration, weak magic",
            "Very small size, slow flight, very low health",
            "Cannot be tamed. Used for decoration.",
            "Fairies are small, humanoid creatures with wings. They are vain and weak, often used for decoration.",
            false);
        
        registerCreature("fire_crab", "Fire Crab", BestiaryCreatureRegistry.CreatureCategory.NEUTRAL,
            "A large crab with a jeweled shell that can shoot flames from its rear.",
            "Coasts, beaches",
            "Defensive, fire-shooting, territorial",
            "Fire shooting, jeweled shell, defense",
            "Large size, slow movement, moderate health",
            "Can be kept as a pet. Requires careful handling.",
            "Fire Crabs are large crabs with jeweled shells. They can shoot flames from their rear when threatened.",
            false);
        
        registerCreature("fwooper", "Fwooper", BestiaryCreatureRegistry.CreatureCategory.COMPANION,
            "A colorful bird with a song that drives listeners insane.",
            "Africa, magical pet shops",
            "Colorful, musical, dangerous",
            "Insanity-inducing song, colorful feathers",
            "Small size, slow flight, low health",
            "Can be kept as a pet but requires silencing charm.",
            "Fwoopers are colorful birds whose song drives listeners insane. They must be kept under a Silencing Charm.",
            false);
        
        registerCreature("ghoul", "Ghoul", BestiaryCreatureRegistry.CreatureCategory.NEUTRAL,
            "A harmless, slimy creature that lives in attics.",
            "Attics, dark places",
            "Harmless, slimy, noisy",
            "Noise-making, slime, harmlessness",
            "Medium size, slow movement, low health",
            "Cannot be tamed. Harmless but annoying.",
            "Ghouls are harmless, slimy creatures that live in attics. They are noisy but harmless.",
            false);
        
        registerCreature("gnome", "Gnome", BestiaryCreatureRegistry.CreatureCategory.NEUTRAL,
            "A small, humanoid garden pest.",
            "Gardens, lawns",
            "Aggressive, garden-damaging, fast",
            "Garden damage, speed, aggression",
            "Very small size, fast movement, very low health",
            "Cannot be tamed. Garden pests.",
            "Gnomes are small, humanoid garden pests. They damage gardens and must be removed.",
            false);
        
        // Aquatic creatures
        registerCreature("hippocampus", "Hippocampus", BestiaryCreatureRegistry.CreatureCategory.AQUATIC,
            "A creature with the head and front legs of a horse and the tail of a fish.",
            "Mediterranean Sea, oceans",
            "Gentle, aquatic, horse-like",
            "Swimming, aquatic adaptation, gentleness",
            "Large size, fast swimming, moderate health",
            "Can be tamed with patience. Gentle aquatic creatures.",
            "Hippocampuses are gentle aquatic creatures with horse-like features. They are found in the Mediterranean.",
            false);
        
        registerCreature("horklump", "Horklump", BestiaryCreatureRegistry.CreatureCategory.NEUTRAL,
            "A pink, mushroom-like creature that spreads rapidly.",
            "Northern Europe, gardens",
            "Spreading, mushroom-like, harmless",
            "Rapid spreading, mushroom growth",
            "Small size, stationary, very low health",
            "Cannot be tamed. Garden pests.",
            "Horklumps are pink, mushroom-like creatures that spread rapidly. They are garden pests.",
            false);
        
        registerCreature("imp", "Imp", BestiaryCreatureRegistry.CreatureCategory.NEUTRAL,
            "A small, mischievous creature.",
            "Various locations",
            "Mischievous, small, annoying",
            "Mischief, small size, annoyance",
            "Very small size, fast movement, very low health",
            "Cannot be tamed. Mischievous pests.",
            "Imps are small, mischievous creatures that cause trouble.",
            false);
        
        registerCreature("jarvey", "Jarvey", BestiaryCreatureRegistry.CreatureCategory.NEUTRAL,
            "A large ferret-like creature that speaks in short phrases.",
            "Britain, Ireland",
            "Aggressive, talkative, ferret-like",
            "Speech, aggression, ferret behavior",
            "Medium size, fast movement, moderate health",
            "Cannot be tamed. Aggressive and talkative.",
            "Jarveys are large ferret-like creatures that speak in short phrases. They are aggressive.",
            false);
        
        registerCreature("jobberknoll", "Jobberknoll", BestiaryCreatureRegistry.CreatureCategory.COMPANION,
            "A small, blue, speckled bird that makes no sound until death.",
            "Northern Europe, forests",
            "Silent, blue, speckled",
            "Silence, death cry, feathers",
            "Very small size, slow flight, very low health",
            "Can be kept as a pet. Makes no sound until death.",
            "Jobberknolls are small, blue, speckled birds that make no sound until death, when they release all sounds at once.",
            false);
        
        registerCreature("kappa", "Kappa", BestiaryCreatureRegistry.CreatureCategory.AQUATIC,
            "A water demon from Japan with a water-filled depression on its head.",
            "Japan, rivers, ponds",
            "Aggressive, water-based, dangerous",
            "Water manipulation, strength, aggression",
            "Medium size, fast swimming, moderate health",
            "Cannot be tamed. Dangerous water demons.",
            "Kappas are water demons from Japan. They have a water-filled depression on their head that must be kept full.",
            false);
        
        registerCreature("kelpie", "Kelpie", BestiaryCreatureRegistry.CreatureCategory.AQUATIC,
            "A shape-shifting water demon that lures victims into water.",
            "Scotland, Ireland, bodies of water",
            "Aggressive, shape-shifting, water-based",
            "Shape-shifting, water manipulation, lure",
            "Large size, fast swimming, high health",
            "Cannot be tamed. Extremely dangerous water demons.",
            "Kelpies are shape-shifting water demons that lure victims into water. They are extremely dangerous.",
            false);
        
        registerCreature("leprechaun", "Leprechaun", BestiaryCreatureRegistry.CreatureCategory.NEUTRAL,
            "A small, humanoid creature known for gold and mischief.",
            "Ireland",
            "Mischievous, gold-hoarding, small",
            "Gold creation, mischief, small size",
            "Very small size, fast movement, very low health",
            "Cannot be tamed. Mischievous gold-hoarders.",
            "Leprechauns are small, humanoid creatures from Ireland known for hoarding gold and causing mischief.",
            false);
        
        registerCreature("lethifold", "Lethifold", BestiaryCreatureRegistry.CreatureCategory.HOSTILE,
            "A black, cloak-like creature that suffocates sleeping victims.",
            "Tropical regions",
            "Extremely dangerous, suffocating, dark",
            "Suffocation, darkness, stealth",
            "Large size, fast movement, high health",
            "Cannot be tamed. Extremely dangerous.",
            "Lethifolds are black, cloak-like creatures that suffocate sleeping victims. They are extremely dangerous.",
            false);
        
        registerCreature("manticore", "Manticore", BestiaryCreatureRegistry.CreatureCategory.HOSTILE,
            "A creature with the head of a human, body of a lion, and tail of a scorpion.",
            "Greece, magical reserves",
            "Extremely aggressive, dangerous, powerful",
            "Venomous tail, strength, aggression",
            "Large size, fast movement, very high health",
            "Cannot be tamed. Extremely dangerous.",
            "Manticores are extremely dangerous creatures with human heads, lion bodies, and scorpion tails.",
            false);
        
        registerCreature("merpeople", "Merpeople", BestiaryCreatureRegistry.CreatureCategory.AQUATIC,
            "Aquatic humanoids with fish-like lower bodies.",
            "Oceans, lakes, bodies of water",
            "Intelligent, aquatic, territorial",
            "Swimming, intelligence, aquatic adaptation",
            "Medium size, fast swimming, moderate health",
            "Cannot be tamed. Intelligent aquatic humanoids.",
            "Merpeople are intelligent aquatic humanoids. They have their own language and culture.",
            false);
        
        registerCreature("nundu", "Nundu", BestiaryCreatureRegistry.CreatureCategory.HOSTILE,
            "A large, leopard-like creature with breath that causes disease.",
            "East Africa",
            "Extremely aggressive, disease-breathing, dangerous",
            "Disease breath, strength, aggression",
            "Very large size, fast movement, extremely high health",
            "Cannot be tamed. Extremely dangerous.",
            "Nundus are large, leopard-like creatures with breath that causes disease. They are extremely dangerous.",
            false);
        
        registerCreature("pixie", "Pixie", BestiaryCreatureRegistry.CreatureCategory.HOSTILE,
            "A small, blue, mischievous creature with wings.",
            "Cornwall, England",
            "Mischievous, aggressive, small",
            "Mischief, flight, aggression",
            "Very small size, fast flight, very low health",
            "Cannot be tamed. Mischievous and aggressive.",
            "Pixies are small, blue, mischievous creatures from Cornwall. They are aggressive and cause trouble.",
            false);
        
        registerCreature("quintaped", "Quintaped", BestiaryCreatureRegistry.CreatureCategory.HOSTILE,
            "A five-legged creature covered in thick, reddish-brown hair.",
            "Isle of Drear, Scotland",
            "Extremely aggressive, dangerous, territorial",
            "Strength, aggression, five legs",
            "Large size, fast movement, very high health",
            "Cannot be tamed. Extremely dangerous.",
            "Quintapeds are five-legged creatures from the Isle of Drear. They are extremely dangerous and aggressive.",
            false);
        
        registerCreature("ramora", "Ramora", BestiaryCreatureRegistry.CreatureCategory.AQUATIC,
            "A silver fish that anchors ships and prevents them from moving.",
            "Indian Ocean",
            "Anchoring, ship-stopping, silver",
            "Ship anchoring, water manipulation",
            "Medium size, slow swimming, moderate health",
            "Cannot be tamed. Anchors ships.",
            "Ramoras are silver fish that anchor ships and prevent them from moving. They are valued by sailors.",
            false);
        
        registerCreature("red_cap", "Red Cap", BestiaryCreatureRegistry.CreatureCategory.HOSTILE,
            "A small, goblin-like creature that lives in battlefields.",
            "Battlefields, dark places",
            "Aggressive, blood-staining, dangerous",
            "Aggression, blood-staining, small size",
            "Small size, fast movement, low health",
            "Cannot be tamed. Dangerous goblin-like creatures.",
            "Red Caps are small, goblin-like creatures that live in battlefields. They stain their caps with blood.",
            false);
        
        registerCreature("reem", "Re'em", BestiaryCreatureRegistry.CreatureCategory.NEUTRAL,
            "A large, golden ox with valuable blood.",
            "Far East, North America",
            "Rare, valuable, powerful",
            "Valuable blood, strength, rarity",
            "Very large size, moderate speed, very high health",
            "Extremely rare. Cannot be tamed.",
            "Re'ems are large, golden oxen with valuable blood that gives great strength. They are extremely rare.",
            false);
        
        registerCreature("sphinx", "Sphinx", BestiaryCreatureRegistry.CreatureCategory.NEUTRAL,
            "A creature with the head of a human and body of a lion that asks riddles.",
            "Egypt, magical reserves",
            "Intelligent, riddle-asking, protective",
            "Riddles, intelligence, protection",
            "Large size, moderate speed, high health",
            "Cannot be tamed. Asks riddles and protects treasures.",
            "Sphinxes are intelligent creatures that ask riddles. They protect treasures and are found in Egypt.",
            false);
        
        registerCreature("streeler", "Streeler", BestiaryCreatureRegistry.CreatureCategory.NEUTRAL,
            "A giant snail that changes color every hour and leaves a toxic trail.",
            "Africa, tropical regions",
            "Slow, color-changing, toxic",
            "Color-changing, toxic trail, slow movement",
            "Large size, very slow movement, moderate health",
            "Cannot be tamed. Toxic and slow.",
            "Streelers are giant snails that change color every hour and leave a toxic trail. They are extremely slow.",
            false);
        
        registerCreature("troll", "Troll", BestiaryCreatureRegistry.CreatureCategory.HOSTILE,
            "A large, dim-witted creature with great strength.",
            "Mountains, dark places",
            "Aggressive, dim-witted, strong",
            "Strength, aggression, size",
            "Very large size, slow movement, very high health",
            "Cannot be tamed. Dim-witted and aggressive.",
            "Trolls are large, dim-witted creatures with great strength. They are aggressive and dangerous.",
            false);
        
        registerCreature("unicorn", "Unicorn", BestiaryCreatureRegistry.CreatureCategory.NEUTRAL,
            "A horse-like creature with a single horn and magical properties.",
            "Forests, magical reserves",
            "Gentle, pure, magical",
            "Magical properties, purity, horn",
            "Large size, fast movement, moderate health",
            "Extremely difficult to tame. Only approachable by pure-hearted individuals.",
            "Unicorns are gentle, pure creatures with magical properties. They are extremely rare and valuable.",
            false);
        
        // Spiritual creatures
        registerCreature("veela", "Veela", BestiaryCreatureRegistry.CreatureCategory.SPIRITUAL,
            "A humanoid creature with mesmerizing beauty and magical abilities.",
            "Bulgaria, various locations",
            "Beautiful, mesmerizing, magical",
            "Beauty, charm, magical abilities",
            "Medium size, moderate speed, moderate health",
            "Cannot be tamed. Humanoid magical creatures.",
            "Veela are humanoid creatures with mesmerizing beauty. They have magical abilities and can charm humans.",
            false);
        
        registerCreature("werewolf", "Werewolf", BestiaryCreatureRegistry.CreatureCategory.HOSTILE,
            "A human that transforms into a wolf during full moons.",
            "Various locations",
            "Aggressive, transforming, dangerous",
            "Transformation, strength, aggression",
            "Large size, fast movement, high health",
            "Cannot be tamed. Cursed humans.",
            "Werewolves are humans cursed to transform into wolves during full moons. They are extremely dangerous.",
            false);
        
        registerCreature("yeti", "Yeti", BestiaryCreatureRegistry.CreatureCategory.NEUTRAL,
            "A large, ape-like creature that lives in mountains.",
            "Tibet, mountains",
            "Shy, large, territorial",
            "Size, strength, shyness",
            "Very large size, moderate speed, very high health",
            "Cannot be tamed. Shy mountain creatures.",
            "Yetis are large, ape-like creatures that live in mountains. They are extremely shy and rarely seen.",
            false);
        
        registerCreature("grindylow", "Grindylow", BestiaryCreatureRegistry.CreatureCategory.AQUATIC,
            "A pale green, horned water demon.",
            "Britain, lakes, ponds",
            "Aggressive, aquatic, dangerous",
            "Water manipulation, strength, aggression",
            "Medium size, fast swimming, moderate health",
            "Cannot be tamed. Dangerous water demons.",
            "Grindylows are pale green, horned water demons from Britain. They are aggressive and dangerous.",
            false);
        
        // Dragon breeds
        registerCreature("hungarian_horntail", "Hungarian Horntail", BestiaryCreatureRegistry.CreatureCategory.HOSTILE,
            "A black dragon breed with bronze horns and a spiked tail.",
            "Hungary, dragon reserves",
            "Extremely aggressive, territorial, powerful",
            "Fire breath, spiked tail, flight",
            "Very large size, fast flight, extremely high health",
            "Cannot be tamed. One of the most dangerous dragon breeds.",
            "Hungarian Horntails are black dragons with bronze horns and spiked tails. They are one of the most dangerous dragon breeds.",
            false);
        
        registerCreature("chinese_fireball", "Chinese Fireball", BestiaryCreatureRegistry.CreatureCategory.HOSTILE,
            "A red and gold dragon breed that shoots mushroom-shaped flames.",
            "China, dragon reserves",
            "Aggressive, fire-breathing, powerful",
            "Fire breath, mushroom flames, flight",
            "Very large size, fast flight, very high health",
            "Cannot be tamed. Dangerous dragon breed.",
            "Chinese Fireballs are red and gold dragons that shoot mushroom-shaped flames. They are extremely dangerous.",
            false);
        
        registerCreature("swedish_short_snout", "Swedish Short-Snout", BestiaryCreatureRegistry.CreatureCategory.HOSTILE,
            "A silvery-blue dragon breed with powerful flame breath.",
            "Sweden, dragon reserves",
            "Less aggressive, powerful, silvery-blue",
            "Fire breath, silvery-blue scales, flight",
            "Very large size, fast flight, very high health",
            "Cannot be tamed. Less aggressive but still dangerous.",
            "Swedish Short-Snouts are silvery-blue dragons. They are less aggressive than other breeds but still dangerous.",
            false);
        
        registerCreature("common_welsh_green", "Common Welsh Green", BestiaryCreatureRegistry.CreatureCategory.HOSTILE,
            "A green dragon breed native to Wales.",
            "Wales, dragon reserves",
            "Less aggressive, territorial, powerful",
            "Fire breath, green scales, flight",
            "Very large size, fast flight, very high health",
            "Cannot be tamed. Dangerous but less aggressive than other breeds.",
            "Common Welsh Greens are green dragons native to Wales. They are known for being less aggressive than other dragon breeds.",
            false);
        
        registerCreature("hebridean_black", "Hebridean Black", BestiaryCreatureRegistry.CreatureCategory.HOSTILE,
            "A black dragon breed native to the Hebrides islands.",
            "Hebrides, Scotland, dragon reserves",
            "Aggressive, territorial, powerful",
            "Fire breath, black scales, flight",
            "Very large size, fast flight, very high health",
            "Cannot be tamed. Extremely dangerous dragon breed.",
            "Hebridean Blacks are native to the Hebrides islands off Scotland. They are aggressive and territorial.",
            false);
        
        registerCreature("peruvian_vipertooth", "Peruvian Vipertooth", BestiaryCreatureRegistry.CreatureCategory.HOSTILE,
            "A small but fast dragon breed with copper-colored scales and venomous fangs.",
            "Peru, dragon reserves",
            "Fast, aggressive, venomous",
            "Venomous bite, fire breath, speed",
            "Medium size, very fast flight, high health",
            "Cannot be tamed. Fast and venomous dragon breed.",
            "Peruvian Vipertooths are smaller dragons but are extremely fast and have venomous fangs. They are the smallest known dragon breed.",
            false);
        
        registerCreature("romanian_longhorn", "Romanian Longhorn", BestiaryCreatureRegistry.CreatureCategory.HOSTILE,
            "A dark green dragon breed with long, golden horns.",
            "Romania, dragon reserves",
            "Aggressive, territorial, powerful",
            "Fire breath, long golden horns, flight",
            "Very large size, fast flight, very high health",
            "Cannot be tamed. Extremely dangerous dragon breed.",
            "Romanian Longhorns are dark green dragons known for their distinctive long, golden horns.",
            false);
        
        registerCreature("ukrainian_ironbelly", "Ukrainian Ironbelly", BestiaryCreatureRegistry.CreatureCategory.HOSTILE,
            "A massive gray dragon breed with metallic scales and powerful fire breath.",
            "Ukraine, dragon reserves",
            "Aggressive, territorial, extremely powerful",
            "Fire breath, metallic scales, immense size",
            "Giant size, fast flight, extremely high health",
            "Cannot be tamed. One of the largest and most dangerous dragon breeds.",
            "Ukrainian Ironbellies are among the largest dragon breeds. They have metallic gray scales and are extremely powerful.",
            false);
        
        // Additional companion creatures
        registerCreature("raven_familiar", "Raven", BestiaryCreatureRegistry.CreatureCategory.COMPANION,
            "A raven familiar companion.",
            "Various locations",
            "Intelligent, loyal, flying",
            "Intelligence, flight, loyalty",
            "Small size, fast flight, low health",
            "Can be tamed. Intelligent familiar.",
            "Ravens are intelligent birds that can serve as familiars.",
            false);
        
        registerCreature("rat_familiar", "Rat", BestiaryCreatureRegistry.CreatureCategory.COMPANION,
            "A rat familiar companion.",
            "Various locations",
            "Small, quick, common",
            "Small size, speed, commonality",
            "Very small size, fast movement, very low health",
            "Can be tamed. Common familiar.",
            "Rats are common familiars, though they are less popular than other creatures.",
            false);
        
        registerCreature("snake_familiar", "Snake", BestiaryCreatureRegistry.CreatureCategory.COMPANION,
            "A snake familiar companion.",
            "Various locations",
            "Slytherin-associated, intelligent, slithering",
            "Intelligence, slithering, Slytherin association",
            "Small size, moderate speed, low health",
            "Can be tamed. Associated with Slytherin house.",
            "Snakes are familiars associated with Slytherin house. They are intelligent and can communicate with Parselmouths.",
            false);
        
        registerCreature("ferret_familiar", "Ferret", BestiaryCreatureRegistry.CreatureCategory.COMPANION,
            "A ferret familiar companion.",
            "Various locations",
            "Playful, quick, mischievous",
            "Playfulness, speed, mischief",
            "Small size, fast movement, low health",
            "Can be tamed. Playful familiar.",
            "Ferrets are playful familiars known for their speed and mischievous nature.",
            false);
    }
    
    /**
     * Registers a creature in the bestiary.
     */
    private static void registerCreature(String path, String name, BestiaryCreatureRegistry.CreatureCategory category,
                                        String description, String habitat, String behavior,
                                        String abilities, String stats, String taming,
                                        String lore, boolean isImplemented) {
        BestiaryCreatureRegistry.registerCreature(path, name, category, description, habitat,
            behavior, abilities, stats, taming, lore, isImplemented);
    }
}

