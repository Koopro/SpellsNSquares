package at.koopro.spells_n_squares.features.artifacts;

import at.koopro.spells_n_squares.features.artifacts.network.PensieveOpenScreenPayload;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

/**
 * Pensieve artifact that stores and replays memories.
 */
public class PensieveItem extends Item {
    
    public PensieveItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (level.isClientSide()) {
            return InteractionResult.PASS;
        }
        
        if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.FAIL;
        }
        
        PensieveData.PensieveComponent component = getPensieveData(stack);
        
        // Store current location as a memory
        Vec3 pos = player.position();
        String location = String.format("%.0f, %.0f, %.0f", pos.x, pos.y, pos.z);
        String description = "Memory from dimension";
        
        component = component.addMemory(description, serverLevel.getGameTime(), location);
        stack.set(PensieveData.PENSIEVE_DATA.get(), component);
        
        // Show stored memories - open GUI on client
        List<PensieveData.MemorySnapshot> memories = component.memories();
        if (memories.isEmpty()) {
            serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.pensieve.no_memories"));
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
            PacketDistributor.sendToPlayer(serverPlayer, payload);
        }
        
        // Visual and audio feedback
        Vec3 eyePos = player.getEyePosition();
        serverLevel.sendParticles(ParticleTypes.ENCHANT,
            eyePos.x, eyePos.y, eyePos.z,
            30, 0.5, 0.5, 0.5, 0.1);
        
        serverLevel.sendParticles(ParticleTypes.END_ROD,
            eyePos.x, eyePos.y, eyePos.z,
            20, 0.3, 0.3, 0.3, 0.05);
        
        level.playSound(null, eyePos.x, eyePos.y, eyePos.z,
            SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.7f, 1.2f);
        
        return InteractionResult.SUCCESS;
    }
    
    /**
     * Gets the Pensieve data component from an item stack.
     */
    public static PensieveData.PensieveComponent getPensieveData(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof PensieveItem)) {
            return new PensieveData.PensieveComponent();
        }
        
        PensieveData.PensieveComponent data = stack.get(PensieveData.PENSIEVE_DATA.get());
        if (data == null) {
            data = new PensieveData.PensieveComponent();
            stack.set(PensieveData.PENSIEVE_DATA.get(), data);
        }
        return data;
    }
}
