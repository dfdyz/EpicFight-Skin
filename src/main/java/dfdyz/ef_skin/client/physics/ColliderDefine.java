package dfdyz.ef_skin.client.physics;

import com.jme3.bullet.collision.shapes.*;
import static dfdyz.ef_skin.utils.ValueUtils.*;

public class ColliderDefine {

    public String type; // box, ball, capsule



    public int group;      // default = 0
    public int[] collision_ignore;

    // 胶囊
    public int axis; // 0,1,2 -> x,y,z
    public float height;

    // 胶囊, 球体
    public float radius;

    // 长方体
    public float[] size;

    // only for chain collider
    public float move_damping = 0.3f;
    public float rot_damping = 0.3f;
    public float mass = 0.1f;
    public float[] limit; // default = [-30, 30, -5, 5, -30, 30]
    public float[] joint_stiffness;
    public float[] joint_damping;
    public float[] equilibrium_point;

    // only apply for kinematic collider
    public float[] offset;
    public String bind;

    public CollisionShape create(){
        if(type.equals("box")){
            return new BoxCollisionShape(size[0] / 2, size[1] / 2, size[2] / 2);
        }
        else if (type.equals("ball")){
            return new SphereCollisionShape(radius);
        }
        else if (type.equals("capsule")){
            return new CapsuleCollisionShape(radius, height, axis);
        }
        return new EmptyShape(false);
    }

    public void prevProcess(){
        mass = limitValue(mass, 0.01f, 10f);
        move_damping = limitValue(move_damping, 0.05f, 0.8f);
        rot_damping = limitValue(rot_damping, 0.05f, 0.8f);

        radius = limitValue(radius, 0.01f, 2f);
        height = limitValue(height, 0.01f, 2f);

        axis = limitValue(axis, 0, 2);

        offset = arrayCheck(offset, 3);
        collision_ignore = arrayCheck(collision_ignore, 0);
        size = arrayCheck(size, 3);
        limit = arrayCheck(limit, 6);

        joint_stiffness = arrayCheck(joint_stiffness, 3);
        joint_damping = arrayCheck(joint_damping, 3);
        equilibrium_point = arrayCheck(equilibrium_point, 3);

        if (bind == null) bind = "";

        for (int i = 0; i < 6; i++) {
            limit[i] = limitValue(limit[i], -360, 360);
        }

        group = ((group % 16) + 16) % 16;
        for (int i = 0; i < collision_ignore.length; i++) {
            collision_ignore[i] = ((collision_ignore[i] % 16) + 16) % 16;
        }

        for (int i = 0; i < 3; i++) {
            offset[i] = limitValue(offset[i], -2f, 2f);
            size[i] = limitValue(size[i], 0.001f, 2f);
            joint_stiffness[i] = limitValue(joint_stiffness[i], 0, 1000);
            joint_damping[i] = limitValue(joint_damping[i], 0, 1);

            if(limit[2*i] <= limit[2*i+1]){
                equilibrium_point[i] = limitValue(equilibrium_point[i], limit[2*i], limit[2*i+1]);
            }
        }

    }

    public int getMask(){
        int mask = 0x0000FFFF;
        for (int ci : collision_ignore) {
            mask = setMaskTo(mask, ci, false);
        }
        return mask;
    }
}
