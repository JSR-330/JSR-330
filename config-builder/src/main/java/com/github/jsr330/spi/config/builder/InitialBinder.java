package com.github.jsr330.spi.config.builder;

/**
 * The initial binder is the entry-point of configuration for a type.
 */
public interface InitialBinder<T> extends Binder<T> {
    
    /**
     * Gets a {@link TypeBinder} back which specifies the implementation.
     */
    <V> TypeBinder<V> instance(Class<V> type);
    
}
