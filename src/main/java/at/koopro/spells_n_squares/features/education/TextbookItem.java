package at.koopro.spells_n_squares.features.education;

import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import at.koopro.spells_n_squares.features.spell.SpellManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
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

/**
 * Textbook item that teaches multiple spells.
 * Represents wizarding school textbooks like "Standard Book of Spells Grade 1".
 */
public class TextbookItem extends Item {
    private final String[] spellIds;
    private final String textbookName;
    
    public TextbookItem(Properties properties, String textbookName, String... spellIds) {
        super(properties);
        this.textbookName = textbookName;
        this.spellIds = spellIds;
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
            Vec3 pos = player.position();
            int learnedCount = 0;
            int alreadyKnownCount = 0;
            
            // Try to learn each spell
            for (String spellId : spellIds) {
                Identifier spellIdentifier = SpellRegistry.spellId(spellId);
                
                if (!SpellRegistry.isRegistered(spellIdentifier)) {
                    continue; // Skip invalid spells
                }
                
                if (SpellManager.hasLearnedSpell(player, spellIdentifier)) {
                    alreadyKnownCount++;
                } else if (SpellManager.learnSpell(player, spellIdentifier)) {
                    learnedCount++;
                }
            }
            
            if (learnedCount > 0) {
                // Visual effect
                serverLevel.sendParticles(ParticleTypes.ENCHANT,
                    pos.x, pos.y + 1.5, pos.z,
                    50, 2.0, 2.0, 2.0, 0.1);
                
                level.playSound(null, pos.x, pos.y, pos.z,
                    SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0f, 1.2f);
                
                // Success message
                serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.textbook.learned", 
                    textbookName, learnedCount));
                
                if (alreadyKnownCount > 0) {
                    serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.textbook.already_known", 
                        alreadyKnownCount));
                }
                
                return InteractionResult.SUCCESS;
            } else if (alreadyKnownCount > 0) {
                serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.textbook.all_known", textbookName));
                return InteractionResult.PASS;
            } else {
                serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.textbook.no_spells", textbookName));
                return InteractionResult.PASS;
            }
        }
        return InteractionResult.PASS;
    }
    
    public void appendHoverText(ItemStack stack, java.util.List<Component> tooltip) {
        tooltip.add(Component.translatable("item.spells_n_squares.textbook.desc", textbookName, spellIds.length));
    }
}

















