package com.github.jsr330.spi.config.builder;

import java.lang.annotation.Annotation;
import java.util.Map;

import com.github.jsr330.spi.ClassInjector;

public interface BindingCondition<T> {
    
    boolean fulfilled(ClassInjector injector, Class<T> type, Map<String, Class<? extends T>[]> inheritanceTree, Annotation qualifier, ClassLoader classLoader);
    
}
