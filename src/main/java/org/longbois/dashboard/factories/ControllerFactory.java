package org.longbois.dashboard.factories;

import java.util.HashMap;
import java.util.Map;

public class ControllerFactory {
    private static final Map<Class<?>, Object> CONTROLLERS = new HashMap<>();

    public static <T> void addController(Class<T> clazz, T controller) {
        CONTROLLERS.put(clazz, controller);
    }

    public static <T> T getController(Class<T> clazz) {
        return clazz.cast(CONTROLLERS.get(clazz));
    }
}