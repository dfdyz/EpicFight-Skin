package dfdyz.ef_skin.skin;

public class SkinInfo {

    // serializable field
    public String name;
    public String author;
    public float[] scale;

    // program cache
    public String path;
    public String md5;
    public String key;

    public boolean check(){
        if (name == null) return false;
        if (author == null) return false;
        //if (scale == null || scale.length != 3) return false;
        return true;
    }
}
