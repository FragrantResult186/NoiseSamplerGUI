package nl.jellejurre.seedchecker;

import java.lang.reflect.Field;
import sun.misc.Unsafe;

public class ReflectionUtils {
    public static Unsafe unsafe = ((Unsafe) getValueFromStaticField(Unsafe.class, "theUnsafe", "lol"));

    public static Field getField(Class<?> clazz, String intermediaryName, String mappedName) throws NoSuchFieldException  {
        try {
            try {
                return clazz.getDeclaredField(intermediaryName);
            } catch (NoSuchFieldException e) {
                return clazz.getDeclaredField(mappedName);
            }
        } catch (NoSuchFieldException e) {
            Class superClass = clazz.getSuperclass();
            if (superClass == null) {
                throw e;
            } else {
                return getField(superClass, intermediaryName, mappedName);
            }
        }
    }

    public static Object getValueFromField(Object instance, String intermediaryName, String mappedName) {
        try {
            Field field = getField(instance.getClass(), intermediaryName, mappedName);
            field.setAccessible(true);
            return field.get(instance);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getValueFromStaticField(Class clazz, String intermediaryName, String mappedName) {
        try {
            Field field = getField(clazz, intermediaryName, mappedName);
            field.setAccessible(true);
            return field.get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setValueOfStaticField(Class clazz, String intermediaryName, String mappedName, Object value) {
        try {
            Field field = getField(clazz, intermediaryName, mappedName);
            field.setAccessible(true);
            field.set(null, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void setValueOfField(Object instance, String intermediaryName, String mappedName, Object value) {
        try {
            Field field = getField(instance.getClass(), intermediaryName, mappedName);
            field.setAccessible(true);
            field.set(instance, value);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
