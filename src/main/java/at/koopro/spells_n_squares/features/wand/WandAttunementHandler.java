package at.koopro.spells_n_squares.features.wand;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.api.addon.events.AddonEventBus;
import at.koopro.spells_n_squares.core.api.addon.events.SpellCastEvent;
import at.koopro.spells_n_squares.core.registry.ModTags;
import at.koopro.spells_n_squares.features.wand.WandVisualEffects;
import at.koopro.spells_n_squares.core.util.EventUtils;
import at.koopro.spells_n_squares.core.util.PlayerItemUtils;
import at.koopro.spells_n_squares.features.spell.SpellManager;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles wand attunement rituals.
 * Players must cast spells in a specific sequence to attune their wand.
 */
public class WandAttunementHandler {
    
    // Track attunement progress per player (player -> sequence progress)
    private static final Map<Player, AttunementProgress> attunementProgress = new HashMap<>();
    
    // Timeout for attunement sequence (10 seconds)
    private static final int SEQUENCE_TIMEOUT = 200; // ticks
    
    /**
     * Represents attunement progress for a player.
     */
    private static class AttunementProgress {
        final ItemStack wand;
        final WandCore core;
        final WandWood wood;
        int sequenceIndex;
        int lastCastTick;
        
        AttunementProgress(ItemStack wand, WandCore core, WandWood wood, int currentTick) {
            this.wand = wand;
            this.core = core;
            this.wood = wood;
            this.sequenceIndex = 0;
            this.lastCastTick = currentTick;
        }
    }
    
    /**
     * Starts an attunement ritual for a player.
     * Returns true if the ritual can start (wand has core/wood but isn't attuned).
     */
    public static boolean startAttunement(Player player, Level level) {
        ItemStack wand = PlayerItemUtils.findHeldItemByTag(player, ModTags.WANDS).orElse(ItemStack.EMPTY);
        if (wand.isEmpty()) {
            return false;
        }
        
        WandCore core = WandDataHelper.getCore(wand);
        WandWood wood = WandDataHelper.getWood(wand);
        
        if (core == null || wood == null) {
            return false;
        }
        
        if (WandDataHelper.isAttuned(wand)) {
            return false; // Already attuned
        }
        
        // Start the ritual
        attunementProgress.put(player, new AttunementProgress(wand, core, wood, (int) level.getGameTime()));
        return true;
    }
    
    /**
     * Checks if a player is currently performing an attunement ritual.
     */
    public static boolean isAttuning(Player player) {
        return attunementProgress.containsKey(player);
    }
    
    /**
     * Gets the required spell sequence for attuning a wand.
     * The sequence depends on the core/wood combination.
     */
    private static Identifier[] getRequiredSequence(WandCore core, WandWood wood) {
        // Simple sequence: cast Lumos, then Accio, then Protego
        // In a full implementation, this could vary by core/wood
        return new Identifier[]{
            Identifier.fromNamespaceAndPath(SpellsNSquares.MODID, "lumos"),
            Identifier.fromNamespaceAndPath(SpellsNSquares.MODID, "accio"),
            Identifier.fromNamespaceAndPath(SpellsNSquares.MODID, "protego")
        };
    }
    
    /**
     * Initialize the handler by registering with AddonEventBus and game event bus.
     * This should be called during mod initialization.
     */
    public static void initialize() {
        WandAttunementHandler instance = new WandAttunementHandler();
        AddonEventBus.getInstance().register(instance);
        // Register instance to game event bus for PlayerTickEvent
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.register(instance);
    }
    
    /**
     * Called when a spell is cast. Checks if it matches the attunement sequence.
     * Also spawns wand visual effects.
     */
    @net.neoforged.bus.api.SubscribeEvent
    public void onSpellCast(SpellCastEvent event) {
        if (event.isCanceled()) {
            return;
        }
        
        Player player = event.getPlayer();
        Identifier spellId = event.getSpell().getId();
        Level level = event.getLevel();
        
        at.koopro.spells_n_squares.core.util.SafeEventHandler.execute(() -> {
            // Spawn wand visual effects
            ItemStack wand = PlayerItemUtils.findHeldItemByTag(player, ModTags.WANDS).orElse(ItemStack.EMPTY);
            if (!wand.isEmpty()) {
                WandVisualEffects.spawnCastTrail(level, player, wand);
            }
            AttunementProgress progress = attunementProgress.get(player);
            if (progress == null) {
                return;
            }
            
            // Check timeout
            int currentTick = (int) level.getGameTime();
            if (currentTick - progress.lastCastTick > SEQUENCE_TIMEOUT) {
                // Sequence timed out
                attunementProgress.remove(player);
                return;
            }
            
            // Get required sequence
            Identifier[] sequence = getRequiredSequence(progress.core, progress.wood);
            
            // Check if the cast spell matches the next in sequence
            if (spellId.equals(sequence[progress.sequenceIndex])) {
                progress.sequenceIndex++;
                progress.lastCastTick = currentTick;
                
                // Check if sequence is complete
                if (progress.sequenceIndex >= sequence.length) {
                    // Success! Attune the wand
                    WandDataHelper.setAttuned(progress.wand, true);
                    attunementProgress.remove(player);
                    
                    // Visual feedback
                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(
                            net.minecraft.core.particles.ParticleTypes.TOTEM_OF_UNDYING,
                            player.getX(), player.getY() + 1.0, player.getZ(),
                            20, 0.5, 0.5, 0.5, 0.1
                        );
                    }
                }
            } else {
                // Wrong spell - reset sequence
                attunementProgress.remove(player);
                
                // Apply misfire debuff
                if (level instanceof ServerLevel serverLevel) {
                    // Random spell cast as penalty
                    int randomSlot = level.getRandom().nextInt(SpellManager.MAX_SLOTS);
                    SpellManager.castSpellInSlot(player, level, randomSlot);
                }
            }
        }, "handling spell cast for attunement", player);
    }
    
    /**
     * Ticks attunement progress and cleans up timed-out sequences.
     */
    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        if (!EventUtils.isServerSide(event)) {
            return;
        }
        
        Player player = event.getEntity();
        at.koopro.spells_n_squares.core.util.SafeEventHandler.execute(() -> {
            AttunementProgress progress = attunementProgress.get(player);
            if (progress == null) {
                return;
            }
            
            // Check timeout
            int currentTick = (int) player.level().getGameTime();
            if (currentTick - progress.lastCastTick > SEQUENCE_TIMEOUT) {
                attunementProgress.remove(player);
            }
        }, "ticking wand attunement", player);
    }
    
    /**
     * Cleans up attunement progress when player disconnects.
     */
    public static void clearPlayerData(Player player) {
        attunementProgress.remove(player);
    }
}
