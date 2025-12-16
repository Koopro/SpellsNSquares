package at.koopro.spells_n_squares.features.spell;

import at.koopro.spells_n_squares.core.registry.ModEntities;
import at.koopro.spells_n_squares.core.registry.ModSounds;
import at.koopro.spells_n_squares.core.registry.ModTags;
import at.koopro.spells_n_squares.core.util.PlayerItemUtils;
import at.koopro.spells_n_squares.features.playerclass.PlayerClass;
import at.koopro.spells_n_squares.features.playerclass.PlayerClassManager;
import at.koopro.spells_n_squares.features.spell.entity.LightOrbEntity;
import at.koopro.spells_n_squares.features.spell.entity.LightningBeamEntity;
import at.koopro.spells_n_squares.features.spell.entity.ShieldOrbEntity;
import at.koopro.spells_n_squares.features.spell.LumosManager;
import at.koopro.spells_n_squares.features.wand.WandAffinity;
import at.koopro.spells_n_squares.features.wand.WandAffinityManager;
import at.koopro.spells_n_squares.features.wand.WandCore;
import at.koopro.spells_n_squares.features.wand.WandDataHelper;
import at.koopro.spells_n_squares.features.wand.WandWood;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.Set;

/**
 * Comprehensive debug commands for testing and debugging all mod systems.
 * 
 * Main command: /spells_n_squaresdebug
 * 
 * Categories:
 * - spells: Spell management (list, info, slots, cooldowns, cast)
 * - wand: Wand management (info, set, attune, affinity, lumos)
 * - player: Player data (class, data, clear)
 * - test: Visual testing (lightorb, shieldorb, particles, sound)
 * - help: Command help system
 */
