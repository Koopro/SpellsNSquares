package at.koopro.spells_n_squares.core.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;

import java.util.Optional;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Data component for storing player model modifications.
 * Handles scaling of individual body parts and overall player size.
 */
public final class PlayerModelDataComponent {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PlayerModelData>> TYPE =
        DATA_COMPONENTS.register(
            "player_model_data",
            () -> DataComponentType.<PlayerModelData>builder()
                .persistent(PlayerModelData.CODEC)
                .build()
        );
    
    /**
     * Player model data record.
     * Stores scale values for each body part and overall player scale.
     */
    public record PlayerModelData(
        float scale,              // Overall scale (default 1.0)
        float headScale,          // Head scale (default 1.0)
        float bodyScale,          // Body/torso scale (default 1.0)
        float leftArmScale,       // Left arm scale (default 1.0)
        float rightArmScale,      // Right arm scale (default 1.0)
        float leftLegScale,       // Left leg scale (default 1.0)
        float rightLegScale,      // Right leg scale (default 1.0)
        float hitboxScale,        // Hitbox scale (default 1.0)
        Float width,              // Custom width (optional, null = default)
        Float height              // Custom height (optional, null = default)
    ) {
        public static final Codec<PlayerModelData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.FLOAT.optionalFieldOf("scale", 1.0f).forGetter(PlayerModelData::scale),
                Codec.FLOAT.optionalFieldOf("headScale", 1.0f).forGetter(PlayerModelData::headScale),
                Codec.FLOAT.optionalFieldOf("bodyScale", 1.0f).forGetter(PlayerModelData::bodyScale),
                Codec.FLOAT.optionalFieldOf("leftArmScale", 1.0f).forGetter(PlayerModelData::leftArmScale),
                Codec.FLOAT.optionalFieldOf("rightArmScale", 1.0f).forGetter(PlayerModelData::rightArmScale),
                Codec.FLOAT.optionalFieldOf("leftLegScale", 1.0f).forGetter(PlayerModelData::leftLegScale),
                Codec.FLOAT.optionalFieldOf("rightLegScale", 1.0f).forGetter(PlayerModelData::rightLegScale),
                Codec.FLOAT.optionalFieldOf("hitboxScale", 1.0f).forGetter(PlayerModelData::hitboxScale),
                Codec.FLOAT.optionalFieldOf("width").forGetter(d -> Optional.ofNullable(d.width())),
                Codec.FLOAT.optionalFieldOf("height").forGetter(d -> Optional.ofNullable(d.height()))
            ).apply(instance, (scale, headScale, bodyScale, leftArmScale, rightArmScale, leftLegScale, rightLegScale, hitboxScale, widthOpt, heightOpt) -> 
                new PlayerModelData(scale, headScale, bodyScale, leftArmScale, rightArmScale, leftLegScale, rightLegScale, hitboxScale, widthOpt.orElse(null), heightOpt.orElse(null)))
        );
        
        /**
         * Creates default player model data (all scales = 1.0).
         */
        public static PlayerModelData empty() {
            return new PlayerModelData(
                1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
                null, null
            );
        }
        
        /**
         * Creates a copy with updated overall scale.
         */
        public PlayerModelData withScale(float scale) {
            return new PlayerModelData(
                scale, headScale(), bodyScale(), leftArmScale(), rightArmScale(),
                leftLegScale(), rightLegScale(), hitboxScale(), width(), height()
            );
        }
        
        /**
         * Creates a copy with updated head scale.
         */
        public PlayerModelData withHeadScale(float headScale) {
            return new PlayerModelData(
                scale(), headScale, bodyScale(), leftArmScale(), rightArmScale(),
                leftLegScale(), rightLegScale(), hitboxScale(), width(), height()
            );
        }
        
        /**
         * Creates a copy with updated body scale.
         */
        public PlayerModelData withBodyScale(float bodyScale) {
            return new PlayerModelData(
                scale(), headScale(), bodyScale, leftArmScale(), rightArmScale(),
                leftLegScale(), rightLegScale(), hitboxScale(), width(), height()
            );
        }
        
        /**
         * Creates a copy with updated left arm scale.
         */
        public PlayerModelData withLeftArmScale(float leftArmScale) {
            return new PlayerModelData(
                scale(), headScale(), bodyScale(), leftArmScale, rightArmScale(),
                leftLegScale(), rightLegScale(), hitboxScale(), width(), height()
            );
        }
        
        /**
         * Creates a copy with updated right arm scale.
         */
        public PlayerModelData withRightArmScale(float rightArmScale) {
            return new PlayerModelData(
                scale(), headScale(), bodyScale(), leftArmScale(), rightArmScale,
                leftLegScale(), rightLegScale(), hitboxScale(), width(), height()
            );
        }
        
        /**
         * Creates a copy with updated left leg scale.
         */
        public PlayerModelData withLeftLegScale(float leftLegScale) {
            return new PlayerModelData(
                scale(), headScale(), bodyScale(), leftArmScale(), rightArmScale(),
                leftLegScale, rightLegScale(), hitboxScale(), width(), height()
            );
        }
        
        /**
         * Creates a copy with updated right leg scale.
         */
        public PlayerModelData withRightLegScale(float rightLegScale) {
            return new PlayerModelData(
                scale(), headScale(), bodyScale(), leftArmScale(), rightArmScale(),
                leftLegScale(), rightLegScale, hitboxScale(), width(), height()
            );
        }
        
        /**
         * Creates a copy with updated hitbox scale.
         */
        public PlayerModelData withHitboxScale(float hitboxScale) {
            return new PlayerModelData(
                scale(), headScale(), bodyScale(), leftArmScale(), rightArmScale(),
                leftLegScale(), rightLegScale(), hitboxScale, width(), height()
            );
        }
        
        /**
         * Creates a copy with updated body part scale.
         */
        public PlayerModelData withBodyPartScale(BodyPart part, float scale) {
            return switch (part) {
                case HEAD -> withHeadScale(scale);
                case BODY -> withBodyScale(scale);
                case LEFT_ARM -> withLeftArmScale(scale);
                case RIGHT_ARM -> withRightArmScale(scale);
                case LEFT_LEG -> withLeftLegScale(scale);
                case RIGHT_LEG -> withRightLegScale(scale);
            };
        }
        
        /**
         * Gets the scale for a specific body part.
         */
        public float getBodyPartScale(BodyPart part) {
            return switch (part) {
                case HEAD -> headScale;
                case BODY -> bodyScale;
                case LEFT_ARM -> leftArmScale;
                case RIGHT_ARM -> rightArmScale;
                case LEFT_LEG -> leftLegScale;
                case RIGHT_LEG -> rightLegScale;
            };
        }
        
        /**
         * Resets all scales to default (1.0).
         */
        public PlayerModelData reset() {
            return empty();
        }
        
        /**
         * Resets a specific body part to default scale (1.0).
         */
        public PlayerModelData resetBodyPart(BodyPart part) {
            return withBodyPartScale(part, 1.0f);
        }
    }
    
    /**
     * Enum for body parts that can be scaled.
     */
    public enum BodyPart {
        HEAD,
        BODY,
        LEFT_ARM,
        RIGHT_ARM,
        LEFT_LEG,
        RIGHT_LEG
    }
}

