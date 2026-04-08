package dfdyz.ef_skin.client.physics;

import com.jme3.bullet.objects.PhysicsRigidBody;
import dfdyz.ef_anim_phy.physics.EntityAnimationPhysics;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.ArrayList;
import java.util.List;

public class SafeAnimPhy extends EntityAnimationPhysics implements AutoCloseable {

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

    public boolean isReady(){
        return warmuped;
    }
}
