package at.koopro.spells_n_squares.features.spell;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import at.koopro.spells_n_squares.core.registry.ModDataComponents;
import at.koopro.spells_n_squares.core.registry.ModSounds;
import at.koopro.spells_n_squares.core.registry.ModTags;
import at.koopro.spells_n_squares.features.spell.entity.LightOrbEntity;
import at.koopro.spells_n_squares.features.spell.entity.ShieldOrbEntity;
import at.koopro.spells_n_squares.features.spell.entity.LightningBeamEntity;

/**
 * Registry and initialization of all mod spells.
 * Register your custom spells here.
 * 
 * To create a new spell:
 * 1. Use SpellRegistry.register() with a new SimpleSpell instance
 * 2. Provide: spell ID (use SpellRegistry.spellId("name")), display name, description, and cooldown in ticks
 * 3. Override the cast() method to implement the spell's behavior
 * 4. Return true if the spell was successfully cast, false otherwise
 * 5. Create an icon texture at: assets/{modid}/textures/spell/{spell_name}.png (16x16 pixels recommended)
 * 
 * Example:
 * SpellRegistry.register(new SimpleSpell(
 *     SpellRegistry.spellId("my_spell"),
 *     "My Spell",
 *     "Does something cool",
 *     60 // 3 second cooldown (20 ticks = 1 second)
 * ) {
 *     @Override
 *     public boolean cast(Player player, Level level) {
 *         // Your spell logic here
 *         return true;
 *     }
 * });
 * 
 * The icon will automatically be loaded from: assets/{modid}/textures/spell/my_spell.png
 * If you want a custom icon path, you can pass it as the 5th parameter to SimpleSpell constructor.
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
        SpellRegistry.register(new SimpleSpell(
            SpellRegistry.spellId("heal"),
            "Heal",
            "Restores 4 hearts of health",
            60 // 3 second cooldown
        ) {
            @Override
            public boolean cast(Player player, Level level) {
                if (player.getHealth() >= player.getMaxHealth()) {
                    return false; // Already at full health
                }
                
                float healAmount = 8.0f; // 4 hearts
                player.heal(healAmount);
                
                // Visual and audio feedback
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5f, 1.2f);
                
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.HEART,
                        player.getX(), player.getY() + 1.0, player.getZ(),
                        5, 0.5, 0.5, 0.5, 0.1);
                }
                
                return true;
            }
        });
        
        SpellRegistry.register(new SimpleSpell(
            SpellRegistry.spellId("teleport"),
            "Teleport",
            "Teleports you forward 10 blocks",
            100 // 5 second cooldown
        ) {
            @Override
            public boolean cast(Player player, Level level) {
                Vec3 lookVec = player.getLookAngle();
                Vec3 currentPos = player.position();
                Vec3 targetPos = currentPos.add(lookVec.scale(10.0));
                
                // Check if target position is safe (not in a block)
                if (level.getBlockState(net.minecraft.core.BlockPos.containing(targetPos)).isAir()) {
                    player.teleportTo(targetPos.x, targetPos.y, targetPos.z);
                    
                    level.playSound(null, currentPos.x, currentPos.y, currentPos.z,
                        SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
                    level.playSound(null, targetPos.x, targetPos.y, targetPos.z,
                        SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
                    
                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.PORTAL,
                            currentPos.x, currentPos.y + 1.0, currentPos.z,
                            20, 0.5, 0.5, 0.5, 0.1);
                        serverLevel.sendParticles(ParticleTypes.PORTAL,
                            targetPos.x, targetPos.y + 1.0, targetPos.z,
                            20, 0.5, 0.5, 0.5, 0.1);
                    }
                    
                    return true;
                }
                
                return false; // Can't teleport into a block
            }
        });
        
        SpellRegistry.register(new SimpleSpell(
            SpellRegistry.spellId("fireball"),
            "Fireball",
            "Shoots a fireball forward",
            40 // 2 second cooldown
        ) {
            @Override
            public boolean cast(Player player, Level level) {
                if (!(level instanceof ServerLevel serverLevel)) {
                    return false;
                }
                
                Vec3 lookVec = player.getLookAngle();
                Vec3 spawnPos = player.getEyePosition().add(lookVec.scale(1.5));
                
                // Create the fireball entity
                Entity fireball = EntityType.SMALL_FIREBALL.create(serverLevel, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
                if (fireball != null) {
                    fireball.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
                    fireball.setDeltaMovement(lookVec.x * 0.5, lookVec.y * 0.5, lookVec.z * 0.5);
                    serverLevel.addFreshEntity(fireball);
                    
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0f, 1.0f);
                    
                    return true;
                }
                
                return false;
            }
        });
        
        SpellRegistry.register(new SimpleSpell(
            SpellRegistry.spellId("lightning"),
            "Lightning",
            "Fires a deadly beam from your wand tip",
            80 // 4 second cooldown
        ) {
            @Override
            public boolean cast(Player player, Level level) {
                // Get block player is looking at (within 32 blocks)
                net.minecraft.world.phys.HitResult hitResult = player.pick(32.0, 1.0f, false);
                
                if (hitResult.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK) {
                    net.minecraft.world.phys.BlockHitResult blockHit = (net.minecraft.world.phys.BlockHitResult) hitResult;
                    net.minecraft.core.BlockPos targetPos = blockHit.getBlockPos().above();
                    
                    if (level instanceof ServerLevel serverLevel) {
                        // Start at wand tip (reuse LightOrb spawn-style offset)
                        Vec3 eye = player.getEyePosition();
                        Vec3 look = player.getLookAngle().normalize();
                        Vec3 wandTip = eye.add(look.scale(0.6)).add(0, -0.1, 0);

                        Vec3 end = new Vec3(
                            targetPos.getX() + 0.5,
                            targetPos.getY() + 0.5,
                            targetPos.getZ() + 0.5
                        );

                        // Default beam color: bluish-white
                        int color = 0xFF80D8FF;

                        LightningBeamEntity beam = new LightningBeamEntity(
                            level,
                            player,
                            wandTip,
                            end,
                            color,
                            8 // lifetime in ticks
                        );
                        serverLevel.addFreshEntity(beam);

                        // Thunder sound at impact
                        level.playSound(null, targetPos,
                            SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 0.8f, 1.2f);
                        
                        return true;
                    }
                }
                
                return false;
            }
        });
        
        SpellRegistry.register(new SimpleSpell(
            SpellRegistry.spellId("protego"),
            "Protego",
            "Creates a protective shield that reduces incoming damage",
            120 // 6 second cooldown
        ) {
            @Override
            public boolean cast(Player player, Level level) {
                if (!(level instanceof ServerLevel serverLevel)) {
                    return false;
                }
                
                // Remove any existing shield for this player
                for (Entity entity : level.getEntitiesOfClass(
                        ShieldOrbEntity.class, 
                        player.getBoundingBox().inflate(5.0))) {
                    ShieldOrbEntity shield = (ShieldOrbEntity) entity;
                    if (shield.getOwner() == player) {
                        shield.discard();
                    }
                }
                
                // Apply Resistance effect (reduces damage by 20% per level)
                // Level 2 = 40% damage reduction, duration 8 seconds (160 ticks)
                player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.RESISTANCE,
                    160, // 8 seconds
                    1,   // Level 2 (40% damage reduction)
                    false, // Not ambient
                    true,  // Show particles
                    true   // Show icon
                ));
                
                // Add absorption hearts for extra protection (4 hearts = 8 HP)
                player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.ABSORPTION,
                    160, // 8 seconds
                    1,   // Level 2 (4 absorption hearts)
                    false,
                    true,
                    true
                ));
                
                // Spawn the shield orb entity
                ShieldOrbEntity shield = new ShieldOrbEntity(level, player);
                Vec3 pos = player.position().add(0, player.getBbHeight() * 0, 0);
                shield.setPos(pos.x, pos.y, pos.z);
                serverLevel.addFreshEntity(shield);
                
                // Visual and audio feedback
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0f, 1.5f);
                
                // Initial burst effect when shield is cast
                serverLevel.sendParticles(
                    ParticleTypes.ELECTRIC_SPARK,
                    pos.x, pos.y, pos.z,
                    50, 1.5, 1.0, 1.5, 0.1
                );
                
                serverLevel.sendParticles(
                    ParticleTypes.ENCHANT,
                    pos.x, pos.y, pos.z,
                    30, 1.2, 0.8, 1.2, 0.05
                );
                
                return true;
            }
        });
        
        SpellRegistry.register(new SimpleSpell(
            SpellRegistry.spellId("apparition"),
            "Apparition",
            "Line-of-sight teleportation. Risk of Splinching if used carelessly.",
            80 // 4 second cooldown
        ) {
            @Override
            public boolean cast(Player player, Level level) {
                if (!(level instanceof ServerLevel serverLevel)) {
                    return false;
                }
                
                // 1. Raycast to find destination (100 block range)
                Vec3 eyePos = player.getEyePosition();
                Vec3 lookVec = player.getLookAngle();
                Vec3 endPos = eyePos.add(lookVec.scale(100.0));
                
                // Use RaycastContext for proper block collision detection
                net.minecraft.world.level.ClipContext clipContext = new net.minecraft.world.level.ClipContext(
                    eyePos,
                    endPos,
                    net.minecraft.world.level.ClipContext.Block.COLLIDER,
                    net.minecraft.world.level.ClipContext.Fluid.NONE,
                    player
                );
                
                net.minecraft.world.phys.BlockHitResult hitResult = level.clip(clipContext);
                
                if (hitResult.getType() != net.minecraft.world.phys.HitResult.Type.BLOCK) {
                    return false; // No valid block to teleport to
                }
                
                // 2. Calculate safe teleport position (on top of the hit block)
                net.minecraft.core.BlockPos hitPos = hitResult.getBlockPos();
                Vec3 originPos = player.position();
                
                // Find a safe position above the hit block
                Vec3 targetPos = findSafeTeleportPosition(level, hitPos, hitResult.getDirection());
                
                // Fallback: if no perfect safe spot found, try to place player at hit position with offset
                if (targetPos == null) {
                    // Try placing directly on top of the hit block
                    net.minecraft.core.BlockPos topPos = hitPos.above();
                    if (isSafePosition(level, topPos) && isSafePosition(level, topPos.above())) {
                        targetPos = new Vec3(
                            topPos.getX() + 0.5,
                            topPos.getY(),
                            topPos.getZ() + 0.5
                        );
                    } else {
                        // Last resort: use the hit position itself (might clip into block slightly)
                        Vec3 hitVec = hitResult.getLocation();
                        targetPos = new Vec3(
                            hitVec.x,
                            hitPos.getY() + 1.0,
                            hitVec.z
                        );
                    }
                }
                
                // 3. Calculate Splinch chance
                double splinchChance = calculateSplinchChance(player);
                boolean splinched = level.getRandom().nextDouble() < splinchChance;
                
                // 4. Disapparition effects (origin)
                spawnApparitionParticles(serverLevel, originPos);
                playApparitionSound(level, originPos);
                
                // 5. Handle Splinching
                if (splinched) {
                    handleSplinching(player, level, serverLevel);
                    // Still teleport, but hurt
                }
                
                // 6. Teleport player
                player.teleportTo(targetPos.x, targetPos.y, targetPos.z);
                
                // 7. Apparition effects (destination)
                spawnApparitionParticles(serverLevel, targetPos);
                playApparitionSound(level, targetPos);
                
                return true;
            }
            
            /**
             * Finds a safe position to teleport to above the hit block.
             * Returns null if no safe position is found.
             */
            private Vec3 findSafeTeleportPosition(Level level, net.minecraft.core.BlockPos hitPos, net.minecraft.core.Direction hitDirection) {
                // Always try to place player on top of the hit block, regardless of which face was hit
                // Start from the block above the hit position
                net.minecraft.core.BlockPos startPos = hitPos.above();
                
                // Check up to 3 blocks above to find a safe spot
                for (int i = 0; i < 3; i++) {
                    net.minecraft.core.BlockPos feetPos = startPos.offset(0, i, 0);
                    net.minecraft.core.BlockPos headPos = feetPos.above();
                    
                    // Check if both feet and head positions are safe (air or non-solid)
                    if (isSafePosition(level, feetPos) && isSafePosition(level, headPos)) {
                        // Check the block below to ensure we're standing on solid ground
                        net.minecraft.core.BlockPos groundPos = feetPos.below();
                        net.minecraft.world.level.block.state.BlockState groundState = level.getBlockState(groundPos);
                        // Ground should be solid (not air) and not a fluid
                        if (!groundState.isAir() && groundState.getFluidState().isEmpty()) {
                            // Return position centered on the block, with player's eye height offset
                            return new Vec3(
                                feetPos.getX() + 0.5,
                                feetPos.getY(),
                                feetPos.getZ() + 0.5
                            );
                        }
                    }
                }
                
                return null;
            }
            
            /**
             * Checks if a block position is safe for teleportation.
             */
            private boolean isSafePosition(Level level, net.minecraft.core.BlockPos pos) {
                net.minecraft.world.level.block.state.BlockState state = level.getBlockState(pos);
                // Safe if air or has no collision shape, and not lava
                return (state.isAir() || state.getCollisionShape(level, pos).isEmpty()) && 
                       !state.getFluidState().is(net.minecraft.tags.FluidTags.LAVA);
            }
            
            /**
             * Calculates the chance of Splinching based on player state.
             */
            private double calculateSplinchChance(Player player) {
                double chance = 0.05; // 5% base chance
                
                // Increase chance if player is sprinting
                if (player.isSprinting()) {
                    chance += 0.20; // +20%
                }
                
                // Increase chance if player is moving (has velocity)
                Vec3 velocity = player.getDeltaMovement();
                double speed = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
                if (speed > 0.1) {
                    chance += 0.15; // +15% if moving
                }
                
                // Increase chance if player is falling
                if (velocity.y < -0.2) {
                    chance += 0.10; // +10% if falling
                }
                
                // Increase chance if player is low on health (below 5 hearts = 10 HP)
                if (player.getHealth() < 10.0f) {
                    chance += 0.15; // +15% if low health
                }
                
                // Cap at 80% maximum chance
                return Math.min(chance, 0.80);
            }
            
            /**
             * Handles the Splinching effect when apparition goes wrong.
             */
            private void handleSplinching(Player player, Level level, ServerLevel serverLevel) {
                // Determine if it's a minor or major splinch
                boolean majorSplinch = level.getRandom().nextDouble() < 0.3; // 30% chance of major splinch
                
                if (majorSplinch) {
                    // Major Splinch: More severe effects
                    player.hurtServer(serverLevel, level.damageSources().magic(), 8.0f); // 4 hearts
                    
                    // Apply status effects
                    player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                        net.minecraft.world.effect.MobEffects.SLOWNESS,
                        600, // 30 seconds
                        2,   // Level 3 (severe slowness)
                        false,
                        true,
                        true
                    ));
                    
                    player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                        net.minecraft.world.effect.MobEffects.MINING_FATIGUE,
                        600, // 30 seconds
                        1,   // Level 2 (mining fatigue)
                        false,
                        true,
                        true
                    ));
                    
                    player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                        net.minecraft.world.effect.MobEffects.WITHER,
                        600, // 30 seconds
                        0,   // Level 1 (bleeding effect)
                        false,
                        true,
                        true
                    ));
                    
                    // Rarely drop off-hand item at origin
                    if (level.getRandom().nextDouble() < 0.1) { // 10% chance
                        net.minecraft.world.item.ItemStack offHand = player.getOffhandItem();
                        if (!offHand.isEmpty()) {
                            net.minecraft.world.entity.item.ItemEntity itemEntity = 
                                new net.minecraft.world.entity.item.ItemEntity(
                                    level,
                                    player.getX(),
                                    player.getY(),
                                    player.getZ(),
                                    offHand.copy()
                                );
                            level.addFreshEntity(itemEntity);
                            player.setItemInHand(net.minecraft.world.InteractionHand.OFF_HAND, 
                                net.minecraft.world.item.ItemStack.EMPTY);
                        }
                    }
                    
                    if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                        serverPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "You have been Splinched! Your body parts were left behind..."));
                    }
                } else {
                    // Minor Splinch: Just damage
                    player.hurtServer(serverLevel, level.damageSources().magic(), 4.0f); // 2 hearts
                    if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                        serverPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "You have been Splinched!"));
                    }
                }
                
                // Play flesh ripping sound
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.PLAYER_HURT, SoundSource.PLAYERS, 1.0f, 0.5f);
                
                // Spawn blood particles
                serverLevel.sendParticles(ParticleTypes.DAMAGE_INDICATOR,
                    player.getX(), player.getY() + 1.0, player.getZ(),
                    15, 0.5, 0.5, 0.5, 0.1);
            }
            
            /**
             * Spawns particles for the apparition effect (disapparition/apparition).
             */
            private void spawnApparitionParticles(ServerLevel level, Vec3 pos) {
                // Mix of different particles for the "twist" effect
                // Poof/explosion particles
                level.sendParticles(ParticleTypes.POOF,
                    pos.x, pos.y, pos.z,
                    30, 0.5, 0.5, 0.5, 0.1);
                
                // Black smoke (squid ink) for dark aesthetic
                level.sendParticles(ParticleTypes.SQUID_INK,
                    pos.x, pos.y, pos.z,
                    20, 0.6, 0.6, 0.6, 0.05);
                
                // White smoke (cloud) for the "pop"
                level.sendParticles(ParticleTypes.CLOUD,
                    pos.x, pos.y, pos.z,
                    15, 0.4, 0.4, 0.4, 0.08);
                
                // Portal particles for magical effect
                level.sendParticles(ParticleTypes.PORTAL,
                    pos.x, pos.y, pos.z,
                    25, 0.5, 0.5, 0.5, 0.05);
            }
            
            /**
             * Plays the "crack" sound for apparition.
             */
            private void playApparitionSound(Level level, Vec3 pos) {
                // Mix of sounds to create the "crack" effect
                // High-pitched explosion for the pop
                level.playSound(null, pos.x, pos.y, pos.z,
                    SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.8f, 2.0f);
                
                // Enderman teleport for magical effect
                level.playSound(null, pos.x, pos.y, pos.z,
                    SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.6f, 1.5f);
            }
        });
        
        SpellRegistry.register(new SimpleSpell(
            SpellRegistry.spellId("lumos"),
            "Lumos / Nox",
            "Toggles a magical light around you",
            20 // 1 second cooldown
        ) {
            @Override
            public boolean cast(Player player, Level level) {
                ItemStack wand = findWand(player);
                if (wand.isEmpty()) {
                    return false;
                }

                boolean isActive = wand.getOrDefault(ModDataComponents.LUMOS_ACTIVE.get(), false);
                boolean isSneaking = player.isShiftKeyDown();

                // If lumos is active and player sneaks while casting: shoot light orb and turn off
                if (isActive && isSneaking && !level.isClientSide()) {
                    Vec3 eye = player.getEyePosition();
                    Vec3 look = player.getLookAngle().normalize();
                    Vec3 spawnPos = eye.add(look.scale(0.6)).add(0, -0.1, 0);
                    Vec3 velocity = look.scale(0.7);
                    LightOrbEntity orb = new LightOrbEntity(level, player, spawnPos, velocity, 80);
                    level.addFreshEntity(orb);

                    // Turn off Lumos on the wand
                    wand.set(ModDataComponents.LUMOS_ACTIVE.get(), false);
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.NOX.value(), SoundSource.PLAYERS, 0.7f, 1.0f);
                    return true;
                }

                // Default toggle lumos state
                var newStateOpt = LumosManager.toggleLumos(player);
                if (newStateOpt.isEmpty()) {
                    return false;
                }
                boolean newState = newStateOpt.get();
                
                // Play appropriate sound
                var soundEvent = newState ? ModSounds.LUMOS.value() : ModSounds.NOX.value();
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    soundEvent, SoundSource.PLAYERS, 0.7f, 1.0f);
                
                // Visual feedback
                if (level instanceof ServerLevel serverLevel) {
                    if (newState) {
                        // Lumos: bright particles
                        serverLevel.sendParticles(ParticleTypes.END_ROD,
                            player.getX(), player.getY() + 1.0, player.getZ(),
                            15, 0.5, 0.5, 0.5, 0.1);
                        serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                            player.getX(), player.getY() + 1.0, player.getZ(),
                            10, 0.3, 0.3, 0.3, 0.05);
                    } else {
                        // Nox: dark particles
                        serverLevel.sendParticles(ParticleTypes.SMOKE,
                            player.getX(), player.getY() + 1.0, player.getZ(),
                            10, 0.3, 0.3, 0.3, 0.05);
                    }
                }
                
                return true;
            }
        });
    }

    /**
     * Finds a wand in main or offhand.
     */
    private static ItemStack findWand(Player player) {
        ItemStack main = player.getMainHandItem();
        if (!main.isEmpty() && main.is(ModTags.WANDS)) {
            return main;
        }
        ItemStack off = player.getOffhandItem();
        if (!off.isEmpty() && off.is(ModTags.WANDS)) {
            return off;
        }
        return ItemStack.EMPTY;
    }
    
    /**
     * Helper class for creating simple spells without implementing all methods.
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
            // Use the translation key format: spell.{namespace}.{path}.name
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
    }
}

