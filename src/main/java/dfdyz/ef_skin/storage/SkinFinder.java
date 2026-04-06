package dfdyz.ef_skin.storage;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.mojang.logging.LogUtils;
import dfdyz.ef_skin.client.texture.ByteTexture;
import dfdyz.ef_skin.skin.SkinInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Map;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class SkinFinder {

    public static final Gson gson = new Gson();
    public static final Path skinRoot = FMLPaths.GAMEDIR.get().resolve("EF_Skin");
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void setupPath(){
        try {
            if(!skinRoot.toFile().mkdir())
                skinRoot.toFile().mkdir();
        }catch (Exception ignore){
        }
    }

    public static Map<String, SkinInfo> getSkinList(){
        var root = skinRoot.toFile();

        var skins = root.listFiles();

        assert skins != null;

        Map<String, SkinInfo> result = Maps.newHashMap();
        for (File skin : skins) {
            try {
                var info_file = skin.toPath().resolve("info.json").toFile();
                try (var inputStream = new FileInputStream(info_file)){
                    var skin_info = gson.fromJson(new InputStreamReader(inputStream), SkinInfo.class);
                    if(skin_info.check()){
                        skin_info.path = skin.toString();
                        skin_info.key = skin.getName();
                        LOGGER.info(MessageFormat.format("Find \"{0}\".", info_file.toString()));
                        result.put(skin.getName(), skin_info);
                    }
                    else {
                        LOGGER.error(MessageFormat.format("Read \"{0}\" failed.", info_file));
                    }
                }
            } catch (Exception e){
                LOGGER.error(e.getMessage());
            }
        }
        return result;
    }

}
