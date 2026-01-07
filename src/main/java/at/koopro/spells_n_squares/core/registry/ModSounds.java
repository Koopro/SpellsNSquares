package at.koopro.spells_n_squares.core.registry;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.util.registry.ModIdentifierHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for all mod sounds.
 */
public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, SpellsNSquares.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> LUMOS = SOUNDS.register(
            "lumos",
            () -> SoundEvent.createVariableRangeEvent(ModIdentifierHelper.modId("lumos"))
    );
    
    public static final DeferredHolder<SoundEvent, SoundEvent> NOX = SOUNDS.register(
            "nox",
            () -> SoundEvent.createVariableRangeEvent(ModIdentifierHelper.modId("nox"))
    );
}
