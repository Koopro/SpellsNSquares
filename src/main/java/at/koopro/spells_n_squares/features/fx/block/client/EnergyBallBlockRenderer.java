package at.koopro.spells_n_squares.features.fx.block.client;

import at.koopro.spells_n_squares.core.client.RendererUtils;
import at.koopro.spells_n_squares.core.client.ShaderRenderHelper;
import at.koopro.spells_n_squares.features.fx.ShaderEffectHandler;
import at.koopro.spells_n_squares.features.fx.block.EnergyBallBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.util.Mth;

/**
 * Renderer for the EnergyBallBlock.
 * Renders a glowing energy ball using the lumos orb shader.
 */
public class EnergyBallBlockRenderer implements BlockEntityRenderer<EnergyBallBlockEntity, BlockEntityRenderState> {
    public EnergyBallBlockRenderer(BlockEntityRendererProvider.Context context) {
    }
    
    @Override
    public BlockEntityRenderState createRenderState() {
        return new BlockEntityRenderState();
    }
    
    private static final float ENERGY_BALL_SIZE = 0.4f;
    private static final float ROTATION_SPEED = 0.15f;
    private static final float BOB_SPEED = 0.08f;
    private static final float BOB_AMPLITUDE = 0.05f;
    
    @Override
    public void submit(BlockEntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector,
                      CameraRenderState cameraState) {
        // Get time for animation
        float time = (Minecraft.getInstance().level != null 
            ? Minecraft.getInstance().level.getGameTime() 
            : 0) + (Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false));
        
        // Update shader time uniform if using energy ball shader
        ShaderRenderHelper.updateTimeUniform(ShaderEffectHandler.ENERGY_BALL_SHADER);
        
        // Render the energy ball using shader-backed render type with bloom
        net.minecraft.client.renderer.rendertype.RenderType renderType = 
            ShaderRenderHelper.getEnergyBallRenderType();
        
        collector.submitCustomGeometry(
            poseStack,
            renderType,
            (pose, buffer) -> {
                PoseStack ps = new PoseStack();
                ps.last().set(pose);
                ps.pushPose();
                
                // Center the energy ball
                ps.translate(0.5, 0.5, 0.5);
                
                // Rotate around Y axis
                ps.mulPose(Axis.YP.rotation(time * ROTATION_SPEED));
                
                // Bob up and down
                float bob = Mth.sin(time * BOB_SPEED) * BOB_AMPLITUDE;
                ps.translate(0, bob, 0);
                
                // Render the energy ball - the shader handles all the bloom effects
                // Single cube render - the fragment shader creates the bloom with radial gradients
                RendererUtils.renderCube(buffer, ps, ENERGY_BALL_SIZE, 0xFFFFFFFF); // White color, shader handles the bloom
                
                ps.popPose();
            }
        );
    }
}

