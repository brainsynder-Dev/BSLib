package lib.brainsynder.nms.key;

import lib.brainsynder.ServerVersion;
import lib.brainsynder.nms.IParticlePacket;
import lib.brainsynder.particle.DustOptions;
import lib.brainsynder.particle.Particle;
import lib.brainsynder.reflection.Reflection;
import lib.brainsynder.storage.TriLoc;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class BaseParticlePacket extends IParticlePacket {
    private Constructor<?> packetConstructor = null;
    private boolean newParticlePacketConstructor = false, newParticle = false;
    private Class<?> enumParticle = null;

    public BaseParticlePacket() {
        try {
            Class<?> packetClass = Reflection.getNmsClass("PacketPlayOutWorldParticles");
            if (ServerVersion.isEqualOld(ServerVersion.v1_8_R3)) {
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
            } else {

                newParticlePacketConstructor = true;
                if (ServerVersion.isEqualNew(ServerVersion.v1_14_R1)) {
                    enumParticle = Reflection.getNmsClass("Particles");
                    newParticle = true;
                }else{
                    enumParticle = Reflection.getNmsClass("EnumParticle");
                }
                packetConstructor = packetClass.getDeclaredConstructor(
                        enumParticle,
                        Boolean.TYPE,
                        Float.TYPE,
                        Float.TYPE,
                        Float.TYPE,
                        Float.TYPE,
                        Float.TYPE,
                        Float.TYPE,
                        Float.TYPE,
                        Integer.TYPE,
                        int[].class);
            }
        } catch (Exception ex) {}
    }

    @Override
    public Object getPacket(Particle type, TriLoc<Float> loc, TriLoc<Float> offset, float speed, int count, Object data) {
        int[] inner = new int[0];
        float offsetX = offset.getX(),
                offsetY = offset.getY(),
                offsetZ = offset.getZ();

        if ((type == Particle.ITEM_CRACK)
                || (type == Particle.BLOCK_CRACK)
                || (type == Particle.ITEM_TAKE)
                || (type == Particle.BLOCK_DUST)) {
            if (data == null) {
                inner = new int[] {1,0};
            }else if (data instanceof ItemStack){
                ItemStack stack = (ItemStack)data;
                inner = new int[] {stack.getType().getId(), stack.getDurability()};
            }else if (data instanceof int[]){
                inner = (int[])data;
            }
        }
        if (data instanceof DustOptions) {
            DustOptions dust = (DustOptions) data;
            offsetX=getColor(dust.getColor().getRed());
            offsetY=getColor(dust.getColor().getGreen());
            offsetZ=getColor(dust.getColor().getBlue());
        }

        if (!type.isCompatable()) return null;
        try {
            Object packet;
            if (newParticlePacketConstructor) {
                Object particleType = null;
                if (newParticle) {
                    for (Field field : enumParticle.getFields()) {
                        if (field.getName().equalsIgnoreCase(type.name())) {
                            particleType = field.get(null);
                            break;
                        }
                    }
                    if (particleType == null) throw new NullPointerException("Unable to find particle '"+type.name()+"' in Particles.java Field");
                }else{
                    particleType = enumParticle.getEnumConstants()[type.getId()];
                }

                packet = packetConstructor.newInstance(
                        particleType,
                        true,
                        (float) loc.getX(),
                        (float) loc.getY(),
                        (float) loc.getZ(),
                        (float) offsetX,
                        (float) offsetY,
                        (float) offsetZ,
                        (float) speed,
                        count,
                        inner);
            } else {
                packet = packetConstructor.newInstance(
                        type.getName(),
                        (float) loc.getX(),
                        (float) loc.getY(),
                        (float) loc.getZ(),
                        (float) offsetX,
                        (float) offsetY,
                        (float) offsetZ,
                        (float) speed,
                        count);
            }
            return packet;
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
