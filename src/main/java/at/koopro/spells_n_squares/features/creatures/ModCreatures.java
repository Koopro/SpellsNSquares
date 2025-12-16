package at.koopro.spells_n_squares.features.creatures;

import at.koopro.spells_n_squares.core.registry.CreatureRegistry;
import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;

/**
 * Registry and initialization of all mod creatures.
 */
public final class ModCreatures {
    private ModCreatures() {
    }
    
    /**
     * Registers all creature types in the mod.
     * Call this during mod initialization.
     */
    public static void register() {
        // Companion creatures
        CreatureRegistry.register("owl", new CreatureType(
            ModIdentifierHelper.modId("owl"),
            "Owl",
            "A magical owl that can deliver mail and items",
            CreatureType.CreatureCategory.COMPANION,
            true,
            false
        ));
        
        CreatureRegistry.register("cat", new CreatureType(
            ModIdentifierHelper.modId("cat"),
            "Cat",
            "A familiar cat companion with loyalty mechanics",
            CreatureType.CreatureCategory.COMPANION,
            true,
            false
        ));
        
        CreatureRegistry.register("toad", new CreatureType(
            ModIdentifierHelper.modId("toad"),
            "Toad",
            "A pet toad companion",
            CreatureType.CreatureCategory.COMPANION,
            true,
            false
        ));
        
        CreatureRegistry.register("niffler", new CreatureType(
            ModIdentifierHelper.modId("niffler"),
            "Niffler",
            "A treasure-hunting creature that finds valuable items",
            CreatureType.CreatureCategory.COMPANION,
            true,
            false
        ));
        
        CreatureRegistry.register("bowtruckle", new CreatureType(
            ModIdentifierHelper.modId("bowtruckle"),
            "Bowtruckle",
            "A small tree guardian that helps with plant growth",
            CreatureType.CreatureCategory.COMPANION,
            true,
            false
        ));
        
        CreatureRegistry.register("puffskein", new CreatureType(
            ModIdentifierHelper.modId("puffskein"),
            "Puffskein",
            "A fluffy pet creature that provides comfort and regeneration",
            CreatureType.CreatureCategory.COMPANION,
            true,
            false
        ));
        
        CreatureRegistry.register("kneazle", new CreatureType(
            ModIdentifierHelper.modId("kneazle"),
            "Kneazle",
            "An intelligent cat-like creature that can detect untrustworthy people",
            CreatureType.CreatureCategory.COMPANION,
            true,
            false
        ));
        
        CreatureRegistry.register("erumpent", new CreatureType(
            ModIdentifierHelper.modId("erumpent"),
            "Erumpent",
            "An explosive horned creature, dangerous but tamable",
            CreatureType.CreatureCategory.COMPANION,
            true,
            false
        ));
        
        CreatureRegistry.register("mooncalf", new CreatureType(
            ModIdentifierHelper.modId("mooncalf"),
            "Mooncalf",
            "A shy creature that appears during full moon",
            CreatureType.CreatureCategory.COMPANION,
            true,
            false
        ));
        
        CreatureRegistry.register("raven_familiar", new CreatureType(
            ModIdentifierHelper.modId("raven_familiar"),
            "Raven",
            "An intelligent bird familiar companion",
            CreatureType.CreatureCategory.COMPANION,
            true,
            false
        ));
        
        CreatureRegistry.register("rat_familiar", new CreatureType(
            ModIdentifierHelper.modId("rat_familiar"),
            "Rat",
            "A small and quick familiar companion",
            CreatureType.CreatureCategory.COMPANION,
            true,
            false
        ));
        
        CreatureRegistry.register("snake_familiar", new CreatureType(
            ModIdentifierHelper.modId("snake_familiar"),
            "Snake",
            "A snake familiar companion",
            CreatureType.CreatureCategory.COMPANION,
            true,
            false
        ));
        
        CreatureRegistry.register("ferret_familiar", new CreatureType(
            ModIdentifierHelper.modId("ferret_familiar"),
            "Ferret",
            "A playful and quick familiar companion",
            CreatureType.CreatureCategory.COMPANION,
            true,
            false
        ));
        
        CreatureRegistry.register("augurey", new CreatureType(
            ModIdentifierHelper.modId("augurey"),
            "Augurey",
            "Also known as the Irish Phoenix, a greenish-black bird whose cry was once believed to foretell death",
            CreatureType.CreatureCategory.COMPANION,
            true,
            false
        ));
        
        CreatureRegistry.register("demiguise", new CreatureType(
            ModIdentifierHelper.modId("demiguise"),
            "Demiguise",
            "A peaceful, ape-like creature that can turn invisible and has precognitive abilities",
            CreatureType.CreatureCategory.COMPANION,
            true,
            false
        ));
        
        CreatureRegistry.register("fwooper", new CreatureType(
            ModIdentifierHelper.modId("fwooper"),
            "Fwooper",
            "A brightly colored bird whose song can drive listeners insane",
            CreatureType.CreatureCategory.COMPANION,
            true,
            false
        ));
        
        CreatureRegistry.register("jobberknoll", new CreatureType(
            ModIdentifierHelper.modId("jobberknoll"),
            "Jobberknoll",
            "A small, blue speckled bird that remains silent until its death",
            CreatureType.CreatureCategory.COMPANION,
            true,
            false
        ));
        
        // Neutral creatures
        CreatureRegistry.register("billywig", new CreatureType(
            ModIdentifierHelper.modId("billywig"),
            "Billywig",
            "A sapphire-blue insect native to Australia; its sting causes giddiness and levitation",
            CreatureType.CreatureCategory.NEUTRAL,
            true,
            false
        ));
        
        CreatureRegistry.register("centaur", new CreatureType(
            ModIdentifierHelper.modId("centaur"),
            "Centaur",
            "A creature with the upper body of a human and the lower body of a horse",
            CreatureType.CreatureCategory.NEUTRAL,
            true,
            false
        ));
        
        CreatureRegistry.register("clabbert", new CreatureType(
            ModIdentifierHelper.modId("clabbert"),
            "Clabbert",
            "A tree-dwelling creature resembling a cross between a monkey and a frog",
            CreatureType.CreatureCategory.NEUTRAL,
            true,
            false
        ));
        
        CreatureRegistry.register("diricawl", new CreatureType(
            ModIdentifierHelper.modId("diricawl"),
            "Diricawl",
            "A plump, flightless bird capable of vanishing and reappearing elsewhere",
            CreatureType.CreatureCategory.NEUTRAL,
            true,
            false
        ));
        
        CreatureRegistry.register("fairy", new CreatureType(
            ModIdentifierHelper.modId("fairy"),
            "Fairy",
            "A small, humanoid creature with insect-like wings",
            CreatureType.CreatureCategory.NEUTRAL,
            true,
            false
        ));
        
        CreatureRegistry.register("fire_crab", new CreatureType(
            ModIdentifierHelper.modId("fire_crab"),
            "Fire Crab",
            "A large, turtle-like creature that shoots flames from its rear end",
            CreatureType.CreatureCategory.NEUTRAL,
            true,
            false
        ));
        
        CreatureRegistry.register("ghoul", new CreatureType(
            ModIdentifierHelper.modId("ghoul"),
            "Ghoul",
            "A slimy, buck-toothed creature that often inhabits attics and barns",
            CreatureType.CreatureCategory.NEUTRAL,
            true,
            false
        ));
        
        CreatureRegistry.register("gnome", new CreatureType(
            ModIdentifierHelper.modId("gnome"),
            "Gnome",
            "A small, mischievous creature that infests gardens",
            CreatureType.CreatureCategory.NEUTRAL,
            true,
            false
        ));
        
        CreatureRegistry.register("horklump", new CreatureType(
            ModIdentifierHelper.modId("horklump"),
            "Horklump",
            "A pink, bristly creature that resembles a mushroom",
            CreatureType.CreatureCategory.NEUTRAL,
            true,
            false
        ));
        
        CreatureRegistry.register("imp", new CreatureType(
            ModIdentifierHelper.modId("imp"),
            "Imp",
            "A small, mischievous creature similar to a pixie but less dangerous",
            CreatureType.CreatureCategory.NEUTRAL,
            true,
            false
        ));
        
        CreatureRegistry.register("jarvey", new CreatureType(
            ModIdentifierHelper.modId("jarvey"),
            "Jarvey",
            "A ferret-like creature capable of human speech, though it usually speaks in rude and fast phrases",
            CreatureType.CreatureCategory.NEUTRAL,
            true,
            false
        ));
        
        CreatureRegistry.register("leprechaun", new CreatureType(
            ModIdentifierHelper.modId("leprechaun"),
            "Leprechaun",
            "A small, mischievous creature known for its love of gold",
            CreatureType.CreatureCategory.NEUTRAL,
            true,
            false
        ));
        
        CreatureRegistry.register("reem", new CreatureType(
            ModIdentifierHelper.modId("reem"),
            "Re'em",
            "A giant oxen with golden hides, whose blood grants immense strength",
            CreatureType.CreatureCategory.NEUTRAL,
            true,
            false
        ));
        
        CreatureRegistry.register("sphinx", new CreatureType(
            ModIdentifierHelper.modId("sphinx"),
            "Sphinx",
            "A creature with the head of a human and the body of a lion, known for posing riddles",
            CreatureType.CreatureCategory.NEUTRAL,
            true,
            false
        ));
        
        CreatureRegistry.register("streeler", new CreatureType(
            ModIdentifierHelper.modId("streeler"),
            "Streeler",
            "A giant snail that changes color hourly and leaves a poisonous trail",
            CreatureType.CreatureCategory.NEUTRAL,
            true,
            false
        ));
        
        CreatureRegistry.register("troll", new CreatureType(
            ModIdentifierHelper.modId("troll"),
            "Troll",
            "A large, dim-witted creature known for its immense strength",
            CreatureType.CreatureCategory.NEUTRAL,
            true,
            false
        ));
        
        CreatureRegistry.register("unicorn", new CreatureType(
            ModIdentifierHelper.modId("unicorn"),
            "Unicorn",
            "A pure-white, horse-like creature with a single horn on its forehead",
            CreatureType.CreatureCategory.NEUTRAL,
            true,
            false
        ));
        
        CreatureRegistry.register("yeti", new CreatureType(
            ModIdentifierHelper.modId("yeti"),
            "Yeti",
            "Also known as the Abominable Snowman, a giant humanoid creature native to the Himalayas",
            CreatureType.CreatureCategory.NEUTRAL,
            true,
            false
        ));
        
        // Mount creatures
        CreatureRegistry.register("hippogriff", new CreatureType(
            ModIdentifierHelper.modId("hippogriff"),
            "Hippogriff",
            "A mountable flying creature",
            CreatureType.CreatureCategory.MOUNT,
            true,
            false
        ));
        
        CreatureRegistry.register("thestral", new CreatureType(
            ModIdentifierHelper.modId("thestral"),
            "Thestral",
            "A mountable flying creature visible only to those who've seen death",
            CreatureType.CreatureCategory.MOUNT,
            true,
            false
        ));
        
        CreatureRegistry.register("occamy", new CreatureType(
            ModIdentifierHelper.modId("occamy"),
            "Occamy",
            "A shape-shifting serpent that can grow/shrink, protective of eggs",
            CreatureType.CreatureCategory.MOUNT,
            true,
            false
        ));
        
        CreatureRegistry.register("thunderbird", new CreatureType(
            ModIdentifierHelper.modId("thunderbird"),
            "Thunderbird",
            "A large bird that creates storms, powerful flying mount",
            CreatureType.CreatureCategory.MOUNT,
            true,
            false
        ));
        
        CreatureRegistry.register("graphorn", new CreatureType(
            ModIdentifierHelper.modId("graphorn"),
            "Graphorn",
            "A large aggressive beast, very tough and powerful mount",
            CreatureType.CreatureCategory.MOUNT,
            true,
            false
        ));
        
        CreatureRegistry.register("zouwu", new CreatureType(
            ModIdentifierHelper.modId("zouwu"),
            "Zouwu",
            "A fast cat-like mount that can teleport short distances",
            CreatureType.CreatureCategory.MOUNT,
            true,
            false
        ));
        
        // Hostile creatures
        CreatureRegistry.register("dementor", new CreatureType(
            ModIdentifierHelper.modId("dementor"),
            "Dementor",
            "A soul-sucking entity that requires a Patronus to defeat",
            CreatureType.CreatureCategory.HOSTILE,
            false,
            true
        ));
        
        CreatureRegistry.register("boggart", new CreatureType(
            ModIdentifierHelper.modId("boggart"),
            "Boggart",
            "A shape-shifting fear entity",
            CreatureType.CreatureCategory.HOSTILE,
            false,
            true
        ));
        
        CreatureRegistry.register("acromantula", new CreatureType(
            ModIdentifierHelper.modId("acromantula"),
            "Acromantula",
            "A large spider enemy",
            CreatureType.CreatureCategory.HOSTILE,
            false,
            true
        ));
        
        CreatureRegistry.register("dragon", new CreatureType(
            ModIdentifierHelper.modId("dragon"),
            "Dragon",
            "A rare, powerful boss creature",
            CreatureType.CreatureCategory.HOSTILE,
            false,
            true
        ));
        
        CreatureRegistry.register("swooping_evil", new CreatureType(
            ModIdentifierHelper.modId("swooping_evil"),
            "Swooping Evil",
            "A venomous flying creature for combat",
            CreatureType.CreatureCategory.HOSTILE,
            false,
            true
        ));
        
        CreatureRegistry.register("basilisk", new CreatureType(
            ModIdentifierHelper.modId("basilisk"),
            "Basilisk",
            "A giant serpent with petrifying gaze, extremely dangerous boss",
            CreatureType.CreatureCategory.HOSTILE,
            false,
            true
        ));
        
        CreatureRegistry.register("chimaera", new CreatureType(
            ModIdentifierHelper.modId("chimaera"),
            "Chimaera",
            "A multi-headed beast, dangerous hybrid creature",
            CreatureType.CreatureCategory.HOSTILE,
            false,
            true
        ));
        
        CreatureRegistry.register("ashwinder", new CreatureType(
            ModIdentifierHelper.modId("ashwinder"),
            "Ashwinder",
            "A serpent that arises from magical fires left burning too long",
            CreatureType.CreatureCategory.HOSTILE,
            false,
            true
        ));
        
        CreatureRegistry.register("doxy", new CreatureType(
            ModIdentifierHelper.modId("doxy"),
            "Doxy",
            "A small, fairy-like creature with venomous bites",
            CreatureType.CreatureCategory.HOSTILE,
            false,
            true
        ));
        
        CreatureRegistry.register("erkling", new CreatureType(
            ModIdentifierHelper.modId("erkling"),
            "Erkling",
            "An elf-like creature that lures children with music before eating them",
            CreatureType.CreatureCategory.HOSTILE,
            false,
            true
        ));
        
        CreatureRegistry.register("lethifold", new CreatureType(
            ModIdentifierHelper.modId("lethifold"),
            "Lethifold",
            "A dangerous, black, cloak-like creature that suffocates its victims in their sleep",
            CreatureType.CreatureCategory.HOSTILE,
            false,
            true
        ));
        
        CreatureRegistry.register("manticore", new CreatureType(
            ModIdentifierHelper.modId("manticore"),
            "Manticore",
            "A beast with the head of a man, body of a lion, and tail of a scorpion",
            CreatureType.CreatureCategory.HOSTILE,
            false,
            true
        ));
        
        CreatureRegistry.register("nundu", new CreatureType(
            ModIdentifierHelper.modId("nundu"),
            "Nundu",
            "A giant leopard-like creature whose breath causes disease and death",
            CreatureType.CreatureCategory.HOSTILE,
            false,
            true
        ));
        
        CreatureRegistry.register("pixie", new CreatureType(
            ModIdentifierHelper.modId("pixie"),
            "Pixie",
            "A small, mischievous creature known for its pranks",
            CreatureType.CreatureCategory.HOSTILE,
            false,
            true
        ));
        
        CreatureRegistry.register("quintaped", new CreatureType(
            ModIdentifierHelper.modId("quintaped"),
            "Quintaped",
            "A five-legged creature with a taste for human flesh",
            CreatureType.CreatureCategory.HOSTILE,
            false,
            true
        ));
        
        CreatureRegistry.register("red_cap", new CreatureType(
            ModIdentifierHelper.modId("red_cap"),
            "Red Cap",
            "A dwarf-like creature that lurks in places where blood has been shed",
            CreatureType.CreatureCategory.HOSTILE,
            false,
            true
        ));
        
        CreatureRegistry.register("hungarian_horntail", new CreatureType(
            ModIdentifierHelper.modId("hungarian_horntail"),
            "Hungarian Horntail",
            "A dragon breed with black scales, bronze horns, and a spiked tail",
            CreatureType.CreatureCategory.HOSTILE,
            false,
            true
        ));
        
        CreatureRegistry.register("chinese_fireball", new CreatureType(
            ModIdentifierHelper.modId("chinese_fireball"),
            "Chinese Fireball",
            "A dragon breed with smooth red scales and golden spikes around its face",
            CreatureType.CreatureCategory.HOSTILE,
            false,
            true
        ));
        
        CreatureRegistry.register("swedish_short_snout", new CreatureType(
            ModIdentifierHelper.modId("swedish_short_snout"),
            "Swedish Short-Snout",
            "A silvery-blue dragon breed with a short snout and powerful fire breath",
            CreatureType.CreatureCategory.HOSTILE,
            false,
            true
        ));
        
        CreatureRegistry.register("common_welsh_green", new CreatureType(
            ModIdentifierHelper.modId("common_welsh_green"),
            "Common Welsh Green",
            "A green dragon breed that is less aggressive than other breeds",
            CreatureType.CreatureCategory.HOSTILE,
            false,
            true
        ));
        
        CreatureRegistry.register("hebridean_black", new CreatureType(
            ModIdentifierHelper.modId("hebridean_black"),
            "Hebridean Black",
            "A black dragon breed native to the Hebrides islands",
            CreatureType.CreatureCategory.HOSTILE,
            false,
            true
        ));
        
        CreatureRegistry.register("peruvian_vipertooth", new CreatureType(
            ModIdentifierHelper.modId("peruvian_vipertooth"),
            "Peruvian Vipertooth",
            "A small but fast dragon breed with copper-colored scales and venomous fangs",
            CreatureType.CreatureCategory.HOSTILE,
            false,
            true
        ));
        
        CreatureRegistry.register("romanian_longhorn", new CreatureType(
            ModIdentifierHelper.modId("romanian_longhorn"),
            "Romanian Longhorn",
            "A dark green dragon breed with long, golden horns",
            CreatureType.CreatureCategory.HOSTILE,
            false,
            true
        ));
        
        CreatureRegistry.register("ukrainian_ironbelly", new CreatureType(
            ModIdentifierHelper.modId("ukrainian_ironbelly"),
            "Ukrainian Ironbelly",
            "A massive gray dragon breed with metallic scales and powerful fire breath",
            CreatureType.CreatureCategory.HOSTILE,
            false,
            true
        ));
        
        // Aquatic creatures
        CreatureRegistry.register("hippocampus", new CreatureType(
            ModIdentifierHelper.modId("hippocampus"),
            "Hippocampus",
            "A sea creature with the head and front legs of a horse and the tail of a fish",
            CreatureType.CreatureCategory.AQUATIC,
            true,
            false
        ));
        
        CreatureRegistry.register("kappa", new CreatureType(
            ModIdentifierHelper.modId("kappa"),
            "Kappa",
            "A water-dwelling creature from Japan that resembles a monkey with fish-like scales",
            CreatureType.CreatureCategory.AQUATIC,
            true,
            false
        ));
        
        CreatureRegistry.register("kelpie", new CreatureType(
            ModIdentifierHelper.modId("kelpie"),
            "Kelpie",
            "A shape-shifting water demon that often appears as a horse",
            CreatureType.CreatureCategory.AQUATIC,
            true,
            false
        ));
        
        CreatureRegistry.register("merpeople", new CreatureType(
            ModIdentifierHelper.modId("merpeople"),
            "Merpeople",
            "Aquatic beings with the upper body of a human and the tail of a fish",
            CreatureType.CreatureCategory.AQUATIC,
            true,
            false
        ));
        
        CreatureRegistry.register("ramora", new CreatureType(
            ModIdentifierHelper.modId("ramora"),
            "Ramora",
            "A silver fish native to the Indian Ocean, known for its power to anchor ships",
            CreatureType.CreatureCategory.AQUATIC,
            true,
            false
        ));
        
        CreatureRegistry.register("grindylow", new CreatureType(
            ModIdentifierHelper.modId("grindylow"),
            "Grindylow",
            "A pale-green water demon with long, thin fingers and sharp horns",
            CreatureType.CreatureCategory.AQUATIC,
            true,
            false
        ));
        
        // Spiritual creatures
        CreatureRegistry.register("veela", new CreatureType(
            ModIdentifierHelper.modId("veela"),
            "Veela",
            "Beautiful, semi-human magical beings who can enchant men with their dance",
            CreatureType.CreatureCategory.SPIRITUAL,
            true,
            false
        ));
        
        // Special creatures
        CreatureRegistry.register("werewolf", new CreatureType(
            ModIdentifierHelper.modId("werewolf"),
            "Werewolf",
            "A human who transforms into a wolf-like creature during the full moon",
            CreatureType.CreatureCategory.HOSTILE,
            false,
            true
        ));
    }
}

