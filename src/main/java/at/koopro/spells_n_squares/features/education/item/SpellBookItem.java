package at.koopro.spells_n_squares.features.education.item;

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
 * Spell book item that can teach spells.
 * Textbooks from the wizarding world that teach specific spells.
 */
public class SpellBookItem extends Item {
    private final String spellId;
    private final boolean consumable;
    
    public SpellBookItem(Properties properties, String spellId) {
        this(properties, spellId, true);
    }
    
    public SpellBookItem(Properties properties, String spellId, boolean consumable) {
        super(properties);
        this.spellId = spellId;
        this.consumable = consumable;
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
            Identifier spellIdentifier = SpellRegistry.spellId(spellId);
            
            // Check if spell exists
            if (!SpellRegistry.isRegistered(spellIdentifier)) {
                serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.spell_book.invalid", spellId));
                return InteractionResult.FAIL;
            }
            
            // Check if already learned
            if (SpellManager.hasLearnedSpell(player, spellIdentifier)) {
                serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.spell_book.already_known", spellId));
                return InteractionResult.PASS;
            }
            
            // Learn the spell
            boolean newlyLearned = SpellManager.learnSpell(player, spellIdentifier);
            
            if (newlyLearned) {
                Vec3 pos = player.position();
                
                // Visual effect
                serverLevel.sendParticles(ParticleTypes.ENCHANT,
                    pos.x, pos.y + 1.5, pos.z,
                    30, 1.5, 1.5, 1.5, 0.1);
                
                level.playSound(null, pos.x, pos.y, pos.z,
                    SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0f, 1.2f);
                
                // Success message
                serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.spell_book.learned", spellId));
                
                // Consume item if consumable
                if (consumable) {
                    ItemStack stack = player.getItemInHand(hand);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                }
                
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
    
    // Note: Tooltip API varies with mappings; keep this simple and non-overriding for now.
    public void appendHoverText(ItemStack stack, java.util.List<Component> tooltip) {
        tooltip.add(Component.translatable("item.spells_n_squares.spell_book.desc", spellId));
    }
}
