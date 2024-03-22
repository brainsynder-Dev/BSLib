package lib.brainsynder.reflection;

import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class Reflection {
    private static final HashMap<Class<? extends Entity>, Method> handles = new HashMap<>();



    public static void sendPacket(Player player, Object packet)
            throws IllegalArgumentException {
        if (packet == null) return;
        Object handle = getHandle(player);
        if (handle == null) return;
        Object connection = getNMSFields(handle, "server.level", "playerConnection", "b");
        Method sendPacket = getMethod(getNmsClass("PlayerConnection", "server.network"), new String[]{"sendPacket", "a"}, getNmsClass("Packet", "network.protocol"));
        if (connection == null) return;
        if (sendPacket == null) return;
        try {
            sendPacket.invoke(connection, packet);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }

    public static <T> T invokeNMSStaticMethod(String className, String method, Class<?>[] parameterClasses, Object... params) {
        return invokeNMSMethod(className, method, null, parameterClasses, params);
    }

    public static <T> T initiateClass(Class<?> clazz) {
        try {
            return (T) clazz.newInstance();
        } catch (Exception e) {
        }
        return null;
    }

    public static <T> T initiateClass(Constructor<?> constructor, Object... args) {
        try {
            return (T) constructor.newInstance(args);
        } catch (Exception e) {
        }
        return null;
    }

    public static Constructor<?> fillConstructor(Class<?> clazz, Class<?>... values) {
        try {
            return clazz.getDeclaredConstructor(values);
        } catch (Exception e) {
        }
        return null;
    }

    @Deprecated
    public static <T> T invokeNMSMethod(String className, String method, Object invoker, Class<?>[] parameterClasses, Object... params) {
        return invokeNMSMethod(className, "", method, invoker, parameterClasses);
    }

    public static <T> T invokeNMSMethod(String className, String subLoc, String method, Object invoker, Class<?>[] parameterClasses, Object... params) {
        try {
            Class e = getNmsClass(className, subLoc);
            Method m = e.getDeclaredMethod(method, parameterClasses);
            m.setAccessible(true);
            return (T) m.invoke(invoker, params);
        } catch (Exception var7) {
            var7.printStackTrace();
            return null;
        }
    }

    public static <T> T invokeNMSMethod(String method, Object invoker, Class<?>[] parameterClasses, Object... params) {
        Objects.requireNonNull(invoker, "Invoker cannot be null");
        return invokeNMSMethod(invoker.getClass().getSimpleName(), method, invoker, parameterClasses, params);
    }

    public static <T> T invokeNMSMethod(String method, Object invoker) {
        Objects.requireNonNull(invoker, "Invoker cannot be null");
        return invokeNMSMethod(method, invoker, new Class[0], new Object[0]);
    }

    public static Object newNMS(String className) {
        return newNMS(className, "", new Class[0]);
    }

    @Deprecated
    public static Object newNMS(String className, Class<?>[] parameterClasses, Object... params) {
        return newNMS(className, "", parameterClasses, params);
    }

    public static Object newNMS(String className, String subLoc, Class<?>[] parameterClasses, Object... params) {
        try {
            Class ex = getNmsClass(className, subLoc);
            Constructor constructor = ex.getDeclaredConstructor(parameterClasses);
            constructor.setAccessible(true);
            return constructor.newInstance(params);
        } catch (Exception var5) {
            var5.printStackTrace();
            return null;
        }
    }

    public static <T> T getNMSFields(Object owner, String subLoc, String... fieldNames) {
        for (String fieldName : fieldNames) {
            try {
                return getNMSField(owner.getClass().getSimpleName(), subLoc, owner, fieldName);
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
            }
        }
        return null;
    }

    public static <T> T getNMSField(Object owner, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        return getNMSField(owner.getClass().getSimpleName(), owner, fieldName);
    }

    @Deprecated
    public static <T> T getNMSField(String className, Object owner, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        return getNMSField(className, "", owner, fieldName);
    }

    public static <T> T getNMSField(String className, String subLoc, Object owner, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Class ex = getNmsClass(className, subLoc);
        Field field = ex.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(owner);
    }

    public static <T> T getNMSStaticField(String className, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        return getNMSField(className, null, fieldName);
    }

    public static <T> T invokeBukkitStaticMethod(String className, String method, Class<?>[] parameterClasses, Object... params) {
        return invokeBukkitMethod(className, method, null, parameterClasses, params);
    }

    public static <T> T invokeBukkitMethod(String className, String method, Object invoker, Class<?>[] parameterClasses, Object... params) {

        try {
            Class e = getCBCClass(className);
            Method m = e.getDeclaredMethod(method, parameterClasses);
            m.setAccessible(true);
            return (T) m.invoke(invoker, params);
        } catch (Exception var7) {
            var7.printStackTrace();
            return null;
        }
    }

    public static <T> T invokeBukkitMethod(String method, Object invoker, Class<?>[] parameterClasses, Object... params) {
        String version = getBukkitPackageVersion()+".";
        if (PaperLib.isPaper() && version.equals(".")) version = "";

        return invokeBukkitMethod(invoker.getClass().getName().replace("org.bukkit.craftbukkit." + version, ""), method, invoker, parameterClasses, params);
    }

    public static <T> T invokeBukkitMethod(String method, Object invoker) {
        return invokeBukkitMethod(method, invoker, new Class[0], new Object[0]);
    }

    public static Object newBukkit(String className) {
        return newBukkit(className, new Class[0]);
    }

    public static Object newBukkit(String className, Class<?>[] parameterClasses, Object... params) {
        try {
            Class ex = getCBCClass(className);
            Constructor constructor = ex.getConstructor(parameterClasses);
            constructor.setAccessible(true);
            return constructor.newInstance(params);
        } catch (Exception var5) {
            var5.printStackTrace();
            return null;
        }
    }

    public static <T> T getBukkitField(Object owner, String fieldName) {
        String version = getBukkitPackageVersion()+".";
        if (PaperLib.isPaper() && version.equals(".")) version = "";

        return getBukkitField(owner.getClass().getName().replace("org.bukkit.craftbukkit." + version, ""), owner, fieldName);
    }

    public static <T> T getBukkitField(String className, Object owner, String fieldName) {
        try {
            Class ex = getCBCClass(className);
            Field field = ex.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(owner);
        } catch (Exception var5) {
            var5.printStackTrace();
            return null;
        }
    }

    public static <T> T getBukkitStaticField(String className, String fieldName) {
        return getBukkitField(className, null, fieldName);
    }

    public static Object getEntityHandle(Entity entity) {
        return invokeMethod(getMethod(getCBCClass("entity.CraftEntity"), "getHandle"), entity);
    }

    public static Object getHandle(Entity entity) {
        try {
            if (handles.get(entity.getClass()) != null) {
                return handles.get(entity.getClass()).invoke(entity);
            }
            Method entity_getHandle = entity.getClass().getMethod("getHandle");
            handles.put(entity.getClass(), entity_getHandle);
            return entity_getHandle.invoke(entity);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static <T> T getPrivateField(String fieldName, Class clazz, Object object) {
        Field field;
        T o = null;
        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            o = (T) field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return o;
    }

    public Object getPrivateStatic(Class<?> clazz, String f) throws Exception {
        Field field = clazz.getDeclaredField(f);
        field.setAccessible(true);
        return field.get(null);
    }

    public static Class<?> getNmsClass(String name) {
        String version = getBukkitPackageVersion()+".";
        if (PaperLib.isPaper() && version.equals(".")) version = "";

        Class<?> clazz = null;
        try {
            clazz = Class.forName("net.minecraft.server." + version + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return clazz;
    }

    public static Class<?> getNmsClass(String name, String subLoc) {
        Class<?> clazz;
        try {
            if (subLoc == null || subLoc.length() == 0) {
                clazz = Class.forName("net.minecraft." + name);
            } else {
                clazz = Class.forName("net.minecraft." + subLoc + "." + name);
            }
        } catch (ClassNotFoundException ex) {
            clazz = getNmsClass(name);
        }
        return clazz;
    }

    public static String getBukkitPackageVersion() {
        try {
            return Bukkit.getServer().getClass().getPackage().getName().substring(23);
        } catch (Exception e) {
            return "";
        }
    }

    public static Object getFieldValue(Field field, Object instance) {
        try {
            return field.get(instance);
        } catch (IllegalAccessException var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public static Object invoke(Method method, Object instance, Object... parameters) {
        if (method == null) {
            return null;
        } else {
            try {
                return method.invoke(instance, parameters);
            } catch (InvocationTargetException | IllegalAccessException var4) {
                var4.printStackTrace();
                return null;
            }
        }
    }

    public static <T> T getWorldHandle(World world) {
        return invokeMethod(getMethod(getCBCClass("CraftWorld"), "getHandle"), world);
    }

    public static Field getField(Class clazz, String name) {
        try {
            return setFieldAccessible(clazz.getDeclaredField(name));
        } catch (Throwable var3) {
            return null;
        }
    }

    public static Constructor getConstructor(Class cl, Class... classes) {
        try {
            Constructor var8 = cl.getDeclaredConstructor(classes);
            var8.setAccessible(true);
            return var8;
        } catch (Throwable var7) {
            StringBuilder sb = new StringBuilder();
            Class[] var3 = classes;
            int var4 = classes.length;

            for (int var5 = 0; var5 < var4; ++var5) {
                Class c = var3[var5];
                sb.append(", ").append(c.getName());
            }

            return null;
        }
    }

    public static Field getFirstFieldOfType(Class cl, Class returnType, String... matches) {
        Field[] var3 = cl.getDeclaredFields();
        int var4 = var3.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            Field f = var3[var5];
            Type[] types;
            if (f.getType() == returnType && f.getGenericType() instanceof ParameterizedType && matches.length == (types = ((ParameterizedType) f.getGenericType()).getActualTypeArguments()).length) {
                boolean match = true;

                for (int i = 0; i < matches.length; ++i) {
                    if (!((Class) types[i]).getName().matches(matches[i])) {
                        match = false;
                        break;
                    }
                }

                if (match) {
                    return setFieldAccessible(f);
                }
            }
        }

        return null;
    }

    public static Field setFieldAccessible(Field f) {
        try {
            f.setAccessible(true);
            return f;
        } catch (Throwable var3) {
            return null;
        }
    }


    public static Class getCBCClass(String className) {
        String version = getBukkitPackageVersion()+".";
        if (PaperLib.isPaper() && version.equals(".")) version = "";

        try {
            return Class.forName("org.bukkit.craftbukkit." + version + className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    // This is mostly useful in Reflection of multiple version
    public static Class getBukkitClass(String className) {
        try {
            return Class.forName("org.bukkit." + className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... params) {
        try {
            return clazz.getDeclaredMethod(methodName, params);
        } catch (NoSuchMethodException var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public static Method getMethod(Class<?> clazz, String[] methodName, Class<?>... params) {
        for (String method : methodName) {
            try {
                return clazz.getDeclaredMethod(method, params);
            } catch (NoSuchMethodException ignored) {}
        }
        try {
            throw new NoSuchMethodException("Methods " + Arrays.toString(methodName) + " were not found to exist in class " + clazz.getSimpleName());
        } catch (NoSuchMethodException e) { // temp
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T invokeMethod(Method method, Object instance, Object... args) {
        try {
            return (T) method.invoke(instance, args);
        } catch (IllegalAccessException var4) {
            return null;
        } catch (InvocationTargetException var5) {
            var5.printStackTrace();
            return null;
        }
    }
}
