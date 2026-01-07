package at.koopro.spells_n_squares.core.registry.addon;

import at.koopro.spells_n_squares.core.util.registry.AddonRegistryUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Helper class for addons to register blocks.
 * Provides a DeferredRegister for the addon's namespace.
 */
public final class AddonBlockRegistry {
    private final String addonId;
    private final DeferredRegister<Block> blocks;
    
    public AddonBlockRegistry(String addonId) {
        this.addonId = addonId;
        this.blocks = DeferredRegister.create(Registries.BLOCK, addonId);
    }
    
    /**
     * Gets the DeferredRegister for blocks.
     * Register this with the mod event bus during registry registration phase.
     * @return The DeferredRegister
     */
    public DeferredRegister<Block> getDeferredRegister() {
        return blocks;
    }
    
    /**
     * Registers a block.
     * @param name The block name (will be used as the registry path)
     * @param blockSupplier The supplier that creates the block
     * @return The DeferredHolder for the registered block
     */
    public net.neoforged.neoforge.registries.DeferredHolder<Block, Block> registerBlock(String name, Supplier<Block> blockSupplier) {
        return blocks.register(name, id -> {
            // Ensure the identifier uses the correct addon namespace
            AddonRegistryUtils.validateNamespace(addonId, id, "block");
            return blockSupplier.get();
        });
    }
    
    /**
     * Registers a block with custom properties factory.
     * @param name The block name
     * @param blockFactory The factory that creates the block from an Identifier
     * @return The DeferredHolder for the registered block
     */
    public net.neoforged.neoforge.registries.DeferredHolder<Block, Block> registerBlock(String name, Function<Identifier, Block> blockFactory) {
        return blocks.register(name, blockFactory);
    }
    
    /**
     * Creates an Identifier for a block within the addon's namespace.
     * @param path The path part of the block ID
     * @return The Identifier
     */
    public Identifier blockId(String path) {
        return AddonRegistryUtils.addonId(addonId, path);
    }
    
    /**
     * Gets the addon ID.
     * @return The addon ID
     */
    public String getAddonId() {
        return addonId;
    }
}









