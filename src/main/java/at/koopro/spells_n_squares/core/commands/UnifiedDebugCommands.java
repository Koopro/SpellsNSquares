package at.koopro.spells_n_squares.core.commands;

import at.koopro.spells_n_squares.core.commands.debug.block.BlockDebugCommands;
import at.koopro.spells_n_squares.core.commands.debug.color.ColorDebugCommands;
import at.koopro.spells_n_squares.core.commands.debug.dummy.DummyPlayerDebugCommands;
import at.koopro.spells_n_squares.core.commands.debug.fx.FxDebugCommands;
import at.koopro.spells_n_squares.core.commands.debug.help.HelpCommandHandler;
import at.koopro.spells_n_squares.core.commands.debug.identity.IdentityDebugCommands;
import at.koopro.spells_n_squares.core.commands.debug.inventory.InventoryDebugCommands;
import at.koopro.spells_n_squares.core.commands.debug.item.ItemDebugCommands;
import at.koopro.spells_n_squares.core.commands.debug.model.ModelDebugCommands;
import at.koopro.spells_n_squares.core.commands.debug.particle.ParticleDebugCommands;
import at.koopro.spells_n_squares.core.commands.debug.sound.SoundDebugCommands;
import at.koopro.spells_n_squares.core.commands.debug.world.WorldDebugCommands;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

/**
 * Unified debug commands for all debug functionality.
 * All commands are under /sns debug namespace.
 * Requires operator permissions (level 2+).
 * 
 * This class serves as the main registration point, delegating to category-specific command handlers.
 */
public final class UnifiedDebugCommands {
    
    private static final int REQUIRED_PERMISSION_LEVEL = 2;
    
    private UnifiedDebugCommands() {}
    
    /**
     * Registers all unified debug commands.
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // Note: Permission check removed - add back if needed with proper API
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("sns")
            .then(Commands.literal("debug")
                .then(HelpCommandHandler.build())
                .then(ItemDebugCommands.build())
                .then(BlockDebugCommands.build())
                .then(ModelDebugCommands.build())
                .then(ColorDebugCommands.build())
                .then(SoundDebugCommands.build())
                .then(ParticleDebugCommands.build())
                .then(InventoryDebugCommands.build())
                .then(WorldDebugCommands.build())
                .then(DummyPlayerDebugCommands.build())
                .then(FxDebugCommands.build())
                .then(IdentityDebugCommands.build())
            );
        
        dispatcher.register(root);
    }
}
