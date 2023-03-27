package lib.brainsynder.utils;

import java.util.*;

public class EnumUtils {

    public static <E extends Enum<E>> Map<String, E> getEnumMap(Class<E> target) {
        LinkedHashMap var1 = new LinkedHashMap<>();
        for (Enum<E> var5 : target.getEnumConstants()) var1.put(var5.name(), var5);
        return var1;
    }

    public static <E extends Enum<E>> List<E> getEnumList(Class<E> target) {
        return new ArrayList<>(Arrays.asList(target.getEnumConstants()));
    }

    public static <E extends Enum<E>> boolean isValidEnum(Class<E> target, String name) {
        if (name == null) return false;

        try {
            Enum.valueOf(target, name);
            return true;
        } catch (IllegalArgumentException var3) {
            return false;
        }
    }

    public static <E extends Enum<E>> E getEnum(Class<E> target, String name) {
        if (name == null) return null;

        try {
            return Enum.valueOf(target, name);
        } catch (IllegalArgumentException var3) {
            return null;
        }
    }
}