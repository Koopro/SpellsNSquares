package at.koopro.spells_n_squares.core.registry;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.features.storage.StorageRegistry;
import at.koopro.spells_n_squares.features.wand.registry.WandRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for all mod creative mode tabs.
 */
public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = 
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SpellsNSquares.MODID);
    
    // Wands and spells tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> WANDS_SPELLS_TAB = CREATIVE_TABS.register(
        "wands_spells",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.spells_n_squares.wands_spells"))
            .icon(() -> new ItemStack(WandRegistry.DEMO_WAND.get()))
            .displayItems((parameters, output) -> {
                output.accept(WandRegistry.DEMO_WAND.get());
                // Add other wand/spell related items here
            })
            .build()
    );
    
    // Storage tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> STORAGE_TAB = CREATIVE_TABS.register(
        "storage",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.spells_n_squares.storage"))
            .icon(() -> new ItemStack(StorageRegistry.NEWTS_CASE_ITEM.get()))
            .displayItems((parameters, output) -> {
                output.accept(StorageRegistry.POCKET_DIMENSION.get());
                output.accept(StorageRegistry.NEWTS_CASE_ITEM.get());
            })
            .build()
    );
}
