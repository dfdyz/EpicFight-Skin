package dfdyz.ef_skin.mixins;


import dfdyz.ef_skin.storage.ClientSkinManager;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerRenderer.class)
public class MixinPlayerRenderer {

    @Inject(method = "getTextureLocation(Lnet/minecraft/client/player/AbstractClientPlayer;)Lnet/minecraft/resources/ResourceLocation;",
            at = @At("HEAD"), cancellable = true,
            remap = false
    )
    public void ef_skin$getTexture(AbstractClientPlayer entity, CallbackInfoReturnable<ResourceLocation> cir){
        if (ClientSkinManager.hasSkin(entity)){
            cir.setReturnValue(ClientSkinManager.getSkin(entity).texture);
            cir.cancel();
        }
    }
}
