package dfdyz.ef_skin.client.physics;

import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Quaternion;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class JointSpring {

    protected final PhysicsRigidBody A, B;

    protected final Vector3f equilibrium;
    protected final Vector3f stiffness;

    public JointSpring(PhysicsRigidBody a, PhysicsRigidBody b, Vector3f equilibrium, Vector3f stiffness) {
        A = a;
        B = b;
        this.equilibrium = equilibrium;
        this.stiffness = stiffness;
    }

    private static final Quaternionf rA = new Quaternionf();
    private static final Quaternionf rB = new Quaternionf();
    private static final Vector3f euler = new Vector3f();
    private static final Vector3f totalTorque = new Vector3f();


    private static final Quaternion rAb = new Quaternion();
    private static final Quaternion rBb = new Quaternion();
    private static final com.jme3.math.Vector3f t = new com.jme3.math.Vector3f();

    public void feedback(){
        A.getPhysicsRotation(rAb);
        B.getPhysicsRotation(rBb);

        rA.set(rAb.getX(), rAb.getY(), rAb.getZ(), rAb.getW());
        rB.set(rBb.getX(), rBb.getY(), rBb.getZ(), rBb.getW());

        var deltaR = rA.invert().mul(rB);
        deltaR.getEulerAnglesYXZ(euler);
        rA.set(rAb.getX(), rAb.getY(), rAb.getZ(), rAb.getW());
        totalTorque.set(
                getSphereDelta(euler.x, equilibrium.x) * stiffness.x,
                getSphereDelta(euler.y, equilibrium.y) * stiffness.y,
                getSphereDelta(euler.z, equilibrium.z) * stiffness.z
        );
        totalTorque.rotate(rA);
        B.applyTorque(t.set(totalTorque.x, totalTorque.y, totalTorque.z));
    }

    private static final float DoublePi = Mth.PI * 2;
    public float getSphereDelta(float a, float b){
        float result = (b-a) % (DoublePi);

        if(result < -Mth.PI){
            return result + DoublePi;
        }
        else if(result > Mth.PI){
            return result - DoublePi;
        }
        return result;
    }
}
