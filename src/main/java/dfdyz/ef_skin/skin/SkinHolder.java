package dfdyz.ef_skin.skin;

import com.google.gson.Gson;
import dfdyz.ef_anim_phy.physics.EntityAnimationPhysics;
import dfdyz.ef_skin.EFSkinMod;
import dfdyz.ef_skin.client.physics.PhysicsStruct;
import dfdyz.ef_skin.client.physics.SafeAnimPhy;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;

public class SkinHolder {
    public final SkinInfo info;
    public final Armature armature;
    public final AssetAccessor<HumanoidMesh> mesh;
    public SafeAnimPhy animPhys;
    public final PhysicsStruct physicsStruct;

    public final ResourceLocation texture;
    // todo: pbr support

    public SkinHolder(SkinInfo info, Armature armature, AssetAccessor<HumanoidMesh> mesh, PhysicsStruct physicsStruct, ResourceLocation texture) {
        this.info = info;
        this.armature = armature;
        this.mesh = mesh;
        this.physicsStruct = physicsStruct;
        this.texture = texture;
    }

    public void destroy(){
        mesh.get().destroy();
        if(animPhys != null)
            animPhys.destroy();
        Minecraft.getInstance().getTextureManager().release(texture);
    }

    public boolean firstTick = true;
    public void initPhysics(LivingEntityPatch<?> entityPatch){
        if(animPhys != null || physicsStruct == null) return;
        try {
            animPhys = physicsStruct.construct(entityPatch, armature);
            EFSkinMod.LOGGER.info(MessageFormat.format("Created physics for {0}.", info.key));
        }catch (Exception e){
            e.printStackTrace(System.err);
        }
    }

    public void reloadPhysics(){
        // todo
    }

}
