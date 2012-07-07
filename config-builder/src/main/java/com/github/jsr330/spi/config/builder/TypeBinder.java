package com.github.jsr330.spi.config.builder;

import javax.inject.Provider;

/**
 * This binder is used to specify an implementation for a type.
 */
public interface TypeBinder<T> extends Binder<T> {
    
    /**
     * The type to instance has to be instantiable to use this method without exception.
     */
    InstancingBinder<T> asSingleton();
    
    /**
     * Marks the type as singleton with the specified implementation.
     */
    InstancingBinder<T> asSingleton(Class<? extends T> type);
    
    /**
     * Specifies an implementation.
     */
    InstancingBinder<T> as(Class<? extends T> type);
    
    /**
     * Defines a provider.
     */
    ConditionalBinder<T> with(Provider<? extends T> provider);
    
}
