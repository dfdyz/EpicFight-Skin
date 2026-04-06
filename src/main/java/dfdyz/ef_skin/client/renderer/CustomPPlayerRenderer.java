package dfdyz.ef_skin.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dfdyz.ef_skin.class_attachment_accessor.model.ArmatureShadow;
import dfdyz.ef_skin.storage.ClientSkinManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EntityType;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.renderer.patched.entity.PPlayerRenderer;
import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;

@SuppressWarnings("UnstableApiUsage")
public class CustomPPlayerRenderer extends PPlayerRenderer {

    public CustomPPlayerRenderer(EntityRendererProvider.Context context, EntityType<?> entityType) {
        super(context, entityType);
    }

    @Override
    public void render(AbstractClientPlayer entity, AbstractClientPlayerPatch<AbstractClientPlayer> entitypatch,
                       PlayerRenderer renderer, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {
        boolean hasSkin = ClientSkinManager.hasSkin(entity);
        var skin = hasSkin ? ClientSkinManager.getSkin(entity) : null;
        ArmatureShadow armatureShadow = (ArmatureShadow) entitypatch;
        if(hasSkin) armatureShadow.ef_skin$storeShadow(skin.armature);
        super.render(entity, entitypatch, renderer, buffer, poseStack, packedLight, partialTicks);
        renderPhysicsDebug(entity, buffer, poseStack, partialTicks);
        if(hasSkin) armatureShadow.ef_skin$reserve();
    }

    public void renderPhysicsDebug(AbstractClientPlayer entity, MultiBufferSource buffer, PoseStack poseStack, float partialTicks){
        if(!ClientSkinManager.hasSkin(entity)) return;
        if(Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()){
            var skin = ClientSkinManager.getSkin(entity);
            if (skin.animPhys != null && skin.animPhys.isReady()){
                poseStack.pushPose();
                skin.animPhys.renderDebug(entity, poseStack, buffer, partialTicks);
                poseStack.popPose();
            }
        }
    }

    @Override
    public void setArmaturePose(AbstractClientPlayerPatch<AbstractClientPlayer> entityPatch, Armature armature, float partialTicks) {
        if(ClientSkinManager.hasSkin(entityPatch.getOriginal())){
            var skin = ClientSkinManager.getSkin(entityPatch.getOriginal());
            if(skin.animPhys != null){
                Pose pose = entityPatch.getAnimator().getPose(partialTicks);
                setJointTransforms(entityPatch, armature, pose, partialTicks);
                skin.animPhys.feedbackPose(armature, pose, partialTicks);
                return;
            }
        }
        super.setArmaturePose(entityPatch, armature, partialTicks);
    }


    @Override
    public AssetAccessor<HumanoidMesh> getMeshProvider(AbstractClientPlayerPatch<AbstractClientPlayer> entityPatch) {
        if(ClientSkinManager.hasSkin(entityPatch.getOriginal())){
            var skin = ClientSkinManager.getSkin(entityPatch.getOriginal());
            return skin.mesh;
        }
        return super.getMeshProvider(entityPatch);
    }
}
