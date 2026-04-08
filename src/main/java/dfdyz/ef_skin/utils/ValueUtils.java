package dfdyz.ef_skin.utils;

public class ValueUtils {
    public static int[] arrayCheck(int[] src, int minLen){
        if (src == null || src.length < minLen){
            src = new int[minLen];
        }
        return src;
    }

    public static float[] arrayCheck(float[] src, int minLen){
        if (src == null || src.length < minLen){
            src = new float[minLen];
        }
        return src;
    }

    public static int limitValue(int value, int min, int max){
        return Math.max(min, Math.min(max, value));
    }

    public static float limitValue(float value, float min, float max){
        return Math.max(min, Math.min(max, value));
    }

    public static int setMaskTo(int origin, int id, boolean collision){
        int g = 0x0001 << (id % 16);
        if(collision){
            return (origin | g);
        }
        else {
            return (origin & (~g)) & 0x0000FFFF;
        }
    }
}
