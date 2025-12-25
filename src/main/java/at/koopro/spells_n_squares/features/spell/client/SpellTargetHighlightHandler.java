package at.koopro.spells_n_squares.features.spell.client;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import at.koopro.spells_n_squares.features.spell.Spell;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

import java.util.OptionalDouble;

/**
 * Client-side handler for highlighting targeted entities and blocks when holding a spell.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID, value = Dist.CLIENT)
public class SpellTargetHighlightHandler {
    
    private static final double TARGET_RANGE = 12.0; // Same as Wingardium Leviosa range
    private static final int HIGHLIGHT_COLOR = 0x80FFD700; // Gold color with transparency (ARGB)
    private static final int VALID_TARGET_COLOR = 0x8000FF00; // Green for valid targets
    private static final int INVALID_TARGET_COLOR = 0x80FF0000; // Red for invalid targets
    private static final int OUT_OF_RANGE_COLOR = 0x80808080; // Gray for out of range
    
    // Caching for raycast results
    private static EntityHitResult cachedEntityHit = null;
    private static BlockHitResult cachedBlockHit = null;
    private static Vec3 cachedEyePos = null;
    private static Vec3 cachedLookVec = null;
    private static int cacheAge = 0;
    private static final int CACHE_DURATION = 3; // Cache for 3 ticks
    private static final double VIEW_CHANGE_THRESHOLD = 0.01; // Minimum change to invalidate cache
    
    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent.AfterEntities event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return;
        }
        
        // Only highlight if holding a spell
        if (!ClientSpellData.isHoldingSpell()) {
            // Clear cache when not holding spell
            cachedEntityHit = null;
            cachedBlockHit = null;
            return;
        }
        
        // Get the currently selected spell
        int selectedSlot = ClientSpellData.getSelectedSlot();
        Identifier spellId = ClientSpellData.getSpellInSlot(selectedSlot);
        if (spellId == null) {
            return;
        }
        
        Spell spell = SpellRegistry.get(spellId);
        if (spell == null || !spell.isHoldToCast()) {
            return;
        }
        
        // Get current view position and direction
        Vec3 eyePos = mc.player.getEyePosition();
        Vec3 lookVec = mc.player.getLookAngle();
        
        // Check if view has changed significantly (invalidate cache)
        boolean viewChanged = false;
        if (cachedEyePos == null || cachedLookVec == null) {
            viewChanged = true;
        } else {
            double eyeChange = cachedEyePos.distanceTo(eyePos);
            double lookChange = cachedLookVec.distanceTo(lookVec);
            if (eyeChange > VIEW_CHANGE_THRESHOLD || lookChange > VIEW_CHANGE_THRESHOLD) {
                viewChanged = true;
            }
        }
        
        // Update cache if view changed or cache expired
        if (viewChanged || cacheAge >= CACHE_DURATION) {
            cachedEyePos = eyePos;
            cachedLookVec = lookVec;
            cacheAge = 0;
            
            Vec3 endPos = eyePos.add(lookVec.scale(TARGET_RANGE));
            
            // Perform raycast to find what player is looking at
            cachedEntityHit = getEntityHitResult(mc.player, eyePos, endPos);
            
            // Cache block hit result
            HitResult blockHit = mc.player.pick(TARGET_RANGE, 1.0f, false);
            if (blockHit.getType() == HitResult.Type.BLOCK) {
                cachedBlockHit = (BlockHitResult) blockHit;
            } else {
                cachedBlockHit = null;
            }
        } else {
            cacheAge++;
        }
        
        // Render cached entity hit
        if (cachedEntityHit != null) {
            Entity targetEntity = cachedEntityHit.getEntity();
            double distance = eyePos.distanceTo(targetEntity.position());
            boolean inRange = distance <= TARGET_RANGE;
            
            // Only highlight items and small entities (same as Wingardium Leviosa)
            if (targetEntity instanceof ItemEntity || 
                (targetEntity.getBbWidth() < 1.0 && targetEntity.getBbHeight() < 1.5)) {
                int color = inRange ? VALID_TARGET_COLOR : OUT_OF_RANGE_COLOR;
                renderEntityHighlight(event, targetEntity, color);
                
                // Add subtle particles for valid targets
                if (inRange && mc.level != null && mc.level.getGameTime() % 5 == 0) {
                    Vec3 pos = targetEntity.position().add(0, targetEntity.getBbHeight() * 0.5, 0);
                    if (mc.level instanceof net.minecraft.client.multiplayer.ClientLevel clientLevel) {
                        clientLevel.addParticle(ParticleTypes.ENCHANT, 
                            pos.x, pos.y, pos.z, 0, 0.1, 0);
                    }
                }
                return;
            }
        }
        
        // Render cached block hit
        if (cachedBlockHit != null) {
            BlockPos blockPos = cachedBlockHit.getBlockPos();
            double distance = eyePos.distanceTo(Vec3.atCenterOf(blockPos));
            boolean inRange = distance <= TARGET_RANGE;
            int color = inRange ? VALID_TARGET_COLOR : OUT_OF_RANGE_COLOR;
            renderBlockHighlight(event, blockPos, color);
        }
    }
    
    /**
     * Performs a raycast to find entities the player is looking at.
     */
    private static EntityHitResult getEntityHitResult(net.minecraft.world.entity.player.Player player, Vec3 start, Vec3 end) {
        EntityHitResult result = null;
        double closestDistance = Double.MAX_VALUE;
        
        Vec3 direction = end.subtract(start);
        double maxDistance = direction.length();
        direction = direction.normalize();
        
        // Check all entities in a box around the ray
        AABB searchBox = new AABB(start, end).inflate(2.0);
        Level level = player.level();
        
        for (Entity entity : level.getEntitiesOfClass(Entity.class, searchBox, 
            e -> e != player && e.isAlive() && !e.isSpectator())) {
            
            AABB entityBox = entity.getBoundingBox();
            Vec3 hitPos = entityBox.clip(start, end).orElse(null);
            
            if (hitPos != null) {
                double distance = start.distanceTo(hitPos);
                if (distance < maxDistance && distance < closestDistance) {
                    closestDistance = distance;
                    result = new EntityHitResult(entity, hitPos);
                }
            }
        }
        
        return result;
    }
    
    /**
     * Renders a highlight outline around an entity.
     */
    private static void renderEntityHighlight(RenderLevelStageEvent.AfterEntities event, Entity entity, int color) {
        Minecraft mc = Minecraft.getInstance();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        
        AABB boundingBox = entity.getBoundingBox();
        
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        
        Vec3 cameraPos = mc.player.getEyePosition();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        
        Matrix4f poseMatrix = poseStack.last().pose();
        VertexConsumer buffer = bufferSource.getBuffer(RenderTypes.lines());
        
        // Extract color components (ARGB format)
        float alpha = ((color >> 24) & 0xFF) / 255.0f;
        float red = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8) & 0xFF) / 255.0f;
        float blue = (color & 0xFF) / 255.0f;
        
        // Draw box edges (12 edges of a cube)
        renderBoxEdges(buffer, poseMatrix, boundingBox, red, green, blue, alpha);
        
        poseStack.popPose();
    }
    
    /**
     * Renders a highlight outline around a block.
     */
    private static void renderBlockHighlight(RenderLevelStageEvent.AfterEntities event, BlockPos pos, int color) {
        Minecraft mc = Minecraft.getInstance();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        
        Vec3 cameraPos = mc.player.getEyePosition();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        
        // Create AABB for the block
        AABB blockBox = new AABB(pos);
        
        Matrix4f poseMatrix = poseStack.last().pose();
        VertexConsumer buffer = bufferSource.getBuffer(RenderTypes.lines());
        
        // Extract color components (ARGB format)
        float alpha = ((color >> 24) & 0xFF) / 255.0f;
        float red = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8) & 0xFF) / 255.0f;
        float blue = (color & 0xFF) / 255.0f;
        
        // Draw box edges (12 edges of a cube)
        renderBoxEdges(buffer, poseMatrix, blockBox, red, green, blue, alpha);
        
        poseStack.popPose();
    }
    
    /**
     * Renders the 12 edges of a bounding box.
     */
    private static void renderBoxEdges(VertexConsumer buffer, Matrix4f poseMatrix, AABB box, 
                                       float r, float g, float b, float a) {
        float minX = (float) box.minX;
        float minY = (float) box.minY;
        float minZ = (float) box.minZ;
        float maxX = (float) box.maxX;
        float maxY = (float) box.maxY;
        float maxZ = (float) box.maxZ;
        
        int packedLight = 0xF000F0; // Full brightness
        int packedOverlay = 0; // No overlay
        
        // Bottom face
        drawLine(buffer, poseMatrix, minX, minY, minZ, maxX, minY, minZ, r, g, b, a, packedLight, packedOverlay);
        drawLine(buffer, poseMatrix, maxX, minY, minZ, maxX, minY, maxZ, r, g, b, a, packedLight, packedOverlay);
        drawLine(buffer, poseMatrix, maxX, minY, maxZ, minX, minY, maxZ, r, g, b, a, packedLight, packedOverlay);
        drawLine(buffer, poseMatrix, minX, minY, maxZ, minX, minY, minZ, r, g, b, a, packedLight, packedOverlay);
        
        // Top face
        drawLine(buffer, poseMatrix, minX, maxY, minZ, maxX, maxY, minZ, r, g, b, a, packedLight, packedOverlay);
        drawLine(buffer, poseMatrix, maxX, maxY, minZ, maxX, maxY, maxZ, r, g, b, a, packedLight, packedOverlay);
        drawLine(buffer, poseMatrix, maxX, maxY, maxZ, minX, maxY, maxZ, r, g, b, a, packedLight, packedOverlay);
        drawLine(buffer, poseMatrix, minX, maxY, maxZ, minX, maxY, minZ, r, g, b, a, packedLight, packedOverlay);
        
        // Vertical edges
        drawLine(buffer, poseMatrix, minX, minY, minZ, minX, maxY, minZ, r, g, b, a, packedLight, packedOverlay);
        drawLine(buffer, poseMatrix, maxX, minY, minZ, maxX, maxY, minZ, r, g, b, a, packedLight, packedOverlay);
        drawLine(buffer, poseMatrix, maxX, minY, maxZ, maxX, maxY, maxZ, r, g, b, a, packedLight, packedOverlay);
        drawLine(buffer, poseMatrix, minX, minY, maxZ, minX, maxY, maxZ, r, g, b, a, packedLight, packedOverlay);
    }
    
    /**
     * Draws a line between two points.
     */
    private static void drawLine(VertexConsumer buffer, Matrix4f poseMatrix,
                                float x1, float y1, float z1, float x2, float y2, float z2,
                                float r, float g, float b, float a, int packedLight, int packedOverlay) {
        float lineWidth = 2.0f; // Line width for rendering
        
        buffer.addVertex(poseMatrix, x1, y1, z1)
            .setColor(r, g, b, a)
            .setUv(0, 0)
            .setOverlay(packedOverlay)
            .setLight(packedLight)
            .setNormal(0, 1, 0)
            .setLineWidth(lineWidth);
        
        buffer.addVertex(poseMatrix, x2, y2, z2)
            .setColor(r, g, b, a)
            .setUv(0, 0)
            .setOverlay(packedOverlay)
            .setLight(packedLight)
            .setNormal(0, 1, 0)
            .setLineWidth(lineWidth);
    }
    
}








