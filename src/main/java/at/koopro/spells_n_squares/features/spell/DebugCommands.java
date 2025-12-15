package at.koopro.spells_n_squares.features.spell;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import at.koopro.spells_n_squares.features.spell.entity.LightningBeamEntity;

/**
 * Simple debug commands to test spell visuals.
 *
 * /spells_n_squaresdebug lightning  - fires a beam from wand tip to crosshair hit
 * /spells_n_squaresdebug dummy      - spawns a dummy player-like entity (uses a real player clone for now)
 */
public class DebugCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("spells_n_squaresdebug");

        root.then(Commands.literal("lightning")
            .executes(ctx -> debugLightning(ctx.getSource())));

        root.then(Commands.literal("dummy")
            .executes(ctx -> spawnDummy(ctx.getSource())));
        root.then(Commands.literal("dummyplayer")
            .executes(ctx -> spawnDummyPlayer(ctx.getSource())));

        dispatcher.register(root);
    }

    private static int debugLightning(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player) || !(source.getLevel() instanceof ServerLevel serverLevel)) {
            return 0;
        }

        // Raycast like the lightning spell (32 blocks)
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

        // For now, just spawn a harmless armor stand at the crosshair hit to act as a dummy target.
        var hitResult = player.pick(16.0, 1.0f, false);
        Vec3 pos = hitResult.getLocation();
        net.minecraft.world.entity.decoration.ArmorStand armorStand = new net.minecraft.world.entity.decoration.ArmorStand(
            net.minecraft.world.entity.EntityType.ARMOR_STAND,
            serverLevel
        );
        armorStand.setPos(pos.x, pos.y, pos.z);
        armorStand.setCustomName(net.minecraft.network.chat.Component.literal("Spells_n_Squares Dummy"));
        armorStand.setInvulnerable(false);
        serverLevel.addFreshEntity(armorStand);
        return 1;
    }

    private static int spawnDummyPlayer(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player) || !(source.getLevel() instanceof ServerLevel serverLevel)) {
            return 0;
        }

        // Raycast 16 blocks and spawn a dummy player entity there
        var hitResult = player.pick(16.0, 1.0f, false);
        Vec3 pos = hitResult.getLocation();

        var dummyType = at.koopro.spells_n_squares.core.registry.ModEntities.DUMMY_PLAYER.get();
        var dummy = new at.koopro.spells_n_squares.features.spell.entity.DummyPlayerEntity(dummyType, serverLevel);
        dummy.setPos(pos.x, pos.y, pos.z);
        dummy.setCustomName(net.minecraft.network.chat.Component.literal("Spells_n_Squares Dummy"));
        dummy.setCustomNameVisible(true);
        serverLevel.addFreshEntity(dummy);
        return 1;
    }
}

