package at.koopro.spells_n_squares.features.artifacts.item;

import at.koopro.spells_n_squares.features.artifacts.GobletOfFireData;
import at.koopro.spells_n_squares.features.artifacts.base.BaseArtifactItem;
import at.koopro.spells_n_squares.features.artifacts.network.GobletOfFirePayload;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Goblet of Fire - tournament selection artifact.
 * Now extends BaseArtifactItem for common artifact patterns.
 */
public class GobletOfFireItem extends BaseArtifactItem {
    private static final int CHAMPIONS_TO_SELECT = 3;
    
    public GobletOfFireItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        
        if (!(level instanceof ServerLevel serverLevel) || !(context.getPlayer() instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.FAIL;
        }
        
        ItemStack stack = context.getItemInHand();
        // Enter tournament when right-clicking on a block
        return enterTournament(serverLevel, serverPlayer, stack);
    }
    
    @Override
    protected InteractionResult onArtifactUse(Level level, ServerPlayer player, InteractionHand hand, ItemStack stack) {
        GobletOfFireData.GobletOfFireComponent component = getGobletOfFireData(stack);
        
        // Open tournament screen
        var participantDataList = component.participants().stream()
            .map(p -> new GobletOfFirePayload.ParticipantData(
                p.playerId(),
                p.playerName(),
                p.entryTick()
            ))
            .toList();
        
        var payload = new GobletOfFirePayload(
            participantDataList,
            component.selectedChampions(),
            component.tournamentActive()
        );
        PacketDistributor.sendToPlayer(player, payload);
        
        return InteractionResult.SUCCESS;
    }
    
    /**
     * Enters a player into the tournament.
     */
    private InteractionResult enterTournament(ServerLevel level, ServerPlayer player, ItemStack stack) {
        GobletOfFireData.GobletOfFireComponent component = getGobletOfFireData(stack);
        
        if (component.hasParticipant(player.getUUID())) {
            player.sendSystemMessage(Component.translatable("message.spells_n_squares.goblet_of_fire.already_entered"));
            return InteractionResult.FAIL;
        }
        
        // Add participant
        GobletOfFireData.Participant participant = new GobletOfFireData.Participant(
            player.getUUID(),
            player.getName().getString(),
            level.getGameTime()
        );
        
        GobletOfFireData.GobletOfFireComponent newComponent = component.withParticipant(participant);
        stack.set(GobletOfFireData.GOBLET_OF_FIRE_DATA.get(), newComponent);
        
        player.sendSystemMessage(Component.translatable("message.spells_n_squares.goblet_of_fire.entered"));
        
        // Visual effect
        level.sendParticles(ParticleTypes.FLAME,
            player.getX(), player.getY() + 1.0, player.getZ(),
            20, 0.3, 0.3, 0.3, 0.05);
        
        level.playSound(null, player.blockPosition(), SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0f, 1.0f);
        
        // Auto-select champions if enough participants
        if (newComponent.participants().size() >= CHAMPIONS_TO_SELECT && !newComponent.tournamentActive()) {
            selectChampions(level, stack, newComponent);
        }
        
        return InteractionResult.SUCCESS;
    }
    
    /**
     * Selects champions from participants.
     */
    private void selectChampions(ServerLevel level, ItemStack stack, GobletOfFireData.GobletOfFireComponent component) {
        List<GobletOfFireData.Participant> participants = component.participants();
        if (participants.size() < CHAMPIONS_TO_SELECT) {
            return;
        }
        
        // Randomly select champions
        List<GobletOfFireData.Participant> shuffled = new ArrayList<>(participants);
        Collections.shuffle(shuffled, new Random(level.getGameTime()));
        
        List<UUID> champions = new ArrayList<>();
        for (int i = 0; i < Math.min(CHAMPIONS_TO_SELECT, shuffled.size()); i++) {
            champions.add(shuffled.get(i).playerId());
        }
        
        GobletOfFireData.GobletOfFireComponent newComponent = component.selectChampions(champions);
        stack.set(GobletOfFireData.GOBLET_OF_FIRE_DATA.get(), newComponent);
        
        // Announce champions
        for (UUID championId : champions) {
            Player champion = level.getPlayerByUUID(championId);
            if (champion instanceof ServerPlayer serverChampion) {
                serverChampion.sendSystemMessage(Component.translatable("message.spells_n_squares.goblet_of_fire.selected"));
            }
        }
        
        // Visual effect at goblet location (if placed as block, use block pos; otherwise use player pos)
        // Simplified: just show particles around champions
        for (UUID championId : champions) {
            Player champion = level.getPlayerByUUID(championId);
            if (champion != null) {
                level.sendParticles(ParticleTypes.FLAME,
                    champion.getX(), champion.getY() + 1.0, champion.getZ(),
                    50, 1.0, 1.0, 1.0, 0.1);
            }
        }
    }
    
    /**
     * Gets the goblet of fire data component from an item stack.
     */
    public static GobletOfFireData.GobletOfFireComponent getGobletOfFireData(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof GobletOfFireItem)) {
            return new GobletOfFireData.GobletOfFireComponent();
        }
        
        return at.koopro.spells_n_squares.core.data.DataComponentHelper.getOrCreateData(
            stack,
            GobletOfFireData.GOBLET_OF_FIRE_DATA.get(),
            GobletOfFireData.GobletOfFireComponent::new
        );
    }
}
