package lib.brainsynder.nms;

import lib.brainsynder.ServerVersion;
import lib.brainsynder.particle.Particle;
import lib.brainsynder.storage.TriLoc;

public abstract class IParticlePacket {
    private static IParticlePacket particlePacket = null;

    public abstract Object getPacket(Particle particle, TriLoc<Float> loc, TriLoc<Float> offset, float speed, int count, Object data);

    public static IParticlePacket getInstance() {
        if (particlePacket != null) return particlePacket;

        try {
            Class<?> clazz = Class.forName("simple.brainsynder.nms." + ServerVersion.getVersion().name() + ".ParticlePacket");
            if (IParticlePacket.class.isAssignableFrom(clazz)) {
                particlePacket = (IParticlePacket) clazz.getConstructor().newInstance();
            }
        } catch (Exception e) {
            try {
                particlePacket = (IParticlePacket) Class.forName("lib.brainsynder.nms.key.BaseParticlePacket").newInstance();
            } catch (InstantiationException | ClassNotFoundException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
        return particlePacket;
    }
}
