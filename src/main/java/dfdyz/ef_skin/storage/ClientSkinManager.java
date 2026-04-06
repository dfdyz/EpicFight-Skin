package dfdyz.ef_skin.storage;

import com.google.gson.Gson;
import com.mojang.logging.LogUtils;
import dfdyz.ef_skin.EFSkinMod;
import dfdyz.ef_skin.client.physics.PhysicsStruct;
import dfdyz.ef_skin.client.texture.ByteTexture;
import dfdyz.ef_skin.skin.SkinHolder;
import dfdyz.ef_skin.skin.SkinInfo;
import dfdyz.ef_skin.utils.JsonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;
import yesman.epicfight.api.asset.JsonAssetLoader;
import yesman.epicfight.api.asset.SelfAccessor;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ClientSkinManager {
    public static final Gson gson = new Gson();
    private static final ConcurrentHashMap<UUID, SkinHolder> skinMap = new ConcurrentHashMap<>();
    private static final Logger LOGGER = LogUtils.getLogger();

    public static boolean hasSkin(LivingEntity entity){
        return skinMap.containsKey(entity.getUUID());
    }

    public static SkinHolder getSkin(LivingEntity entity){
        return skinMap.get(entity.getUUID());
    }

    public static void putSkin(LivingEntity entity, SkinHolder skin){
        skinMap.put(entity.getUUID(), skin);
    }

    public static void loadSkinForEntity(LivingEntityPatch<?> livingEntityPatch, SkinInfo info){
        Thread thread = new Thread(() -> {
            if(hasSkin(livingEntityPatch.getOriginal())){
                var skin = getSkin(livingEntityPatch.getOriginal());
                skinMap.remove(livingEntityPatch.getOriginal());
                skin.destroy();
            }

            var holder = ClientSkinManager.load(livingEntityPatch, info);
            if(holder != null){
                skinMap.put(livingEntityPatch.getOriginal().getUUID(), holder);
                LOGGER.info(MessageFormat.format("Success to load model {0}.", info.key));
            }
            else
                LOGGER.error(MessageFormat.format("Failed to load model {0}.", info.key));
        });
        thread.start();
    }

    public static void clean(){
        var helper = new ArrayList<SkinHolder>(skinMap.values().size());
        helper.addAll(skinMap.values());
        skinMap.clear();
        helper.forEach((h) -> {
            h.destroy();
        });
    }

    public static SkinHolder load(LivingEntityPatch<?> entityPatch, SkinInfo skinInfo){
        var model_root = new File(skinInfo.path).toPath();
        var model_file = model_root.resolve("model.json").toFile();
        try (var is = new FileInputStream(model_file)){
            JsonAssetLoader loader = new JsonAssetLoader(is, ResourceLocation.fromNamespaceAndPath("ef_skin", skinInfo.key));
            JsonUtils.patchMesh(loader.getRootJson());

            var armature = loader.loadArmature(HumanoidArmature::new);
            LOGGER.info(MessageFormat.format("Loaded armature {0}", model_file.toString()));
            var mesh = new SelfAccessor<>(ResourceLocation.fromNamespaceAndPath(EFSkinMod.MODID, skinInfo.key),
                    loader.loadSkinnedMesh(HumanoidMesh::new));
            LOGGER.info(MessageFormat.format("Loaded mesh {0}", model_file.toString()));

            var texture_location = ResourceLocation.fromNamespaceAndPath("ef_skin", skinInfo.key);
            var texture_path = model_root.resolve("texture.png");
            var texture_byte = Files.readAllBytes(texture_path);
            LOGGER.info(MessageFormat.format("Loaded texture {0}", texture_path.toString()));
            loadTexture(texture_location, texture_byte);

            PhysicsStruct physicsStruct = null;
            try {
                var phy_def_file = model_root.resolve("physics.json").toFile();
                try (var is2 = new FileInputStream(phy_def_file)) {
                    physicsStruct = gson.fromJson(new InputStreamReader(is2), PhysicsStruct.class);
                    physicsStruct.prevProcess();
                    LOGGER.info(MessageFormat.format("Loaded physics define {0}", phy_def_file.toString()));
                }
            }catch (Exception e){
                e.printStackTrace(System.err);
            }

            return new SkinHolder(skinInfo, armature, mesh, physicsStruct, texture_location);
        }
        catch (Exception e){
            e.printStackTrace(System.err);
        }
        return null;
    }

    public static void loadTexture(ResourceLocation texture_location, byte[] texture_byte){
        var texture = new ByteTexture(texture_byte, texture_location);
        Minecraft.getInstance().getTextureManager().register(texture_location, texture);
    }

}
