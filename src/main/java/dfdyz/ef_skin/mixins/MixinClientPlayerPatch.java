package dfdyz.ef_skin.mixins;

import dfdyz.ef_skin.class_attachment_accessor.model.ArmatureShadow;
import dfdyz.ef_skin.storage.ClientSkinManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;

@Mixin(AbstractClientPlayerPatch.class)
public abstract class MixinClientPlayerPatch {

    @Inject(method = "preTickClient", at = @At("TAIL"), remap = false)
    public void ef_skin$preTickClient(CallbackInfo ci){
        Object o = this;
        AbstractClientPlayerPatch<?> cpp = (AbstractClientPlayerPatch<?>) o;
        var cp = cpp.getOriginal();
        if(ClientSkinManager.hasSkin(cp)){
            var skin = ClientSkinManager.getSkin(cp);
            if(skin.firstTick) {
                skin.initPhysics(cpp);
                skin.firstTick = false;
            }
            if(skin.animPhys != null){
                ArmatureShadow shadow = (ArmatureShadow) cpp;
                shadow.ef_skin$storeShadow(skin.armature);
                skin.animPhys.tick();
                if(Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()){
                    skin.animPhys.updateSBCCache();
                }
                shadow.ef_skin$reserve();
            }
        }
    }

}
