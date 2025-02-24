package de.pascalpex.pexnpc.util;

import de.pascalpex.pexnpc.Main;

import java.lang.reflect.Field;
import java.util.logging.Level;

public class ReflectionHelper {

    public static Object getValue(Object instance, String name) {

        Object result = null;
        try {
            Field field = instance.getClass().getDeclaredField(name);
            field.setAccessible(true);

            result = field.get(instance);

            field.setAccessible(false);

        } catch (Exception e) {
            Main.logger().log(Level.SEVERE, "Error getting field " + name + " for " + instance);
        }
        return result;
    }

}
