package lib.brainsynder.reflection;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Objects;

public class Reflection {
    private static HashMap<Class<? extends Entity>, Method> handles = new HashMap<>();

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

    public static <T> T invokeNMSMethod(String className, String method, Object invoker, Class<?>[] parameterClasses, Object... params) {

        try {
            Class e = getNmsClass(className);
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
        return newNMS(className, new Class[0]);
    }

    public static Object newNMS(String className, Class<?>[] parameterClasses, Object... params) {
        try {
            Class ex = getNmsClass(className);
            Constructor constructor = ex.getDeclaredConstructor(parameterClasses);
            constructor.setAccessible(true);
            return constructor.newInstance(params);
        } catch (Exception var5) {
            var5.printStackTrace();
            return null;
        }
    }

    public static <T> T getNMSField(Object owner, String fieldName) {
        return getNMSField(owner.getClass().getSimpleName(), owner, fieldName);
    }

    public static <T> T getNMSField(String className, Object owner, String fieldName) {
        try {
            Class ex = getNmsClass(className);
            Field field = ex.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(owner);
        } catch (Exception var5) {
            var5.printStackTrace();
            return null;
        }
    }

    public static <T> T getNMSStaticField(String className, String fieldName) {
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
        return invokeBukkitMethod(invoker.getClass().getName().replace("org.bukkit.craftbukkit." + getVersion() + ".", ""), method, invoker, parameterClasses, params);
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
        return getBukkitField(owner.getClass().getName().replace("org.bukkit.craftbukkit." + getVersion() + ".", ""), owner, fieldName);
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
        Class<?> clazz = null;
        try {
            clazz = Class.forName("net.minecraft.server." + getVersion() + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return clazz;
    }

    private static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().substring(23);
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
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            int modifiers = modifiersField.getInt(f);
            modifiersField.setInt(f, modifiers & -17);
            return f;
        } catch (Throwable var3) {
            return null;
        }
    }


    public static Class getCBCClass(String className) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + getVersion() + "." + className);
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
