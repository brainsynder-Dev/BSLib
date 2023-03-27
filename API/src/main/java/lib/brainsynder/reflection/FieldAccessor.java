package lib.brainsynder.reflection;

import java.lang.reflect.Field;

public abstract class FieldAccessor<T> {
    public abstract T get(Object instance);

    public abstract void set(Object instance, T value);

    public abstract boolean hasField(Object instance);

    public static <T> FieldAccessor<T> getField(Class<?> targetClass, String fieldName, Class<T> fieldType) {
        return getField(targetClass, fieldName, fieldType, 0);
    }

    private static <T> FieldAccessor<T> getField(Class<?> targetClass, String fieldName, Class<T> fieldType, int index) {
        while (true) {
            for (Field field : targetClass.getDeclaredFields()) {
                if (((fieldName == null) || (field.getName().equals(fieldName))) && (fieldType.isAssignableFrom(field.getType())) && (index-- <= 0)) {
                    field.setAccessible(true);

                    return new FieldAccessor<>() {
                        public T get(Object instance) {
                            try {
                                return (T) field.get(instance);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException("Cannot access reflection.", e);
                            }
                        }

                        public void set(Object instance, T value) {
                            try {
                                field.set(instance, value);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException("Cannot access reflection.", e);
                            }
                        }

                        public boolean hasField(Object instance) {
                            return field.getDeclaringClass().isAssignableFrom(instance.getClass());
                        }
                    };
                }
            }

            if (targetClass.getSuperclass() != null) {
                targetClass = targetClass.getSuperclass();
                continue;
            }
            throw new IllegalArgumentException("Cannot find field with type " + fieldType);
        }
    }
}