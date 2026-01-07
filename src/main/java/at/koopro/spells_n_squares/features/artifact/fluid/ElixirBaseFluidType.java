package at.koopro.spells_n_squares.features.artifact.fluid;

import at.koopro.spells_n_squares.features.artifact.fluid.client.ElixirBaseClientFluidType;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.function.Consumer;

/**
 * Custom FluidType for Elixir Base that uses water texture with orange/gold tint.
 * Improved version following potion fluid patterns.
 */
public class ElixirBaseFluidType extends FluidType {
    
    public ElixirBaseFluidType(Properties properties) {
        super(properties);
    }
    
    public void initializeClient(Consumer<net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions> consumer) {
        consumer.accept(new ElixirBaseClientFluidType());
    }
    
    @Override
    public Component getDescription(FluidStack stack) {
        return Component.translatable("fluid.spells_n_squares.elixir_base");
    }
}

