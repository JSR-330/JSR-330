package com.github.jsr330.spi;

import java.lang.annotation.Annotation;
import java.util.Map;

import javax.inject.Provider;

import com.github.jsr330.instance.TypeContainer;

public interface TypeConfig {
    
    <T> Provider<T> getProvider(ClassInjector injector, Class<T> type, Map<String, Class<? extends T>[]> inheritanceTree, Annotation qualifier,
            ClassLoader classLoader);
    
    <T> TypeContainer getTypeContainer(ClassInjector injector, Class<T> type, Map<String, Class<? extends T>[]> inheritanceTree, Annotation qualifier,
            ClassLoader classLoader);
    
}
