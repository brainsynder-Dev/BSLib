package lib.brainsynder.reflection;

import com.google.common.base.Preconditions;
import lib.brainsynder.ServerVersion;
import lib.brainsynder.strings.StringUtilities;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Reflection {
    private static ServerVersion VERSION = ServerVersion.UNKNOWN;

    // These Maps are used for caching results of class/method searches and such
    private static final Map<String, Class> CLASS_MAP;
    private static final Map<String, Method> METHOD_MAP;

    static {
        CLASS_MAP = new HashMap<>();
        METHOD_MAP = new HashMap<>();


        try {
            VERSION = ServerVersion.getVersion();
        } catch (Throwable ignored) {
        }
    }


    /**
     * This function returns the handle of an entity in Java.
     *
     * @param entity The entity object for which we want to retrieve the handle. The handle is an internal Minecraft server
     *               object that represents the entity and is used for various server-side operations.
     * @return The method is returning an object of type T, which is a generic type. The actual type of T will depend on
     * the type of entity passed as a parameter and the type of the handle returned by the entity's getHandle() method.
     */
    public static <T> T getHandle(Entity entity) {
        Preconditions.checkNotNull(entity, "entity can not be null");
        return invokeMethod(getMethod(getCraftClass("CraftEntity"), "getHandle"), entity);
    }


    /**
     * This Java function returns the handle of a CraftWorld object.
     *
     * @param world The "world" parameter is an instance of the "World" class, which represents a Minecraft world. It is
     *              used as an argument to the "getWorldHandle" method to retrieve the underlying handle object for the world.
     * @return The method is returning an object of type T, which is the result of invoking the "getHandle" method on the
     * "CraftWorld" class, passing in the "world" parameter. The specific type of object returned depends on the
     * implementation of the "getHandle" method.
     */
    public static <T> T getWorldHandle(World world) {
        Preconditions.checkNotNull(world, "world can not be null");
        return invokeMethod(getMethod(getCraftClass("CraftWorld"), "getHandle"), world);
    }


    /**
     * This Java function returns a Class object for a given class name in the Bukkit API.
     *
     * @param className The parameter "className" is a String variable that represents the name of the class that we want
     *                  to retrieve from the "org.bukkit.craftbukkit" package.
     * @return The method is returning a Class object that corresponds to the specified class name in the Bukkit
     * CraftBukkit API. If the class is not found, it returns null after printing the stack trace of the exception.
     */
    public static Class getCraftClass(String className) {
        Preconditions.checkNotNull(className, "className can not be null");
        Preconditions.checkArgument(className.isBlank(), "className can not be empty/blank");
        return getClass("org.bukkit.craftbukkit." + VERSION.getNMS() + "." + className);
    }

    /**
     * This function returns a Java class object for a given Minecraft class name.
     *
     * @param className The parameter `className` is a `String` representing the name of a class in the Minecraft game's
     * codebase. The method `getMinecraftClass` takes this parameter and returns the corresponding `Class` object for that
     * class. The method achieves this by appending the `className` parameter to the
     * @return The method `getMinecraftClass` returns a `Class` object for the class with the specified name in the
     * `net.minecraft` package. The name of the class is passed as a parameter to the method.
     */
    public static Class getMinecraftClass(String className) {
        Preconditions.checkNotNull(className, "className can not be null");
        Preconditions.checkArgument(className.isBlank(), "className can not be empty/blank");
        return getClass("net.minecraft." + className);
    }

    /**
     * This function returns a Minecraft class based on the class name and sub-location provided.
     *
     * @param className The name of the class that you want to retrieve.
     * @param subLoc The subLoc parameter is a String that represents the sub-package location within the "net.minecraft"
     * package where the desired class is located.
     * @return The method is returning a Class object for the specified Minecraft class with the given className and
     * subLoc.
     */
    public static Class getMinecraftClass(String className, String subLoc) {
        if ((subLoc == null) || subLoc.isBlank()) return getMinecraftClass(className);
        return getClass("net.minecraft." + subLoc + "." + className);
    }

    /**
     * This function returns a Class object for a given path and caches it for future use.
     *
     * @param path The parameter "path" is a String that represents the fully qualified name of a Java class.
     * @return The method is returning a Class object.
     */
    public static Class getClass(String path) {
        Preconditions.checkNotNull(path, "path can not be null");
        Preconditions.checkArgument(path.isBlank(), "path can not be empty/blank");
        if (CLASS_MAP.containsKey(path)) return CLASS_MAP.get(path);

        try {
            Class clazz = Class.forName(path);
            return CLASS_MAP.computeIfAbsent(path, s -> clazz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    /*     Method methods...     */

    /**
     * This function returns a method with the specified name and parameters from a given class, or null if it does not
     * exist.
     *
     * @param clazz      The class on which the method is defined or inherited from.
     * @param methodName The name of the method that you want to retrieve.
     * @return The method `getMethod` returns a `Method` object that represents the method with the specified name and
     * parameter types in the given class. If the method is not found, it returns `null`.
     */
    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... params) {
        Preconditions.checkNotNull(clazz, "class can not be null");
        Preconditions.checkArgument(((methodName == null) || methodName.isBlank()), "methodName can not be null/empty/blank");

        // This code is generating a unique key for a method based on its class, name, and parameter types.
        StringBuilder key = new StringBuilder().append(clazz.getCanonicalName()).append("$").append(methodName);

        List<Class> paramList = Arrays.asList(params);
        if (!paramList.isEmpty()) {
            key.append("(");

            for (Class paramClass : paramList) {
                key.append(paramClass.getCanonicalName()).append(",");
            }
            key.append(")");
        }

        String finalKey = StringUtilities.replaceLast(",", "", key.toString());
        if (METHOD_MAP.containsKey(finalKey)) return METHOD_MAP.get(finalKey);

        return METHOD_MAP.computeIfAbsent(finalKey, s -> {
            try {
                return clazz.getDeclaredMethod(methodName, params);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * This function returns a method with a given name and parameters from a given class, or throws an exception if the
     * method does not exist.
     *
     * @param clazz      The class object for which you want to retrieve a method.
     * @param methodName An array of Strings representing the names of the methods to search for in the given class.
     * @return The method is returning a java.lang.reflect.Method object that represents the method with the specified name
     * and parameter types declared by the class represented by the Class object. If none of the methods with the specified
     * names and parameter types are found, it throws a NoSuchMethodException.
     */
    public static Method getMethod(Class<?> clazz, String[] methodName, Class<?>... params) {
        for (String method : methodName) {
            try {
                return getMethod(clazz, method, params);
            } catch (Exception ignored) {
            }
        }

        throw new ReflectionException("Methods " + Arrays.toString(methodName) + " were not found to exist in class " + clazz.getSimpleName());
    }

    /**
     * This function invokes a given method on a given instance with the provided arguments using Java reflection.
     *
     * @param method   The method to be invoked. It is of type java.lang.reflect.Method.
     * @param instance The object instance on which the method is to be invoked. It is the object whose method is being called.
     * @return The method is returning an object of type T, which is determined by the generic type parameter specified
     * when the method is called.
     */
    public static <T> T invokeMethod(Method method, Object instance, Object... args) {
        Preconditions.checkNotNull(method, "method can not be null");
        try {
            return (T) method.invoke(instance, args);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            throw new ReflectionException("Unable to invoke method", exception);
        }
    }
}
