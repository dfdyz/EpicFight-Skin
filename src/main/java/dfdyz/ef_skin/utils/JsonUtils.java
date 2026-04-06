package dfdyz.ef_skin.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class JsonUtils {



    private static JsonObject emptyPart(){
        var p = new JsonObject();
        p.addProperty("stride", 3);
        p.addProperty("count", 0);
        p.add("array", new JsonArray());
        return p;
    }

    private static final String[] humanoid_parts = new String[]{
            "rightArm","hat","leftPants","rightPants",
            "rightLeg","leftArm","leftSleeve","torso",
            "head","jacket","rightSleeve","leftLeg",
    };
    public static void patchMesh(JsonObject rootJson){
        var vertices =  rootJson.get("vertices").getAsJsonObject();
        JsonObject parts = vertices.has("parts") ? vertices.getAsJsonObject("parts") : new JsonObject();
        for (String p : humanoid_parts) {
            if(!parts.has(p)) parts.add(p, emptyPart());
        }
        vertices.add("parts", parts);
    }


}
