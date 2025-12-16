package at.koopro.spells_n_squares.block.automation;

import at.koopro.spells_n_squares.block.BaseInteractiveBlock;
import at.koopro.spells_n_squares.features.potions.PotionBrewingManager;
import at.koopro.spells_n_squares.features.potions.PotionRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Self-stirring cauldron block for automated potion brewing.
 */
public class SelfStirringCauldronBlock extends BaseInteractiveBlock {
    
    public SelfStirringCauldronBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    protected String getInteractionMessageKey() {
        return "message.spells_n_squares.cauldron.description";
    }
    
    @Override
    protected InteractionResult onServerInteract(BlockState state, Level level, BlockPos pos, 
                                                  ServerPlayer serverPlayer, InteractionHand hand, 
                                                  BlockHitResult hit) {
        ItemStack heldItem = serverPlayer.getItemInHand(hand);
        
        // Check if there's an active brewing session
        Optional<PotionBrewingManager.BrewingSession> session = PotionBrewingManager.getBrewingSession(pos);
        
        if (session.isPresent()) {
            PotionBrewingManager.BrewingSession brew = session.get();
            float progress = brew.getProgressPercent();
            serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.cauldron.brewing", 
                (int)(progress * 100)));
            return InteractionResult.SUCCESS;
        }
        
        // Try to start brewing if player is holding ingredients
        if (!heldItem.isEmpty() && heldItem.getItem() instanceof at.koopro.spells_n_squares.features.potions.PotionIngredientItem) {
            // Collect ingredients from player inventory
            List<ItemStack> ingredients = collectIngredients(serverPlayer);
            
            if (ingredients.isEmpty()) {
                serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.cauldron.no_ingredients"));
                return InteractionResult.FAIL;
            }
            
            // Find matching recipe
            Optional<PotionRecipe> recipe = PotionBrewingManager.findMatchingRecipe(ingredients);
            
            if (recipe.isEmpty()) {
                serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.cauldron.no_recipe"));
                return InteractionResult.FAIL;
            }
            
            // Start brewing
            if (PotionBrewingManager.startBrewing(pos, recipe.get(), ingredients)) {
                serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.cauldron.brewing_started", 
                    recipe.get().potionType()));
                return InteractionResult.SUCCESS;
            }
        }
        
        // Default message
        serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.cauldron.description"));
        return InteractionResult.SUCCESS;
    }
    
    /**
     * Collects potion ingredients from player's inventory.
     */
    private List<ItemStack> collectIngredients(Player player) {
        List<ItemStack> ingredients = new ArrayList<>();
        
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.getItem() instanceof at.koopro.spells_n_squares.features.potions.PotionIngredientItem) {
                ingredients.add(stack.copy());
            }
        }
        
        return ingredients;
    }
    
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, net.minecraft.util.RandomSource random) {
        if (level.isClientSide()) {
            return;
        }
        
        // Check if brewing is active
        Optional<PotionBrewingManager.BrewingSession> session = PotionBrewingManager.getBrewingSession(pos);
        boolean isBrewing = session.isPresent();
        
        if (level instanceof ServerLevel serverLevel) {
            Vec3 center = Vec3.atCenterOf(pos);
            
            // More particles when brewing
            float particleChance = isBrewing ? 0.2f : 0.1f;
            
            if (random.nextFloat() < particleChance) {
                serverLevel.sendParticles(ParticleTypes.BUBBLE,
                    center.x, center.y, center.z,
                    isBrewing ? 5 : 2, 0.2, 0.1, 0.2, 0.01);
            }
            
            // Add enchant particles when brewing
            if (isBrewing && random.nextFloat() < 0.05f) {
                serverLevel.sendParticles(ParticleTypes.ENCHANT,
                    center.x, center.y, center.z,
                    3, 0.3, 0.1, 0.3, 0.02);
            }
        }
    }
}

