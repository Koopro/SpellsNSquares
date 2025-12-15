package at.koopro.spells_n_squares.network;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import at.koopro.spells_n_squares.core.network.SpellCastPayload;
import at.koopro.spells_n_squares.core.network.SpellCooldownSyncPayload;
import at.koopro.spells_n_squares.core.network.SpellSlotAssignPayload;
import at.koopro.spells_n_squares.core.network.SpellSlotsSyncPayload;
import at.koopro.spells_n_squares.features.playerclass.network.PlayerClassSyncPayload;
import at.koopro.spells_n_squares.features.spell.LumosManager;
import at.koopro.spells_n_squares.features.spell.SpellManager;
import at.koopro.spells_n_squares.features.spell.client.ClientSpellData;
import at.koopro.spells_n_squares.features.spell.network.LumosStateSyncPayload;
import at.koopro.spells_n_squares.features.spell.network.LumosTogglePayload;
import at.koopro.spells_n_squares.core.registry.ModTags;

/**
 * Handles network packet registration and processing for the mod.
 */
public class ModNetwork {
    
    /**
     * Registers all custom network payloads.
     * This should be called from the mod's event bus during initialization.
     */
    @SubscribeEvent
    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        final var registrar = event.registrar("1");
        
        // Register spell cast payload (client -> server)
        registrar.playToServer(
            SpellCastPayload.TYPE,
            SpellCastPayload.STREAM_CODEC,
            SpellCastPayloadHandler::handle
        );
        
        // Register spell slots sync payload (server -> client)
        registrar.playToClient(
            SpellSlotsSyncPayload.TYPE,
            SpellSlotsSyncPayload.STREAM_CODEC,
            SpellSlotsSyncPayloadHandler::handle
        );
        
        // Register spell cooldown sync payload (server -> client)
        registrar.playToClient(
            SpellCooldownSyncPayload.TYPE,
            SpellCooldownSyncPayload.STREAM_CODEC,
            SpellCooldownSyncPayloadHandler::handle
        );
        
        // Register spell slot assignment payload (client -> server)
        registrar.playToServer(
            SpellSlotAssignPayload.TYPE,
            SpellSlotAssignPayload.STREAM_CODEC,
            SpellSlotAssignPayloadHandler::handle
        );
        
        // Register player class sync payload (server -> client)
        registrar.playToClient(
            PlayerClassSyncPayload.TYPE,
            PlayerClassSyncPayload.STREAM_CODEC,
            PlayerClassSyncPayloadHandler::handle
        );
        
        // Register lumos state sync payload (server -> client)
        registrar.playToClient(
            LumosStateSyncPayload.TYPE,
            LumosStateSyncPayload.STREAM_CODEC,
            LumosStateSyncPayloadHandler::handle
        );

        // Register lumos toggle payload (client -> server)
        registrar.playToServer(
            LumosTogglePayload.TYPE,
            LumosTogglePayload.STREAM_CODEC,
            LumosTogglePayloadHandler::handle
        );
    }
    
    /**
     * Handles spell cast payloads on the server side.
     */
    private static class SpellCastPayloadHandler {
        public static void handle(SpellCastPayload payload, IPayloadContext context) {
            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer serverPlayer) {
                    // Validate slot
                    if (payload.slot() < 0 || payload.slot() > 3) {
                        return; // Invalid slot, ignore
                    }
                    
                    // Cast the spell on the server
                    if (serverPlayer.level() instanceof ServerLevel serverLevel) {
                        SpellManager.castSpellInSlot(serverPlayer, serverLevel, payload.slot());
                    }
                }
            });
        }
    }
    
    /**
     * Handles spell slots sync payloads on the client side.
     */
    private static class SpellSlotsSyncPayloadHandler {
        public static void handle(SpellSlotsSyncPayload payload, IPayloadContext context) {
            context.enqueueWork(() -> {
                if (context.player() != null && context.player().level().isClientSide()) {
                    ClientSpellData.setSpellSlot(SpellManager.SLOT_TOP, payload.top().orElse(null));
                    ClientSpellData.setSpellSlot(SpellManager.SLOT_BOTTOM, payload.bottom().orElse(null));
                    ClientSpellData.setSpellSlot(SpellManager.SLOT_LEFT, payload.left().orElse(null));
                    ClientSpellData.setSpellSlot(SpellManager.SLOT_RIGHT, payload.right().orElse(null));
                }
            });
        }
    }
    
    /**
     * Handles spell cooldown sync payloads on the client side.
     */
    private static class SpellCooldownSyncPayloadHandler {
        public static void handle(SpellCooldownSyncPayload payload, IPayloadContext context) {
            context.enqueueWork(() -> {
                if (context.player() != null && context.player().level().isClientSide()) {
                    ClientSpellData.updateCooldowns(payload.toMap());
                }
            });
        }
    }
    
    /**
     * Handles spell slot assignment payloads on the server side.
     */
    private static class SpellSlotAssignPayloadHandler {
        public static void handle(SpellSlotAssignPayload payload, IPayloadContext context) {
            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer serverPlayer) {
                    // Validate slot
                    if (payload.slot() < 0 || payload.slot() > 3) {
                        return; // Invalid slot, ignore
                    }
                    
                    // Assign spell to slot
                    SpellManager.setSpellInSlot(serverPlayer, payload.slot(), payload.spellId().orElse(null));
                }
            });
        }
    }
    
    /**
     * Handles player class sync payloads on the client side.
     */
    private static class PlayerClassSyncPayloadHandler {
        public static void handle(PlayerClassSyncPayload payload, IPayloadContext context) {
            context.enqueueWork(() -> {
                if (context.player() != null && context.player().level().isClientSide()) {
                    ClientSpellData.setPlayerClass(payload.playerClass());
                }
            });
        }
    }
    
    /**
     * Handles lumos state sync payloads on the client side.
     */
    private static class LumosStateSyncPayloadHandler {
        public static void handle(LumosStateSyncPayload payload, IPayloadContext context) {
            context.enqueueWork(() -> {
                if (context.player() != null && context.player().level().isClientSide()) {
                    LumosManager.setLumosActive(context.player(), payload.active());
                }
            });
        }
    }

    /**
     * Handles lumos toggle requests on the server side.
     */
    private static class LumosTogglePayloadHandler {
        public static void handle(LumosTogglePayload payload, IPayloadContext context) {
            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer serverPlayer) {
                    var stack = serverPlayer.getItemInHand(payload.hand());
                    if (stack.is(ModTags.WANDS)) {
                        LumosManager.toggleLumos(serverPlayer, stack);
                    }
                }
            });
        }
    }
}
