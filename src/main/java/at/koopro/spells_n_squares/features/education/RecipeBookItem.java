package at.koopro.spells_n_squares.features.education;

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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Recipe Book - potion recipes (Advanced Potion Making style).
 * Displays information about known potion recipes.
 */
public class RecipeBookItem extends Item {
    
    private static final String[] KNOWN_RECIPES = {
        "Healing Potion - Regeneration",
        "Strength Potion - Strength Boost",
        "Invisibility Potion - Invisibility",
        "Pepperup Potion - Regeneration + Fire Resistance",
        "Skele-Gro - Regeneration + Strength",
        "Wit-Sharpening Potion - Night Vision + Speed",
        "Polyjuice Potion - Invisibility + Night Vision",
        "Draught of Living Death - Weakness + Slowness"
    };
    
    public RecipeBookItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
            Vec3 pos = player.position();
            
            // Visual effect
            serverLevel.sendParticles(ParticleTypes.ENCHANT,
                pos.x, pos.y + 1.0, pos.z,
                20, 1.0, 1.0, 1.0, 0.1);
            
            level.playSound(null, pos.x, pos.y, pos.z,
                SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 0.8f, 1.0f);
            
            // Display recipe book
            serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.recipe_book.title"));
            serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.recipe_book.known_recipes", KNOWN_RECIPES.length));
            
            for (String recipe : KNOWN_RECIPES) {
                serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.recipe_book.recipe", recipe));
            }
            
            return InteractionResult.SUCCESS;
        }
        
        return InteractionResult.PASS;
    }
}
















