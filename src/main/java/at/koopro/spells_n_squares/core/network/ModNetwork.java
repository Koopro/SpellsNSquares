package at.koopro.spells_n_squares.core.network;

import at.koopro.spells_n_squares.core.registry.AddonRegistry;
import at.koopro.spells_n_squares.core.registry.ModTags;
import at.koopro.spells_n_squares.features.playerclass.network.PlayerClassSyncPayload;
import at.koopro.spells_n_squares.features.spell.LumosManager;
import at.koopro.spells_n_squares.features.spell.SpellManager;
import at.koopro.spells_n_squares.features.spell.client.ClientSpellData;
import at.koopro.spells_n_squares.features.spell.network.LumosStateSyncPayload;
import at.koopro.spells_n_squares.features.spell.network.LumosTogglePayload;
import at.koopro.spells_n_squares.features.spell.network.SpellCastPayload;
import at.koopro.spells_n_squares.features.spell.network.SpellCooldownSyncPayload;
import at.koopro.spells_n_squares.features.spell.network.SpellShootPayload;
import at.koopro.spells_n_squares.features.spell.network.SpellSlotAssignPayload;
import at.koopro.spells_n_squares.features.spell.network.SpellSlotsSyncPayload;
import at.koopro.spells_n_squares.features.convenience.WaypointSystem;
import at.koopro.spells_n_squares.features.convenience.network.WaypointListPayload;
import at.koopro.spells_n_squares.features.convenience.network.WaypointSelectionPayload;
import at.koopro.spells_n_squares.features.artifacts.network.PensieveOpenScreenPayload;
import at.koopro.spells_n_squares.features.artifacts.network.MaraudersMapPayload;
import at.koopro.spells_n_squares.features.artifacts.network.GobletOfFirePayload;
import at.koopro.spells_n_squares.core.network.FXTestPayload;
import at.koopro.spells_n_squares.features.transportation.network.BroomMovementInputPayload;
import at.koopro.spells_n_squares.features.transportation.BroomEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Handles network packet registration and processing for the mod.
 */
public class ModNetwork {
    
