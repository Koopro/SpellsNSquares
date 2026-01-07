package at.koopro.spells_n_squares.features.spell.item;

import at.koopro.spells_n_squares.core.registry.RegistryHelper;
import at.koopro.spells_n_squares.features.spell.entity.DummyPlayerEntity;
import at.koopro.spells_n_squares.features.spell.entity.DummyPlayerModelType;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Spawn egg item for Dummy Player entity.
 * Right-click to spawn a dummy player at the target location.
 */
public class DummyPlayerSpawnEggItem extends Item {
    private final DummyPlayerModelType defaultModelType;
    
    public DummyPlayerSpawnEggItem(Identifier id, DummyPlayerModelType defaultModelType) {
        super(RegistryHelper.createItemProperties(id));
        this.defaultModelType = defaultModelType;
    }
    
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.FAIL;
        }
        
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.FAIL;
        }
        
        // Spawn dummy player at the clicked position
        Vec3 spawnPos = Vec3.atBottomCenterOf(context.getClickedPos().above());
        
        DummyPlayerEntity dummyPlayer = new DummyPlayerEntity(
            at.koopro.spells_n_squares.features.spell.manager.SpellEntityRegistry.DUMMY_PLAYER.get(),
            serverLevel
        );
        dummyPlayer.setModelType(defaultModelType);
        dummyPlayer.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
        
        serverLevel.addFreshEntity(dummyPlayer);
        
        // Consume item if not in creative mode
        if (!player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }
        
        return InteractionResult.CONSUME;
    }
}

