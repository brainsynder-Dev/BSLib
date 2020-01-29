package lib.brainsynder.nms.key.v8_lower;

import lib.brainsynder.nms.IParticlePacket;
import lib.brainsynder.particle.DustOptions;
import lib.brainsynder.particle.Particle;
import lib.brainsynder.reflection.Reflection;
import lib.brainsynder.storage.TriLoc;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class BaseParticlePacket extends IParticlePacket {
    private Constructor<?> packetConstructor = null;
    private Class<?> enumParticle = null;

    public BaseParticlePacket() {
        try {
            Class<?> packetClass = Reflection.getNmsClass("PacketPlayOutWorldParticles");
            packetConstructor = packetClass.getConstructor(
                    String.class,
                    Float.TYPE,
                    Float.TYPE,
                    Float.TYPE,
                    Float.TYPE,
                    Float.TYPE,
                    Float.TYPE,
                    Float.TYPE,
                    Integer.TYPE);
        } catch (Exception ex) {
        }
    }

    @Override
    public Object getPacket(Particle type, TriLoc<Float> loc, TriLoc<Float> offset, float speed, int count, Object data) {
        if (!type.isCompatable()) return null;
        float offsetX = offset.getX(),
                offsetY = offset.getY(),
                offsetZ = offset.getZ();

        if (data instanceof DustOptions) {
            DustOptions dust = (DustOptions) data;
            offsetX = getColor(dust.getColor().getRed());
            offsetY = getColor(dust.getColor().getGreen());
            offsetZ = getColor(dust.getColor().getBlue());
        }

        try {
            return packetConstructor.newInstance(
                    type.getName(),
                    (float) loc.getX(),
                    (float) loc.getY(),
                    (float) loc.getZ(),
                    (float) offsetX,
                    (float) offsetY,
                    (float) offsetZ,
                    (float) speed,
                    count);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException ignored) {}
        return null;
    }

    private float getColor(double value) {
        if (value <= 0.0F) {
            value = -1.0F;
        }

        return ((float) value) / 255.0F;
    }

}
