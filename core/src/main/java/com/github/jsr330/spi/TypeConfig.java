package com.github.jsr330.spi;

import java.lang.annotation.Annotation;
import java.util.Map;

import javax.inject.Provider;

import com.github.jsr330.instance.TypeContainer;

/**
 * A configuration for a {@link ClassInjector}.
 */
public interface TypeConfig {
    
    /**
     * Gets a provider back that satisfy the needs of {@link ClassInjector} or null.
     * 
     * @param injector The asking class injector.
     * @param type The Type to instantiate.
     * @param inheritanceTree The entire inheritance tree of the known classes.
     * @param qualifier A possible annotation for the instance.
     * @param classLoader The class loader used to get the inheritance tree.
     * 
     * @return Gets a {@code null} value back or a proper {@link Provider}.
     */
    <T> Provider<T> getProvider(ClassInjector injector, Class<T> type, Map<String, Class<? extends T>[]> inheritanceTree, Annotation qualifier,
            ClassLoader classLoader);
    
    /**
     * Gets a {@link TypeContainer} or null back.
     * 
     * @param injector The asking class injector.
     * @param type The Type to instantiate.
     * @param inheritanceTree The entire inheritance tree of the known classes.
     * @param qualifier A possible annotation for the instance.
     * @param classLoader The class loader used to get the inheritance tree.
     * 
     * @return Gets a {@code null} value back or a proper {@link TypeContainer}.
     */
    <T> TypeContainer getTypeContainer(ClassInjector injector, Class<T> type, Map<String, Class<? extends T>[]> inheritanceTree, Annotation qualifier,
            ClassLoader classLoader);
    
}
