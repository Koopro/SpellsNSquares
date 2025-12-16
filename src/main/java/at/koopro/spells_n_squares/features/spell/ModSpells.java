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
        // TODO: Re-enable when spell classes are implemented
        // SpellRegistry.register(new HealSpell());
        // SpellRegistry.register(new TeleportSpell());
        // SpellRegistry.register(new FireballSpell());
        // SpellRegistry.register(new LightningSpell());
        // SpellRegistry.register(new ProtegoSpell());
        // SpellRegistry.register(new ApparitionSpell());
        // SpellRegistry.register(new LumosSpell());
        
        // Utility spells
        // TODO: Re-enable when spell classes are implemented
        // SpellRegistry.register(new EntityDetectionSpell());
        // SpellRegistry.register(new ExtensionCharmSpell());
        // SpellRegistry.register(new PortableCraftingSpell());
        // SpellRegistry.register(new ItemRecallSpell());
        // SpellRegistry.register(new NoxSpell());
        // SpellRegistry.register(new HomenumRevelioSpell());
        // SpellRegistry.register(new RevelioSpell());
        // SpellRegistry.register(new FiniteIncantatemSpell());
        // SpellRegistry.register(new ApareciumSpell());
        // SpellRegistry.register(new PriorIncantatoSpell());
        
        // Weather control spells
        // TODO: Re-enable when spell classes are implemented
        // SpellRegistry.register(new MetelojinxSpell());
        // SpellRegistry.register(new AraniaExumaiSpell());
        // SpellRegistry.register(new VentusSpell());
        
        // Advanced defensive spells
        // TODO: Re-enable when spell classes are implemented
        // SpellRegistry.register(new ExpelliarmusSpell());
        // SpellRegistry.register(new StupefySpell());
        
        // Combat spells
        // TODO: Re-enable when spell classes are implemented
        // SpellRegistry.register(new BombardaSpell());
        // SpellRegistry.register(new ConfringoSpell());
        // SpellRegistry.register(new DiffindoSpell());
        // SpellRegistry.register(new DepulsoSpell());
        // SpellRegistry.register(new DescendoSpell());
        // SpellRegistry.register(new FlipendoSpell());
        // SpellRegistry.register(new ImpedimentaSpell());
        // SpellRegistry.register(new RictusempraSpell());
        
        // Charm spells
        // TODO: Re-enable when spell classes are implemented
        // SpellRegistry.register(new ReparoSpell());
        // SpellRegistry.register(new AlohomoraSpell());
        // SpellRegistry.register(new LevitationSpell());
        // SpellRegistry.register(new ColloportusSpell());
        // SpellRegistry.register(new DuroSpell());
        // SpellRegistry.register(new EngorgioSpell());
        // SpellRegistry.register(new ReducioSpell());
        // SpellRegistry.register(new ScourgifySpell());
        // SpellRegistry.register(new TergeoSpell());
        SpellRegistry.register(new AccioSpell());
        SpellRegistry.register(new RiddikulusSpell());
        
        // Healing spells
        // TODO: Re-enable when spell classes are implemented
        // SpellRegistry.register(new EpiskeySpell());
        // SpellRegistry.register(new VulneraSanenturSpell());
        // SpellRegistry.register(new AnapneoSpell());
        // SpellRegistry.register(new FerulaSpell());
        
        // Memory/mental spells
        // TODO: Re-enable when spell classes are implemented
        // SpellRegistry.register(new LegilimensSpell());
        // SpellRegistry.register(new OcclumencySpell());
        
        // Transfiguration spells
        // TODO: Re-enable when spell classes are implemented
        // SpellRegistry.register(new TransfigurationSpell());
        // SpellRegistry.register(new SerpensortiaSpell());
        // SpellRegistry.register(new AvisSpell());
        
        // Curses (Dark Magic)
        // TODO: Re-enable when spell classes are implemented
        // SpellRegistry.register(new CrucioSpell());
        // SpellRegistry.register(new AvadaKedavraSpell());
        // SpellRegistry.register(new ImperioSpell());
        // SpellRegistry.register(new SectumsempraSpell());
        
        // Fire spells
        // TODO: Re-enable when spell classes are implemented
        // SpellRegistry.register(new IncendioSpell());
        
        // Defensive/Protective spells
        // TODO: Re-enable when spell classes are implemented
        // SpellRegistry.register(new PatronusSpell());
        
        // Utility/Combat spells
        // TODO: Re-enable when spell classes are implemented
        // SpellRegistry.register(new PetrificusTotalusSpell());
        // SpellRegistry.register(new ConfundoSpell());
        // SpellRegistry.register(new AguamentiSpell());
        // SpellRegistry.register(new GlaciusSpell());
        // SpellRegistry.register(new ReductoSpell());
        // SpellRegistry.register(new ObliviateSpell());
        
        // Class joining spells
        // TODO: Re-enable when spell classes are implemented
        // SpellRegistry.register(new MorsmordreSpell());
        // SpellRegistry.register(new PhoenixCallSpell());
        
        // Animagus spells
        // TODO: Re-enable when spell classes are implemented
        // SpellRegistry.register(new AnimagusTransformationSpell());
        
        // Additional utility spells
        SpellRegistry.register(new ImmobulusSpell());
        SpellRegistry.register(new SilencioSpell());
        // TODO: Re-enable when spell classes are implemented
        // SpellRegistry.register(new IncarcerousSpell());
        
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
