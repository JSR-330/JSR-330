package com.github.jsr330.spi.config.builder;

import java.lang.annotation.Annotation;
import java.util.Map;

import com.github.jsr330.spi.ClassInjector;

/**
 * A condition that has to be fulfilled in order for a configuration to be applied.
 */
public interface BindingCondition<T> {
    
    /**
     * If the condition is fulfilled the corresponding configuration is applied.
     */
    boolean fulfilled(ClassInjector injector, Class<T> type, Map<String, Class<? extends T>[]> inheritanceTree, Annotation qualifier, ClassLoader classLoader);
    
}
