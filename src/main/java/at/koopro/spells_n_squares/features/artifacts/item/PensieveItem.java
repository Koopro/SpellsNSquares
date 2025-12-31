package at.koopro.spells_n_squares.features.artifacts.item;

import at.koopro.spells_n_squares.core.data.ItemDataHelper;
import at.koopro.spells_n_squares.features.artifacts.data.PensieveData;
import at.koopro.spells_n_squares.core.util.effect.EffectUtils;
import at.koopro.spells_n_squares.features.artifacts.base.BaseArtifactItem;
import at.koopro.spells_n_squares.features.artifacts.network.PensieveOpenScreenPayload;
import at.koopro.spells_n_squares.features.artifacts.util.ArtifactDataHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

/**
 * Pensieve artifact that stores and replays memories.
 */
public class PensieveItem extends BaseArtifactItem {
    
    public PensieveItem(Properties properties) {
        super(properties);
    }
    
    @Override
    protected InteractionResult onArtifactUse(Level level, ServerPlayer player, InteractionHand hand, ItemStack stack) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.FAIL;
        }
        
        PensieveData.PensieveComponent component = ArtifactDataHelper.getPensieveData(stack);
        
        // Store current location as a memory
        Vec3 pos = player.position();
        String location = String.format("%.0f, %.0f, %.0f", pos.x, pos.y, pos.z);
        String description = "Memory from dimension";
        
        component = component.addMemory(description, serverLevel.getGameTime(), location);
        ItemDataHelper.setData(stack, PensieveData.PENSIEVE_DATA.get(), component);
        
        // Show stored memories - open GUI on client
        List<PensieveData.MemorySnapshot> memories = component.memories();
        if (memories.isEmpty()) {
            sendMessage(player, "message.spells_n_squares.pensieve.no_memories");
        } else {
            // Send network packet to open memory viewing screen on client
            var memoryDataList = memories.stream()
                .map(mem -> new PensieveOpenScreenPayload.MemoryData(
                    mem.description(),
                    mem.timestamp(),
                    mem.location()
                ))
                .toList();
            
            var payload = new PensieveOpenScreenPayload(memoryDataList);
            PacketDistributor.sendToPlayer(player, payload);
        }
        
        // Visual and audio feedback (override default to use custom sound)
        Vec3 eyePos = player.getEyePosition();
        EffectUtils.spawnMagicalParticles(serverLevel, eyePos);
        serverLevel.playSound(null, eyePos.x, eyePos.y, eyePos.z,
            SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.7f, 1.2f);
        
        return InteractionResult.SUCCESS;
    }
    
    @Override
    protected void spawnArtifactEffects(net.minecraft.server.level.ServerLevel level, ServerPlayer player) {
        // Effects already spawned in onArtifactUse with custom sound
        // Override to prevent double spawning
    }
    
    /**
     * Gets the Pensieve data component from an item stack.
     * @deprecated Use {@link ArtifactDataHelper#getPensieveData(ItemStack)} instead
     */
    @Deprecated(forRemoval = false) // Keep for backward compatibility
    public static PensieveData.PensieveComponent getPensieveData(ItemStack stack) {
        return ArtifactDataHelper.getPensieveData(stack);
    }
}
