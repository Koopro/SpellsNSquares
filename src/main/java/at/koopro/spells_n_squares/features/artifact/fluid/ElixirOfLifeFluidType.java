package at.koopro.spells_n_squares.features.artifact.fluid;

import at.koopro.spells_n_squares.features.artifact.fluid.client.ElixirOfLifeClientFluidType;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.function.Consumer;

/**
 * Custom FluidType for Elixir of Life that uses water texture with gold/yellow tint.
 * Improved version following potion fluid patterns.
 */
public class ElixirOfLifeFluidType extends FluidType {
    
    public ElixirOfLifeFluidType(Properties properties) {
        super(properties);
    }
    
    public void initializeClient(Consumer<net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions> consumer) {
        consumer.accept(new ElixirOfLifeClientFluidType());
    }
    
    @Override
    public Component getDescription(FluidStack stack) {
        return Component.translatable("fluid.spells_n_squares.elixir_of_life");
    }
}

