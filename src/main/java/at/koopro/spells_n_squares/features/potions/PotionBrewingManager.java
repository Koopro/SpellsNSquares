package at.koopro.spells_n_squares.features.potions;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.*;

/**
 * Manages potion brewing logic and active brewing sessions.
 */
public final class PotionBrewingManager {
    private PotionBrewingManager() {
    }
    
    // Map of cauldron positions to active brewing sessions
    private static final Map<BlockPos, BrewingSession> activeBrews = new HashMap<>();
    
    // Registry of all potion recipes
    private static final List<PotionRecipe> recipes = new ArrayList<>();
    
    /**
     * Represents an active brewing session.
     */
    public static class BrewingSession {
        private final PotionRecipe recipe;
        private final List<ItemStack> ingredients;
        private int progress; // Current progress in ticks
        private final int totalTime;
        public BrewingSession(PotionRecipe recipe, List<ItemStack> ingredients, BlockPos cauldronPos) {
            this.recipe = recipe;
            this.ingredients = new ArrayList<>(ingredients);
            this.totalTime = recipe.brewingTime();
            this.progress = 0;
        }
        
        public boolean tick(ServerLevel level) {
            progress++;
            return progress >= totalTime;
        }
        
        public PotionRecipe getRecipe() {
            return recipe;
        }
        
        public int getProgress() {
            return progress;
        }
        
        public int getTotalTime() {
            return totalTime;
        }
        
        public float getProgressPercent() {
            return (float) progress / totalTime;
        }
        
        public ItemStack createResult() {
            int quality = recipe.calculateQuality(ingredients);
            PotionData.PotionComponent component = new PotionData.PotionComponent(
                recipe.result().effects(),
                quality,
                recipe.result().potionType()
            );
            
            // Create potion item stack (will be created by specific potion item)
            ItemStack stack = new ItemStack(getPotionItemForType(recipe.potionType()), 1);
            stack.set(PotionData.POTION_DATA.get(), component);
            return stack;
        }
        
        private net.minecraft.world.item.Item getPotionItemForType(String type) {
            // Resolve potion item by type, preferring the dedicated potions registry.
            return switch (type) {
                case "felix_felicis" ->
                    at.koopro.spells_n_squares.features.potions.PotionsRegistry.FELIX_FELICIS.get();
                case "veritaserum" ->
                    at.koopro.spells_n_squares.features.potions.PotionsRegistry.VERITASERUM.get();
                case "shrinking_solution" ->
                    at.koopro.spells_n_squares.features.potions.PotionsRegistry.SHRINKING_SOLUTION_POTION.get();
                case "swelling_solution" ->
                    at.koopro.spells_n_squares.features.potions.PotionsRegistry.SWELLING_SOLUTION_POTION.get();
                case "murtlap_essence" ->
                    at.koopro.spells_n_squares.features.potions.PotionsRegistry.MURTLAP_ESSENCE_POTION.get();
                case "draught_of_living_death" ->
                    at.koopro.spells_n_squares.features.potions.PotionsRegistry.DRAUGHT_OF_LIVING_DEATH_POTION.get();
                case "draught_of_peace" ->
                    at.koopro.spells_n_squares.features.potions.PotionsRegistry.DRAUGHT_OF_PEACE_POTION.get();
                default -> net.minecraft.world.item.Items.POTION; // Fallback
            };
        }
    }
    
    /**
     * Registers a potion recipe.
     */
    public static void registerRecipe(PotionRecipe recipe) {
        recipes.add(recipe);
    }
    
    /**
     * Gets all registered recipes.
     */
    public static List<PotionRecipe> getRecipes() {
        return Collections.unmodifiableList(recipes);
    }
    
    /**
     * Finds a matching recipe for the given ingredients.
     */
    public static Optional<PotionRecipe> findMatchingRecipe(List<ItemStack> ingredients) {
        for (PotionRecipe recipe : recipes) {
            if (recipe.matchesIngredients(ingredients)) {
                return Optional.of(recipe);
            }
        }
        return Optional.empty();
    }
    
    /**
     * Starts a brewing session at a cauldron.
     */
    public static boolean startBrewing(BlockPos cauldronPos, PotionRecipe recipe, List<ItemStack> ingredients) {
        if (activeBrews.containsKey(cauldronPos)) {
            return false; // Already brewing
        }
        
        BrewingSession session = new BrewingSession(recipe, ingredients, cauldronPos);
        activeBrews.put(cauldronPos, session);
        return true;
    }
    
    /**
     * Gets the active brewing session at a cauldron.
     */
    public static Optional<BrewingSession> getBrewingSession(BlockPos cauldronPos) {
        return Optional.ofNullable(activeBrews.get(cauldronPos));
    }
    
    /**
     * Ticks all active brewing sessions.
     */
    public static void tickBrewingSessions(ServerLevel level) {
        Iterator<Map.Entry<BlockPos, BrewingSession>> iterator = activeBrews.entrySet().iterator();
        
        while (iterator.hasNext()) {
            Map.Entry<BlockPos, BrewingSession> entry = iterator.next();
            BlockPos pos = entry.getKey();
            BrewingSession session = entry.getValue();
            
            // Check if cauldron still exists
            if (!level.getBlockState(pos).getBlock().equals(
                at.koopro.spells_n_squares.features.automation.AutomationRegistry.SELF_STIRRING_CAULDRON.get())) {
                iterator.remove();
                continue;
            }
            
            // Tick the session
            if (session.tick(level)) {
                // Brewing complete - create result
                ItemStack result = session.createResult();
                
                // Spawn result item above the cauldron
                Vec3 spawnPos = Vec3.atCenterOf(pos).add(0, 0.5, 0);
                ItemEntity itemEntity = new ItemEntity(level, spawnPos.x, spawnPos.y, spawnPos.z, result);
                itemEntity.setDefaultPickUpDelay();
                level.addFreshEntity(itemEntity);
                
                // Spawn completion particles
                level.sendParticles(
                    net.minecraft.core.particles.ParticleTypes.ENCHANT,
                    spawnPos.x, spawnPos.y, spawnPos.z,
                    20, 0.3, 0.3, 0.3, 0.1
                );

                // Notify nearby players that brewing has finished
                level.players().stream()
                    .filter(player -> player.distanceToSqr(spawnPos.x, spawnPos.y, spawnPos.z) <= 64)
                    .forEach(player -> player.displayClientMessage(
                        net.minecraft.network.chat.Component.translatable(
                            "message.spells_n_squares.potion.brew_complete",
                            session.getRecipe().id().toString()
                        ),
                        true
                    ));
                
                iterator.remove();
            }
        }
    }
    
    /**
     * Cancels a brewing session.
     */
    public static void cancelBrewing(BlockPos cauldronPos) {
        activeBrews.remove(cauldronPos);
    }
    
    /**
     * Initializes default potion recipes.
     */
    public static void initializeDefaultRecipes() {
        // Recipes will be registered when ingredient items are created
        // This method will be called from ModInitialization
    }
}
