package dfdyz.ef_skin.client.physics;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.joints.Constraint;
import com.jme3.bullet.joints.New6Dof;
import com.jme3.bullet.joints.motors.MotorParam;
import com.jme3.math.Matrix3f;
import dfdyz.ef_anim_phy.physics.bodies.DynamicBoneChain;
import dfdyz.ef_anim_phy.physics.bodies.JointSpring;
import org.joml.Vector3f;
import yesman.epicfight.api.model.Armature;

import java.util.HashMap;

import static com.jme3.bullet.RotationOrder.ZXY;

public class ChainDefine {
    public String[] bone_sequence;
    public HashMap<String, ColliderDefine> colliders;
    public float rare_length = 0.3f;

    public DynamicBoneChain<?> construct(SafeAnimPhy physics, Armature armature){
        float[] mass = new float[bone_sequence.length];
        CollisionShape[] shapes = new CollisionShape[bone_sequence.length];
        int[] groups = new int[bone_sequence.length];
        int[] masks = new int[bone_sequence.length];
        float[] move_damp = new float[bone_sequence.length];
        float[] rot_damp = new float[bone_sequence.length];

        for (int i = 0; i < bone_sequence.length; i++) {
            var bn = bone_sequence[i];
            var define = colliders.get(bn);
            mass[i] = define.mass;
            shapes[i] = define.create();
            groups[i] = define.group;
            masks[i] = define.getMask();
            move_damp[i] = define.move_damping;
            rot_damp[i] = define.rot_damping;
        }

        return new DynamicBoneChain<>(physics.physicsSpace, armature, bone_sequence,
                mass, move_damp, rot_damp, shapes, groups, masks, rare_length,
                (bn, A, B, povA, povB) ->
                {
                    var define = colliders.get(bn);
                    var joint1 = new New6Dof(A, B, povA, povB, Matrix3f.IDENTITY, Matrix3f.IDENTITY, ZXY);
                    joint1.set(MotorParam.LowerLimit, 3, (float) Math.toRadians(define.limit[0])); // X axis
                    joint1.set(MotorParam.UpperLimit, 3, (float) Math.toRadians(define.limit[1]));
                    joint1.set(MotorParam.LowerLimit, 4, (float) Math.toRadians(define.limit[2])); // Y axis
                    joint1.set(MotorParam.UpperLimit, 4, (float) Math.toRadians(define.limit[3]));
                    joint1.set(MotorParam.LowerLimit, 5, (float) Math.toRadians(define.limit[4])); // Z axis
                    joint1.set(MotorParam.UpperLimit, 5, (float) Math.toRadians(define.limit[5]));

                    if(define.joint_stiffness[0] >= 1e-8f ||
                            define.joint_stiffness[1] >= 1e-8f||
                            define.joint_stiffness[2] >= 1e-8f){
                        physics.jointSprings.add(
                                new JointSpring(A, B,
                                        new Vector3f((float) Math.toRadians(define.equilibrium_point[0]),
                                                (float) Math.toRadians(define.equilibrium_point[1]),
                                                (float) Math.toRadians(define.equilibrium_point[2])),
                                        new Vector3f(define.joint_stiffness[0], define.joint_stiffness[1], define.joint_stiffness[2])
                                )
                        );
                    }

                    for (int j = 0; j < 3; j++) {
                        if(define.joint_damping[j] > 1e-8f){
                            //System.out.println(define.joint_stiffness[j]);
                            joint1.enableSpring(j+3, true);
                            joint1.getRotationMotor(j).setDampingLimited(true);
                            joint1.set(MotorParam.Stiffness, j+3, 0);
                            joint1.set(MotorParam.Damping, j+3, define.joint_damping[j]);
                        }
                    }

                    return new Constraint[]{joint1};
                });
    }

}