public class DebugCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("spells_n_squaresdebug");

        // Legacy commands (kept for compatibility)
        root.then(Commands.literal("lightning")
            .executes(ctx -> debugLightning(ctx.getSource())));
        root.then(Commands.literal("dummy")
            .executes(ctx -> spawnDummy(ctx.getSource())));
        root.then(Commands.literal("dummyplayer")
            .executes(ctx -> spawnDummyPlayer(ctx.getSource())));

        // Spell management commands
        LiteralArgumentBuilder<CommandSourceStack> spells = Commands.literal("spells");
        spells.then(Commands.literal("list")
            .executes(ctx -> listSpells(ctx.getSource())));
        spells.then(Commands.literal("info")
            .then(Commands.argument("spell_id", StringArgumentType.string())
                .suggests(spellIdSuggestions())
                .executes(ctx -> spellInfo(ctx.getSource(), StringArgumentType.getString(ctx, "spell_id")))));

        LiteralArgumentBuilder<CommandSourceStack> slots = Commands.literal("slots");
        slots.executes(ctx -> {
            try {
                return showSlots(ctx.getSource(), getPlayer(ctx.getSource()));
            } catch (CommandSyntaxException e) {
                ctx.getSource().sendFailure(Component.literal("This command can only be used by players"));
                return 0;
            }
        });
        slots.then(Commands.argument("player", EntityArgument.player())
            .executes(ctx -> showSlots(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"))));

        LiteralArgumentBuilder<CommandSourceStack> slot = Commands.literal("slot");
        slot.then(Commands.literal("set")
            .then(Commands.argument("slot", StringArgumentType.string())
                .suggests(slotNumberSuggestions())
                .then(Commands.argument("spell_id", StringArgumentType.string())
                    .suggests(spellIdSuggestions())
                    .executes(ctx -> {
                        try {
                            return setSlot(ctx.getSource(), 
                                parseSlotNumber(StringArgumentType.getString(ctx, "slot")),
                                StringArgumentType.getString(ctx, "spell_id"));
                        } catch (CommandSyntaxException e) {
                            String errorMsg = e.getRawMessage().getString();
                            ctx.getSource().sendFailure(Component.literal(errorMsg));
                            return 0;
                        }
                    }))));
        slot.then(Commands.literal("clear")
            .then(Commands.argument("slot", StringArgumentType.string())
                .suggests(slotNumberSuggestions())
                .executes(ctx -> {
                    try {
                        return clearSlot(ctx.getSource(), 
                            parseSlotNumber(StringArgumentType.getString(ctx, "slot")));
                    } catch (CommandSyntaxException e) {
                        ctx.getSource().sendFailure(Component.literal(e.getMessage()));
                        return 0;
                    }
                })));

        LiteralArgumentBuilder<CommandSourceStack> cooldowns = Commands.literal("cooldowns");
        cooldowns.executes(ctx -> {
            try {
                return showCooldowns(ctx.getSource(), getPlayer(ctx.getSource()));
            } catch (CommandSyntaxException e) {
                ctx.getSource().sendFailure(Component.literal("This command can only be used by players"));
                return 0;
            }
        });
        cooldowns.then(Commands.argument("player", EntityArgument.player())
            .executes(ctx -> showCooldowns(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"))));

        LiteralArgumentBuilder<CommandSourceStack> cooldown = Commands.literal("cooldown");
        cooldown.then(Commands.literal("clear")
            .executes(ctx -> {
                try {
                    return clearAllCooldowns(ctx.getSource(), getPlayer(ctx.getSource()));
                } catch (CommandSyntaxException e) {
                    ctx.getSource().sendFailure(Component.literal("This command can only be used by players"));
                    return 0;
                }
            }));
        cooldown.then(Commands.literal("clear")
            .then(Commands.argument("spell_id", StringArgumentType.string())
                .suggests(cooldownSpellSuggestions())
                .executes(ctx -> {
                    try {
                        return clearCooldown(ctx.getSource(), 
                            getPlayer(ctx.getSource()),
                            StringArgumentType.getString(ctx, "spell_id"));
                    } catch (CommandSyntaxException e) {
                        ctx.getSource().sendFailure(Component.literal("This command can only be used by players"));
                        return 0;
                    }
                })));

        root.then(Commands.literal("cast")
            .then(Commands.argument("spell_id", StringArgumentType.string())
                .suggests(spellIdSuggestions())
                .executes(ctx -> castSpell(ctx.getSource(), 
                    StringArgumentType.getString(ctx, "spell_id")))));

        // Wand management commands
        LiteralArgumentBuilder<CommandSourceStack> wand = Commands.literal("wand");
        wand.then(Commands.literal("info")
            .executes(ctx -> {
                try {
                    return wandInfo(ctx.getSource(), getPlayer(ctx.getSource()));
                } catch (CommandSyntaxException e) {
                    ctx.getSource().sendFailure(Component.literal("This command can only be used by players"));
                    return 0;
                }
            }));
        wand.then(Commands.literal("info")
            .then(Commands.argument("player", EntityArgument.player())
                .executes(ctx -> wandInfo(ctx.getSource(), EntityArgument.getPlayer(ctx, "player")))));
        
        wand.then(Commands.literal("set")
            .then(Commands.argument("core", StringArgumentType.string())
                .suggests(wandCoreSuggestions())
                .then(Commands.argument("wood", StringArgumentType.string())
                    .suggests(wandWoodSuggestions())
                    .executes(ctx -> setWandData(ctx.getSource(),
                        StringArgumentType.getString(ctx, "core"),
                        StringArgumentType.getString(ctx, "wood"),
                        false))
                    .then(Commands.literal("true")
                        .executes(ctx -> setWandData(ctx.getSource(),
                            StringArgumentType.getString(ctx, "core"),
                            StringArgumentType.getString(ctx, "wood"),
                            true)))
                    .then(Commands.literal("false")
                        .executes(ctx -> setWandData(ctx.getSource(),
                            StringArgumentType.getString(ctx, "core"),
                            StringArgumentType.getString(ctx, "wood"),
                            false))))));
        
        wand.then(Commands.literal("attune")
            .executes(ctx -> toggleAttune(ctx.getSource())));
        
        wand.then(Commands.literal("affinity")
            .executes(ctx -> {
                try {
                    return wandAffinity(ctx.getSource(), getPlayer(ctx.getSource()));
                } catch (CommandSyntaxException e) {
                    ctx.getSource().sendFailure(Component.literal("This command can only be used by players"));
                    return 0;
                }
            }));
        wand.then(Commands.literal("affinity")
            .then(Commands.argument("player", EntityArgument.player())
                .executes(ctx -> wandAffinity(ctx.getSource(), EntityArgument.getPlayer(ctx, "player")))));

        root.then(Commands.literal("lumos")
            .executes(ctx -> toggleLumos(ctx.getSource())));
        root.then(Commands.literal("lumos")
            .then(Commands.literal("on")
                .executes(ctx -> setLumos(ctx.getSource(), true))));
        root.then(Commands.literal("lumos")
            .then(Commands.literal("off")
                .executes(ctx -> setLumos(ctx.getSource(), false))));

        // Player data commands
        LiteralArgumentBuilder<CommandSourceStack> player = Commands.literal("player");
        player.then(Commands.literal("class")
            .executes(ctx -> {
                try {
                    return showPlayerClass(ctx.getSource(), getPlayer(ctx.getSource()));
                } catch (CommandSyntaxException e) {
                    ctx.getSource().sendFailure(Component.literal("This command can only be used by players"));
                    return 0;
                }
            }));
        player.then(Commands.literal("class")
            .then(Commands.argument("player", EntityArgument.player())
                .executes(ctx -> showPlayerClass(ctx.getSource(), EntityArgument.getPlayer(ctx, "player")))));
        player.then(Commands.literal("class")
            .then(Commands.literal("set")
                .then(Commands.argument("class_name", StringArgumentType.string())
                    .suggests(playerClassSuggestions())
                    .executes(ctx -> {
                        try {
                            return setPlayerClass(ctx.getSource(),
                                getPlayer(ctx.getSource()),
                                StringArgumentType.getString(ctx, "class_name"));
                        } catch (CommandSyntaxException e) {
                            ctx.getSource().sendFailure(Component.literal("This command can only be used by players"));
                            return 0;
                        }
                    }))));
        
        player.then(Commands.literal("data")
            .executes(ctx -> {
                try {
                    return showPlayerData(ctx.getSource(), getPlayer(ctx.getSource()));
                } catch (CommandSyntaxException e) {
                    ctx.getSource().sendFailure(Component.literal("This command can only be used by players"));
                    return 0;
                }
            }));
        player.then(Commands.literal("data")
            .then(Commands.argument("player", EntityArgument.player())
                .executes(ctx -> showPlayerData(ctx.getSource(), EntityArgument.getPlayer(ctx, "player")))));
        
        player.then(Commands.literal("clear")
            .executes(ctx -> {
                try {
                    return clearPlayerData(ctx.getSource(), getPlayer(ctx.getSource()));
                } catch (CommandSyntaxException e) {
                    ctx.getSource().sendFailure(Component.literal("This command can only be used by players"));
                    return 0;
                }
            }));
        player.then(Commands.literal("clear")
            .then(Commands.argument("player", EntityArgument.player())
                .executes(ctx -> clearPlayerData(ctx.getSource(), EntityArgument.getPlayer(ctx, "player")))));

        // Visual testing commands
        LiteralArgumentBuilder<CommandSourceStack> test = Commands.literal("test");
        test.then(Commands.literal("lightorb")
            .executes(ctx -> testLightOrb(ctx.getSource())));
        test.then(Commands.literal("shieldorb")
            .executes(ctx -> testShieldOrb(ctx.getSource())));
        // Particles command - simplified to use common particles
        test.then(Commands.literal("particles")
            .then(Commands.literal("end_rod")
                .executes(ctx -> testParticlesSimple(ctx.getSource(), ParticleTypes.END_ROD, 10))
                .then(Commands.argument("count", IntegerArgumentType.integer(1, 100))
                    .executes(ctx -> testParticlesSimple(ctx.getSource(), ParticleTypes.END_ROD,
                        IntegerArgumentType.getInteger(ctx, "count")))))
            .then(Commands.literal("electric_spark")
                .executes(ctx -> testParticlesSimple(ctx.getSource(), ParticleTypes.ELECTRIC_SPARK, 10))
                .then(Commands.argument("count", IntegerArgumentType.integer(1, 100))
                    .executes(ctx -> testParticlesSimple(ctx.getSource(), ParticleTypes.ELECTRIC_SPARK,
                        IntegerArgumentType.getInteger(ctx, "count")))))
            .then(Commands.literal("totem")
                .executes(ctx -> testParticlesSimple(ctx.getSource(), ParticleTypes.TOTEM_OF_UNDYING, 10))
                .then(Commands.argument("count", IntegerArgumentType.integer(1, 100))
                    .executes(ctx -> testParticlesSimple(ctx.getSource(), ParticleTypes.TOTEM_OF_UNDYING,
                        IntegerArgumentType.getInteger(ctx, "count"))))));
        test.then(Commands.literal("sound")
            .then(Commands.argument("sound_id", StringArgumentType.string())
                .suggests(soundIdSuggestions())
                .executes(ctx -> testSound(ctx.getSource(),
                    StringArgumentType.getString(ctx, "sound_id"), 1.0f, 1.0f))
                .then(Commands.argument("volume", FloatArgumentType.floatArg(0.0f, 2.0f))
                    .executes(ctx -> testSound(ctx.getSource(),
                        StringArgumentType.getString(ctx, "sound_id"),
                        FloatArgumentType.getFloat(ctx, "volume"),
                        1.0f))
                    .then(Commands.argument("pitch", FloatArgumentType.floatArg(0.0f, 2.0f))
                        .executes(ctx -> testSound(ctx.getSource(),
                            StringArgumentType.getString(ctx, "sound_id"),
                            FloatArgumentType.getFloat(ctx, "volume"),
                            FloatArgumentType.getFloat(ctx, "pitch")))))));

        // Utility commands
        root.then(Commands.literal("help")
            .executes(ctx -> showHelp(ctx.getSource(), null)));
        root.then(Commands.literal("help")
            .then(Commands.argument("category", StringArgumentType.string())
                .suggests(helpCategorySuggestions())
                .executes(ctx -> showHelp(ctx.getSource(), 
                    StringArgumentType.getString(ctx, "category")))));

        // Summary command
        root.then(Commands.literal("summary")
            .executes(ctx -> {
                try {
                    return showSummary(ctx.getSource(), getPlayer(ctx.getSource()));
                } catch (CommandSyntaxException e) {
                    ctx.getSource().sendFailure(Component.literal("This command can only be used by players"));
                    return 0;
                }
            }));
        root.then(Commands.literal("summary")
            .then(Commands.argument("player", EntityArgument.player())
                .executes(ctx -> showSummary(ctx.getSource(), EntityArgument.getPlayer(ctx, "player")))));

        // Validate command
        root.then(Commands.literal("validate")
            .executes(ctx -> {
                try {
                    return validatePlayerData(ctx.getSource(), getPlayer(ctx.getSource()));
                } catch (CommandSyntaxException e) {
                    ctx.getSource().sendFailure(Component.literal("This command can only be used by players"));
                    return 0;
                }
            }));

        // Reset command
        root.then(Commands.literal("reset")
            .executes(ctx -> {
                try {
                    return resetPlayerData(ctx.getSource(), getPlayer(ctx.getSource()));
                } catch (CommandSyntaxException e) {
                    ctx.getSource().sendFailure(Component.literal("This command can only be used by players"));
                    return 0;
                }
            }));

        // Register all subcommands
        root.then(spells);
        root.then(slots);
        root.then(slot);
        root.then(cooldowns);
        root.then(cooldown);
        root.then(wand);
        root.then(player);
        root.then(test);

        dispatcher.register(root);
    }

    // Helper methods
    private static ServerPlayer getPlayer(CommandSourceStack source) throws CommandSyntaxException {
        if (source.getEntity() instanceof ServerPlayer player) {
            return player;
        }
        throw new CommandSyntaxException(
            new com.mojang.brigadier.exceptions.SimpleCommandExceptionType(
                net.minecraft.network.chat.Component.literal("This command can only be used by players")),
            net.minecraft.network.chat.Component.literal("This command can only be used by players")
        );
    }

    private static ServerLevel getServerLevel(CommandSourceStack source) {
        return source.getLevel();
    }

    // Helper to find similar spell IDs for error messages
    private static String findSimilarSpellId(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        
        String inputLower = input.toLowerCase();
        String bestMatch = null;
        int bestScore = Integer.MAX_VALUE;
        
        // Extract path part if it's a full identifier
        String inputPath = inputLower;
        if (input.contains(":")) {
            String[] parts = input.split(":", 2);
            inputPath = parts.length > 1 ? parts[1] : parts[0];
        }
        
        for (Identifier spellId : SpellRegistry.getAllIds()) {
            String idStr = spellId.toString().toLowerCase();
            String path = spellId.getPath().toLowerCase();
            
            // Check if path starts with input or input starts with path
            if (path.startsWith(inputPath) || inputPath.startsWith(path)) {
                int score = Math.abs(path.length() - inputPath.length());
                if (score < bestScore) {
                    bestScore = score;
                    bestMatch = spellId.toString();
                }
            }
            // Also check full ID
            else if (idStr.contains(inputLower) || inputLower.contains(idStr)) {
                int score = Math.abs(idStr.length() - inputLower.length());
                if (score < bestScore) {
                    bestScore = score;
                    bestMatch = spellId.toString();
                }
            }
        }
        
        return bestMatch;
    }

    // Slot parsing helper - supports both numbers and names
    private static int parseSlotNumber(String slotStr) throws CommandSyntaxException {
        if (slotStr == null || slotStr.isEmpty()) {
            throw new CommandSyntaxException(
                new com.mojang.brigadier.exceptions.SimpleCommandExceptionType(
                    Component.literal("Invalid slot")),
                Component.literal("Slot must be 0-3 or top/bottom/left/right")
            );
        }
        
        String lower = slotStr.toLowerCase();
        switch (lower) {
            case "0":
            case "top":
                return SpellManager.SLOT_TOP;
            case "1":
            case "bottom":
                return SpellManager.SLOT_BOTTOM;
            case "2":
            case "left":
                return SpellManager.SLOT_LEFT;
            case "3":
            case "right":
                return SpellManager.SLOT_RIGHT;
            default:
                try {
                    int slot = Integer.parseInt(slotStr);
                    if (slot >= 0 && slot < SpellManager.MAX_SLOTS) {
                        return slot;
                    }
                } catch (NumberFormatException e) {
                    // Not a number, continue to throw error
                }
                throw new CommandSyntaxException(
                    new com.mojang.brigadier.exceptions.SimpleCommandExceptionType(
                        Component.literal("Invalid slot")),
                    Component.literal("Slot must be 0-3 or top/bottom/left/right, got: " + slotStr)
                );
        }
    }

    // Suggestion Providers
    private static SuggestionProvider<CommandSourceStack> spellIdSuggestions() {
        return (context, builder) -> {
            String remaining = builder.getRemaining().toLowerCase();
            for (Identifier spellId : SpellRegistry.getAllIds()) {
                String idStr = spellId.toString();
                if (idStr.toLowerCase().startsWith(remaining)) {
                    builder.suggest(idStr);
                }
            }
            return builder.buildFuture();
        };
    }

    private static SuggestionProvider<CommandSourceStack> cooldownSpellSuggestions() {
        return (context, builder) -> {
            try {
                ServerPlayer player = getPlayer(context.getSource());
                String remaining = builder.getRemaining().toLowerCase();
                Map<Identifier, Integer> cooldowns = SpellManager.getPlayerCooldowns(player);
                if (cooldowns != null) {
                    for (Identifier spellId : cooldowns.keySet()) {
                        if (cooldowns.get(spellId) > 0) {
                            String idStr = spellId.toString();
                            if (idStr.toLowerCase().startsWith(remaining)) {
                                builder.suggest(idStr);
                            }
                        }
                    }
                }
            } catch (CommandSyntaxException e) {
                // If not a player, suggest all spells
                return spellIdSuggestions().getSuggestions(context, builder);
            }
            return builder.buildFuture();
        };
    }

    private static SuggestionProvider<CommandSourceStack> wandCoreSuggestions() {
        return (context, builder) -> {
            String remaining = builder.getRemaining().toLowerCase();
            for (WandCore core : WandCore.values()) {
                String coreId = core.getId();
                if (coreId.toLowerCase().startsWith(remaining)) {
                    builder.suggest(coreId);
                }
            }
            return builder.buildFuture();
        };
    }

    private static SuggestionProvider<CommandSourceStack> wandWoodSuggestions() {
        return (context, builder) -> {
            String remaining = builder.getRemaining().toLowerCase();
            for (WandWood wood : WandWood.values()) {
                String woodId = wood.getId();
                if (woodId.toLowerCase().startsWith(remaining)) {
                    builder.suggest(woodId);
                }
            }
            return builder.buildFuture();
        };
    }

    private static SuggestionProvider<CommandSourceStack> playerClassSuggestions() {
        return (context, builder) -> {
            String remaining = builder.getRemaining().toLowerCase();
            for (PlayerClass playerClass : PlayerClass.values()) {
                // Suggest both enum name and display name
                String enumName = playerClass.name();
                String displayName = playerClass.getDisplayName();
                if (enumName.toLowerCase().startsWith(remaining)) {
                    builder.suggest(enumName);
                } else if (displayName.toLowerCase().startsWith(remaining)) {
                    builder.suggest(displayName);
                }
            }
            return builder.buildFuture();
        };
    }

    private static SuggestionProvider<CommandSourceStack> helpCategorySuggestions() {
        return (context, builder) -> {
            String remaining = builder.getRemaining().toLowerCase();
            String[] categories = {"spells", "wand", "player", "test"};
            for (String category : categories) {
                if (category.toLowerCase().startsWith(remaining)) {
                    builder.suggest(category);
                }
            }
            return builder.buildFuture();
        };
    }

    private static SuggestionProvider<CommandSourceStack> soundIdSuggestions() {
        return (context, builder) -> {
            String remaining = builder.getRemaining().toLowerCase();
            
            // Mod sounds
            String[] modSounds = {
                "spells_n_squares:lumos",
                "spells_n_squares:nox",
                "spells_n_squares:rubber_duck_squeak",
                "spells_n_squares:flashlight_on",
                "spells_n_squares:flashlight_off"
            };
            for (String sound : modSounds) {
                if (sound.toLowerCase().startsWith(remaining)) {
                    builder.suggest(sound);
                }
            }
            
            // Common vanilla sounds
            String[] vanillaSounds = {
                "minecraft:entity.experience_orb.pickup",
                "minecraft:entity.player.levelup",
                "minecraft:block.note_block.note",
                "minecraft:entity.lightning_bolt.thunder",
                "minecraft:entity.firework_rocket.launch"
            };
            for (String sound : vanillaSounds) {
                if (sound.toLowerCase().startsWith(remaining)) {
                    builder.suggest(sound);
                }
            }
            
            return builder.buildFuture();
        };
    }

    private static SuggestionProvider<CommandSourceStack> slotNumberSuggestions() {
        return (context, builder) -> {
            String remaining = builder.getRemaining().toLowerCase();
            String[] slotNames = {"top", "bottom", "left", "right"};
            String[] slotDisplayNames = {"Top", "Bottom", "Left", "Right"};
            for (int i = 0; i < 4; i++) {
                String slotNum = String.valueOf(i);
                String slotName = slotNames[i];
                String slotDisplayName = slotDisplayNames[i];
                String suggestion = slotNum + " (" + slotDisplayName + ")";
                
                // Suggest both number and name
                if (slotNum.startsWith(remaining) || slotName.startsWith(remaining)) {
                    builder.suggest(slotNum, Component.literal(suggestion));
                    // Also suggest the name alias
                    if (slotName.startsWith(remaining)) {
                        builder.suggest(slotName, Component.literal(suggestion));
                    }
                }
            }
            return builder.buildFuture();
        };
    }

    // Spell management commands
    private static int listSpells(CommandSourceStack source) {
        Map<Identifier, Spell> spells = SpellRegistry.getAll();
        source.sendSuccess(() -> Component.literal("Registered Spells (" + spells.size() + "):"), false);
        
        StringBuilder spellList = new StringBuilder();
        boolean first = true;
        for (Identifier id : spells.keySet()) {
            if (!first) spellList.append(", ");
            spellList.append(id.toString());
            first = false;
        }
        
        source.sendSuccess(() -> Component.literal(spellList.toString()), false);
        return spells.size();
    }

    private static int spellInfo(CommandSourceStack source, String spellIdStr) {
        Identifier spellId;
        try {
            spellId = Identifier.parse(spellIdStr);
        } catch (Exception e) {
            source.sendFailure(Component.literal("Invalid spell ID format: " + spellIdStr));
            return 0;
        }

        Spell spell = SpellRegistry.get(spellId);
        if (spell == null) {
            // Find similar spell IDs
            String similar = findSimilarSpellId(spellIdStr);
            if (similar != null) {
                source.sendFailure(Component.literal("Spell not found: " + spellId + ". Did you mean: " + similar + "?"));
            } else {
                source.sendFailure(Component.literal("Spell not found: " + spellId + ". Use '/spells_n_squaresdebug spells list' to see all spells."));
            }
            return 0;
        }

        source.sendSuccess(() -> Component.literal("=== Spell Info ==="), false);
        source.sendSuccess(() -> Component.literal("ID: " + spell.getId()), false);
        source.sendSuccess(() -> Component.literal("Name: " + spell.getTranslatableName().getString()), false);
        source.sendSuccess(() -> Component.literal("Description: " + spell.getDescription()), false);
        source.sendSuccess(() -> Component.literal("Cooldown: " + spell.getCooldown() + " ticks (" + 
            (spell.getCooldown() / 20.0) + "s)"), false);
        source.sendSuccess(() -> Component.literal("Icon: " + spell.getIcon()), false);
        
        return 1;
    }

    private static int showSlots(CommandSourceStack source, ServerPlayer player) {
        source.sendSuccess(() -> Component.literal("=== Spell Slots for " + player.getName().getString() + " ==="), false);
        
        String[] slotNames = {"Top", "Bottom", "Left", "Right"};
        for (int i = 0; i < SpellManager.MAX_SLOTS; i++) {
            final int slotIndex = i;
            Identifier spellId = SpellManager.getSpellInSlot(player, slotIndex);
            String spellName = spellId != null ? spellId.toString() : "Empty";
            int cooldown = spellId != null ? SpellManager.getRemainingCooldown(player, spellId) : 0;
            String cooldownStr = cooldown > 0 ? " (Cooldown: " + cooldown + " ticks)" : "";
            source.sendSuccess(() -> Component.literal("Slot " + slotIndex + " (" + slotNames[slotIndex] + "): " + spellName + cooldownStr), false);
        }
        
        return 1;
    }

    private static int setSlot(CommandSourceStack source, int slot, String spellIdStr) {
        ServerPlayer player;
        try {
            player = getPlayer(source);
        } catch (CommandSyntaxException e) {
            source.sendFailure(Component.literal("This command can only be used by players"));
            return 0;
        }
        Identifier spellId;
        try {
            spellId = spellIdStr.equals("null") || spellIdStr.isEmpty() ? null : Identifier.parse(spellIdStr);
        } catch (Exception e) {
            source.sendFailure(Component.literal("Invalid spell ID format: " + spellIdStr));
            return 0;
        }

        if (spellId != null && !SpellRegistry.isRegistered(spellId)) {
            String similar = findSimilarSpellId(spellIdStr);
            if (similar != null) {
                source.sendFailure(Component.literal("Spell not registered: " + spellId + ". Did you mean: " + similar + "?"));
            } else {
                source.sendFailure(Component.literal("Spell not registered: " + spellId + ". Use '/spells_n_squaresdebug spells list' to see all spells."));
            }
            return 0;
        }

        SpellManager.setSpellInSlot(player, slot, spellId);
        String slotName = new String[]{"Top", "Bottom", "Left", "Right"}[slot];
        String result = spellId != null ? spellId.toString() : "cleared";
        source.sendSuccess(() -> Component.literal("Set slot " + slot + " (" + slotName + ") to: " + result), true);
        return 1;
    }

    private static int clearSlot(CommandSourceStack source, int slot) {
        return setSlot(source, slot, "null");
    }

    private static int showCooldowns(CommandSourceStack source, ServerPlayer player) {
        Map<Identifier, Integer> cooldowns = SpellManager.getPlayerCooldowns(player);
        if (cooldowns == null || cooldowns.isEmpty()) {
            source.sendSuccess(() -> Component.literal("No active cooldowns for " + player.getName().getString()), false);
            return 0;
        }

        source.sendSuccess(() -> Component.literal("=== Cooldowns for " + player.getName().getString() + " ==="), false);
        for (Map.Entry<Identifier, Integer> entry : cooldowns.entrySet()) {
            int remaining = entry.getValue();
            double seconds = remaining / 20.0;
            source.sendSuccess(() -> Component.literal(entry.getKey() + ": " + remaining + " ticks (" + 
                String.format("%.1f", seconds) + "s)"), false);
        }
        return cooldowns.size();
    }

    private static int clearAllCooldowns(CommandSourceStack source, ServerPlayer player) {
        Map<Identifier, Integer> cooldowns = SpellManager.getPlayerCooldowns(player);
        if (cooldowns != null) {
            for (Identifier spellId : cooldowns.keySet()) {
                SpellManager.setCooldown(player, spellId, 0);
            }
        }
        source.sendSuccess(() -> Component.literal("Cleared all cooldowns for " + player.getName().getString()), true);
        return 1;
    }

    private static int clearCooldown(CommandSourceStack source, ServerPlayer player, String spellIdStr) {
        Identifier spellId;
        try {
            spellId = Identifier.parse(spellIdStr);
        } catch (Exception e) {
            source.sendFailure(Component.literal("Invalid spell ID format: " + spellIdStr));
            return 0;
        }

        SpellManager.setCooldown(player, spellId, 0);
        source.sendSuccess(() -> Component.literal("Cleared cooldown for " + spellId), true);
        return 1;
    }

    private static int castSpell(CommandSourceStack source, String spellIdStr) {
        ServerPlayer player;
        try {
            player = getPlayer(source);
        } catch (CommandSyntaxException e) {
            source.sendFailure(Component.literal("This command can only be used by players"));
            return 0;
        }
        ServerLevel level = getServerLevel(source);
        
        Identifier spellId;
        try {
            spellId = Identifier.parse(spellIdStr);
        } catch (Exception e) {
            source.sendFailure(Component.literal("Invalid spell ID format: " + spellIdStr));
            return 0;
        }

        Spell spell = SpellRegistry.get(spellId);
        if (spell == null) {
            String similar = findSimilarSpellId(spellIdStr);
            if (similar != null) {
                source.sendFailure(Component.literal("Spell not found: " + spellId + ". Did you mean: " + similar + "?"));
            } else {
                source.sendFailure(Component.literal("Spell not found: " + spellId + ". Use '/spells_n_squaresdebug spells list' to see all spells."));
            }
            return 0;
        }

        // Bypass cooldowns and slots - direct cast
        boolean success = spell.cast(player, level);
        if (success) {
            source.sendSuccess(() -> Component.literal("Cast spell: " + spellId), true);
        } else {
            source.sendFailure(Component.literal("Failed to cast spell: " + spellId));
        }
        return success ? 1 : 0;
    }

    // Wand management commands
    private static int wandInfo(CommandSourceStack source, ServerPlayer player) {
        ItemStack wand = PlayerItemUtils.findHeldItemByTag(player, ModTags.WANDS).orElse(ItemStack.EMPTY);
        if (wand.isEmpty()) {
            source.sendFailure(Component.literal("Player " + player.getName().getString() + " is not holding a wand"));
            return 0;
        }

        WandCore core = WandDataHelper.getCore(wand);
        WandWood wood = WandDataHelper.getWood(wand);
        boolean attuned = WandDataHelper.isAttuned(wand);
        boolean lumosActive = LumosManager.isLumosActive(player);

        source.sendSuccess(() -> Component.literal("=== Wand Info for " + player.getName().getString() + " ==="), false);
        source.sendSuccess(() -> Component.literal("Core: " + (core != null ? core.name() : "None")), false);
        source.sendSuccess(() -> Component.literal("Wood: " + (wood != null ? wood.name() : "None")), false);
        source.sendSuccess(() -> Component.literal("Attuned: " + attuned), false);
        source.sendSuccess(() -> Component.literal("Lumos Active: " + lumosActive), false);
        
        if (core != null && wood != null) {
            WandAffinity affinity = WandAffinityManager.getPlayerWandAffinity(player);
            source.sendSuccess(() -> Component.literal("Affinity Power: " + String.format("%.1f%%", affinity.powerModifier() * 100)), false);
            source.sendSuccess(() -> Component.literal("Affinity Cooldown: " + String.format("%.1f%%", affinity.cooldownModifier() * 100)), false);
            source.sendSuccess(() -> Component.literal("Affinity Crit Chance: " + String.format("%.1f%%", affinity.critChanceBonus() * 100)), false);
        }
        
        return 1;
    }

    private static int setWandData(CommandSourceStack source, String coreStr, String woodStr, boolean attuned) {
        ServerPlayer player;
        try {
            player = getPlayer(source);
        } catch (CommandSyntaxException e) {
            source.sendFailure(Component.literal("This command can only be used by players"));
            return 0;
        }
        ItemStack wand = PlayerItemUtils.findHeldItemByTag(player, ModTags.WANDS).orElse(ItemStack.EMPTY);
        if (wand.isEmpty()) {
            source.sendFailure(Component.literal("You must be holding a wand"));
            return 0;
        }

        WandCore core = WandCore.fromId(coreStr.toLowerCase());
        if (core == null) {
            source.sendFailure(Component.literal("Invalid core: " + coreStr + ". Valid: phoenix_feather, dragon_heartstring, unicorn_hair"));
            return 0;
        }

        WandWood wood = WandWood.fromId(woodStr.toLowerCase());
        if (wood == null) {
            source.sendFailure(Component.literal("Invalid wood: " + woodStr));
            return 0;
        }

        WandDataHelper.setWandData(wand, core, wood, attuned);
        source.sendSuccess(() -> Component.literal("Set wand: " + core.name() + " + " + wood.name() + 
            (attuned ? " (Attuned)" : "")), true);
        return 1;
    }

    private static int toggleAttune(CommandSourceStack source) {
        ServerPlayer player;
        try {
            player = getPlayer(source);
        } catch (CommandSyntaxException e) {
            source.sendFailure(Component.literal("This command can only be used by players"));
            return 0;
        }
        ItemStack wand = PlayerItemUtils.findHeldItemByTag(player, ModTags.WANDS).orElse(ItemStack.EMPTY);
        if (wand.isEmpty()) {
            source.sendFailure(Component.literal("You must be holding a wand"));
            return 0;
        }

        boolean current = WandDataHelper.isAttuned(wand);
        WandDataHelper.setAttuned(wand, !current);
        source.sendSuccess(() -> Component.literal("Wand attunement: " + (!current ? "ON" : "OFF")), true);
        return 1;
    }

    private static int wandAffinity(CommandSourceStack source, ServerPlayer player) {
        WandAffinity affinity = WandAffinityManager.getPlayerWandAffinity(player);
        source.sendSuccess(() -> Component.literal("=== Wand Affinity for " + player.getName().getString() + " ==="), false);
        source.sendSuccess(() -> Component.literal("Power Modifier: " + String.format("%.1f%%", affinity.powerModifier() * 100)), false);
        source.sendSuccess(() -> Component.literal("Cooldown Modifier: " + String.format("%.1f%%", affinity.cooldownModifier() * 100)), false);
        source.sendSuccess(() -> Component.literal("Miscast Chance: " + String.format("%.1f%%", affinity.miscastChance() * 100)), false);
        source.sendSuccess(() -> Component.literal("Crit Chance Bonus: " + String.format("%.1f%%", affinity.critChanceBonus() * 100)), false);
        source.sendSuccess(() -> Component.literal("Stability Bonus: " + String.format("%.1f%%", affinity.stabilityBonus() * 100)), false);
        return 1;
    }

    private static int toggleLumos(CommandSourceStack source) {
        ServerPlayer player;
        try {
            player = getPlayer(source);
        } catch (CommandSyntaxException e) {
            source.sendFailure(Component.literal("This command can only be used by players"));
            return 0;
        }
        var result = LumosManager.toggleLumos(player);
        if (result.isPresent()) {
            boolean newState = result.get();
            source.sendSuccess(() -> Component.literal("Lumos: " + (newState ? "ON" : "OFF")), true);
            return 1;
        }
        source.sendFailure(Component.literal("No wand found in hand"));
        return 0;
    }

    private static int setLumos(CommandSourceStack source, boolean on) {
        ServerPlayer player;
        try {
            player = getPlayer(source);
        } catch (CommandSyntaxException e) {
            source.sendFailure(Component.literal("This command can only be used by players"));
            return 0;
        }
        boolean success = LumosManager.setLumosActive(player, on);
        if (success) {
            source.sendSuccess(() -> Component.literal("Lumos: " + (on ? "ON" : "OFF")), true);
            return 1;
        }
        source.sendFailure(Component.literal("No wand found in hand"));
        return 0;
    }

    // Player data commands
    private static int showPlayerClass(CommandSourceStack source, ServerPlayer player) {
        PlayerClass playerClass = PlayerClassManager.getPlayerClass(player);
        source.sendSuccess(() -> Component.literal("Player Class for " + player.getName().getString() + ": " + 
            playerClass.getDisplayName() + " - " + playerClass.getDescription()), false);
        return 1;
    }

    private static int setPlayerClass(CommandSourceStack source, ServerPlayer player, String className) {
        PlayerClass playerClass = PlayerClass.fromName(className);
        if (playerClass == PlayerClass.NONE && !className.equalsIgnoreCase("none")) {
            source.sendFailure(Component.literal("Invalid player class: " + className));
            return 0;
        }

        PlayerClassManager.setPlayerClass(player, playerClass);
        source.sendSuccess(() -> Component.literal("Set player class for " + player.getName().getString() + 
            " to: " + playerClass.getDisplayName()), true);
        return 1;
    }

    private static int showPlayerData(CommandSourceStack source, ServerPlayer player) {
        source.sendSuccess(() -> Component.literal("=== Player Data for " + player.getName().getString() + " ==="), false);
        
        // Player class
        PlayerClass playerClass = PlayerClassManager.getPlayerClass(player);
        source.sendSuccess(() -> Component.literal("Class: " + playerClass.getDisplayName()), false);
        
        // Spell slots
        source.sendSuccess(() -> Component.literal("Spell Slots:"), false);
        String[] slotNames = {"Top", "Bottom", "Left", "Right"};
        for (int i = 0; i < SpellManager.MAX_SLOTS; i++) {
            final int slotIndex = i;
            Identifier spellId = SpellManager.getSpellInSlot(player, slotIndex);
            String spellName = spellId != null ? spellId.toString() : "Empty";
            source.sendSuccess(() -> Component.literal("  " + slotNames[slotIndex] + ": " + spellName), false);
        }
        
        // Cooldowns
        Map<Identifier, Integer> cooldowns = SpellManager.getPlayerCooldowns(player);
        if (cooldowns != null && !cooldowns.isEmpty()) {
            source.sendSuccess(() -> Component.literal("Cooldowns:"), false);
            for (Map.Entry<Identifier, Integer> entry : cooldowns.entrySet()) {
                source.sendSuccess(() -> Component.literal("  " + entry.getKey() + ": " + entry.getValue() + " ticks"), false);
            }
        }
        
        // Wand info
        ItemStack wand = PlayerItemUtils.findHeldItemByTag(player, ModTags.WANDS).orElse(ItemStack.EMPTY);
        if (!wand.isEmpty()) {
            WandCore core = WandDataHelper.getCore(wand);
            WandWood wood = WandDataHelper.getWood(wand);
            boolean attuned = WandDataHelper.isAttuned(wand);
            boolean lumosActive = LumosManager.isLumosActive(player);
            source.sendSuccess(() -> Component.literal("Wand: " + (core != null ? core.name() : "None") + 
                " + " + (wood != null ? wood.name() : "None") + (attuned ? " (Attuned)" : "")), false);
            source.sendSuccess(() -> Component.literal("Lumos: " + (lumosActive ? "ON" : "OFF")), false);
        } else {
            source.sendSuccess(() -> Component.literal("Wand: Not holding a wand"), false);
        }
        
        return 1;
    }

    private static int clearPlayerData(CommandSourceStack source, ServerPlayer player) {
        // Clear spell slots
        for (int i = 0; i < SpellManager.MAX_SLOTS; i++) {
            SpellManager.setSpellInSlot(player, i, null);
        }
        
        // Clear cooldowns
        Map<Identifier, Integer> cooldowns = SpellManager.getPlayerCooldowns(player);
        if (cooldowns != null) {
            for (Identifier spellId : cooldowns.keySet()) {
                SpellManager.setCooldown(player, spellId, 0);
            }
        }
        
        // Clear player class
        PlayerClassManager.setPlayerClass(player, PlayerClass.NONE);
        
        source.sendSuccess(() -> Component.literal("Cleared all data for " + player.getName().getString()), true);
        return 1;
    }

    private static int showSummary(CommandSourceStack source, ServerPlayer player) {
        source.sendSuccess(() -> Component.literal("=== Quick Summary for " + player.getName().getString() + " ==="), false);
        
        // Player class
        PlayerClass playerClass = PlayerClassManager.getPlayerClass(player);
        source.sendSuccess(() -> Component.literal("Class: " + playerClass.getDisplayName()), false);
        
        // Active spells in slots
        int activeSpells = 0;
        String[] slotNames = {"Top", "Bottom", "Left", "Right"};
        StringBuilder slotSummary = new StringBuilder("Slots: ");
        boolean first = true;
        for (int i = 0; i < SpellManager.MAX_SLOTS; i++) {
            Identifier spellId = SpellManager.getSpellInSlot(player, i);
            if (spellId != null) {
                if (!first) slotSummary.append(", ");
                slotSummary.append(slotNames[i]).append("=").append(spellId.getPath());
                activeSpells++;
                first = false;
            }
        }
        if (activeSpells == 0) {
            slotSummary.append("None");
        }
        source.sendSuccess(() -> Component.literal(slotSummary.toString()), false);
        
        // Active cooldowns count
        Map<Identifier, Integer> cooldowns = SpellManager.getPlayerCooldowns(player);
        int activeCooldowns = cooldowns != null ? (int)cooldowns.values().stream().filter(c -> c > 0).count() : 0;
        source.sendSuccess(() -> Component.literal("Active Cooldowns: " + activeCooldowns), false);
        
        // Wand status
        ItemStack wand = PlayerItemUtils.findHeldItemByTag(player, ModTags.WANDS).orElse(ItemStack.EMPTY);
        if (!wand.isEmpty()) {
            WandCore core = WandDataHelper.getCore(wand);
            WandWood wood = WandDataHelper.getWood(wand);
            boolean attuned = WandDataHelper.isAttuned(wand);
            boolean lumosActive = LumosManager.isLumosActive(player);
            String wandStatus = (core != null ? core.name() : "None") + "+" + 
                               (wood != null ? wood.name() : "None") + 
                               (attuned ? " (Attuned)" : "");
            source.sendSuccess(() -> Component.literal("Wand: " + wandStatus + ", Lumos: " + (lumosActive ? "ON" : "OFF")), false);
        } else {
            source.sendSuccess(() -> Component.literal("Wand: Not holding a wand"), false);
        }
        
        return 1;
    }

    private static int validatePlayerData(CommandSourceStack source, ServerPlayer player) {
        source.sendSuccess(() -> Component.literal("=== Validating Data for " + player.getName().getString() + " ==="), false);
        
        int issues = 0;
        
        // Validate spell slots
        for (int i = 0; i < SpellManager.MAX_SLOTS; i++) {
            Identifier spellId = SpellManager.getSpellInSlot(player, i);
            if (spellId != null && !SpellRegistry.isRegistered(spellId)) {
                source.sendFailure(Component.literal("Issue: Slot " + i + " has invalid spell: " + spellId));
                issues++;
            }
        }
        
        // Validate cooldowns
        Map<Identifier, Integer> cooldowns = SpellManager.getPlayerCooldowns(player);
        if (cooldowns != null) {
            for (Identifier spellId : cooldowns.keySet()) {
                if (!SpellRegistry.isRegistered(spellId)) {
                    source.sendFailure(Component.literal("Issue: Cooldown for invalid spell: " + spellId));
                    issues++;
                }
            }
        }
        
        // Validate wand data
        ItemStack wand = PlayerItemUtils.findHeldItemByTag(player, ModTags.WANDS).orElse(ItemStack.EMPTY);
        if (!wand.isEmpty()) {
            WandCore core = WandDataHelper.getCore(wand);
            WandWood wood = WandDataHelper.getWood(wand);
            if (core == null && wood != null) {
                source.sendFailure(Component.literal("Issue: Wand has wood but no core"));
                issues++;
            } else if (wood == null && core != null) {
                source.sendFailure(Component.literal("Issue: Wand has core but no wood"));
                issues++;
            }
        }
        
        if (issues == 0) {
            source.sendSuccess(() -> Component.literal("Validation passed: No issues found"), false);
        } else {
            source.sendFailure(Component.literal("Validation found " + issues + " issue(s)"));
        }
        
        return issues == 0 ? 1 : 0;
    }

    private static int resetPlayerData(CommandSourceStack source, ServerPlayer player) {
        // Clear spell slots
        for (int i = 0; i < SpellManager.MAX_SLOTS; i++) {
            SpellManager.setSpellInSlot(player, i, null);
        }
        
        // Clear cooldowns
        Map<Identifier, Integer> cooldowns = SpellManager.getPlayerCooldowns(player);
        if (cooldowns != null) {
            for (Identifier spellId : cooldowns.keySet()) {
                SpellManager.setCooldown(player, spellId, 0);
            }
        }
        
        // Clear player class
        PlayerClassManager.setPlayerClass(player, PlayerClass.NONE);
        
        // Clear lumos (if holding wand)
        ItemStack wand = PlayerItemUtils.findHeldItemByTag(player, ModTags.WANDS).orElse(ItemStack.EMPTY);
        if (!wand.isEmpty()) {
            LumosManager.setLumosActive(player, false);
        }
        
        source.sendSuccess(() -> Component.literal("Reset all debug data for " + player.getName().getString()), true);
        return 1;
    }

    // Visual testing commands
    private static int testLightOrb(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player) || 
            !(source.getLevel() instanceof ServerLevel serverLevel)) {
            return 0;
        }

        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle().normalize();
        Vec3 spawnPos = eye.add(look.scale(0.6)).add(0, -0.1, 0);
        Vec3 velocity = look.scale(0.7);
        
        LightOrbEntity orb = new LightOrbEntity(serverLevel, player, spawnPos, velocity, 80);
        serverLevel.addFreshEntity(orb);
        
        source.sendSuccess(() -> Component.literal("Spawned light orb"), true);
        return 1;
    }

    private static int testShieldOrb(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player) || 
            !(source.getLevel() instanceof ServerLevel serverLevel)) {
            return 0;
        }

        ShieldOrbEntity shield = new ShieldOrbEntity(serverLevel, player);
        shield.setPos(player.getX(), player.getY() + 1.0, player.getZ());
        serverLevel.addFreshEntity(shield);
        
        source.sendSuccess(() -> Component.literal("Spawned shield orb"), true);
        return 1;
    }

    private static int testParticlesSimple(CommandSourceStack source, net.minecraft.core.particles.SimpleParticleType particle, int count) {
        if (!(source.getEntity() instanceof ServerPlayer player) || 
            !(source.getLevel() instanceof ServerLevel serverLevel)) {
            return 0;
        }

        var hitResult = player.pick(16.0, 1.0f, false);
        Vec3 pos = hitResult.getLocation();
        
        serverLevel.sendParticles(particle, pos.x, pos.y, pos.z, count, 0.5, 0.5, 0.5, 0.1);
        source.sendSuccess(() -> Component.literal("Spawned " + count + " particles at crosshair"), true);
        return 1;
    }

    private static int testSound(CommandSourceStack source, String soundIdStr, float volume, float pitch) {
        if (!(source.getEntity() instanceof ServerPlayer player) || 
            !(source.getLevel() instanceof ServerLevel serverLevel)) {
            return 0;
        }

        // Try to find sound in mod registry first
        SoundEvent sound = null;
        try {
            Identifier soundId = Identifier.parse(soundIdStr);
            // Check mod sounds
            if (soundId.getNamespace().equals("spells_n_squares")) {
                if (soundId.getPath().equals("lumos")) {
                    sound = ModSounds.LUMOS.value();
                } else if (soundId.getPath().equals("nox")) {
                    sound = ModSounds.NOX.value();
                }
            }
        } catch (Exception e) {
            // Will try vanilla sounds
        }

        if (sound == null) {
            // Try vanilla sound
            try {
                Identifier soundId = Identifier.parse(soundIdStr);
                var holderOpt = net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.getOptional(soundId);
                if (holderOpt.isPresent()) {
                    sound = holderOpt.get();
                }
            } catch (Exception e) {
                source.sendFailure(Component.literal("Invalid sound ID: " + soundIdStr));
                return 0;
            }
        }

        if (sound == null) {
            source.sendFailure(Component.literal("Sound not found: " + soundIdStr));
            return 0;
        }

        serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), 
            sound, SoundSource.PLAYERS, volume, pitch);
        source.sendSuccess(() -> Component.literal("Played sound: " + soundIdStr), true);
        return 1;
    }

    // Utility commands
    private static int showHelp(CommandSourceStack source, String category) {
        if (category == null) {
            source.sendSuccess(() -> Component.literal("=== Spells_n_Squares Debug Commands ==="), false);
            source.sendSuccess(() -> Component.literal("Categories: spells, wand, player, test, help"), false);
            source.sendSuccess(() -> Component.literal("Use /spells_n_squaresdebug help <category> for category-specific help"), false);
            return 1;
        }

        switch (category.toLowerCase()) {
            case "spells":
                source.sendSuccess(() -> Component.literal("=== Spell Commands ==="), false);
                source.sendSuccess(() -> Component.literal("/spells_n_squaresdebug spells list - List all registered spells"), false);
                source.sendSuccess(() -> Component.literal("/spells_n_squaresdebug spells info <spell_id> - Show spell details"), false);
                source.sendSuccess(() -> Component.literal("/spells_n_squaresdebug slots [player] - Show spell slot assignments"), false);
                source.sendSuccess(() -> Component.literal("/spells_n_squaresdebug slot set <slot> <spell_id> - Assign spell to slot"), false);
                source.sendSuccess(() -> Component.literal("/spells_n_squaresdebug slot clear <slot> - Clear spell from slot"), false);
                source.sendSuccess(() -> Component.literal("/spells_n_squaresdebug cooldowns [player] - Show active cooldowns"), false);
                source.sendSuccess(() -> Component.literal("/spells_n_squaresdebug cooldown clear [spell_id] - Clear cooldown(s)"), false);
                source.sendSuccess(() -> Component.literal("/spells_n_squaresdebug cast <spell_id> - Cast spell directly"), false);
                break;
            case "wand":
                source.sendSuccess(() -> Component.literal("=== Wand Commands ==="), false);
                source.sendSuccess(() -> Component.literal("/spells_n_squaresdebug wand info [player] - Show wand data"), false);
                source.sendSuccess(() -> Component.literal("/spells_n_squaresdebug wand set <core> <wood> [attuned] - Set wand data"), false);
                source.sendSuccess(() -> Component.literal("/spells_n_squaresdebug wand attune - Toggle attunement"), false);
                source.sendSuccess(() -> Component.literal("/spells_n_squaresdebug wand affinity [player] - Show affinity stats"), false);
                source.sendSuccess(() -> Component.literal("/spells_n_squaresdebug lumos [on|off] - Control lumos state"), false);
                break;
            case "player":
                source.sendSuccess(() -> Component.literal("=== Player Commands ==="), false);
                source.sendSuccess(() -> Component.literal("/spells_n_squaresdebug player class [player] - Show player class"), false);
                source.sendSuccess(() -> Component.literal("/spells_n_squaresdebug player class set <class_name> - Set player class"), false);
                source.sendSuccess(() -> Component.literal("/spells_n_squaresdebug player data [player] - Show all player data"), false);
                source.sendSuccess(() -> Component.literal("/spells_n_squaresdebug player clear [player] - Clear all player data"), false);
                break;
            case "test":
                source.sendSuccess(() -> Component.literal("=== Test Commands ==="), false);
                source.sendSuccess(() -> Component.literal("/spells_n_squaresdebug test lightorb - Spawn light orb entity"), false);
                source.sendSuccess(() -> Component.literal("/spells_n_squaresdebug test shieldorb - Spawn shield orb entity"), false);
                source.sendSuccess(() -> Component.literal("/spells_n_squaresdebug test particles <type> [count] - Spawn particles"), false);
                source.sendSuccess(() -> Component.literal("/spells_n_squaresdebug test sound <sound_id> [volume] [pitch] - Play sound"), false);
                break;
            default:
                source.sendFailure(Component.literal("Unknown category: " + category));
                return 0;
        }
        return 1;
    }

    // Legacy commands (kept for compatibility)
    private static int debugLightning(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player) || !(source.getLevel() instanceof ServerLevel serverLevel)) {
            return 0;
        }

        var hitResult = player.pick(32.0, 1.0f, false);
        if (hitResult.getType() != net.minecraft.world.phys.HitResult.Type.BLOCK) {
            return 0;
        }

        var blockHit = (net.minecraft.world.phys.BlockHitResult) hitResult;
        var targetPos = blockHit.getBlockPos().above();

        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle().normalize();
        Vec3 wandTip = eye.add(look.scale(0.6)).add(0, -0.1, 0);

        Vec3 end = new Vec3(
            targetPos.getX() + 0.5,
            targetPos.getY() + 0.5,
            targetPos.getZ() + 0.5
        );

        int color = 0xFF80D8FF;
        LightningBeamEntity beam = new LightningBeamEntity(serverLevel, player, wandTip, end, color, 8);
        serverLevel.addFreshEntity(beam);

        return 1;
    }

    private static int spawnDummy(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player) || !(source.getLevel() instanceof ServerLevel serverLevel)) {
            return 0;
        }

        var hitResult = player.pick(16.0, 1.0f, false);
        Vec3 pos = hitResult.getLocation();
        ArmorStand armorStand = new ArmorStand(
            EntityType.ARMOR_STAND,
            serverLevel
        );
        armorStand.setPos(pos.x, pos.y, pos.z);
        armorStand.setCustomName(Component.literal("Spells_n_Squares Dummy"));
        armorStand.setInvulnerable(false);
        serverLevel.addFreshEntity(armorStand);
        return 1;
    }

    private static int spawnDummyPlayer(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player) || !(source.getLevel() instanceof ServerLevel serverLevel)) {
            return 0;
        }

        var hitResult = player.pick(16.0, 1.0f, false);
        Vec3 pos = hitResult.getLocation();

        var dummyType = ModEntities.DUMMY_PLAYER.get();
        var dummy = new at.koopro.spells_n_squares.features.spell.entity.DummyPlayerEntity(dummyType, serverLevel);
        dummy.setPos(pos.x, pos.y, pos.z);
        dummy.setCustomName(net.minecraft.network.chat.Component.literal("Spells_n_Squares Dummy"));
        dummy.setCustomNameVisible(true);
        serverLevel.addFreshEntity(dummy);
        return 1;
    }
}
