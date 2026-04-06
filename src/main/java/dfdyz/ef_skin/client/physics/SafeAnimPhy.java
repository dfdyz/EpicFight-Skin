package dfdyz.ef_skin.client.physics;

import dfdyz.ef_anim_phy.physics.EntityAnimationPhysics;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.ArrayList;
import java.util.List;

public class SafeAnimPhy extends EntityAnimationPhysics implements AutoCloseable {

    public final List<JointSpring> jointSprings = new ArrayList<>();

    public SafeAnimPhy(LivingEntityPatch<?> entityPatch) {
        super(entityPatch);
    }

    @Override
    public void close() {
        destroy();
    }

    public void tick(){
        this.tick(true);
    }

    @Override
    public void updateStaticBonesStep(float dx, float dy, float dz, float yRotO, float yRot, Armature armature, Animator animator, float pt) {
        super.updateStaticBonesStep(dx, dy, dz, yRotO, yRot, armature, animator, pt);
        if(warmuped){
            jointSprings.forEach(JointSpring::feedback);
        }
    }

    public boolean isReady(){
        return warmuped;
    }

}
