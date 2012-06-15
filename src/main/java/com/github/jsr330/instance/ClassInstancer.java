package com.github.jsr330.instance;

import java.lang.annotation.Annotation;
import java.util.Map;

public interface ClassInstancer {
    
    <T> T instance(Class<T> type, Map<String, Class<?>[]> inheritanceTree, ClassLoader classLoader);
    
    <T> T instance(Class<?> type, Map<String, Class<?>[]> inheritanceTree, ClassLoader classLoader, Class<?>[] generics, Annotation qualifier);
    
}
