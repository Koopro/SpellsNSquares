package at.koopro.spells_n_squares.features.education;

import at.koopro.spells_n_squares.features.spell.SpellManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * Spell Journal - tracks learned spells (like Hermione's notes).
 * Displays all spells the player has learned.
 */
public class SpellJournalItem extends Item {
    
    public SpellJournalItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            // Get all learned spells
            List<String> learnedSpells = new ArrayList<>();
            
            // Check each spell slot
            net.minecraft.resources.Identifier topSpell = SpellManager.getSpellInSlot(player, SpellManager.SLOT_TOP);
            net.minecraft.resources.Identifier bottomSpell = SpellManager.getSpellInSlot(player, SpellManager.SLOT_BOTTOM);
            net.minecraft.resources.Identifier leftSpell = SpellManager.getSpellInSlot(player, SpellManager.SLOT_LEFT);
            net.minecraft.resources.Identifier rightSpell = SpellManager.getSpellInSlot(player, SpellManager.SLOT_RIGHT);
            
            if (topSpell != null) learnedSpells.add(topSpell.getPath());
            if (bottomSpell != null) learnedSpells.add(bottomSpell.getPath());
            if (leftSpell != null) learnedSpells.add(leftSpell.getPath());
            if (rightSpell != null) learnedSpells.add(rightSpell.getPath());
            
            // Display journal
            serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.spell_journal.title"));
            serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.spell_journal.count", learnedSpells.size()));
            
            if (learnedSpells.isEmpty()) {
                serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.spell_journal.empty"));
            } else {
                for (String spellId : learnedSpells) {
                    serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.spell_journal.spell", spellId));
                }
            }
            
            return InteractionResult.SUCCESS;
        }
        
        return InteractionResult.PASS;
    }
}












