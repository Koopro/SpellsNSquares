package at.koopro.spells_n_squares.features.artifact.fluid;

import at.koopro.spells_n_squares.features.artifact.ArtifactRegistry;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * Helper class for converting between Elixir items and FluidStacks.
 * Similar to PotionFluid helper methods.
 */
public final class ElixirFluidHelper {
    private ElixirFluidHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Standard amount for one elixir item (in millibuckets).
     * One bucket = 1000 mB, so one elixir item = 250 mB (like potions).
     */
    private static final int ELIXIR_AMOUNT = 250;
    
    /**
     * Converts an Elixir of Life item stack to a FluidStack.
     * 
     * @param stack The item stack (must be ElixirOfLifeItem)
     * @return A FluidStack containing the elixir, or empty if invalid
     */
    public static FluidStack fromItemStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return FluidStack.EMPTY;
        }
        
        // Check if it's an Elixir of Life item
        if (stack.is(ArtifactRegistry.ELIXIR_OF_LIFE.get())) {
            FluidStack fluidStack = new FluidStack(ElixirFluids.ELIXIR_OF_LIFE.get(), ELIXIR_AMOUNT);
            return fluidStack;
        }
        
        return FluidStack.EMPTY;
    }
    
    /**
     * Converts a FluidStack to an Elixir of Life item stack.
     * 
     * @param stack The fluid stack (must be Elixir of Life fluid)
     * @return An ItemStack containing the elixir item, or empty if invalid
     */
    public static ItemStack toItemStack(FluidStack stack) {
        if (stack.isEmpty() || stack.getAmount() < ELIXIR_AMOUNT) {
            return ItemStack.EMPTY;
        }
        
        // Check if it's Elixir of Life fluid
        if (stack.getFluid() == ElixirFluids.ELIXIR_OF_LIFE.get()) {
            ItemStack itemStack = new ItemStack(ArtifactRegistry.ELIXIR_OF_LIFE.get());
            return itemStack;
        }
        
        return ItemStack.EMPTY;
    }
    
    /**
     * Creates a FluidStack of Elixir Base with the specified amount.
     * 
     * @param amount The amount in millibuckets
     * @return A FluidStack of Elixir Base
     */
    public static FluidStack elixirBase(int amount) {
        return new FluidStack(ElixirFluids.ELIXIR_BASE.get(), amount);
    }
    
    /**
     * Creates a FluidStack of Elixir of Life with the specified amount.
     * 
     * @param amount The amount in millibuckets
     * @return A FluidStack of Elixir of Life
     */
    public static FluidStack elixirOfLife(int amount) {
        return new FluidStack(ElixirFluids.ELIXIR_OF_LIFE.get(), amount);
    }
    
    /**
     * Creates a standard bucket (1000 mB) of Elixir Base.
     * 
     * @return A FluidStack containing 1000 mB of Elixir Base
     */
    public static FluidStack elixirBaseBucket() {
        return elixirBase(1000);
    }
    
    /**
     * Creates a standard bucket (1000 mB) of Elixir of Life.
     * 
     * @return A FluidStack containing 1000 mB of Elixir of Life
     */
    public static FluidStack elixirOfLifeBucket() {
        return elixirOfLife(1000);
    }
}

