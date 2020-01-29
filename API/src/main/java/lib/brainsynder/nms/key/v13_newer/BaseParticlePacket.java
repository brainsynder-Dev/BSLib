package lib.brainsynder.nms.key.v13_newer;

import lib.brainsynder.nms.IParticlePacket;
import lib.brainsynder.particle.DustOptions;
import lib.brainsynder.particle.Particle;
import lib.brainsynder.reflection.Reflection;
import lib.brainsynder.storage.TriLoc;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BaseParticlePacket extends IParticlePacket {
    private Constructor<?> packetConstructor = null, dustOption;
    private Method createBlockData, toNMS;

    public BaseParticlePacket() {
        toNMS = Reflection.getMethod(Reflection.getCBCClass("CraftParticle"), "toNMS", org.bukkit.Particle.class, Object.class);
        createBlockData = Reflection.getMethod(Bukkit.class, "createBlockData", Material.class);
        try {
            dustOption = Reflection.getConstructor(Class.forName("org.bukkit.Particle$DustOptions"), Color.class, Float.TYPE);
            packetConstructor = Reflection.getNmsClass("PacketPlayOutWorldParticles").getDeclaredConstructor(
                    Reflection.getNmsClass("ParticleParam"),
                    Boolean.TYPE, // longDistance
                    Float.TYPE, // x
                    Float.TYPE, // y
                    Float.TYPE, // z
                    Float.TYPE, // offsetX
                    Float.TYPE, // offsetY
                    Float.TYPE, // offsetZ
                    Float.TYPE, // speed
                    Integer.TYPE); // count
        } catch (Exception ex) {
        }
    }

    @Override
    public Object getPacket(Particle type, TriLoc<Float> loc, TriLoc<Float> offset, float speed, int count, Object data) {
        if (!type.isCompatable()) return null;
        float offsetX = offset.getX(),
                offsetY = offset.getY(),
                offsetZ = offset.getZ();

        Object target = null;
        if (data instanceof ItemStack) {
            ItemStack item = (ItemStack) data;
            if (item.getType().isBlock() && (type.name().contains("BLOCK"))) {
                target = Reflection.invoke(createBlockData, null, item.getType());
            } else {
                target = item;
            }
        }

        if (data instanceof DustOptions) {
            DustOptions dustOptions = (DustOptions) data;
            target = Reflection.initiateClass(dustOption, dustOptions.getColor(), dustOptions.getSize());
        }

        Object param = Reflection.invoke(toNMS, null, org.bukkit.Particle.valueOf(type.name()), target);
        try {
            return packetConstructor.newInstance(
                    param,
                    true,
                    (float) loc.getX(),
                    (float) loc.getY(),
                    (float) loc.getZ(),
                    (float) offsetX,
                    (float) offsetY,
                    (float) offsetZ,
                    (float) speed,
                    count);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException ignored) {
        }
        return null;
    }
}
