package com.github.jsr330.spi.config.builder;

import com.github.jsr330.spi.TypeConfig;

/**
 * A binder is responsible for getting a type configuration.
 */
public interface Binder<T> {
    
    /**
     * Gets the {@link TypeConfig}.
     */
    TypeConfig build();
    
}
