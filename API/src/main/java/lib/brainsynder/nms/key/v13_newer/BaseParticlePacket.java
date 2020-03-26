package lib.brainsynder.nms.key.v13_newer;

import lib.brainsynder.nms.ParticlePacket;
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

/**
 * {@link net.minecraft.server.v1_15_R1.PacketPlayOutWorldParticles}
 */
public class BaseParticlePacket extends ParticlePacket {
    private Constructor<?> packetConstructor = null, dustOption;
    private Method createBlockData, toNMS;

    public BaseParticlePacket() {
        try {
            dustOption = Reflection.getConstructor(Class.forName("org.bukkit.Particle$DustOptions"), Color.class, Float.TYPE);
            packetConstructor = Reflection.getNmsClass("PacketPlayOutWorldParticles").getDeclaredConstructor(
                    Reflection.getNmsClass("ParticleParam"),
                    Boolean.TYPE, // longDistance
                    Double.TYPE, // x
                    Double.TYPE, // y
                    Double.TYPE, // z
                    Float.TYPE, // offsetX
                    Float.TYPE, // offsetY
                    Float.TYPE, // offsetZ
                    Float.TYPE, // speed
                    Integer.TYPE); // count
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        toNMS = Reflection.getMethod(Reflection.getCBCClass("CraftParticle"), "toNMS", org.bukkit.Particle.class, Object.class);
        createBlockData = Reflection.getMethod(Bukkit.class, "createBlockData", Material.class);
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
/*
> [23:30:04] [Server thread/WARN]: java.lang.NullPointerException
> [23:30:04] [Server thread/WARN]: at plots.brainsynder.shaded.bslib.nms.key.v13_newer.BaseParticlePacket.getPacket(BaseParticlePacket.java:65)
> [23:30:04] [Server thread/WARN]: at plots.brainsynder.shaded.bslib.particle.ParticleMaker.createPacket(ParticleMaker.java:223)
> [23:30:04] [Server thread/WARN]: at plots.brainsynder.shaded.bslib.particle.ParticleMaker.sendToPlayer(ParticleMaker.java:182)
> [23:30:04] [Server thread/WARN]: at plots.brainsynder.shaded.bslib.particle.ParticleMaker.lambda$sendToLocation$0(ParticleMaker.java:171)
> [23:30:04] [Server thread/WARN]: at java.util.ArrayList.forEach(ArrayList.java:1257)
> [23:30:04] [Server thread/WARN]: at plots.brainsynder.shaded.bslib.particle.ParticleMaker.sendToLocation(ParticleMaker.java:169)
> [23:30:04] [Server thread/WARN]: at plots.brainsynder.Main.handleEvent(Main.java:157)
> [23:30:04] [Server thread/WARN]: at plots.brainsynder.Main.onRedstone(Main.java:145)
> [23:30:04] [Server thread/WARN]: at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
> [23:30:04] [Server thread/WARN]: at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
> [23:30:04] [Server thread/WARN]: at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
> [23:30:04] [Server thread/WARN]: at java.lang.reflect.Method.invoke(Method.java:498)
> [23:30:04] [Server thread/WARN]: at org.bukkit.plugin.java.JavaPluginLoader$1.execute(JavaPluginLoader.java:316)
> [23:30:04] [Server thread/WARN]: at org.bukkit.plugin.RegisteredListener.callEvent(RegisteredListener.java:70)
> [23:30:04] [Server thread/WARN]: at org.bukkit.plugin.SimplePluginManager.fireEvent(SimplePluginManager.java:529)
> [23:30:04] [Server thread/WARN]: at org.bukkit.plugin.SimplePluginManager.callEvent(SimplePluginManager.java:514)
> [23:30:04] [Server thread/WARN]: at net.minecraft.server.v1_15_R1.IDispenseBehavior$9.a(IDispenseBehavior.java:147)
> [23:30:04] [Server thread/WARN]: at net.minecraft.server.v1_15_R1.DispenseBehaviorItem.dispense(DispenseBehaviorItem.java:15)
> [23:30:04] [Server thread/WARN]: at net.minecraft.server.v1_15_R1.BlockDispenser.dispense(BlockDispenser.java:63)
> [23:30:04] [Server thread/WARN]: at net.minecraft.server.v1_15_R1.BlockDispenser.tick(BlockDispenser.java:89)
> [23:30:04] [Server thread/WARN]: at net.minecraft.server.v1_15_R1.IBlockData.a(SourceFile:263)
> [23:30:04] [Server thread/WARN]: at net.minecraft.server.v1_15_R1.WorldServer.b(WorldServer.java:590)
> [23:30:04] [Server thread/WARN]: at net.minecraft.server.v1_15_R1.TickListServer.b(TickListServer.java:82)
> [23:30:04] [Server thread/WARN]: at net.minecraft.server.v1_15_R1.WorldServer.doTick(WorldServer.java:303)
> [23:30:04] [Server thread/WARN]: at net.minecraft.server.v1_15_R1.MinecraftServer.b(MinecraftServer.java:1076)
> [23:30:04] [Server thread/WARN]: at net.minecraft.server.v1_15_R1.DedicatedServer.b(DedicatedServer.java:393)
> [23:30:04] [Server thread/WARN]: at net.minecraft.server.v1_15_R1.MinecraftServer.a(MinecraftServer.java:978)
> [23:30:04] [Server thread/WARN]: at net.minecraft.server.v1_15_R1.MinecraftServer.run(MinecraftServer.java:823)
> [23:30:04] [Server thread/WARN]: at java.lang.Thread.run(Thread.java:748)
> [23:30:04] [Server thread/WARN]: java.lang.NullPointerException
> [23:30:04] [Server thread/WARN]: at plots.brainsynder.shaded.bslib.nms.key.v13_newer.BaseParticlePacket.getPacket(BaseParticlePacket.java:65)
> [23:30:04] [Server thread/WARN]: at plots.brainsynder.shaded.bslib.particle.ParticleMaker.createPacket(ParticleMaker.java:223)
> [23:30:04] [Server thread/WARN]: at plots.brainsynder.shaded.bslib.particle.ParticleMaker.sendToPlayer(ParticleMaker.java:182)
> [23:30:04] [Server thread/WARN]: at plots.brainsynder.shaded.bslib.particle.ParticleMaker.lambda$sendToLocation$0(ParticleMaker.java:171)
 */
