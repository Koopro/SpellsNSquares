package at.koopro.spells_n_squares.features.artifact.fluid.client;

/**
 * Client-side fluid type extension for Elixir of Life.
 * Uses SimpleTintedClientFluidType with gold/yellow tint.
 * Based on the approach used in Iron's Spells 'n Spellbooks mod.
 */
public class ElixirOfLifeClientFluidType extends SimpleTintedClientFluidType {
    // Gold/yellow color for Elixir of Life (RGB: 255, 215, 0)
    private static final int ELIXIR_OF_LIFE_COLOR = 0xFFD700;
    
    public ElixirOfLifeClientFluidType() {
        super(ELIXIR_OF_LIFE_COLOR);
    }
}

