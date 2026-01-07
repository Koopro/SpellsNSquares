package at.koopro.spells_n_squares.features.artifact.fluid.client;

/**
 * Client-side fluid type extension for Elixir Base.
 * Uses SimpleTintedClientFluidType with orange/gold tint.
 * Based on the approach used in Iron's Spells 'n Spellbooks mod.
 */
public class ElixirBaseClientFluidType extends SimpleTintedClientFluidType {
    // Orange/gold color for Elixir Base (RGB: 255, 165, 0)
    private static final int ELIXIR_BASE_COLOR = 0xFFA500;
    
    public ElixirBaseClientFluidType() {
        super(ELIXIR_BASE_COLOR);
    }
}

