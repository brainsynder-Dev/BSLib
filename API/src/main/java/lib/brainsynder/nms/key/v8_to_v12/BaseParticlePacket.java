package lib.brainsynder.nms.key.v8_to_v12;

import lib.brainsynder.nms.ParticlePacket;
import lib.brainsynder.particle.DustOptions;
import lib.brainsynder.particle.Particle;
import lib.brainsynder.reflection.Reflection;
import lib.brainsynder.storage.TriLoc;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class BaseParticlePacket extends ParticlePacket {
    private Constructor<?> packetConstructor = null;
    private Class<?> enumParticle = null;

    public BaseParticlePacket() {
        try {
            Class<?> packetClass = Reflection.getNmsClass("PacketPlayOutWorldParticles");
                    enumParticle = Reflection.getNmsClass("EnumParticle");
                packetConstructor = packetClass.getDeclaredConstructor(
                        enumParticle,
                        Boolean.TYPE,
                        Double.TYPE,
                        Double.TYPE,
                        Double.TYPE,
                        Float.TYPE,
                        Float.TYPE,
                        Float.TYPE,
                        Float.TYPE,
                        Integer.TYPE,
                        int[].class);
        } catch (Exception ex) {}
    }

    @Override
    public Object getPacket(Particle type, TriLoc<Float> loc, TriLoc<Float> offset, float speed, int count, Object data) {
        if (!type.isCompatable()) return null;
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

        try {
            Object particleType = enumParticle.getEnumConstants()[type.getId()];
            return packetConstructor.newInstance(
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
