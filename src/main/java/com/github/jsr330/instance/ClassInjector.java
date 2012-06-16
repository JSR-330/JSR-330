package com.github.jsr330.instance;

import java.lang.annotation.Annotation;
import java.util.Map;

public interface ClassInjector {
    
    void injectStaticMembers(Map<String, Class<?>> classes, Map<String, Class<?>[]> inheritanceTree, ClassLoader classLoader);
    
    <T> T instance(Class<?> type, Map<String, Class<?>[]> inheritanceTree, ClassLoader classLoader, Class<?>[] generics, Annotation qualifier);
    
}
