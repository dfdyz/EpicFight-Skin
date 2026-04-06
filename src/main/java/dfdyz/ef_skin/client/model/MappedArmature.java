package dfdyz.ef_skin.client.model;

import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.model.armature.HumanoidArmature;

import java.util.Map;

public class MappedArmature extends HumanoidArmature {
    public MappedArmature(String name, int jointNumber, Joint rootJoint, Map<String, Joint> jointMap) {
        super(name, jointNumber, rootJoint, jointMap);
    }

}