    /**
     * Helper method to safely execute server-side code.
     * @param context The payload context
     * @param action The action to execute if player is a ServerPlayer
     */
    private static void executeOnServer(IPayloadContext context, java.util.function.Consumer<ServerPlayer> action) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                action.accept(serverPlayer);
            }
        });
    }
    
    /**
     * Helper method to safely execute client-side code.
     * @param context The payload context
     * @param action The action to execute if on client side
     */
    private static void executeOnClient(IPayloadContext context, Runnable action) {
        context.enqueueWork(() -> {
            if (context.player() != null && context.player().level().isClientSide()) {
                action.run();
            }
        });
    }
    
    /**
     * Helper method to register a payload handler for client-to-server communication.
     * Wraps the common pattern of executeOnServer for cleaner registration code.
     * 
     * @param registrar The payload registrar
     * @param type The payload type
     * @param codec The stream codec
     * @param handler The handler function that receives the payload and ServerPlayer
     * @param <T> The payload type
     */
    private static <T extends CustomPacketPayload> void registerToServer(
            PayloadRegistrar registrar,
            CustomPacketPayload.Type<T> type,
            StreamCodec<ByteBuf, T> codec,
            BiConsumer<T, ServerPlayer> handler
    ) {
        registrar.playToServer(type, codec, (payload, context) -> 
            executeOnServer(context, serverPlayer -> handler.accept(payload, serverPlayer))
        );
    }
    
    /**
     * Helper method to register a payload handler for server-to-client communication.
     * Wraps the common pattern of executeOnClient for cleaner registration code.
     * 
     * @param registrar The payload registrar
     * @param type The payload type
     * @param codec The stream codec
     * @param handler The handler function that receives the payload
     * @param <T> The payload type
     */
    private static <T extends CustomPacketPayload> void registerToClient(
            PayloadRegistrar registrar,
            CustomPacketPayload.Type<T> type,
            StreamCodec<ByteBuf, T> codec,
            Consumer<T> handler
    ) {
        registrar.playToClient(type, codec, (payload, context) -> 
            executeOnClient(context, () -> handler.accept(payload))
        );
    }
    
    /**
     * Registers all custom network payloads.
     * This should be called from the mod's event bus during initialization.
     */
    @SubscribeEvent
    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        final var registrar = event.registrar("1");
        
        // Register addon network payloads first
        AddonRegistry.registerAllAddonNetworkPayloads(registrar);
        
        // Register spell shoot payload (client -> server)
        registerToServer(registrar, SpellShootPayload.TYPE, SpellShootPayload.STREAM_CODEC, (payload, serverPlayer) -> {
            // Handle shooting entities/blocks away while holding a spell
            if (serverPlayer.level() instanceof ServerLevel serverLevel) {
                net.minecraft.resources.Identifier activeHoldSpell = SpellManager.getActiveHoldSpell(serverPlayer);
                if (activeHoldSpell != null) {
                    at.koopro.spells_n_squares.features.spell.Spell spell = 
                        at.koopro.spells_n_squares.core.registry.SpellRegistry.get(activeHoldSpell);
                    if (spell instanceof at.koopro.spells_n_squares.features.spell.WingardiumLeviosaSpell) {
                        // Shoot entities away for Wingardium Leviosa
                        at.koopro.spells_n_squares.features.spell.WingardiumLeviosaSpell.shootEntities(serverPlayer, serverLevel);
                    }
                }
            }
        });
        
        // Register spell cast payload (client -> server)
        registerToServer(registrar, SpellCastPayload.TYPE, SpellCastPayload.STREAM_CODEC, (payload, serverPlayer) -> {
            int slot = payload.slot();
            
            // Handle stopping hold spells (slot = -1)
            if (slot == -1) {
                SpellManager.stopHoldSpell(serverPlayer);
                return;
            }
            
            // Validate slot
            if (!SpellManager.isValidSlot(slot)) {
                return; // Invalid slot, ignore
            }
            
            // Cast the spell on the server
            if (serverPlayer.level() instanceof ServerLevel serverLevel) {
                net.minecraft.resources.Identifier spellId = SpellManager.getSpellInSlot(serverPlayer, slot);
                if (spellId != null) {
                    at.koopro.spells_n_squares.features.spell.Spell spell = 
                        at.koopro.spells_n_squares.core.registry.SpellRegistry.get(spellId);
                    if (spell != null && spell.isHoldToCast()) {
                        // Check if player is already holding this spell to prevent duplicate casts
                        net.minecraft.resources.Identifier activeHoldSpell = SpellManager.getActiveHoldSpell(serverPlayer);
                        if (activeHoldSpell == null || !activeHoldSpell.equals(spellId)) {
                            // Start hold-to-cast spell (set active BEFORE casting to suppress effects)
                            SpellManager.startHoldSpell(serverPlayer, spellId);
                            // Initial cast to start the effect (effects will be suppressed because spell is now active)
                            SpellManager.castSpellInSlot(serverPlayer, serverLevel, slot);
                        }
                        // If already holding this spell, do nothing - tickHoldSpells will handle it
                    } else {
                        // Normal one-time cast
                        SpellManager.castSpellInSlot(serverPlayer, serverLevel, slot);
                    }
                }
            }
        });
        
        // Register spell slots sync payload (server -> client)
        registerToClient(registrar, SpellSlotsSyncPayload.TYPE, SpellSlotsSyncPayload.STREAM_CODEC, payload -> {
            // Use loop with SpellManager.SLOTS array for consistency
            for (int slot : SpellManager.SLOTS) {
                ClientSpellData.setSpellInSlot(slot, payload.getSlot(slot).orElse(null));
            }
        });
        
        // Register spell cooldown sync payload (server -> client)
        registerToClient(registrar, SpellCooldownSyncPayload.TYPE, SpellCooldownSyncPayload.STREAM_CODEC, payload -> {
            ClientSpellData.updateCooldowns(payload.toMap());
        });
        
        // Register spell slot assignment payload (client -> server)
        registerToServer(registrar, SpellSlotAssignPayload.TYPE, SpellSlotAssignPayload.STREAM_CODEC, (payload, serverPlayer) -> {
            // Validate slot
            if (!SpellManager.isValidSlot(payload.slot())) {
                return; // Invalid slot, ignore
            }
            
            // Assign spell to slot
            SpellManager.setSpellInSlot(serverPlayer, payload.slot(), payload.spellId().orElse(null));
        });
        
        // Register player class sync payload (server -> client)
        registerToClient(registrar, PlayerClassSyncPayload.TYPE, PlayerClassSyncPayload.STREAM_CODEC, payload -> {
            ClientSpellData.setPlayerClass(payload.getPrimaryClass());
        });
        
        // Register lumos state sync payload (server -> client)
        registrar.playToClient(
            LumosStateSyncPayload.TYPE,
            LumosStateSyncPayload.STREAM_CODEC,
            (payload, context) -> executeOnClient(context, () -> {
                if (context.player() != null) {
                    LumosManager.setLumosActive(context.player(), payload.active());
                }
            })
        );

        // Register lumos toggle payload (client -> server)
        registerToServer(registrar, LumosTogglePayload.TYPE, LumosTogglePayload.STREAM_CODEC, (payload, serverPlayer) -> {
            var stack = serverPlayer.getItemInHand(payload.hand());
            if (stack.is(ModTags.WANDS)) {
                LumosManager.toggleLumos(serverPlayer, stack);
            }
        });
        
        // Register waypoint list payload (server -> client)
        registerToClient(registrar, WaypointListPayload.TYPE, WaypointListPayload.STREAM_CODEC, payload -> {
            // Open waypoint selection screen on client
            net.minecraft.client.Minecraft.getInstance().execute(() -> {
                var waypoints = payload.waypoints().stream()
                    .map(wp -> new WaypointSystem.Waypoint(
                        wp.name(),
                        wp.dimension(),
                        wp.position(),
                        wp.createdTick()
                    ))
                    .toList();
                net.minecraft.client.Minecraft.getInstance().setScreen(
                    new at.koopro.spells_n_squares.features.convenience.client.WaypointSelectionScreen(waypoints)
                );
            });
        });
        
        // Register waypoint selection payload (client -> server)
        registerToServer(registrar, WaypointSelectionPayload.TYPE, WaypointSelectionPayload.STREAM_CODEC, (payload, serverPlayer) -> {
            // Delegate teleportation to WaypointSystem
            WaypointSystem.apparateToWaypoint(serverPlayer, payload.waypointName());
        });
        
        // Register Pensieve open screen payload (server -> client)
        registerToClient(registrar, PensieveOpenScreenPayload.TYPE, PensieveOpenScreenPayload.STREAM_CODEC, payload -> {
            // Open Pensieve memory viewing screen on client
            net.minecraft.client.Minecraft.getInstance().execute(() -> {
                var memories = payload.memories().stream()
                    .map(mem -> new at.koopro.spells_n_squares.features.artifacts.PensieveData.MemorySnapshot(
                        mem.description(),
                        mem.timestamp(),
                        mem.location()
                    ))
                    .toList();
                net.minecraft.client.Minecraft.getInstance().setScreen(
                    new at.koopro.spells_n_squares.features.artifacts.client.PensieveMemoryScreen(memories)
                );
            });
        });
        
        // Register Marauder's Map open screen payload (server -> client)
        registerToClient(registrar, MaraudersMapPayload.TYPE, MaraudersMapPayload.STREAM_CODEC, payload -> {
            // Open Marauder's Map screen on client
            net.minecraft.client.Minecraft.getInstance().execute(() -> {
                var locations = payload.playerLocations().stream()
                    .map(loc -> new at.koopro.spells_n_squares.features.artifacts.MaraudersMapData.PlayerLocation(
                        loc.playerId(),
                        loc.playerName(),
                        loc.x(),
                        loc.y(),
                        loc.z(),
                        loc.lastUpdateTick()
                    ))
                    .toList();
                net.minecraft.client.Minecraft.getInstance().setScreen(
                    new at.koopro.spells_n_squares.features.artifacts.client.MaraudersMapScreen(locations)
                );
            });
        });
        
        // Register Goblet of Fire open screen payload (server -> client)
        registerToClient(registrar, GobletOfFirePayload.TYPE, GobletOfFirePayload.STREAM_CODEC, payload -> {
            // Open Goblet of Fire tournament screen on client
            net.minecraft.client.Minecraft.getInstance().execute(() -> {
                var participants = payload.participants().stream()
                    .map(p -> new at.koopro.spells_n_squares.features.artifacts.GobletOfFireData.Participant(
                        p.playerId(),
                        p.playerName(),
                        p.entryTick()
                    ))
                    .toList();
                var champions = java.util.Set.copyOf(payload.champions());
                net.minecraft.client.Minecraft.getInstance().setScreen(
                    new at.koopro.spells_n_squares.features.artifacts.client.GobletOfFireScreen(
                        participants, champions, payload.tournamentActive())
                );
            });
        });
        
        // Register FX test payload (server -> client) for testing screen effects
        registerToClient(registrar, FXTestPayload.TYPE, FXTestPayload.STREAM_CODEC, payload -> {
            net.minecraft.client.Minecraft.getInstance().execute(() -> {
                switch (payload.effectType()) {
                    case "shake":
                        at.koopro.spells_n_squares.features.fx.ScreenEffectManager.triggerShake(
                            payload.param1(), (int)payload.param2());
                        break;
                    case "flash":
                        at.koopro.spells_n_squares.features.fx.ScreenEffectManager.triggerSpellFlash();
                        break;
                    case "vignette":
                        at.koopro.spells_n_squares.features.fx.ScreenEffectManager.triggerDamageVignette();
                        break;
                    case "cut":
                        // Use screen center coordinates (normalized 0-1 for screen space)
                        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
                        int width = mc.getWindow().getGuiScaledWidth();
                        int height = mc.getWindow().getGuiScaledHeight();
                        float centerX = width * 0.5f;
                        float centerY = height * 0.5f;
                        net.minecraft.world.phys.Vec3 start = new net.minecraft.world.phys.Vec3(
                            centerX - 50, centerY, 0);
                        net.minecraft.world.phys.Vec3 end = new net.minecraft.world.phys.Vec3(
                            centerX + 50, centerY, 0);
                        at.koopro.spells_n_squares.features.fx.CutEffectHandler.triggerCut(
                            start, end, payload.param1(), payload.param3(), true);
                        break;
                    case "pipeline_status":
                        // Check and log shader pipeline status
                        com.mojang.logging.LogUtils.getLogger().info("=== Shader Pipeline Status ===");
                        com.mojang.blaze3d.pipeline.RenderPipeline lumosPipeline = 
                            at.koopro.spells_n_squares.features.fx.PostProcessingManager.getLumosOrbRenderPipeline();
                        com.mojang.blaze3d.pipeline.RenderPipeline cutPipeline = 
                            at.koopro.spells_n_squares.features.fx.PostProcessingManager.getCutEffectRenderPipeline();
                        
                        boolean lumosValid = lumosPipeline != null && 
                            at.koopro.spells_n_squares.features.fx.PostProcessingManager.isPipelineValid(lumosPipeline);
                        boolean cutValid = cutPipeline != null && 
                            at.koopro.spells_n_squares.features.fx.PostProcessingManager.isPipelineValid(cutPipeline);
                        
                        com.mojang.logging.LogUtils.getLogger().info("Lumos Orb Pipeline: {}", 
                            lumosValid ? "VALID" : "INVALID or NOT LOADED");
                        com.mojang.logging.LogUtils.getLogger().info("Cut Effect Pipeline: {}", 
                            cutValid ? "VALID" : "INVALID or NOT LOADED");
                        com.mojang.logging.LogUtils.getLogger().info("Check client logs for detailed shader compilation messages");
                        break;
                    case "shader":
                        // Use the shader type string directly from payload
                        String shaderType = payload.shaderType();
                        if (shaderType == null || shaderType.isEmpty()) {
                            shaderType = "spell_cast"; // Default
                        }
                        at.koopro.spells_n_squares.features.fx.ShaderEffectHandler.triggerShaderEffect(
                            shaderType, payload.param1());
                        break;
                }
            });
        });
        
        // Register broom movement input payload (client -> server)
        registerToServer(registrar, BroomMovementInputPayload.TYPE, BroomMovementInputPayload.STREAM_CODEC, (payload, serverPlayer) -> {
            if (serverPlayer.level().getEntity(payload.entityId()) instanceof BroomEntity broom) {
                broom.updateMovementInput(payload.forward(), payload.strafe(), payload.jump());
            }
        });
    }
}




