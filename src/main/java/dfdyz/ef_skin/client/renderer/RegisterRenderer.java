package dfdyz.ef_skin.client.renderer;

import dfdyz.ef_skin.EFSkinMod;
import net.minecraft.world.entity.EntityType;
import yesman.epicfight.api.client.event.types.registry.RegisterPatchedRenderersEvent;


public class RegisterRenderer {
    public static void register(RegisterPatchedRenderersEvent.AddEntity event){
        EFSkinMod.LOGGER.warn("PATCH PLAYER RENDER");
        var ctx = event.getContext();
        event.addPatchedEntityRenderer(
               EntityType.PLAYER, (entityType) -> new CustomPPlayerRenderer(ctx, entityType).initLayerLast(ctx, entityType)
        );
    }
}
