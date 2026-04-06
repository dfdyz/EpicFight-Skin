package dfdyz.ef_skin.mixins;

import dfdyz.ef_skin.class_attachment_accessor.model.ArmatureShadow;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(LivingEntityPatch.class)
public abstract class MixinLivingEntityPatch implements ArmatureShadow {

    @Shadow(remap = false)
    protected Armature armature;
    @Unique
    protected Armature ef_skin$originArmature;

    @Inject(method = "onConstructed(Lnet/minecraft/world/entity/Entity;)V", at = @At("TAIL"), remap = false)
    public void ef_skin$onConstruct(Entity par1, CallbackInfo ci){
        ef_skin$originArmature = armature;
    }

    @Override
    public void ef_skin$storeShadow(Armature armature) {
        this.armature = armature;
    }

    @Override
    public void ef_skin$reserve() {
        armature = ef_skin$originArmature;
    }
}
