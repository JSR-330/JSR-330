package com.github.jsr330.spi.config.builder;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * This is an utility class for method
 */
public class Methods {
    
    /**
     * Gets the static method with the specified parameter set.
     */
    public static <T> Method factoryMethod(Class<T> type, Class<?>... arguments) {
        return factoryMethod(type, null, arguments);
    }
    
    /**
     * Gets the static method with the specified parameter set and name.
     */
    public static <T> Method factoryMethod(Class<T> type, String name, Class<?>... arguments) {
        Class<?>[] parameters;
        boolean equals;
        
        for (Method method : type.getDeclaredMethods()) {
            if (!Modifier.isAbstract(method.getModifiers()) && Modifier.isStatic(method.getModifiers())
                    && (name == null || name.isEmpty() || name.equals(method.getName()))) {
                parameters = method.getParameterTypes();
                if ((arguments == null || arguments.length == 0) && parameters.length == 0) {
                    return method;
                } else if (parameters.length == arguments.length) {
                    equals = true;
                    
                    for (int i = 0; i < arguments.length; i++) {
                        if (!arguments[i].equals(parameters[i])) {
                            equals = false;
                        }
                    }
                    
                    if (equals) {
                        return method;
                    }
                }
            }
        }
        return null;
    }
    
}
