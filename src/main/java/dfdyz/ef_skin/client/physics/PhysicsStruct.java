package dfdyz.ef_skin.client.physics;

import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class PhysicsStruct {
    public ColliderDefine[] bodies;
    public ChainDefine[] chains;

    public void prevProcess(){
        for (ColliderDefine define : bodies) {
            define.prevProcess();
        }
        for (ChainDefine chain : chains) {
            chain.colliders.forEach((k,v) -> v.prevProcess());
        }
    }

    public SafeAnimPhy construct(LivingEntityPatch<?> entityPatch, Armature armature){
        var sap = new SafeAnimPhy(entityPatch);
        sap.prepare(armature);
        for (ColliderDefine define : bodies) {
            var jn = define.bind;
            var jt = armature.searchJointByName(jn);
            var sbc = sap.addStaticBone(jt, define.create(), define.group, define.getMask());
            sbc.offsetPos.set(define.offset[0], define.offset[1], define.offset[2]);
        }

        for (ChainDefine chain : chains) {
            sap.addChain(chain.construct(sap, armature));
        }
        return sap;
    }
}
