package lib.brainsynder.reflection;

import com.google.common.base.Preconditions;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public abstract class FieldAccessor<T> {
    private static final Map<String, FieldAccessor<?>> FIELD_MAP = new HashMap<>();
    private static final FieldAccessor<?> EMPTY = new FieldAccessor<>() {
        @Override
        public Object get(Object instance) {
            return null;
        }

        @Override
        public void set(Object instance, Object value) {

        }

        @Override
        public boolean hasField(Object instance) {
            return false;
        }
    };

    public static <T> FieldAccessor<T> getField(Class<?> targetClass, String fieldName, Class<T> fieldType){
        return getField(targetClass, fieldName, fieldType, true);
    }

    public static <T> FieldAccessor<T> getField(Class<?> targetClass, String fieldName, Class<T> fieldType, boolean errorOut) {
        Preconditions.checkNotNull(targetClass, "targetClass can not be null");
        Preconditions.checkArgument(((fieldName == null) || fieldName.isBlank()), "methodName can not be null/empty/blank");
        Preconditions.checkNotNull(fieldType, "fieldType class can not be null");

        String key = targetClass.getCanonicalName() + "#" + fieldName + "(" + fieldType.getCanonicalName() + ")";
        if (FIELD_MAP.containsKey(key)) return (FieldAccessor<T>) FIELD_MAP.get(key);
        for (Field field : targetClass.getDeclaredFields()) {
            // These two lines of code are used to filter out fields that do not match the specified field name and type.
            if (!field.getName().equals(fieldName)) continue;
            if (!fieldType.isAssignableFrom(field.getType())) continue;
            field.setAccessible(true);

            return (FieldAccessor<T>) FIELD_MAP.computeIfAbsent(key, s -> new FieldAccessor<T>() {
                public T get(Object instance) {
                    try {
                        return (T) field.get(instance);
                    } catch (IllegalAccessException e) {
                        throw new ReflectionException("Unable to get field:", e);
                    }
                }

                public void set(Object instance, T value) {
                    try {
                        field.set(instance, value);
                    } catch (IllegalAccessException e) {
                        throw new ReflectionException("Unable to set field value:", e);
                    }
                }

                public boolean hasField(Object instance) {
                    return field.getDeclaringClass().isAssignableFrom(instance.getClass());
                }
            });
        }

        if (errorOut) {
            throw new ReflectionException("Cannot find field with type " + fieldType);
        }else{
            return (FieldAccessor<T>) EMPTY;
        }
    }



    /**
     * This is an abstract method that returns an object of type T given an instance object.
     *
     * @param instance The "instance" parameter is an object of a class that contains the method being called. It is the
     * object on which the "get" method will be invoked.
     * @return The method is returning an object of type T, which is a generic type parameter. The specific type of object
     * being returned will depend on the implementation of the method in the subclass.
     */
    public abstract T get(Object instance);

    /**
     * This is an abstract method that sets a value of type T to an instance of an object.
     *
     * @param instance The instance parameter is an object of the class that contains the method being called. It
     * represents the instance of the class on which the method is being invoked.
     * @param value The "value" parameter in the method signature represents the value that is being set for a particular
     * instance of an object. The method is designed to set the value of a specific property or field of an object to the
     * provided value. The type of the value is determined by the generic type parameter "T
     */
    public abstract void set(Object instance, T value);

    /**
     * This is an abstract method that returns a boolean value indicating whether an object instance has a field or not.
     *
     * @param instance The "instance" parameter is an object of a class that implements the abstract method "hasField".
     * This parameter is used to check if the instance has a specific field or not. The implementation of this method will
     * vary depending on the class that implements it.
     * @return A boolean value is being returned.
     */
    public abstract boolean hasField(Object instance);
}