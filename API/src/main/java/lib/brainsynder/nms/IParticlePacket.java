package lib.brainsynder.nms;

import lib.brainsynder.ServerVersion;
import lib.brainsynder.particle.Particle;
import lib.brainsynder.storage.TriLoc;

public abstract class IParticlePacket {
    private static IParticlePacket particlePacket = null;

    public abstract Object getPacket(Particle particle, TriLoc<Float> loc, TriLoc<Float> offset, float speed, int count, Object data);

    public static IParticlePacket getInstance() {
        if (particlePacket != null) return particlePacket;
        if (ServerVersion.isOlder(ServerVersion.v1_8_R3)) {
            particlePacket = new lib.brainsynder.nms.key.v8_lower.BaseParticlePacket();
        } else if (ServerVersion.isEqualNew(ServerVersion.v1_8_R3) && ServerVersion.isOlder(ServerVersion.v1_13_R1)) {
            particlePacket = new lib.brainsynder.nms.key.v8_to_v12.BaseParticlePacket();
        } else {
            particlePacket = new lib.brainsynder.nms.key.v13_newer.BaseParticlePacket();
        }
        return particlePacket;
    }
}
