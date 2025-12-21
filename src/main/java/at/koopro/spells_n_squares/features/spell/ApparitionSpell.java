package at.koopro.spells_n_squares.features.spell;

import at.koopro.spells_n_squares.features.convenience.WaypointSystem;
import at.koopro.spells_n_squares.features.convenience.network.WaypointListPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Set;

/**
 * Apparition spell for line-of-sight teleportation with Splinching risk.
 * Sneak to teleport to saved waypoints.
 */
public class ApparitionSpell implements Spell {
    
    private static final int COOLDOWN = 80; // 4 seconds
    private static final double RANGE = 100.0;
    private static final double BASE_SPLINCH_CHANCE = 0.05; // 5%
    private static final double MAX_SPLINCH_CHANCE = 0.80; // 80%
    
    @Override
    public Identifier getId() {
        return at.koopro.spells_n_squares.core.registry.SpellRegistry.spellId("apparition");
    }
    
    @Override
    public String getName() {
        return "Apparition";
    }
    
    @Override
    public String getDescription() {
        return "Line-of-sight teleportation. Sneak to teleport to waypoints. Risk of Splinching if used carelessly.";
    }
    
    @Override
    public int getCooldown() {
        return COOLDOWN;
    }
    
    @Override
    public boolean cast(Player player, Level level) {
        if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }
        
        Vec3 originPos = player.position();
        
        // Check if player is sneaking and has waypoints - teleport to waypoint(s)
        if (player.isShiftKeyDown()) {
            return handleWaypointTeleport(serverPlayer, serverLevel, originPos);
        }
        
