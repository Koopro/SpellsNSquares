package at.koopro.spells_n_squares.core.commands.debug.dummy;

import at.koopro.spells_n_squares.core.data.PlayerModelDataComponent;
import at.koopro.spells_n_squares.core.util.rendering.ColorUtils;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * Debug commands for dummy player operations.
 */
public final class DummyPlayerDebugCommands {
    
    private DummyPlayerDebugCommands() {}
    
    /**
     * Builds the dummy_player command structure.
     */
    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        LiteralArgumentBuilder<CommandSourceStack> dummyPlayer = Commands.literal("dummy_player");
        
        dummyPlayer.then(Commands.literal("spawn")
            .then(Commands.argument("modelType", StringArgumentType.string())
                .executes(DummyPlayerDebugCommands::dummyPlayerSpawn)))
        .then(Commands.literal("scale")
            .then(Commands.argument("entity", EntityArgument.entity())
                .then(Commands.argument("scale", FloatArgumentType.floatArg(0.1f, 10.0f))
                    .executes(DummyPlayerDebugCommands::dummyPlayerScale))))
        .then(Commands.literal("model")
            .then(Commands.argument("entity", EntityArgument.entity())
                .then(Commands.argument("modelType", StringArgumentType.string())
                    .executes(DummyPlayerDebugCommands::dummyPlayerModel))))
        .then(Commands.literal("item")
            .then(Commands.argument("entity", EntityArgument.entity())
                .then(Commands.argument("item", StringArgumentType.string())
                    .executes(DummyPlayerDebugCommands::dummyPlayerItem))))
        .then(Commands.literal("remove")
            .then(Commands.argument("entity", EntityArgument.entity())
                .executes(DummyPlayerDebugCommands::dummyPlayerRemove)));
        
        return dummyPlayer;
    }
    
    private static int dummyPlayerSpawn(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        String modelTypeStr = StringArgumentType.getString(ctx, "modelType");
        
        at.koopro.spells_n_squares.features.spell.entity.DummyPlayerModelType modelType = 
            at.koopro.spells_n_squares.features.spell.entity.DummyPlayerModelType.fromString(modelTypeStr);
        
        if (!(player.level() instanceof ServerLevel level)) {
            ctx.getSource().sendFailure(Component.literal("Must be executed on server"));
            return 0;
        }
        Vec3 pos = player.position().add(player.getLookAngle().scale(3.0));
        
        at.koopro.spells_n_squares.features.spell.entity.DummyPlayerEntity dummyPlayer = 
            new at.koopro.spells_n_squares.features.spell.entity.DummyPlayerEntity(
                at.koopro.spells_n_squares.features.spell.manager.SpellEntityRegistry.DUMMY_PLAYER.get(), 
                level
            );
        dummyPlayer.setModelType(modelType);
        dummyPlayer.setPos(pos.x, pos.y, pos.z);
        
        level.addFreshEntity(dummyPlayer);
        
        ctx.getSource().sendSuccess(() -> ColorUtils.coloredText(
            "Spawned dummy player (" + modelType.toString() + ") at " + 
            String.format("%.1f, %.1f, %.1f", pos.x, pos.y, pos.z), 
            ColorUtils.SPELL_GREEN), true);
        return 1;
    }
    
    private static int dummyPlayerScale(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        net.minecraft.world.entity.Entity entity = EntityArgument.getEntity(ctx, "entity");
        if (!(entity instanceof at.koopro.spells_n_squares.features.spell.entity.DummyPlayerEntity dummyPlayer)) {
            ctx.getSource().sendFailure(Component.literal("Entity is not a dummy player"));
            return 0;
        }
        
        float scale = FloatArgumentType.getFloat(ctx, "scale");
        PlayerModelDataComponent.PlayerModelData data = dummyPlayer.getModelData();
        dummyPlayer.setModelData(data.withScale(scale));
        
        ctx.getSource().sendSuccess(() -> ColorUtils.coloredText(
            "Set dummy player scale to " + scale, 
            ColorUtils.SPELL_GREEN), true);
        return 1;
    }
    
    private static int dummyPlayerModel(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        net.minecraft.world.entity.Entity entity = EntityArgument.getEntity(ctx, "entity");
        if (!(entity instanceof at.koopro.spells_n_squares.features.spell.entity.DummyPlayerEntity dummyPlayer)) {
            ctx.getSource().sendFailure(Component.literal("Entity is not a dummy player"));
            return 0;
        }
        
        String modelTypeStr = StringArgumentType.getString(ctx, "modelType");
        at.koopro.spells_n_squares.features.spell.entity.DummyPlayerModelType modelType = 
            at.koopro.spells_n_squares.features.spell.entity.DummyPlayerModelType.fromString(modelTypeStr);
        
        dummyPlayer.setModelType(modelType);
        
        ctx.getSource().sendSuccess(() -> ColorUtils.coloredText(
            "Set dummy player model to " + modelType.toString(), 
            ColorUtils.SPELL_GREEN), true);
        return 1;
    }
    
    private static int dummyPlayerItem(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        net.minecraft.world.entity.Entity entity = EntityArgument.getEntity(ctx, "entity");
        if (!(entity instanceof at.koopro.spells_n_squares.features.spell.entity.DummyPlayerEntity dummyPlayer)) {
            ctx.getSource().sendFailure(Component.literal("Entity is not a dummy player"));
            return 0;
        }
        
        String itemName = StringArgumentType.getString(ctx, "item");
        Identifier itemId;
        if (itemName.contains(":")) {
            itemId = Identifier.parse(itemName);
        } else {
            itemId = Identifier.fromNamespaceAndPath("minecraft", itemName);
        }
        
        if (!(dummyPlayer.level() instanceof ServerLevel level)) {
            ctx.getSource().sendFailure(Component.literal("Entity level is not a ServerLevel"));
            return 0;
        }
        var registry = level.registryAccess().lookup(Registries.ITEM);
        if (registry.isEmpty()) {
            ctx.getSource().sendFailure(Component.literal("Item registry not available"));
            return 0;
        }
        
        Item item = registry.get().getValue(itemId);
        if (item == null) {
            ctx.getSource().sendFailure(Component.literal("Item not found: " + itemName));
            return 0;
        }
        
        ItemStack itemStack = new ItemStack(item, 1);
        dummyPlayer.setMainHandItem(itemStack);
        
        ctx.getSource().sendSuccess(() -> ColorUtils.coloredText(
            "Set dummy player main hand item to " + itemStack.getDisplayName().getString(), 
            ColorUtils.SPELL_GREEN), true);
        return 1;
    }
    
    private static int dummyPlayerRemove(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        net.minecraft.world.entity.Entity entity = EntityArgument.getEntity(ctx, "entity");
        if (!(entity instanceof at.koopro.spells_n_squares.features.spell.entity.DummyPlayerEntity)) {
            ctx.getSource().sendFailure(Component.literal("Entity is not a dummy player"));
            return 0;
        }
        
        entity.remove(net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
        
        ctx.getSource().sendSuccess(() -> ColorUtils.coloredText(
            "Removed dummy player", 
            ColorUtils.SPELL_GREEN), true);
        return 1;
    }
}

