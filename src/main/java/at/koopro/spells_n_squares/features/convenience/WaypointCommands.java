package at.koopro.spells_n_squares.features.convenience;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Commands for managing waypoints.
 * 
 * Available commands:
 * - /waypoint create <name> - Create a waypoint at your current location
 * - /waypoint list - List all your waypoints
 * - /waypoint remove <name> - Remove a waypoint
 * - /waypoint teleport <name> - Teleport to a waypoint
 */
public class WaypointCommands {
    
    /**
     * Registers all waypoint commands.
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("waypoint");
        
        // waypoint create <name>
        root.then(Commands.literal("create")
            .then(Commands.argument("name", StringArgumentType.string())
                .executes(ctx -> createWaypoint(ctx))));
        
        // waypoint list
        root.then(Commands.literal("list")
            .executes(ctx -> listWaypoints(ctx)));
        
        // waypoint remove <name>
        root.then(Commands.literal("remove")
            .then(Commands.argument("name", StringArgumentType.string())
                .suggests(waypointNameSuggestions())
                .executes(ctx -> removeWaypoint(ctx))));
        
        // waypoint teleport <name>
        root.then(Commands.literal("teleport")
            .then(Commands.argument("name", StringArgumentType.string())
                .suggests(waypointNameSuggestions())
                .executes(ctx -> teleportToWaypoint(ctx))));
        
        // waypoint tp <name> (alias for teleport)
        root.then(Commands.literal("tp")
            .then(Commands.argument("name", StringArgumentType.string())
                .suggests(waypointNameSuggestions())
                .executes(ctx -> teleportToWaypoint(ctx))));
        
        dispatcher.register(root);
    }
    
    /**
     * Creates a waypoint at the player's current location.
     */
    private static int createWaypoint(CommandContext<CommandSourceStack> ctx) {
        try {
            ServerPlayer player = ctx.getSource().getPlayerOrException();
            String name = StringArgumentType.getString(ctx, "name");
            
            if (name == null || name.trim().isEmpty()) {
                ctx.getSource().sendFailure(Component.literal("Waypoint name cannot be empty"));
                return 0;
            }
            
            WaypointSystem.createWaypoint(player, name.trim());
            return 1;
        } catch (CommandSyntaxException e) {
            ctx.getSource().sendFailure(Component.literal("This command can only be used by players"));
            return 0;
        }
    }
    
    /**
     * Lists all waypoints for the player.
     */
    private static int listWaypoints(CommandContext<CommandSourceStack> ctx) {
        try {
            ServerPlayer player = ctx.getSource().getPlayerOrException();
            List<WaypointSystem.Waypoint> waypoints = WaypointSystem.getWaypoints(player.getUUID());
            
            if (waypoints.isEmpty()) {
                ctx.getSource().sendSuccess(() -> Component.literal("No waypoints set"), false);
                return 0;
            }
            
            ctx.getSource().sendSuccess(() -> Component.literal(waypoints.size() + " waypoint(s) in " + 
                player.level().dimension() + ":"), false);
            
            for (WaypointSystem.Waypoint waypoint : waypoints) {
                ctx.getSource().sendSuccess(() -> Component.literal("  " + waypoint.name() + 
                    " at " + waypoint.position().getX() + ", " + 
                    waypoint.position().getY() + ", " + 
                    waypoint.position().getZ() + 
                    " (" + waypoint.dimension() + ")"), false);
            }
            
            return waypoints.size();
        } catch (CommandSyntaxException e) {
            ctx.getSource().sendFailure(Component.literal("This command can only be used by players"));
            return 0;
        }
    }
    
    /**
     * Removes a waypoint.
     */
    private static int removeWaypoint(CommandContext<CommandSourceStack> ctx) {
        try {
            ServerPlayer player = ctx.getSource().getPlayerOrException();
            String name = StringArgumentType.getString(ctx, "name");
            
            if (WaypointSystem.removeWaypoint(player.getUUID(), name)) {
                ctx.getSource().sendSuccess(() -> Component.literal("Waypoint '" + name + "' removed"), true);
                return 1;
            } else {
                ctx.getSource().sendFailure(Component.literal("Waypoint '" + name + "' not found"));
                return 0;
            }
        } catch (CommandSyntaxException e) {
            ctx.getSource().sendFailure(Component.literal("This command can only be used by players"));
            return 0;
        }
    }
    
    /**
     * Teleports the player to a waypoint.
     */
    private static int teleportToWaypoint(CommandContext<CommandSourceStack> ctx) {
        try {
            ServerPlayer player = ctx.getSource().getPlayerOrException();
            String name = StringArgumentType.getString(ctx, "name");
            
            if (WaypointSystem.teleportToWaypoint(player, name)) {
                return 1;
            } else {
                return 0;
            }
        } catch (CommandSyntaxException e) {
            ctx.getSource().sendFailure(Component.literal("This command can only be used by players"));
            return 0;
        }
    }
    
    /**
     * Suggestion provider for waypoint names.
     */
    private static SuggestionProvider<CommandSourceStack> waypointNameSuggestions() {
        return (ctx, builder) -> {
            try {
                ServerPlayer player = ctx.getSource().getPlayerOrException();
                List<WaypointSystem.Waypoint> waypoints = WaypointSystem.getWaypoints(player.getUUID());
                
                for (WaypointSystem.Waypoint waypoint : waypoints) {
                    builder.suggest(waypoint.name());
                }
                
                return builder.buildFuture();
            } catch (CommandSyntaxException e) {
                return CompletableFuture.completedFuture(builder.build());
            }
        };
    }
}
