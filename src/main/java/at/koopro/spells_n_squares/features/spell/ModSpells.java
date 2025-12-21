package at.koopro.spells_n_squares.features.spell;

import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Registry and initialization of all mod spells.
 * 
 * To create a new spell:
 * 1. Create a class that implements the Spell interface
 * 2. Register it in the register() method below
 * 3. Create an icon texture at: assets/{modid}/textures/spell/{spell_name}.png (16x16 pixels recommended)
 * 
 * For addons: Simply register spells with your mod's namespace, and place icons in:
 * assets/{your_modid}/textures/spell/{spell_id_path}.png
 */
public class ModSpells {
    
    /**
     * Registers all spells in the mod.
     * Call this during mod initialization.
     */
    public static void register() {
        // Core spells
        // TODO: Re-enable when spell classes are implemented (HealSpell, TeleportSpell, FireballSpell, LightningSpell, ProtegoSpell, ApparitionSpell, LumosSpell)
        
        // Utility spells
        // TODO: Re-enable when spell classes are implemented (EntityDetectionSpell, ExtensionCharmSpell, PortableCraftingSpell, ItemRecallSpell, NoxSpell, HomenumRevelioSpell, RevelioSpell, FiniteIncantatemSpell, ApareciumSpell, PriorIncantatoSpell)
        
        // Weather control spells
        // TODO: Re-enable when spell classes are implemented (MetelojinxSpell, AraniaExumaiSpell, VentusSpell)
        
        // Advanced defensive spells
        // TODO: Re-enable when spell classes are implemented (ExpelliarmusSpell, StupefySpell)
        
        // Combat spells
        // TODO: Re-enable when spell classes are implemented (BombardaSpell, ConfringoSpell, DiffindoSpell, DepulsoSpell, DescendoSpell, FlipendoSpell, ImpedimentaSpell, RictusempraSpell)
        
        // Charm spells
        // TODO: Re-enable when spell classes are implemented (ReparoSpell, AlohomoraSpell, LevitationSpell, ColloportusSpell, DuroSpell, EngorgioSpell, ReducioSpell, ScourgifySpell, TergeoSpell)
        
        SpellRegistry.register(new AccioSpell());
        SpellRegistry.register(new RiddikulusSpell());
        
        // Healing spells
        // TODO: Re-enable when spell classes are implemented (EpiskeySpell, VulneraSanenturSpell, AnapneoSpell, FerulaSpell)
        
        // Memory/mental spells
        // TODO: Re-enable when spell classes are implemented (LegilimensSpell, OcclumencySpell)
        
        // Transfiguration spells
        // TODO: Re-enable when spell classes are implemented (TransfigurationSpell, SerpensortiaSpell, AvisSpell)
        
        // Curses (Dark Magic)
        // TODO: Re-enable when spell classes are implemented (CrucioSpell, AvadaKedavraSpell, ImperioSpell, SectumsempraSpell)
        
        // Fire spells
        // TODO: Re-enable when spell classes are implemented (IncendioSpell)
        
        // Defensive/Protective spells
        // TODO: Re-enable when spell classes are implemented (PatronusSpell)
        
        // Utility/Combat spells
        // TODO: Re-enable when spell classes are implemented (PetrificusTotalusSpell, ConfundoSpell, AguamentiSpell, GlaciusSpell, ReductoSpell, ObliviateSpell)
        
        // Class joining spells
        // TODO: Re-enable when spell classes are implemented (MorsmordreSpell, PhoenixCallSpell)
        
        // Animagus spells
        // TODO: Re-enable when spell classes are implemented (AnimagusTransformationSpell)
        
        // Additional utility spells
        SpellRegistry.register(new ImmobulusSpell());
        SpellRegistry.register(new SilencioSpell());
        // TODO: Re-enable when spell classes are implemented (IncarcerousSpell)
        
        // Vanishing and movement spells
        SpellRegistry.register(new EvanescoSpell());
        SpellRegistry.register(new LocomotorSpell());
        
        // Jinxes and hexes
        SpellRegistry.register(new TarantallegraSpell());
        SpellRegistry.register(new LanglockSpell());
        SpellRegistry.register(new LevicorpusSpell());
        SpellRegistry.register(new LiberacorpusSpell());
        
        // Communication and utility spells
        SpellRegistry.register(new SonorusSpell());
        SpellRegistry.register(new MuffliatoSpell());
        SpellRegistry.register(new OrchideousSpell());
        SpellRegistry.register(new PericulumSpell());
    }
    
    /**
     * Helper class for creating simple spells without implementing all methods.
     * Provides sensible defaults for optional Spell interface methods.
     */
    public static abstract class SimpleSpell implements Spell {
        private final Identifier id;
        private final String name;
        private final String description;
        private final int cooldown;
        private final Identifier icon;
        
        public SimpleSpell(Identifier id, String name, String description, int cooldown) {
            this(id, name, description, cooldown, null);
        }
        
        public SimpleSpell(Identifier id, String name, String description, int cooldown, Identifier icon) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.cooldown = cooldown;
            this.icon = icon;
        }
        
        @Override
        public Identifier getId() {
            return id;
        }
        
        @Override
        public String getName() {
            return name;
        }
        
        @Override
        public Component getTranslatableName() {
            String translationKey = "spell." + id.getNamespace() + "." + id.getPath() + ".name";
            return Component.translatable(translationKey);
        }
        
        @Override
        public String getDescription() {
            return description;
        }
        
        @Override
        public int getCooldown() {
            return cooldown;
        }
        
        @Override
        public Identifier getIcon() {
            return icon != null ? icon : Spell.super.getIcon();
        }
        
        @Override
        public void spawnCastEffects(Player player, Level level, boolean success) {
            if (success) {
                // Default: screen flash
                if (level.isClientSide()) {
                    at.koopro.spells_n_squares.features.fx.ScreenEffectManager.triggerSpellFlash();
                }
                
                // Screen shake for powerful spells
                if (getVisualEffectIntensity() > 0.7f && level.isClientSide()) {
                    at.koopro.spells_n_squares.features.fx.ScreenEffectManager.triggerShake(
                        0.1f * getVisualEffectIntensity(), 10);
                }
            }
        }
    }
}
