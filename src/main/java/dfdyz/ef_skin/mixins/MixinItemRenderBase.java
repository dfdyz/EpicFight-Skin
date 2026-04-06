package dfdyz.ef_skin.mixins;

import dfdyz.ef_skin.class_attachment_accessor.model.ArmatureShadow;
import dfdyz.ef_skin.storage.ClientSkinManager;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.model.armature.types.ToolHolderArmature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.Map;

@Mixin(RenderItemBase.class)
public abstract class MixinItemRenderBase {


    @Final
    @Shadow(remap = false)
    private boolean alwaysInHand;

    @Final
    @Shadow(remap = false)
    public OpenMatrix4f transformHolder;
    @Final
    @Shadow(remap = false)
    protected Map<String, OpenMatrix4f> mainhandCorrectionTransforms;
    @Final
    @Shadow(remap = false)
    protected Map<String, OpenMatrix4f> offhandCorrectionTransforms;
    @Final
    @Shadow(remap = false)
    protected static Map<String, OpenMatrix4f> GLOBAL_MAINHAND_ITEM_TRANSFORMS;
    @Final
    @Shadow(remap = false)
    protected static Map<String, OpenMatrix4f> GLOBAL_OFFHAND_ITEM_TRANSFORMS;

    @Inject(method = "getCorrectionMatrix", at = @At("HEAD"), cancellable = true, remap = false)
    public void ef_skin$getCorrectionMatrix(LivingEntityPatch<?> entitypatch,
                                            InteractionHand hand, OpenMatrix4f[] poses,
                                            CallbackInfoReturnable<OpenMatrix4f> cir){
        if (!ClientSkinManager.hasSkin(entitypatch.getOriginal())) return;
        Joint parentJoint = null;
        if (this.alwaysInHand) {
            Armature var6 = entitypatch.getArmature();
            if (var6 instanceof ToolHolderArmature toolArmature) {
                parentJoint = hand == InteractionHand.MAIN_HAND ?
                        toolArmature.rightToolJoint() : toolArmature.leftToolJoint();
            }

            if (parentJoint == null) {
                parentJoint = entitypatch.getArmature().rootJoint;
            }
        } else {
            parentJoint = entitypatch.getParentJointOfHand(hand);
        }
        switch (hand) {
            case MAIN_HAND -> this.transformHolder.load(
                    this.mainhandCorrectionTransforms.getOrDefault(parentJoint.getName(),
                            (OpenMatrix4f)GLOBAL_MAINHAND_ITEM_TRANSFORMS.get(parentJoint.getName())));
            case OFF_HAND -> this.transformHolder.load(
                    this.offhandCorrectionTransforms.getOrDefault(parentJoint.getName(),
                            (OpenMatrix4f)GLOBAL_OFFHAND_ITEM_TRANSFORMS.get(parentJoint.getName())));
        }
        Joint real_jt = entitypatch.getArmature().searchJointByName(parentJoint.getName());
        this.transformHolder.mulFront(poses[real_jt.getId()]);
        cir.setReturnValue(this.transformHolder);
        cir.cancel();
    }

}