        // Normal line-of-sight teleportation
        return handleLineOfSightTeleport(player, serverPlayer, level, serverLevel, originPos);
    }
    
    private boolean handleWaypointTeleport(ServerPlayer serverPlayer, ServerLevel serverLevel, Vec3 originPos) {
        var waypoints = WaypointSystem.getWaypoints(serverPlayer.getUUID());
        
        if (waypoints.isEmpty()) {
            serverPlayer.sendSystemMessage(Component.literal("No waypoints available. Create one first!"));
            return false;
        }
        
        // If multiple waypoints, send list to client for selection
        if (waypoints.size() > 1) {
            var waypointDataList = waypoints.stream()
                .map(wp -> new WaypointListPayload.WaypointData(
                    wp.name(),
                    wp.dimension(),
                    wp.position(),
                    wp.createdTick()
                ))
                .toList();
            
            var payload = new WaypointListPayload(waypointDataList);
            PacketDistributor.sendToPlayer(serverPlayer, payload);
            return true; // Spell "cast" - selection will happen via payload
        }
        
        // Single waypoint - teleport immediately
        var waypoint = waypoints.get(0);
        ServerLevel targetLevel = serverLevel.getServer().getLevel(waypoint.dimension());
        
        if (targetLevel == null) {
            serverPlayer.sendSystemMessage(Component.literal("Cannot apparate - dimension not loaded"));
            return false;
        }
        
        Vec3 destPos = new Vec3(
            waypoint.position().getX() + 0.5,
            waypoint.position().getY(),
            waypoint.position().getZ() + 0.5
        );
        
        // Effects at origin
        spawnApparitionParticles(serverLevel, originPos);
        playApparitionSound(serverLevel, originPos);
        
        // Calculate Splinch chance (reduced for waypoint travel)
        double splinchChance = calculateSplinchChance(serverPlayer) * 0.5;
        if (serverLevel.getRandom().nextDouble() < splinchChance) {
            handleSplinching(serverPlayer, serverLevel);
        }
        
        // Teleport player
        serverPlayer.teleportTo(targetLevel, destPos.x, destPos.y, destPos.z,
            Set.of(), serverPlayer.getYRot(), serverPlayer.getXRot(), false);
        
        // Effects at destination
        spawnApparitionParticles(targetLevel, destPos);
        playApparitionSound(targetLevel, destPos);
        
        serverPlayer.sendSystemMessage(Component.literal("Apparated to waypoint '" + waypoint.name() + "'"));
        return true;
    }
    
    private boolean handleLineOfSightTeleport(Player player, ServerPlayer serverPlayer, 
            Level level, ServerLevel serverLevel, Vec3 originPos) {
        // Raycast to find destination
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        Vec3 endPos = eyePos.add(lookVec.scale(RANGE));
        
        ClipContext clipContext = new ClipContext(
            eyePos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player
        );
        
        BlockHitResult hitResult = level.clip(clipContext);
        
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return false; // No valid block to teleport to
        }
        
        BlockPos hitPos = hitResult.getBlockPos();
        Vec3 targetPos = findSafeTeleportPosition(level, hitPos, hitResult.getDirection());
        
        // Fallback positioning
        if (targetPos == null) {
            BlockPos topPos = hitPos.above();
            if (isSafePosition(level, topPos) && isSafePosition(level, topPos.above())) {
                targetPos = new Vec3(topPos.getX() + 0.5, topPos.getY(), topPos.getZ() + 0.5);
            } else {
                Vec3 hitVec = hitResult.getLocation();
                targetPos = new Vec3(hitVec.x, hitPos.getY() + 1.0, hitVec.z);
            }
        }
        
        // Effects at origin
        spawnApparitionParticles(serverLevel, originPos);
        playApparitionSound(level, originPos);
        
        // Handle Splinching
        double splinchChance = calculateSplinchChance(player);
        if (level.getRandom().nextDouble() < splinchChance) {
            handleSplinching(serverPlayer, serverLevel);
        }
        
        // Teleport player
        player.teleportTo(targetPos.x, targetPos.y, targetPos.z);
        
        // Effects at destination
        spawnApparitionParticles(serverLevel, targetPos);
        playApparitionSound(level, targetPos);
        
        return true;
    }
    
    private Vec3 findSafeTeleportPosition(Level level, BlockPos hitPos, Direction hitDirection) {
        BlockPos startPos = hitPos.above();
        
        for (int i = 0; i < 3; i++) {
            BlockPos feetPos = startPos.offset(0, i, 0);
            BlockPos headPos = feetPos.above();
            
            if (isSafePosition(level, feetPos) && isSafePosition(level, headPos)) {
                BlockPos groundPos = feetPos.below();
                BlockState groundState = level.getBlockState(groundPos);
                if (!groundState.isAir() && groundState.getFluidState().isEmpty()) {
                    return new Vec3(feetPos.getX() + 0.5, feetPos.getY(), feetPos.getZ() + 0.5);
                }
            }
        }
        
        return null;
    }
    
    private boolean isSafePosition(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return (state.isAir() || state.getCollisionShape(level, pos).isEmpty()) && 
               !state.getFluidState().is(FluidTags.LAVA);
    }
    
    private double calculateSplinchChance(Player player) {
        double chance = BASE_SPLINCH_CHANCE;
        
        if (player.isSprinting()) {
            chance += 0.20;
        }
        
        Vec3 velocity = player.getDeltaMovement();
        double speed = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
        if (speed > 0.1) {
            chance += 0.15;
        }
        
        if (velocity.y < -0.2) {
            chance += 0.10;
        }
        
        if (player.getHealth() < 10.0f) {
            chance += 0.15;
        }
        
        return Math.min(chance, MAX_SPLINCH_CHANCE);
    }
    
    private void handleSplinching(ServerPlayer player, ServerLevel serverLevel) {
        boolean majorSplinch = serverLevel.getRandom().nextDouble() < 0.3;
        
        if (majorSplinch) {
            player.hurtServer(serverLevel, serverLevel.damageSources().magic(), 8.0f);
            
            player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 600, 2, false, true, true));
            player.addEffect(new MobEffectInstance(MobEffects.MINING_FATIGUE, 600, 1, false, true, true));
            player.addEffect(new MobEffectInstance(MobEffects.WITHER, 600, 0, false, true, true));
            
            if (serverLevel.getRandom().nextDouble() < 0.1) {
                ItemStack offHand = player.getOffhandItem();
                if (!offHand.isEmpty()) {
                    ItemEntity itemEntity = new ItemEntity(
                        serverLevel, player.getX(), player.getY(), player.getZ(), offHand.copy()
                    );
                    serverLevel.addFreshEntity(itemEntity);
                    player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
                }
            }
            
            player.sendSystemMessage(Component.literal("You have been Splinched! Your body parts were left behind..."));
        } else {
            player.hurtServer(serverLevel, serverLevel.damageSources().magic(), 4.0f);
            player.sendSystemMessage(Component.literal("You have been Splinched!"));
        }
        
        serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.PLAYER_HURT, SoundSource.PLAYERS, 1.0f, 0.5f);
        
        serverLevel.sendParticles(ParticleTypes.DAMAGE_INDICATOR,
            player.getX(), player.getY() + 1.0, player.getZ(),
            15, 0.5, 0.5, 0.5, 0.1);
    }
    
    private void spawnApparitionParticles(ServerLevel level, Vec3 pos) {
        level.sendParticles(ParticleTypes.POOF, pos.x, pos.y, pos.z, 30, 0.5, 0.5, 0.5, 0.1);
        level.sendParticles(ParticleTypes.SQUID_INK, pos.x, pos.y, pos.z, 20, 0.6, 0.6, 0.6, 0.05);
        level.sendParticles(ParticleTypes.CLOUD, pos.x, pos.y, pos.z, 15, 0.4, 0.4, 0.4, 0.08);
        level.sendParticles(ParticleTypes.PORTAL, pos.x, pos.y, pos.z, 25, 0.5, 0.5, 0.5, 0.05);
    }
    
    private void playApparitionSound(Level level, Vec3 pos) {
        level.playSound(null, pos.x, pos.y, pos.z,
            SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.8f, 2.0f);
        level.playSound(null, pos.x, pos.y, pos.z,
            SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.6f, 1.5f);
    }
    
    @Override
    public float getVisualEffectIntensity() {
        return 0.7f;
    }
}
